package app;

/**
 * Clase principal que lanza la aplicacion JavaFX.
 * Carga la vista principal desde Main.fxml, le aplica los estilos CSS,
 * configura el icono, centtra la ventana en pantalla y anade una animacion
 * de fade-in al arrancar. Al cerrar la ventana, cierra la conexion con la BD.
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import dao.DataBaseMain;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargamos la vista principal desde el FXML
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/Main.fxml"));
       
        // Aplicamos la hoja de estilos CSS a la escena
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
            getClass().getResource("/estilos/estiloMain.css").toExternalForm()
        );
        
        // Icono de la ventana principal
        Image icono = new Image(getClass().getResourceAsStream("/icons/icon.png"));
        primaryStage.getIcons().add(icono);
        
        primaryStage.setTitle("Prototipo App");
        primaryStage.setScene(scene);
        
        // Ancho fijo de la ventana
        primaryStage.setWidth(1400);
    
        // Centramos la ventana en la pantalla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        double centerX = (screenBounds.getWidth() - primaryStage.getWidth()) / 2;
        double centerY = (screenBounds.getHeight() - primaryStage.getHeight()) / 2;

        primaryStage.show();

        primaryStage.setX((screenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((screenBounds.getHeight() - primaryStage.getHeight()) / 2);

        // Animacion de fade-in al abrir la aplicacion (600ms)
        root.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(600), root);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    
        // Al cerrar la ventana, cerramos la conexion con la BD
        primaryStage.setOnCloseRequest(event -> {
            DataBaseMain.cerrarConexion();
        });
    
   
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
