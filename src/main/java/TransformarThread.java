import java.util.concurrent.BlockingQueue;

public class TransformarThread implements Runnable {
    private BlockingQueue<String> colaProcesamiento;
    private BlockingQueue<double[]> coordenadasQueue;
    private boolean transformando = true;

    public TransformarThread(BlockingQueue<String> colaProcesamiento, BlockingQueue<double[]> coordenadasQueue) {
        this.colaProcesamiento = colaProcesamiento;
        this.coordenadasQueue = coordenadasQueue;
    }

    @Override
    public void run() {
        while (transformando) {
            try {
                String tramaGGA = colaProcesamiento.take();
                double[] coordenadas = extraerLatLong(tramaGGA);

                if (coordenadas[0] == 0 && coordenadas[1] == 0) {
                    System.err.println("Error: Trama GGA inválida o malformada.");
                    continue;
                }

                double[] utmCoordenadas = convertirLatLongaUTM(coordenadas);
                System.out.println("Coordenadas UTM -> X: " + utmCoordenadas[0] + ", Y: " + utmCoordenadas[1]);
                // Enviar las coordenadas UTM a la cola para que el mapa las actualice
                coordenadasQueue.put(utmCoordenadas);

            } catch (InterruptedException e) {
                System.err.println("Error en la transformación de coordenadas: " + e.getMessage());
            }
        }
    }

    private double[] extraerLatLong(String trama) {
        try {
            String[] partes = trama.split(",");
            if (partes.length < 6 || partes[2].isEmpty() || partes[4].isEmpty()) {
                return new double[]{0, 0};
            }
            double latGrados = Double.parseDouble(partes[2].substring(0, 2));
            double latMinutos = Double.parseDouble(partes[2].substring(2));
            double latitud = latGrados + (latMinutos / 60);
            if (partes[3].equals("S")) latitud *= -1;

            double lonGrados = Double.parseDouble(partes[4].substring(0, 3));
            double lonMinutos = Double.parseDouble(partes[4].substring(3));
            double longitud = lonGrados + (lonMinutos / 60);
            if (partes[5].equals("W")) longitud *= -1;

            return new double[]{latitud, longitud};
        } catch (Exception e) {
            System.err.println("Error al extraer latitud y longitud: " + e.getMessage());
            return new double[]{0, 0};
        }
    }

    public static double[] convertirLatLongaUTM(double[] coordenadas) {
        double A = 6378137.0; // Radio ecuatorial
        double F = 1 / 298.257223563; // Achatamiento
        double E2 = F * (2 - F); // Excentricidad cuadrada
        double K0 = 0.9996; // Factor de escala en el meridiano central

        // Convertir coordenadas a radianes
        double latitud = Math.toRadians(coordenadas[0]);
        double longitud = Math.toRadians(coordenadas[1]);

        // Determinar el huso UTM
        int huso = (int) ((coordenadas[1] + 180) / 6) + 1;
        double lambda0 = Math.toRadians(-183 + huso * 6); // Meridiano central del huso

        // Cálculos intermedios
        double N = A / Math.sqrt(1 - E2 * Math.pow(Math.sin(latitud), 2));
        double T = Math.pow(Math.tan(latitud), 2);
        double Cc = (E2 / (1 - E2)) * Math.pow(Math.cos(latitud), 2);
        double AVal = Math.cos(latitud) * (longitud - lambda0);

        // Cálculo de la distancia meridiana (M)
        double M = A * ((1 - E2 / 4 - 3 * Math.pow(E2, 2) / 64 - 5 * Math.pow(E2, 3) / 256) * latitud
                - (3 * E2 / 8 + 3 * Math.pow(E2, 2) / 32 + 45 * Math.pow(E2, 3) / 1024) * Math.sin(2 * latitud)
                + (15 * Math.pow(E2, 2) / 256 + 45 * Math.pow(E2, 3) / 1024) * Math.sin(4 * latitud)
                - (35 * Math.pow(E2, 3) / 3072) * Math.sin(6 * latitud));

        // Coordenadas UTM
        double x = K0 * N * (AVal + ((1 - T + Cc) * Math.pow(AVal, 3)) / 6
                + ((5 - 18 * T + Math.pow(T, 2) + 72 * Cc - 58 * E2) * Math.pow(AVal, 5)) / 120) + 500000;

        double y = K0 * (M + N * Math.tan(latitud) * (Math.pow(AVal, 2) / 2
                + ((5 - T + 9 * Cc + 4 * Math.pow(Cc, 2)) * Math.pow(AVal, 4)) / 24
                + ((61 - 58 * T + Math.pow(T, 2) + 600 * Cc - 330 * E2) * Math.pow(AVal, 6)) / 720));

        // Ajustar Y para el hemisferio sur
        if (coordenadas[0] < 0) {
            y += 10000000;
        }

        return new double[]{x, y};
    }

    public void detenerTransformacion() {
        transformando = false;
    }
}
