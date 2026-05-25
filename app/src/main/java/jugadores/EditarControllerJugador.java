/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jugadores;

import dao.JugadorDAO;
import modelos.Jugador;
import java.sql.Connection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import utils.AlertUtils;
import utils.ValidationUtils;

/**
 *
 * @author nestor
 */
public class EditarControllerJugador {
    @FXML
    private TextField txtNombree, txtEmail, txtNacionalidad;

    @FXML
    private TextField  txtDescripcion;

    @FXML
    private Spinner<Integer> spinnerEdad;

    @FXML
    private ComboBox<String> comboPosicion;

    @FXML
    private Button btnEditar, btnCancelar;
    
    private TableView<Jugador> tablaJugadores;
    private Connection connection;
    private final JugadorDAO jugadorDAO = new JugadorDAO();
    private ValidationSupport vNombre, vDescripcion, vEdad, vEmail, vNacionalidad, vPosicion;

    
    private Jugador jugadorSeleccionado;
    


    @FXML
    public void initialize() {
        spinnerEdad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(18, 99, 18));
        spinnerEdad.getEditor().setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d{0,2}") ? change : null;
        }));
        comboPosicion.getItems().addAll("Top", "Jungla", "Mid", "ADC", "Soporte");
        comboPosicion.setPromptText("Selecciona una Posición");
        inicializarValidaciones();
    }

    private void inicializarValidaciones() {
   GraphicValidationDecoration decorador = ValidationUtils.crearDecorador();

    vNombre = new ValidationSupport();
    vDescripcion = new ValidationSupport();
    vEdad = new ValidationSupport();
    vEmail = new ValidationSupport();
    vNacionalidad = new ValidationSupport();
    vPosicion = new ValidationSupport();

    vNombre.setValidationDecorator(decorador);
    vDescripcion.setValidationDecorator(decorador);
    vEdad.setValidationDecorator(decorador);
    vEmail.setValidationDecorator(decorador);
    vNacionalidad.setValidationDecorator(decorador);
    vPosicion.setValidationDecorator(decorador);

    vNombre.registerValidator(txtNombree, true, ValidationUtils.soloLetras("El nombre no puede estar vacío", "Solo letras permitidas"));
    vDescripcion.registerValidator(txtDescripcion, true, ValidationUtils.obligatorio("La descripción es obligatoria"));
    vEdad.registerValidator(spinnerEdad.getEditor(), true, ValidationUtils.edad(18, 99, "Edad fuera de rango (18-99)", "Introduce un número válido"));
    vEmail.registerValidator(txtEmail, true, ValidationUtils.email("Formato de email inválido"));
    vNacionalidad.registerValidator(txtNacionalidad, true, ValidationUtils.soloLetras("La nacionalidad es obligatoria", "Solo letras permitidas"));
    vPosicion.registerValidator(comboPosicion, true, ValidationUtils.obligatorio("Selecciona una posición"));
}

    public void setJugador(Jugador jugador) {
        this.jugadorSeleccionado = jugador;
        spinnerEdad.getValueFactory().setValue(jugador.getEdad());
        comboPosicion.setValue(jugador.getPosicion());
        
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

public void setConnection(Connection connection) {
    this.connection = connection;
}
    
    @FXML
    private void editarJugador() {

        ValidationUtils.revalidar(vNombre, vDescripcion, vEdad, vEmail, vNacionalidad, vPosicion);

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

        

        jugadorSeleccionado.setNombre(txtNombree.getText().trim());
        jugadorSeleccionado.setDescripcion(txtDescripcion.getText().trim());
        jugadorSeleccionado.setEdad(spinnerEdad.getValue());
        jugadorSeleccionado.setEmail(txtEmail.getText().trim());
        jugadorSeleccionado.setNacionalidad(txtNacionalidad.getText().trim());
        jugadorSeleccionado.setPosicion(comboPosicion.getValue());



        try {
            boolean exito = jugadorDAO.actualizar(jugadorSeleccionado, connection);

            if (exito) {
                tablaJugadores.refresh();
                AlertUtils.mostrarAlerta("Edición exitosa", "Los datos del jugador han sido actualizados correctamente.", Alert.AlertType.INFORMATION);
            } else {
                AlertUtils.mostrarAlerta("Error", "No se encontró el jugador en la base de datos para actualizar.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarAlerta("Error", "Ocurrió un error al actualizar el jugador: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        Stage stage = (Stage) btnEditar.getScene().getWindow();
        stage.close();
}

    
    @FXML
    private void cancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}