import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ProcesadorVelMax {

    private final HashMap<Coordenada, Long> velocidadesMax;

    public ProcesadorVelMax() {
        this.velocidadesMax = new HashMap<>();
        leerVelocidades();
    }

    private void leerVelocidades() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("velocidades_maximas");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\t");
                if (partes.length == 3) {
                    double lat = Double.parseDouble(partes[0]);
                    double lon = Double.parseDouble(partes[1]);
                    long velocidad = Long.parseLong(partes[2]);
                    velocidadesMax.put(new Coordenada(lat, lon), velocidad);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long conseguirVelocidadMax(double targetLon, double targetLat) {
        return velocidadesMax.entrySet().stream()
                .min(Comparator.comparingDouble(entry ->
                        haversine(targetLat, targetLon, entry.getKey().getX(), entry.getKey().getY())))
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    private static double haversine(double x1, double y1, double lat2, double y2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - x1);
        double dLon = Math.toRadians(y2 - y1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(x1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
