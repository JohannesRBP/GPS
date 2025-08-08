import javafx.application.Application;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        // Crear las colas compartidas
        String puertoSerial = "/dev/ttyACM0"; // Puerto serial actualizado
        BlockingQueue<String> colaLectura = new LinkedBlockingQueue<>();
        BlockingQueue<String> colaProcesamiento = new LinkedBlockingQueue<>();
        BlockingQueue<double[]> coordenadasQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Double> velocidadQueue = new LinkedBlockingQueue<>();

        MapaApp.setCoordenadasQueue(coordenadasQueue);
        MapaApp.setVelocidadQueue(velocidadQueue);

        // Iniciar los hilos para lectura, procesamiento y transformación
        SerialLectorThread lector = new SerialLectorThread(puertoSerial, colaLectura);
        ProcesadorThread procesador = new ProcesadorThread(colaLectura, colaProcesamiento);
        TransformarThread transformar = new TransformarThread(colaProcesamiento, coordenadasQueue);
        VelocimetroThread velocimetro = new VelocimetroThread(coordenadasQueue, velocidadQueue);

        Thread hiloLector = new Thread(lector);
        Thread hiloProcesador = new Thread(procesador);
        Thread hiloTransformador = new Thread(transformar);
        Thread hiloVelocimetro = new Thread(velocimetro);

        hiloLector.start();
        hiloProcesador.start();
        hiloTransformador.start();
        hiloVelocimetro.start();

        // Iniciar la aplicación JavaFX
        Application.launch(MapaApp.class);

        // Shutdown hook para detener los hilos
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            lector.detenerLectura();
            procesador.detenerProcesamiento();
            transformar.detenerTransformacion();
            velocimetro.detenerVelocimetro();
        }));
    }
}
