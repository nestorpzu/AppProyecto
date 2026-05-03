/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author npauc
 */
public class AlertUtils {
    public static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        
        alerta.setOnShown(event -> {
            Platform.runLater(()-> {
                Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();
                stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
            });
        });
        alerta.showAndWait();
    }
}
