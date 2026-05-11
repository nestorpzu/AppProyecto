package app;



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
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/Main.fxml"));
       
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
            getClass().getResource("/estilos/estiloMain.css").toExternalForm()
        );
        
        Image icono = new Image(getClass().getResourceAsStream("/icons/icon.png"));
        primaryStage.getIcons().add(icono);
        
        primaryStage.setTitle("Prototipo App");
        primaryStage.setScene(scene);
        
        primaryStage.setWidth(1200);
    
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        double centerX = (screenBounds.getWidth() - primaryStage.getWidth()) / 2;
        double centerY = (screenBounds.getHeight() - primaryStage.getHeight()) / 2;

        primaryStage.show();

        primaryStage.setX((screenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((screenBounds.getHeight() - primaryStage.getHeight()) / 2);

        root.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(600), root);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    
        primaryStage.setOnCloseRequest(event -> {
            DataBaseMain.cerrarConexion();
        });
    
   
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
