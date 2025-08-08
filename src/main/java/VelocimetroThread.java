import java.time.Instant;
import java.util.concurrent.BlockingQueue;

public class VelocimetroThread implements Runnable {
    private final BlockingQueue<double[]> coordenadasQueue;
    private final BlockingQueue<Double> velocidadQueue;
    private boolean calculando = true;
    private double[] coordenadaAnterior = null;
    private long tiempoAnterior = 0;

    public VelocimetroThread(BlockingQueue<double[]> coordenadasQueue, BlockingQueue<Double> velocidadQueue) {
        this.coordenadasQueue = coordenadasQueue;
        this.velocidadQueue = velocidadQueue;
    }

    @Override
    public void run() {
        while (calculando) {
            try {
                // Obtener las coordenadas actuales de la cola
                double[] coordenadaActual = coordenadasQueue.take();
                long tiempoActual = Instant.now().toEpochMilli();


                // Calcular la velocidad si tenemos una coordenada anterior
                double velocidadActual;
                if (coordenadaAnterior != null && tiempoAnterior > 0) {
                    // Distancia en metros
                    double distancia = calcularDistancia(
                            coordenadaAnterior[0], coordenadaAnterior[1],
                            coordenadaActual[0], coordenadaActual[1]);

                    // Tiempo en segundos
                    double tiempo = (tiempoActual - tiempoAnterior) / 1000.0;

                    // Velocidad en m/s
                    velocidadActual = distancia / tiempo;

                    // Convertir a km/h
                    velocidadActual = velocidadActual * 3.6;

                    System.out.println("Velocidad calculada: " + velocidadActual + " km/h");

                    // Poner la velocidad en la cola para que el mapa la utilice
                    velocidadQueue.put(velocidadActual);
                }

                // Guardar coordenada y tiempo para la próxima iteración
                coordenadaAnterior = coordenadaActual.clone();
                tiempoAnterior = tiempoActual;

            } catch (InterruptedException e) {
                System.err.println("Error en el cálculo de velocidad: " + e.getMessage());
            }
        }
    }

    /**
     * Calcula la distancia entre dos puntos utilizando coordenadas UTM en metros.
     *
     * @param x1 Coordenada X del punto 1 (UTM Este)
     * @param y1 Coordenada Y del punto 1 (UTM Norte)
     * @param x2 Coordenada X del punto 2 (UTM Este)
     * @param y2 Coordenada Y del punto 2 (UTM Norte)
     * @return Distancia en metros entre los dos puntos
     */
    private double calcularDistancia(double x1, double y1, double x2, double y2) {
        // Para coordenadas UTM, podemos usar la distancia euclidiana
        // ya que están en metros
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void detenerVelocimetro() {
        calculando = false;
    }
}