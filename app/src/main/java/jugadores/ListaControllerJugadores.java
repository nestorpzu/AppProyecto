package jugadores;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

public class ListaControllerJugadores {

    // Referencias a los elementos en el FXML
    @FXML
    private TextField txtNombreJugador;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private Spinner<Integer> spinnerEdad;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtNacionalidad;
    @FXML
    private ComboBox<String> cmbPosicion;
    @FXML
    private Button btnAplicarFiltros;
    @FXML
    private Button btnCancelar;

    private ObservableList<Jugador> listaOriginal = FXCollections.observableArrayList();
    
    private TableView<Jugador> tablaJugadores; // Referencia a la tabla principal
    
    private ValidationSupport vNombre, vDescripcion, vEdad, vEmail, vNacionalidad, vPosicion;
    private ImageView iconoOk, iconoErr;


/**
* Inicialización de la ventana
*/
    @FXML
    public void initialize() {
        // Inicializar ComboBox y Spinner
        cmbPosicion.getItems().addAll("Top", "Jungla", "Mid", "ADC", "Soporte");
        cmbPosicion.setPromptText("Selecciona una Posición");
        spinnerEdad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(18, 99, 18));
        spinnerEdad.setEditable(true);
    }

    /**
     * Método para aplicar filtros
     */
    @FXML
    private void aplicarFiltros() {
        
        spinnerEdad.commitValue();

        String nombre = txtNombreJugador.getText() == null ? "" : txtNombreJugador.getText().trim();
        String desc   = txtDescripcion.getText() == null ? "" : txtDescripcion.getText().trim();
        String email  = txtEmail.getText() == null ? "" : txtEmail.getText().trim();
        String nac    = txtNacionalidad.getText() == null ? "" : txtNacionalidad.getText().trim();
        Integer edad  = spinnerEdad.getValue();
        String pos    = cmbPosicion.getValue() == null ? "" : cmbPosicion.getValue().trim();

        boolean algunFiltro =
                !nombre.isEmpty() || !desc.isEmpty() || !email.isEmpty() || !nac.isEmpty() || !pos.isEmpty() || true ;

        if (!algunFiltro) {
            mostrarAlerta("Sin filtros", "Rellena al menos un campo para filtrar.", Alert.AlertType.WARNING);
            return;
        }

        ObservableList<Jugador> filtrados = FXCollections.observableArrayList(
            listaOriginal.stream()
                .filter(j -> {
                    boolean ok = true;
                    if (!nombre.isEmpty()) ok &= j.getNombre() != null && j.getNombre().toLowerCase().contains(nombre.toLowerCase());
                    if (!desc.isEmpty())   ok &= j.getDescripcion() != null && j.getDescripcion().toLowerCase().contains(desc.toLowerCase());
                    if (!email.isEmpty())  ok &= j.getEmail() != null && j.getEmail().toLowerCase().contains(email.toLowerCase());
                    if (!nac.isEmpty())    ok &= j.getNacionalidad() != null && j.getNacionalidad().toLowerCase().contains(nac.toLowerCase());
                    if (!pos.isEmpty())    ok &= j.getPosicion() != null && j.getPosicion().equals(pos);
                    // Edad: solo filtra si tú quieres que siempre filtre
                    ok &= j.getEdad() == edad;
                    return ok;
                })
                .collect(Collectors.toList())
        );

        if (filtrados.isEmpty()) {
            mostrarAlerta("Sin resultados", "No se encontraron jugadores con esos filtros.", Alert.AlertType.INFORMATION);
            return;
        }

        tablaJugadores.setItems(filtrados);
        tablaJugadores.refresh();
        cerrarVentana();
    }

    
    /**
 * Método para borrar los filtros y restaurar la tabla con los datos originales.
 */
    @FXML
    public void borrarFiltros() {
        // Verificar si hay datos originales disponibles
        if (listaOriginal == null || listaOriginal.isEmpty()) {
            mostrarAlerta("Error", "No hay datos originales disponibles para restaurar.", Alert.AlertType.WARNING);
            return;
        }

        // Verificar si la tabla ya muestra todos los datos originales
        if (tablaJugadores.getItems().size() == listaOriginal.size()) {
            mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
            return;
        }

        // Restaurar la tabla con los datos originales
        tablaJugadores.setItems(FXCollections.observableArrayList(listaOriginal));
        tablaJugadores.refresh();

        // Mensaje de éxito
        mostrarAlerta("Filtros eliminados", "Se han eliminado los filtros y restaurado todos los jugadores.", Alert.AlertType.INFORMATION);
    }


    /**
     * Método para cerrar la ventana sin aplicar filtros
     */
    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Mostrar una alerta
     */
  private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
    Platform.runLater(() -> {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        // Centrar la alerta en la pantalla
        alerta.setOnShown(event -> {
            Platform.runLater(() -> {
                Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
                if (stage != null) {
                    Screen screen = Screen.getPrimary();
                    Rectangle2D bounds = screen.getVisualBounds();
                    stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
                    stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
                }
            });
        });

        // Mostrar la alerta después de configurar
        alerta.showAndWait();
    });
}

    /**
     * Setter para listaOriginal
     */
    public void setListaOriginal(ObservableList<Jugador> listaOriginal) {
        this.listaOriginal = listaOriginal;
    }

    /**
     * Setter para la tabla principal
     */
    public void setTablaJugadores(TableView<Jugador> tablaJugadores) {
        this.tablaJugadores = tablaJugadores;
    }
}
