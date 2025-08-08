public class UTMToPixelConverter {
    private static double[][] utmRefs = CoordenadasReferencia.calcularCoordenadasUTM();

    // Coordenadas en píxeles correspondientes a las esquinas de la imagen original (8192x4701)
    private static int[][] pixelCoords = {
            {25, 0},      // Esquina superior izquierda
            {8192, 0},    // Esquina superior derecha
            {8192, 4701}, // Esquina inferior derecha
            {0, 4701}     // Esquina inferior izquierda
    };

    // Variables para escalar las coordenadas UTM a píxeles
    private static double scaleX;    // Factor de escala en el eje X
    private static double scaleY;    // Factor de escala en el eje Y
    private static double utmOriginX; // Coordenada UTM de origen en X
    private static double utmOriginY; // Coordenada UTM de origen en Y

    static {
        // Establecer el origen UTM (esquina superior izquierda de la imagen)
        utmOriginX = utmRefs[0][0];
        utmOriginY = utmRefs[0][1];


        scaleX = (pixelCoords[1][0] - pixelCoords[0][0]) / (utmRefs[1][0] - utmRefs[0][0]);

        scaleY = (pixelCoords[3][1] - pixelCoords[0][1]) / (utmRefs[0][1] - utmRefs[3][1]);
    }


    public static int[] convertirUTMAPixel(double[] utm) {
        // Calcular coordenada X en píxeles utilizando el factor de escala y el origen
        int pixelX = (int) ((utm[0] - utmOriginX) * scaleX + pixelCoords[0][0]);

        int pixelY = (int) ((utmOriginY - utm[1]) * scaleY + pixelCoords[0][1]);

        System.out.println("Coordenadas en píxeles -> X: " + pixelX + ", Y: " + pixelY);

        return new int[]{pixelX, pixelY};
    }
}











