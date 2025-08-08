import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Popup;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MapaApp extends Application {
    private static BlockingQueue<double[]> coordenadasQueue;
    private static BlockingQueue<Double> velocidadQueue;

    private static final double IMAGE_WIDTH = 8192;
    private static final double IMAGE_HEIGHT = 4701;

    private static final double CANVAS_WIDTH = 1920;
    private static final double CANVAS_HEIGHT = 1080;

    private static final List<Color> COLORS = List.of(
            Color.GREEN, Color.ORANGE, Color.RED
    );
    private static int colorIndex = 0;
    private static double velocidadActual = 0.0;
    private static Long velocidadMaximaActual = 0L;

    private Popup popupExceso;

    public static void setCoordenadasQueue(BlockingQueue<double[]> queue) {
        coordenadasQueue = queue;
    }

    public static void setVelocidadQueue(BlockingQueue<Double> queue) {
        velocidadQueue = queue;
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        Canvas canvasFondo = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        GraphicsContext gcFondo = canvasFondo.getGraphicsContext2D();

        Canvas canvasPuntos = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        GraphicsContext gcPuntos = canvasPuntos.getGraphicsContext2D();

        Canvas canvasTexto = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        GraphicsContext gcTexto = canvasTexto.getGraphicsContext2D();

        root.getChildren().addAll(canvasFondo, canvasPuntos, canvasTexto);

        Platform.runLater(() -> {
            Image background = new Image(getClass().getResourceAsStream("/insia.jpg"));
            gcFondo.drawImage(background, 30, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        });

        ProcesadorVelMax procesadorVelMax = new ProcesadorVelMax();
        crearPopupExcesoVelocidad();

        // Hilo para actualizar velocidad actual
        new Thread(() -> {
            try {
                while (true) {
                    if (velocidadQueue != null && !velocidadQueue.isEmpty()) {
                        velocidadActual = velocidadQueue.take();
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // Hilo para procesar coordenadas y actualizar visualización
        new Thread(() -> {
            try {
                while (true) {
                    double[] utmCoords = coordenadasQueue.take();
                    if (utmCoords != null) {
                        int[] pixelCoords = UTMToPixelConverter.convertirUTMAPixel(utmCoords);

                        double scaledX = pixelCoords[0] * (CANVAS_WIDTH / IMAGE_WIDTH);
                        double scaledY = pixelCoords[1] * (CANVAS_HEIGHT / IMAGE_HEIGHT);

                        velocidadMaximaActual = procesadorVelMax.conseguirVelocidadMax(utmCoords[0], utmCoords[1]);

                        cambiarColor(velocidadMaximaActual, velocidadActual);

                        Platform.runLater(() -> {
                            // Dibujar puntos dinámicos
                            gcPuntos.setFill(COLORS.get(colorIndex));
                            gcPuntos.fillOval(scaledX, scaledY, 15, 15);

                            // Dibujar cuadro detrás del texto
                            gcTexto.setFill(Color.LIGHTGRAY);
                            gcTexto.fillRect(230, 15, 400, 90);

                            // Mostrar las velocidades en el cuadro
                            gcTexto.setFont(Font.font("Georgia", 24));
                            gcTexto.setFill(Color.DARKBLUE);
                            gcTexto.fillText("Velocidad Máxima: " + velocidadMaximaActual + " km/h", 250, 45);
                            gcTexto.setFill(Color.DARKGREEN);
                            gcTexto.fillText("Velocidad Actual: " + String.format("%.2f", velocidadActual) + " km/h", 250, 80);

                            // Mostrar o ocultar Pop-Up dependiendo del exceso de velocidad
                            if (colorIndex == 2) {
                                mostrarPopupExcesoVelocidad();
                            } else {
                                ocultarPopupExcesoVelocidad();
                            }
                        });
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Scene scene = new Scene(root, CANVAS_WIDTH, CANVAS_HEIGHT);
        primaryStage.setTitle("Mapa - Coordenadas UTM");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void cambiarColor(Long velocidadMax, Double velocidadCalc) {
        double margen = velocidadMax * 0.1;

        if (velocidadCalc < velocidadMax - margen) {
            colorIndex = 0; // Verde
        } else if (velocidadCalc <= velocidadMax + margen) {
            colorIndex = 1; // Naranja
        } else {
            colorIndex = 2; // Rojo
        }
    }

    private void crearPopupExcesoVelocidad() {
        popupExceso = new Popup();
        Canvas canvasPopup = new Canvas(300, 100);
        GraphicsContext gcPopup = canvasPopup.getGraphicsContext2D();

        gcPopup.setFill(Color.RED);
        gcPopup.fillRect(0, 0, 300, 100);
        gcPopup.setFill(Color.WHITE);
        gcPopup.setFont(Font.font("Arial", 20));
        gcPopup.fillText("¡EXCESO DE VELOCIDAD!", 50, 50);

        popupExceso.getContent().add(canvasPopup);
    }

    private void mostrarPopupExcesoVelocidad() {
        if (!popupExceso.isShowing()) {
            popupExceso.show(Stage.getWindows().get(0));
        }
    }

    private void ocultarPopupExcesoVelocidad() {
        if (popupExceso.isShowing()) {
            popupExceso.hide();
        }
    }
}
