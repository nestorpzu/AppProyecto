/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package campeones;

import dao.CampeonDAO;
import modelos.Campeon;
import java.sql.Connection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import utils.AlertUtils;
import utils.ValidationUtils;

/**
 *
 * @author nestor
 */
public class EditarControllerCampeon {

    @FXML private TextField txtNombre;
    @FXML private TextField  txtDescripcion;
     @FXML private ComboBox<String> comboRol;
    @FXML private ComboBox<String> comboDificultad;
    @FXML private Button btnEditar, btnCancelar;

    private Campeon campeonSeleccionado;
    private TableView<Campeon> tablaCampeones;
    private String nombreOriginal;
    private Connection connection;
    private final CampeonDAO campeonDAO = new CampeonDAO();

    @FXML
    public void initialize() {
    comboDificultad.getItems().addAll("Baja", "Media", "Alta");
    comboDificultad.setPromptText("Selecciona una Dificultad");
    comboRol.getItems().addAll("Asesino", "Tanque", "Mago", "Tirador", "Luchador", "Soporte");
    comboRol.setPromptText("Selecciona un Rol");
  
    inicializarValidaciones();
    }

    private ValidationSupport vNombre, vDescripcion, vRol, vDificultad;


    private void inicializarValidaciones() {
        GraphicValidationDecoration decorador = ValidationUtils.crearDecorador();

        vNombre = new ValidationSupport();
        vDescripcion = new ValidationSupport();
        vRol = new ValidationSupport();
        vDificultad = new ValidationSupport();

        vNombre.setValidationDecorator(decorador);
        vDescripcion.setValidationDecorator(decorador);
        vRol.setValidationDecorator(decorador);
        vDificultad.setValidationDecorator(decorador);

        vNombre.registerValidator(txtNombre, true, ValidationUtils.soloLetras("El nombre no puede estar vacío", "Solo letras permitidas"));
        vDescripcion.registerValidator(txtDescripcion, true, ValidationUtils.obligatorio("La descripción es obligatoria"));
        vRol.registerValidator(comboRol, true, ValidationUtils.obligatorio("El rol es obligatorio"));
        vDificultad.registerValidator(comboDificultad, true, ValidationUtils.obligatorio("Debes seleccionar dificultad"));
    }
    

    public void setCampeon(Campeon campeon) {
        this.campeonSeleccionado = campeon;
        this.nombreOriginal = campeon.getNombre();

        Platform.runLater(() -> {
            txtNombre.setText(campeon.getNombre());
            txtDescripcion.setText(campeon.getDescripcion());
            comboRol.setValue(campeon.getRol());
            comboDificultad.setValue(campeon.getDificultad());
        });
    }

    public void setTablaCampeones(TableView<Campeon> tablaCampeones) {
        this.tablaCampeones = tablaCampeones;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @FXML
    private void editarCampeon() {
     
        ValidationUtils.revalidar(vNombre, vDescripcion, vRol, vDificultad);

        boolean todoOk = ValidationUtils.todoValido(vNombre, vDescripcion, vRol, vDificultad);

        if (!todoOk) {
            AlertUtils.mostrarAlerta("Error de validación", "Revisa los campos marcados con error.", Alert.AlertType.WARNING);
            return;
        }

        campeonSeleccionado.setNombre(txtNombre.getText());
        campeonSeleccionado.setDescripcion(txtDescripcion.getText());
        campeonSeleccionado.setRol(comboRol.getValue());
        campeonSeleccionado.setDificultad(comboDificultad.getValue());       


        try {
            boolean exito = campeonDAO.actualizar(campeonSeleccionado, connection);

            if (exito) {
                tablaCampeones.refresh();
                AlertUtils.mostrarAlerta("Edición exitosa", "Los datos del campeón han sido actualizados correctamente.", Alert.AlertType.INFORMATION);
            } else {
                AlertUtils.mostrarAlerta("Error", "No se encontró el campeón en la base de datos para actualizar.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarAlerta("Error", "Ocurrió un error al actualizar el campeón: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        cerrarVentana();
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
