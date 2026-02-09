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
    private CheckBox chkNombre;
    @FXML
    private TextField txtNombreJugador;
    @FXML
    private CheckBox chkDescripcion;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private CheckBox chkEdad;
    @FXML
    private Spinner<Integer> spinnerEdad;
    @FXML
    private CheckBox chkEmail;
    @FXML
    private TextField txtEmail;
    @FXML
    private CheckBox chkNacionalidad;
    @FXML
    private TextField txtNacionalidad;
    @FXML
    private CheckBox chkPosicion;
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

        // Configurar la lógica de los CheckBox
        configurarCheckBoxes();
        inicializarValidaciones();

    }

    private void inicializarValidaciones() {
    // Cargar iconos
    iconoOk = new ImageView(new Image(getClass().getResourceAsStream("/icons/ok_icon.png")));
    iconoErr = new ImageView(new Image(getClass().getResourceAsStream("/icons/error_icon.png")));
    iconoOk.setFitHeight(16); iconoOk.setFitWidth(16);
    iconoErr.setFitHeight(16); iconoErr.setFitWidth(16);

    // Crear validadores
    vNombre = new ValidationSupport();
    vDescripcion = new ValidationSupport();
    vEdad = new ValidationSupport();
    vEmail = new ValidationSupport();
    vNacionalidad = new ValidationSupport();
    vPosicion = new ValidationSupport();

    GraphicValidationDecoration decorador = new GraphicValidationDecoration() {
        @Override
        public void applyValidationDecoration(ValidationMessage message) {
            super.applyValidationDecoration(message);
            Control c = message.getTarget();
            if (message.getSeverity() == Severity.ERROR) {
                c.setStyle("-fx-border-color: red;");
            } else if (message.getSeverity() == Severity.INFO) {
                c.setStyle("-fx-border-color: green;");
            }
        }
    };

    // Asignar decorador a todos
    vNombre.setValidationDecorator(decorador);
    vDescripcion.setValidationDecorator(decorador);
    vEdad.setValidationDecorator(decorador);
    vEmail.setValidationDecorator(decorador);
    vNacionalidad.setValidationDecorator(decorador);
    vPosicion.setValidationDecorator(decorador);

    // Validadores específicos
    vNombre.registerValidator(txtNombreJugador, true, (Control c, String valor) -> {
        if (valor == null || valor.trim().isEmpty())
            return ValidationResult.fromError(c, "Nombre vacío");
        if (!valor.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+$"))
            return ValidationResult.fromError(c, "Solo letras permitidas");
        return ValidationResult.fromInfo(c, "Nombre correcto");
    });

    vDescripcion.registerValidator(txtDescripcion, true,
        Validator.createEmptyValidator("Descripción vacía"));

    vEmail.registerValidator(txtEmail, true,
        Validator.createRegexValidator("Formato de email inválido", "^(.+)@(.+)\\.(.+)$", Severity.ERROR));

    vNacionalidad.registerValidator(txtNacionalidad, true, (Control c, String texto) -> {
        if (texto == null || texto.trim().isEmpty())
            return ValidationResult.fromError(c, "Nacionalidad vacía");
        if (!texto.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+$"))
            return ValidationResult.fromError(c, "Solo letras permitidas");
        return ValidationResult.fromInfo(c, "Correcto");
    });

    vEdad.registerValidator(spinnerEdad.getEditor(), true, (c, v) -> {
        try {
            int edad = Integer.parseInt(spinnerEdad.getEditor().getText());
            if (edad < 18 || edad > 99)
                return ValidationResult.fromError(c, "Edad fuera de rango (18-99)");
        } catch (NumberFormatException e) {
            return ValidationResult.fromError(c, "Edad no válida");
        }
        return ValidationResult.fromInfo(c, "Edad válida");
    });

    vPosicion.registerValidator(cmbPosicion, true,
        Validator.createEmptyValidator("Selecciona una posición"));
}

    
    private void configurarCheckBoxes() {
        txtNombreJugador.disableProperty().bind(chkNombre.selectedProperty().not());
        txtDescripcion.disableProperty().bind(chkDescripcion.selectedProperty().not());
        spinnerEdad.disableProperty().bind(chkEdad.selectedProperty().not());
        txtEmail.disableProperty().bind(chkEmail.selectedProperty().not());
        txtNacionalidad.disableProperty().bind(chkNacionalidad.selectedProperty().not());
        cmbPosicion.disableProperty().bind(chkPosicion.selectedProperty().not());
    }

    /**
     * Método para aplicar filtros
     */
@FXML
private void aplicarFiltros() {
    StringBuilder mensajeAlertas = new StringBuilder();
    StringBuilder filtrosNoSeleccionados = new StringBuilder("No has seleccionado estos filtros:\n");
    boolean algunFiltroSeleccionado = false;

    // Verificar si los filtros están seleccionados y agregar los no seleccionados
    if (!chkNombre.isSelected()) filtrosNoSeleccionados.append("- Nombre\n");
    else algunFiltroSeleccionado = true;
    
    if (!chkDescripcion.isSelected()) filtrosNoSeleccionados.append("- Descripción\n");
    else algunFiltroSeleccionado = true;
    
    if (!chkEdad.isSelected()) filtrosNoSeleccionados.append("- Edad\n");
    else algunFiltroSeleccionado = true;
    
    if (!chkEmail.isSelected()) filtrosNoSeleccionados.append("- Email\n");
    else algunFiltroSeleccionado = true;
    
    if (!chkNacionalidad.isSelected()) filtrosNoSeleccionados.append("- Nacionalidad\n");
    else algunFiltroSeleccionado = true;
    
    if (!chkPosicion.isSelected()) filtrosNoSeleccionados.append("- Posición\n");
    else algunFiltroSeleccionado = true;

    // Si no hay filtros seleccionados, mostrar una advertencia y salir
    if (!algunFiltroSeleccionado) {
        mostrarAlerta("Sin selección", "No hay ningún filtro seleccionado. Por favor, marque al menos uno.", Alert.AlertType.WARNING);
        return;
    }

    // Validar que los campos de los CheckBox seleccionados no estén vacíos
    if (chkNombre.isSelected() && (txtNombreJugador.getText() == null || txtNombreJugador.getText().trim().isEmpty())) {
        mensajeAlertas.append("- El campo 'Nombre' está vacío.\n");
    }
    if (chkDescripcion.isSelected() && (txtDescripcion.getText() == null || txtDescripcion.getText().trim().isEmpty())) {
        mensajeAlertas.append("- El campo 'Descripción' está vacío.\n");
    }
    if (chkEdad.isSelected() && (spinnerEdad.getValue() == null)) {
        mensajeAlertas.append("- Debe seleccionar un valor para el campo 'Edad'.\n");
    }
    if (chkEmail.isSelected() && (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty())) {
        mensajeAlertas.append("- El campo 'Email' está vacío.\n");
    }
    if (chkNacionalidad.isSelected() && (txtNacionalidad.getText() == null || txtNacionalidad.getText().trim().isEmpty())) {
        mensajeAlertas.append("- El campo 'Nacionalidad' está vacío.\n");
    }
    if (chkPosicion.isSelected() && (cmbPosicion.getValue() == null || cmbPosicion.getValue().trim().isEmpty())) {
        mensajeAlertas.append("- Debe seleccionar un valor para el campo 'Posición'.\n");
    }

    // Si hay errores en los campos seleccionados, mostrar alerta y salir
    boolean todoOK = true;

    if (chkNombre.isSelected())
        todoOK &= vNombre.getValidationResult().getErrors().isEmpty();
    if (chkDescripcion.isSelected())
        todoOK &= vDescripcion.getValidationResult().getErrors().isEmpty();
    if (chkEdad.isSelected())
        todoOK &= vEdad.getValidationResult().getErrors().isEmpty();
    if (chkEmail.isSelected())
        todoOK &= vEmail.getValidationResult().getErrors().isEmpty();
    if (chkNacionalidad.isSelected())
        todoOK &= vNacionalidad.getValidationResult().getErrors().isEmpty();
    if (chkPosicion.isSelected())
        todoOK &= vPosicion.getValidationResult().getErrors().isEmpty();

    if (!todoOK) {
        mostrarAlerta("Error de validación", "Revisa los campos marcados con error.", Alert.AlertType.WARNING);
        return;
    }


    // Verificar que listaOriginal no esté vacía antes de filtrar
    if (listaOriginal == null || listaOriginal.isEmpty()) {
        mostrarAlerta("Error", "No hay datos originales disponibles para filtrar.", Alert.AlertType.WARNING);
        return;
    }

    // Aplicar los filtros a la lista
    ObservableList<Jugador> filtrados = FXCollections.observableArrayList(
        listaOriginal.stream()
            .filter(this::cumpleFiltros)
            .collect(Collectors.toList())
    );

    // Si después de filtrar la lista está vacía, mostrar un mensaje
    if (filtrados.isEmpty()) {
    StringBuilder filtrosAplicados = new StringBuilder("No se encontraron jugadores con los siguientes filtros:\n\n");

    if (chkNombre.isSelected()) {
        filtrosAplicados.append("- Nombre: ").append(txtNombreJugador.getText().trim()).append("\n");
    }
    if (chkDescripcion.isSelected()) {
        filtrosAplicados.append("- Descripción: ").append(txtDescripcion.getText().trim()).append("\n");
    }
    if (chkEdad.isSelected()) {
        filtrosAplicados.append("- Edad: ").append(spinnerEdad.getValue()).append("\n");
    }
    if (chkEmail.isSelected()) {
        filtrosAplicados.append("- Email: ").append(txtEmail.getText().trim()).append("\n");
    }
    if (chkNacionalidad.isSelected()) {
        filtrosAplicados.append("- Nacionalidad: ").append(txtNacionalidad.getText().trim()).append("\n");
    }
    if (chkPosicion.isSelected()) {
        filtrosAplicados.append("- Posición: ").append(cmbPosicion.getValue()).append("\n");
    }

    mostrarAlerta("Sin resultados", filtrosAplicados.toString(), Alert.AlertType.INFORMATION);
    return;
}
 else {
        // Actualizar la tabla con los datos filtrados
        tablaJugadores.setItems(filtrados);
        tablaJugadores.refresh();
    }

    

    // Cerrar la ventana
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



    private boolean cumpleFiltros(Jugador jugador) {
        boolean coincide = true;

        if (chkNombre.isSelected()) {
            coincide &= jugador.getNombre().toLowerCase().contains(txtNombreJugador.getText().toLowerCase());
        }
        if (chkDescripcion.isSelected()) {
            coincide &= jugador.getDescripcion().toLowerCase().contains(txtDescripcion.getText().toLowerCase());
        }
        if (chkEdad.isSelected()) {
            coincide &= jugador.getEdad() == spinnerEdad.getValue();
        }
        if (chkEmail.isSelected()) {
            coincide &= jugador.getEmail().toLowerCase().contains(txtEmail.getText().toLowerCase());
        }
        if (chkNacionalidad.isSelected()) {
            coincide &= jugador.getNacionalidad().toLowerCase().contains(txtNacionalidad.getText().toLowerCase());
        }
        if (chkPosicion.isSelected()) {
            coincide &= jugador.getPosicion().equals(cmbPosicion.getValue());
        }

        return coincide;
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
