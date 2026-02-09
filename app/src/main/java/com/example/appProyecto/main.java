package com.example.appProyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el archivo FXML
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/Main.fxml"));
       
        // Configurar la escena
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
        getClass().getResource("/estilos/estiloMain.css").toExternalForm()
        );
        
        
        
        // Agregar un ícono a la ventana
        Image icono = new Image(getClass().getResourceAsStream("/icons/icon.png")); // Ruta del ícono
        primaryStage.getIcons().add(icono); // Agregar el ícono a la ventana principal
        
        // Configurar la ventana principal
        primaryStage.setTitle("Prototipo App");
        primaryStage.setScene(scene);
        
        
        // Establecer el tamaño de la ventana (ancho y alto)
        primaryStage.setWidth(1200); // Establecer el ancho
        
        
        
        // Obtener el tamaño de la pantalla
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

    // Calcular la posición centrada
    double centerX = (screenBounds.getWidth() - primaryStage.getWidth()) / 2;
    double centerY = (screenBounds.getHeight() - primaryStage.getHeight()) / 2;

    // Configurar la posición de la ventana
    primaryStage.setX(centerX);
    primaryStage.setY(centerY);

    // Mostrar la ventana
    primaryStage.show();

    // Ajustar la posición después de mostrar la ventana
    primaryStage.setX((screenBounds.getWidth() - primaryStage.getWidth()) / 2);
    primaryStage.setY((screenBounds.getHeight() - primaryStage.getHeight()) / 2);
    
   
        
    }

    public static void main(String[] args) {
        launch(args); // Inicia la aplicación
    }
}
