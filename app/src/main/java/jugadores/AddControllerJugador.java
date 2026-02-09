package jugadores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
/**
 *
 * @author nestor
 */

public class AddControllerJugador {

    // Referencias a los elementos en el FXML
    @FXML private CheckBox checkNombre;
    @FXML private TextField txtNombre;
    @FXML private CheckBox checkEdad;
    @FXML private Spinner<Integer> spinnerEdad;
    @FXML private CheckBox checkEmail;
    @FXML private TextField txtEmail;
    @FXML private CheckBox checkNacionalidad;
    @FXML private TextField txtNacionalidad;
    @FXML private CheckBox checkPosicion;
    @FXML private ComboBox<String> comboPosicion;
    @FXML private CheckBox checkDescripcion;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnAplicarFiltros;
    @FXML private Button btnCancelar;
    @FXML private TableColumn<Jugador, Boolean> colSeleccionar;

    // Datos y referencias a tabla principal
    private ObservableList<Jugador> listaJugadores;
    private ObservableList<Jugador> listaOriginal;
    private TableView<Jugador> tablaJugadores;

    // Bandera para saber si se ha añadido un jugador
    private boolean jugadorAgregado = false;

    // Soporte de validación para cada campo
    private ValidationSupport vNombre, vEdad, vEmail, vNacionalidad, vPosicion, vDescripcion;

    /**
     * Se ejecuta al cargar la ventana.
     */
    @FXML
    public void initialize() {
        // Inicializar ComboBox de posiciones
        comboPosicion.getItems().addAll("Top", "Jungla", "Mid", "ADC", "Soporte");
        comboPosicion.setPromptText("Selecciona una Posición");

        // Inicializar Spinner de edad con rango 18-99
        spinnerEdad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(18, 99, 18));

