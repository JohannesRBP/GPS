import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

public class SerialLectorThread implements Runnable {
    private String puertoSerial;
    private boolean leyendo;
    private BlockingQueue<String> colaLectura;

    public SerialLectorThread(String puertoSerial, BlockingQueue<String> colaLectura) {
        this.puertoSerial = puertoSerial;
        this.leyendo = true;
        this.colaLectura = colaLectura;
    }

    @Override
    public void run() {
        try (InputStream inputStream = new FileInputStream(puertoSerial)) {
            byte[] buffer = new byte[1024];
            int bytesLeidos;

            while (leyendo) {
                bytesLeidos = inputStream.read(buffer);
                if (bytesLeidos != -1) {
                    String datosLeidos = new String(buffer, 0, bytesLeidos);
                    colaLectura.put(datosLeidos);
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error en la lectura del puerto serie: " + e.getMessage());
        }
    }

    public void detenerLectura() {
        leyendo = false;
    }
}
