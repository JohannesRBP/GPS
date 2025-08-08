import java.util.concurrent.BlockingQueue;

public class ProcesadorThread implements Runnable {
    private BlockingQueue<String> colaLectura;
    private BlockingQueue<String> colaProcesamiento;
    private boolean procesando;
    private StringBuilder acumulador = new StringBuilder();

    public ProcesadorThread(BlockingQueue<String> colaLectura, BlockingQueue<String> colaProcesamiento) {
        this.colaLectura = colaLectura;
        this.colaProcesamiento = colaProcesamiento;
        this.procesando = true;
    }

    @Override
    public void run() {
        while (procesando) {
            try {
                String datos = colaLectura.take();
                acumulador.append(datos);

                int indiceFinLinea;
                while ((indiceFinLinea = acumulador.indexOf("\n")) != -1) {
                    String lineaCompleta = acumulador.substring(0, indiceFinLinea).trim();
                    acumulador.delete(0, indiceFinLinea + 1);

                    if (lineaCompleta.startsWith("$GPGGA")) {
                        System.out.println("Trama GGA completa: " + lineaCompleta);
                        colaProcesamiento.put(lineaCompleta);
                    }
                }
            } catch (InterruptedException e) {
                System.err.println("Error en el procesamiento de datos: " + e.getMessage());
            }
        }
    }

    public void detenerProcesamiento() {
        procesando = false;
    }
}
