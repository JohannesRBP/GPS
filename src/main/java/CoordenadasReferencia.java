public class CoordenadasReferencia {
    public static double[][] calcularCoordenadasUTM() {
        double[][] geograficas = {
                {40.3878028, -3.6347250}, // Esquina superior izquierda
                {40.3878250, -3.6290944}, // Esquina superior derecha
                {40.3853889, -3.6290333}, // Esquina inferior derecha
                {40.3853944, -3.6346861}  // Esquina inferior izquierda
        };

        double[][] utm = new double[geograficas.length][2];
        for (int i = 0; i < geograficas.length; i++) {
            utm[i] = TransformarThread.convertirLatLongaUTM(geograficas[i]);
        }
        return utm;
    }
}