        // Restringir entrada del Spinner a máximo 2 dígitos numéricos
        spinnerEdad.getEditor().setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d{0,2}") ? change : null;
        }));

        configurarCheckBoxes();
        inicializarValidaciones();
    }

    /**
     * Configura validaciones visuales con ControlsFX.
     */
    private void inicializarValidaciones() {
        GraphicValidationDecoration decorador = new GraphicValidationDecoration();

        vNombre = new ValidationSupport();
        vEdad = new ValidationSupport();
        vEmail = new ValidationSupport();
        vNacionalidad = new ValidationSupport();
        vPosicion = new ValidationSupport();
        vDescripcion = new ValidationSupport();

        // Aplicar decorador visual a cada campo
        vNombre.setValidationDecorator(decorador);
        vEdad.setValidationDecorator(decorador);
        vEmail.setValidationDecorator(decorador);
        vNacionalidad.setValidationDecorator(decorador);
        vPosicion.setValidationDecorator(decorador);
        vDescripcion.setValidationDecorator(decorador);

        // Validaciones específicas
        vNombre.registerValidator(txtNombre, true, (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) return ValidationResult.fromError(c, "El nombre no puede estar vacío");
            if (!valor.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+$")) return ValidationResult.fromError(c, "Solo letras permitidas");
            return ValidationResult.fromInfo(c, "Nombre válido");
        });

        vEdad.registerValidator(spinnerEdad.getEditor(), true, (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) return ValidationResult.fromError(c, "La edad no puede estar vacía");
            try {
                int edad = Integer.parseInt(valor.trim());
                if (edad < 18 || edad > 99) return ValidationResult.fromError(c, "Edad debe estar entre 18 y 99");
            } catch (NumberFormatException e) {
                return ValidationResult.fromError(c, "Solo números válidos");
            }
            return ValidationResult.fromInfo(c, "Edad válida");
        });

        vEmail.registerValidator(txtEmail, true,
            Validator.createRegexValidator("Formato de email inválido", "^(.+)@(.+)\\.(.+)$", Severity.ERROR));

        vNacionalidad.registerValidator(txtNacionalidad, true, (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) return ValidationResult.fromError(c, "La nacionalidad es obligatoria");
            if (!valor.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+$")) return ValidationResult.fromError(c, "Solo letras permitidas");
            return ValidationResult.fromInfo(c, "Correcto");
        });

        vPosicion.registerValidator(comboPosicion, true,
            Validator.createEmptyValidator("Selecciona una posición"));

        vDescripcion.registerValidator(txtDescripcion, true,
            Validator.createEmptyValidator("La descripción no puede estar vacía"));
    }

    /**
     * Habilita o deshabilita campos según CheckBoxes.
     */
    private void configurarCheckBoxes() {
        txtNombre.disableProperty().bind(checkNombre.selectedProperty().not());
        spinnerEdad.disableProperty().bind(checkEdad.selectedProperty().not());
        txtEmail.disableProperty().bind(checkEmail.selectedProperty().not());
        txtNacionalidad.disableProperty().bind(checkNacionalidad.selectedProperty().not());
        comboPosicion.disableProperty().bind(checkPosicion.selectedProperty().not());
        txtDescripcion.disableProperty().bind(checkDescripcion.selectedProperty().not());
    }

    /**
     * Recibe lista original y tabla principal para actualizar datos.
     */
    public void setListaYTablaJugadores(ObservableList<Jugador> listaJugadores, TableView<Jugador> tablaJugadores) {
        this.listaJugadores = listaJugadores;
        this.tablaJugadores = tablaJugadores;
    }

    /**
     * Acción al pulsar "Añadir Jugador".
     */
    @FXML
    private void anadirJugador() {
        StringBuilder mensajeError = new StringBuilder();

        // Validación manual de campos obligatorios según selección
        if (checkNombre.isSelected() && txtNombre.getText().trim().isEmpty()) mensajeError.append("- Campo 'Nombre' vacío.\n");
        if (checkEdad.isSelected() && spinnerEdad.getValue() == null) mensajeError.append("- Campo 'Edad' vacío.\n");
        if (checkEmail.isSelected() && txtEmail.getText().trim().isEmpty()) mensajeError.append("- Campo 'Email' vacío.\n");
        if (checkNacionalidad.isSelected() && txtNacionalidad.getText().trim().isEmpty()) mensajeError.append("- Campo 'Nacionalidad' vacío.\n");
        if (checkPosicion.isSelected() && comboPosicion.getValue() == null) mensajeError.append("- Campo 'Posición' vacío.\n");
        if (checkDescripcion.isSelected() && txtDescripcion.getText().trim().isEmpty()) mensajeError.append("- Campo 'Descripción' vacío.\n");

        // Validar campos con ControlsFX si están seleccionados
        boolean valido = true;
        if (checkNombre.isSelected()) valido &= vNombre.getValidationResult().getErrors().isEmpty();
        if (checkEdad.isSelected()) valido &= vEdad.getValidationResult().getErrors().isEmpty();
        if (checkEmail.isSelected()) valido &= vEmail.getValidationResult().getErrors().isEmpty();
        if (checkNacionalidad.isSelected()) valido &= vNacionalidad.getValidationResult().getErrors().isEmpty();
        if (checkPosicion.isSelected()) valido &= vPosicion.getValidationResult().getErrors().isEmpty();
        if (checkDescripcion.isSelected()) valido &= vDescripcion.getValidationResult().getErrors().isEmpty();

        if (!valido) {
            mostrarAlerta("Error de validación", "Revisa los campos marcados con error.", Alert.AlertType.WARNING);
            return;
        }

        // Si no hay ningún campo marcado
        if (!checkNombre.isSelected() && !checkEdad.isSelected() && !checkEmail.isSelected() &&
            !checkNacionalidad.isSelected() && !checkPosicion.isSelected() && !checkDescripcion.isSelected()) {
            mostrarAlerta("Sin selección", "Debe marcar al menos un campo.", Alert.AlertType.WARNING);
            return;
        }

        // Capturar datos ingresados (o usar valores por defecto)
        String nombre = checkNombre.isSelected() ? txtNombre.getText() : "Sin Nombre";
        int edad = checkEdad.isSelected() ? spinnerEdad.getValue() : 0;
        String email = checkEmail.isSelected() ? txtEmail.getText() : "Sin Email";
        String nacionalidad = checkNacionalidad.isSelected() ? txtNacionalidad.getText() : "Sin Nacionalidad";
        String posicion = checkPosicion.isSelected() ? comboPosicion.getValue() : "Sin Posición";
        String descripcion = checkDescripcion.isSelected() ? txtDescripcion.getText() : "Sin Descripción";

        // Insertar en base de datos
        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO jugadores (nombre_jugador, descripcion_jugador, edad, email, nacionalidad, posicion_jugador) VALUES (?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, descripcion);
            preparedStatement.setInt(3, edad);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, nacionalidad);
            preparedStatement.setString(6, posicion);
            preparedStatement.executeUpdate();
            System.out.println("Jugador añadido correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al guardar el jugador: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        // Agregar a lista y refrescar tabla
        Jugador nuevoJugador = new Jugador(nombre, descripcion, edad, email, nacionalidad, posicion, false);
        if (listaOriginal != null) {
            listaOriginal.add(nuevoJugador);
        }
        if (tablaJugadores != null) {
            tablaJugadores.setItems(FXCollections.observableArrayList(listaOriginal));
            tablaJugadores.refresh();
        }

        jugadorAgregado = true;
        cerrarVentana();
    }

    /**
     * Cierra la ventana sin hacer cambios.
     */
    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    /**
     * Cierra la ventana actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra una alerta con título, mensaje y tipo.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.setOnShown(event -> {
            Platform.runLater(() -> {
                Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
                Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
                stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
            });
        });
        alerta.showAndWait();
    }

    // Getter para saber si se agregó el jugador correctamente
    public boolean isJugadorAgregado() {
        return jugadorAgregado;
    }

    // Setters para lista original y tabla
    public void setListaOriginal(ObservableList<Jugador> listaOriginal) {
        this.listaOriginal = listaOriginal;
    }

    public void setTablaJugadores(TableView<Jugador> tablaJugadores) {
        this.tablaJugadores = tablaJugadores;
    }
}
