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
import utils.AlertUtils;

/**
 *
 * @author nestor
 */
public class EditarControllerJugador {
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

        // Revalidar y cortar si hay errores
        vNombre.revalidate();
        vDescripcion.revalidate();
        vEdad.revalidate();
        vEmail.revalidate();
        vNacionalidad.revalidate();
        vPosicion.revalidate();

        boolean todoOK = vNombre.getValidationResult().getErrors().isEmpty()
                && vDescripcion.getValidationResult().getErrors().isEmpty()
                && vEmail.getValidationResult().getErrors().isEmpty()
                && vEdad.getValidationResult().getErrors().isEmpty()
                && vNacionalidad.getValidationResult().getErrors().isEmpty()
                && vPosicion.getValidationResult().getErrors().isEmpty();

        if (!todoOK) {
            AlertUtils.mostrarAlerta("Error de validación", "Revisa los campos marcados con error.", Alert.AlertType.WARNING);
            return;
        }

        if (jugadorSeleccionado == null) {
            AlertUtils.mostrarAlerta("Error", "No se seleccionó ningún jugador para editar.", Alert.AlertType.WARNING);
            return;
        }

        

        //  Actualizar siempre desde los campos
        jugadorSeleccionado.setNombre(txtNombree.getText().trim());
        jugadorSeleccionado.setDescripcion(txtDescripcion.getText().trim());
        jugadorSeleccionado.setEdad(spinnerEdad.getValue());
        jugadorSeleccionado.setEmail(txtEmail.getText().trim());
        jugadorSeleccionado.setNacionalidad(txtNacionalidad.getText().trim());
        jugadorSeleccionado.setPosicion(comboPosicion.getValue());



        // **Actualizar en la base de datos**
        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE jugadores SET nombre_jugador=?, descripcion_jugador=?, edad=?, email=?, nacionalidad=?, posicion_jugador=? WHERE idJugadores=?")) {

            preparedStatement.setString(1, jugadorSeleccionado.getNombre());
            preparedStatement.setString(2, jugadorSeleccionado.getDescripcion());
            preparedStatement.setInt(3, jugadorSeleccionado.getEdad());
            preparedStatement.setString(4, jugadorSeleccionado.getEmail());
            preparedStatement.setString(5, jugadorSeleccionado.getNacionalidad());
            preparedStatement.setString(6, jugadorSeleccionado.getPosicion());
            preparedStatement.setInt(7, jugadorSeleccionado.getId()); // Buscar por el nombre antiguo

            int filasAfectadas = preparedStatement.executeUpdate();

            if (filasAfectadas > 0) {
                tablaJugadores.refresh(); // Actualizar la vista
                AlertUtils.mostrarAlerta("Edición exitosa", "Los datos del jugador han sido actualizados correctamente.", Alert.AlertType.INFORMATION);
            } else {
                AlertUtils.mostrarAlerta("Error", "No se encontró el jugador en la base de datos para actualizar.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarAlerta("Error", "Ocurrió un error al actualizar el jugador: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        // Cerrar la ventana después de la edición
        Stage stage = (Stage) btnEditar.getScene().getWindow();
        stage.close();
}

    
    @FXML
    private void cancelar() {
        // Cerrar la ventana sin realizar cambios
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}