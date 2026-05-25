package controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

/**
 * Controlador de la ventana de ayuda/manual de usuario.
 * Carga el archivo HTML del manual en un WebView para su visualizacion.
 */
public class AyudaController {

    @FXML
    private WebView webView;

    @FXML
    public void initialize() {
        String url = getClass().getResource("/help/manual.html").toExternalForm();
        webView.getEngine().load(url);
    }
}