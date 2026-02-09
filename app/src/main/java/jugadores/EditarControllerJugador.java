/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jugadores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

/**
 *
 * @author nestor
 */
public class EditarControllerJugador {

    @FXML
    private CheckBox checkNombre, checkDescripcion, checkEdad, checkEmail, checkNacionalidad, checkPosicion;

    @FXML
    private TextField txtNombree, txtEmail, txtNacionalidad;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private Spinner<Integer> spinnerEdad;

    @FXML
    private ComboBox<String> comboPosicion;

    @FXML
    private Button btnEditar, btnCancelar;

    private Jugador jugador; // Referencia al jugador que se está editando
    @FXML
    private TableView<Jugador> tablaJugadores;
    
    private ValidationSupport vNombre, vDescripcion, vEdad, vEmail, vNacionalidad, vPosicion;
    private ImageView iconoOk, iconoErr;

    
    private Jugador jugadorSeleccionado;
    
    private String nombreOriginal;

    @FXML
    public void initialize() {
        // Inicializar el Spinner para que tenga un rango de valores y un valor predeterminado
        spinnerEdad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(18, 99, 18));

        comboPosicion.getItems().addAll("Top", "Jungla", "Mid", "ADC", "Soporte");
        comboPosicion.setPromptText("Selecciona una Posición");
        configurarCheckBoxes();
        inicializarValidaciones();
    }

    private void inicializarValidaciones() {
    // Cargar iconos desde /resources/img/
    iconoOk = new ImageView(new Image(getClass().getResourceAsStream("/icons/ok_icon.png")));
    iconoErr = new ImageView(new Image(getClass().getResourceAsStream("/icons/error_icon.png")));
    iconoOk.setFitWidth(16); iconoOk.setFitHeight(16);
    iconoErr.setFitWidth(16); iconoErr.setFitHeight(16);

    // Crear los ValidationSupport
    vNombre = new ValidationSupport();
    vDescripcion = new ValidationSupport();
    vEdad = new ValidationSupport();
    vEmail = new ValidationSupport();
    vNacionalidad = new ValidationSupport();
    vPosicion = new ValidationSupport();

    // Decorador visual personalizado
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

    // Aplicar decorador a todos
    vNombre.setValidationDecorator(decorador);
    vDescripcion.setValidationDecorator(decorador);
    vEdad.setValidationDecorator(decorador);
    vEmail.setValidationDecorator(decorador);
    vNacionalidad.setValidationDecorator(decorador);
    vPosicion.setValidationDecorator(decorador);

    // Validaciones
    vNombre.registerValidator(txtNombree, true,
            Validator.createEmptyValidator("El nombre no puede estar vacío"));

    vDescripcion.registerValidator(txtDescripcion, true,
            Validator.createEmptyValidator("La descripción es obligatoria"));

    vEmail.registerValidator(txtEmail, true,
            Validator.createRegexValidator("Formato de email inválido", "^(.+)@(.+)\\.(.+)$", Severity.ERROR));

    vNacionalidad.registerValidator(txtNacionalidad, true,
            Validator.createEmptyValidator("La nacionalidad es obligatoria"));

    vEdad.registerValidator(spinnerEdad.getEditor(), true, (c, value) -> {
        try {
            int edad = Integer.parseInt(spinnerEdad.getEditor().getText());
            if (edad < 18 || edad > 99) {
                return ValidationResult.fromError(c, "Edad fuera de rango (18-99)");
            }
        } catch (NumberFormatException e) {
            return ValidationResult.fromError(c, "Edad debe ser un número");
        }
        return ValidationResult.fromInfo(c, "OK");
    });

    vPosicion.registerValidator(comboPosicion, true,
            Validator.createEmptyValidator("Selecciona una posición"));
}

    
    private void configurarCheckBoxes() {
    // Los campos se habilitan sólo si el CheckBox correspondiente está marcado
    txtNombree.disableProperty().bind(checkNombre.selectedProperty().not());
    txtDescripcion.disableProperty().bind(checkDescripcion.selectedProperty().not());
    spinnerEdad.disableProperty().bind(checkEdad.selectedProperty().not());
    txtEmail.disableProperty().bind(checkEmail.selectedProperty().not());
    txtNacionalidad.disableProperty().bind(checkNacionalidad.selectedProperty().not());
    comboPosicion.disableProperty().bind(checkPosicion.selectedProperty().not());
}
    
    // Otros métodos del controlador
    public void setJugador(Jugador jugador) {
        // Configurar los valores del jugador en los campos
        this.jugadorSeleccionado = jugador;
        this.nombreOriginal = jugador.getNombre();
        spinnerEdad.getValueFactory().setValue(jugador.getEdad());
        comboPosicion.setValue(jugador.getPosicion());
        this.jugador = jugador; // Asignar el jugador a una variable de clase
        
    // Actualizar los campos con los datos del jugador
    Platform.runLater(() -> {
        txtNombree.setText(jugador.getNombre());
        txtDescripcion.setText(jugador.getDescripcion());
        spinnerEdad.getValueFactory().setValue(jugador.getEdad());
        txtEmail.setText(jugador.getEmail());
        txtNacionalidad.setText(jugador.getNacionalidad());
        comboPosicion.setValue(jugador.getPosicion());
        
    });
    }

    public void setTablaJugadores(TableView<Jugador> tablaJugadores) {
    this.tablaJugadores = tablaJugadores;
   
}
    
    @FXML
    private void editarJugador() {
    StringBuilder mensajeError = new StringBuilder();

    // Verificar que los CheckBox marcados tengan datos válidos
    if (checkNombre.isSelected() && (txtNombree.getText() == null || txtNombree.getText().trim().isEmpty())) {
        mensajeError.append("- Debe ingresar un valor para el campo Nombre.\n");
    }
    if (checkEdad.isSelected() && (spinnerEdad.getValue() == null)) {
        mensajeError.append("- Debe seleccionar un valor para el campo Edad.\n");
    }
    if (checkEmail.isSelected() && (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty())) {
        mensajeError.append("- Debe ingresar un valor para el campo Email.\n");
    }
    if (checkNacionalidad.isSelected() && (txtNacionalidad.getText() == null || txtNacionalidad.getText().trim().isEmpty())) {
        mensajeError.append("- Debe ingresar un valor para el campo Nacionalidad.\n");
    }
    if (checkPosicion.isSelected() && (comboPosicion.getValue() == null || comboPosicion.getValue().trim().isEmpty())) {
        mensajeError.append("- Debe seleccionar un valor para el campo Posición.\n");
    }
    if (checkDescripcion.isSelected() && (txtDescripcion.getText() == null || txtDescripcion.getText().trim().isEmpty())) {
        mensajeError.append("- Debe ingresar un valor para el campo Descripción.\n");
    }

    // Si hay errores, mostrar un mensaje y detener la ejecución
    boolean todoOK = vNombre.getValidationResult().getErrors().isEmpty()
              && vDescripcion.getValidationResult().getErrors().isEmpty()
              && vEmail.getValidationResult().getErrors().isEmpty()
              && vEdad.getValidationResult().getErrors().isEmpty()
              && vNacionalidad.getValidationResult().getErrors().isEmpty()
              && vPosicion.getValidationResult().getErrors().isEmpty();

    if (!todoOK) {
        mostrarAlerta("Error de validación", "Revisa los campos marcados con error.", Alert.AlertType.WARNING);
        return;
    }


    // Verificar que hay un jugador seleccionado
    if (jugadorSeleccionado == null) {
        mostrarAlerta("Error", "No se seleccionó ningún jugador para editar.", Alert.AlertType.WARNING);
        return;
    }
    
    // Si ningún CheckBox está marcado, mostrar advertencia
    if (!checkNombre.isSelected() && !checkEdad.isSelected() && !checkEmail.isSelected() &&
        !checkNacionalidad.isSelected() && !checkPosicion.isSelected() && !checkDescripcion.isSelected()) {
        mostrarAlerta("Sin selección", "No hay ningún campo seleccionado. Por favor, marque al menos uno.", Alert.AlertType.WARNING);
        return;
    }

    // Verificar que tablaJugadores no sea null antes de usarla
    if (tablaJugadores == null) {
        System.out.println("tablaJugadores no está inicializada.");
        return;
    }

    // Obtener el jugador seleccionado
    Jugador jugadorSeleccionado = tablaJugadores.getSelectionModel().getSelectedItem();
    if (jugadorSeleccionado == null) {
        mostrarAlerta("Error", "No se seleccionó ningún jugador. Por favor, seleccione un jugador de la tabla.", Alert.AlertType.WARNING);
        System.out.println("No se seleccionó ningún jugador.");
        return;
    }

    // Guardar los datos antiguos para la actualización en la base de datos
    String nombreAnterior = jugadorSeleccionado.getNombre();

    // Actualizar los datos del objeto en memoria
    if (checkNombre.isSelected()) jugadorSeleccionado.setNombre(txtNombree.getText());
    if (checkDescripcion.isSelected()) jugadorSeleccionado.setDescripcion(txtDescripcion.getText());
    if (checkEdad.isSelected()) jugadorSeleccionado.setEdad(spinnerEdad.getValue());
    if (checkEmail.isSelected()) jugadorSeleccionado.setEmail(txtEmail.getText());
    if (checkNacionalidad.isSelected()) jugadorSeleccionado.setNacionalidad(txtNacionalidad.getText());
    if (checkPosicion.isSelected()) jugadorSeleccionado.setPosicion(comboPosicion.getValue());

        
    
    // **Actualizar en la base de datos**
    try (Connection connection = baseDatos.DataBaseMain.getConnection();
         PreparedStatement stmt = connection.prepareStatement(
                 "UPDATE jugadores SET nombre_jugador=?, descripcion_jugador=?, edad=?, email=?, nacionalidad=?, posicion_jugador=? WHERE nombre_jugador=?")) {

        stmt.setString(1, jugadorSeleccionado.getNombre());
        stmt.setString(2, jugadorSeleccionado.getDescripcion());
        stmt.setInt(3, jugadorSeleccionado.getEdad());
        stmt.setString(4, jugadorSeleccionado.getEmail());
        stmt.setString(5, jugadorSeleccionado.getNacionalidad());
        stmt.setString(6, jugadorSeleccionado.getPosicion());
        stmt.setString(7, nombreAnterior); // Buscar por el nombre antiguo

        int filasAfectadas = stmt.executeUpdate();

        if (filasAfectadas > 0) {
            tablaJugadores.refresh(); // Actualizar la vista
            mostrarAlerta("Edición exitosa", "Los datos del jugador han sido actualizados correctamente.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se encontró el jugador en la base de datos para actualizar.", Alert.AlertType.ERROR);
        }
    } catch (Exception e) {
        e.printStackTrace();
        mostrarAlerta("Error", "Ocurrió un error al actualizar el jugador: " + e.getMessage(), Alert.AlertType.ERROR);
    }

    // Cerrar la ventana después de la edición
    Stage stage = (Stage) btnEditar.getScene().getWindow();
    stage.close();
}

    
private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
       
        
        // Centrar la alerta en la pantalla después de mostrarla
    alerta.setOnShown(event -> {
        Platform.runLater(() -> {
            Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
        });
    });
    alerta.showAndWait();
        
    }
    
    @FXML
    private void cancelar() {
        // Cerrar la ventana sin realizar cambios
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}