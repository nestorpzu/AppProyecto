/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package campeones;

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
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
public class EditarControllerCampeon {

    @FXML private CheckBox checkNombre, checkDescripcion, checkRol, checkDificultad;
    @FXML private TextField txtNombre, txtRol;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> comboDificultad;
    @FXML private Button btnEditar, btnCancelar;

    private Campeon campeonSeleccionado;
    private TableView<Campeon> tablaCampeones;
    private String nombreOriginal;

    @FXML
    public void initialize() {
        // Inicializar ComboBox
       comboDificultad.getItems().addAll("Baja", "Media", "Alta");
    comboDificultad.setPromptText("Selecciona una Dificultad");

    configurarCheckBoxes();
    inicializarValidaciones();
    }

    private ValidationSupport vNombre, vDescripcion, vRol, vDificultad;
    private ImageView iconoOk, iconoError;

private void inicializarValidaciones() {
    // Cargar imágenes de recursos
    iconoOk = new ImageView(new Image(getClass().getResourceAsStream("/icons/ok_icon.png")));
    iconoError = new ImageView(new Image(getClass().getResourceAsStream("/icons/error_icon.png")));
    iconoOk.setFitHeight(16); iconoOk.setFitWidth(16);
    iconoError.setFitHeight(16); iconoError.setFitWidth(16);

    vNombre = new ValidationSupport();
    vDescripcion = new ValidationSupport();
    vRol = new ValidationSupport();
    vDificultad = new ValidationSupport();

    // Decorador personalizado
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

    vNombre.setValidationDecorator(decorador);
    vDescripcion.setValidationDecorator(decorador);
    vRol.setValidationDecorator(decorador);
    vDificultad.setValidationDecorator(decorador);

    vNombre.registerValidator(txtNombre, (Control c, String text) -> {
        if (text == null || text.trim().isEmpty()) {
            return ValidationResult.fromError(c, "Nombre vacío");
        }
        return ValidationResult.fromInfo(c, "OK");
    });

    vDescripcion.registerValidator(txtDescripcion, (Control c, String text) -> {
        if (text == null || text.trim().isEmpty()) {
            return ValidationResult.fromError(c, "Descripción vacía");
        }
        return ValidationResult.fromInfo(c, "OK");
    });

    vRol.registerValidator(txtRol, (Control c, String text) -> {
        if (text == null || text.trim().isEmpty()) {
            return ValidationResult.fromError(c, "Rol vacío");
        }
        return ValidationResult.fromInfo(c, "OK");
    });

    vDificultad.registerValidator(comboDificultad, true,
        Validator.createEmptyValidator("Debes seleccionar dificultad"));
}

    
    private void configurarCheckBoxes() {
        txtNombre.disableProperty().bind(checkNombre.selectedProperty().not());
        txtDescripcion.disableProperty().bind(checkDescripcion.selectedProperty().not());
        txtRol.disableProperty().bind(checkRol.selectedProperty().not());
        comboDificultad.disableProperty().bind(checkDificultad.selectedProperty().not());
    }

    public void setCampeon(Campeon campeon) {
        this.campeonSeleccionado = campeon;
        this.nombreOriginal = campeon.getNombre();

        // Actualizar los campos con los datos del campeón
        Platform.runLater(() -> {
            txtNombre.setText(campeon.getNombre());
            txtDescripcion.setText(campeon.getDescripcion());
            txtRol.setText(campeon.getRol());
            comboDificultad.setValue(campeon.getDificultad());
        });
    }

    public void setTablaCampeones(TableView<Campeon> tablaCampeones) {
        this.tablaCampeones = tablaCampeones;
    }

    @FXML
    private void editarCampeon() {
        StringBuilder mensajeError = new StringBuilder();

     

    // Validación con estilos visuales
    if (checkNombre.isSelected() && (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty())) {
        mensajeError.append("- El campo 'Nombre' está vacío.\n");
        
    }
    if (checkDescripcion.isSelected() && (txtDescripcion.getText() == null || txtDescripcion.getText().trim().isEmpty())) {
        mensajeError.append("- El campo 'Descripción' está vacío.\n");
        
    }
    if (checkRol.isSelected() && (txtRol.getText() == null || txtRol.getText().trim().isEmpty())) {
        mensajeError.append("- Debes ingresar un Rol.\n");
       
    }
    if (checkDificultad.isSelected() && (comboDificultad.getValue() == null || comboDificultad.getValue().trim().isEmpty())) {
        mensajeError.append("- Debes seleccionar una Dificultad.\n");
       
    }

    boolean todoOk = vNombre.getValidationResult().getErrors().isEmpty()
              && vDescripcion.getValidationResult().getErrors().isEmpty()
              && vRol.getValidationResult().getErrors().isEmpty()
              && vDificultad.getValidationResult().getErrors().isEmpty();

if (!todoOk) {
    mostrarAlerta("Error de validación", "Revisa los campos marcados con error.", Alert.AlertType.WARNING);
    return;
}


        // Obtener el nombre anterior para actualizar en la base de datos
        String nombreAnterior = campeonSeleccionado.getNombre();

        // Actualizar los datos del objeto en memoria
        if (checkNombre.isSelected()) campeonSeleccionado.setNombre(txtNombre.getText());
        if (checkDescripcion.isSelected()) campeonSeleccionado.setDescripcion(txtDescripcion.getText());
        if (checkRol.isSelected()) campeonSeleccionado.setRol(txtRol.getText());
        if (checkDificultad.isSelected()) campeonSeleccionado.setDificultad(comboDificultad.getValue());

        // **Actualizar en la base de datos**
        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE campeones SET nombre_campeon=?, descripcion_campeon=?, rol_mapa=?, dificultad=? WHERE nombre_campeon=?")) {

            stmt.setString(1, campeonSeleccionado.getNombre());
            stmt.setString(2, campeonSeleccionado.getDescripcion());
            stmt.setString(3, campeonSeleccionado.getRol());
            stmt.setString(4, campeonSeleccionado.getDificultad());
            stmt.setString(5, nombreAnterior);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                tablaCampeones.refresh(); // Actualizar la vista
                mostrarAlerta("Edición exitosa", "Los datos del campeón han sido actualizados correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se encontró el campeón en la base de datos para actualizar.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al actualizar el campeón: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        cerrarVentana();
    }
    
  




    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

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
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
