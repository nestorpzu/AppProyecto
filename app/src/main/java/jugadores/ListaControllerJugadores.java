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
import utils.AlertUtils;

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
        // Esto bloquea cualquier cosa que no sean dígitos (y permite vacío porque \\d* acepta cadena vacía).
        spinnerEdad.getEditor().setTextFormatter(new TextFormatter<>(c ->
        c.getControlNewText().matches("\\d*") ? c : null
        ));
        spinnerEdad.setEditable(true);

        // Esto sirve para que no de errores el spinner..
        SpinnerValueFactory.IntegerSpinnerValueFactory vf =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(18, 99, 18) {
            @Override
            public void increment(int steps) {
                if (getValue() == null) {
                    setValue(18);              
                    return;              
                }
                super.increment(steps);
            }

            @Override
            public void decrement(int steps) {
                if (getValue() == null) {
                    setValue(18);
                    return;
                }
                super.decrement(steps);
            }
        };

        spinnerEdad.setValueFactory(vf);  
    }
    
    /**
     * Método para aplicar filtros
     */
    
    @FXML
    private void aplicarFiltros() {
        
        spinnerEdad.commitValue();
        
        String edadTxt = spinnerEdad.getEditor().getText().trim();
            boolean usarEdad = !edadTxt.isEmpty();
            Integer edad = usarEdad ? Integer.parseInt(edadTxt) : null;

        String nombre = txtNombreJugador.getText() == null ? "" : txtNombreJugador.getText().trim();
        String desc   = txtDescripcion.getText() == null ? "" : txtDescripcion.getText().trim();
        String email  = txtEmail.getText() == null ? "" : txtEmail.getText().trim();
        String nac    = txtNacionalidad.getText() == null ? "" : txtNacionalidad.getText().trim();      
        String pos    = cmbPosicion.getValue() == null ? "" : cmbPosicion.getValue().trim();

        boolean algunFiltro =
            !nombre.isEmpty() || !desc.isEmpty() || !email.isEmpty() || !nac.isEmpty() || !pos.isEmpty() || edad != null;

        if (!algunFiltro) {
            AlertUtils.mostrarAlerta("Sin filtros", "Rellena al menos un campo para filtrar.", Alert.AlertType.WARNING);
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
                    if (edad != null) ok &= j.getEdad() == edad;
                    return ok;
                })
                .collect(Collectors.toList())
        );

        if (filtrados.isEmpty()) {
            AlertUtils.mostrarAlerta("Sin resultados", "No se encontraron jugadores con esos filtros.", Alert.AlertType.INFORMATION);
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
            AlertUtils.mostrarAlerta("Error", "No hay datos originales disponibles para restaurar.", Alert.AlertType.WARNING);
            return;
        }

        // Verificar si la tabla ya muestra todos los datos originales
        if (tablaJugadores.getItems().size() == listaOriginal.size()) {
            AlertUtils.mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
            return;
        }

        // Restaurar la tabla con los datos originales
        tablaJugadores.setItems(FXCollections.observableArrayList(listaOriginal));
        tablaJugadores.refresh();

        // Mensaje de éxito
        AlertUtils.mostrarAlerta("Filtros eliminados", "Se han eliminado los filtros y restaurado todos los jugadores.", Alert.AlertType.INFORMATION);
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
