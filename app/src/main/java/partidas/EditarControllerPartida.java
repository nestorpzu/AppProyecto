/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package partidas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import java.time.LocalDate;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
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


public class EditarControllerPartida {

    @FXML private TextField txtJugador, txtCampeon, txtKDA;
    @FXML private DatePicker dateFecha;
    @FXML private ComboBox<String> cmbResultado;
    @FXML private Button btnGuardar, btnCancelar;
    @FXML private TableView<Partida> tablaPartidas;

    private Partida partidaSeleccionada;
    private String jugadorOriginal, campeonOriginal;
    private ValidationSupport vFecha, vKDA, vResultado;
    private ImageView iconoOk, iconoErr;


    @FXML
    public void initialize() {
        // Inicializar ComboBox con valores predefinidos
        cmbResultado.getItems().addAll("Victoria", "Derrota", "Empate");
        cmbResultado.setPromptText("Selecciona un resultado");

        inicializarValidaciones();

        txtJugador.setEditable(false);
        txtCampeon.setEditable(false);
    }

    private void inicializarValidaciones() {
    iconoOk = new ImageView(new Image(getClass().getResourceAsStream("/icons/ok_icon.png")));
    iconoErr = new ImageView(new Image(getClass().getResourceAsStream("/icons/error_icon.png")));
    iconoOk.setFitWidth(16); iconoOk.setFitHeight(16);
    iconoErr.setFitWidth(16); iconoErr.setFitHeight(16);

    vFecha = new ValidationSupport();
    vKDA = new ValidationSupport();
    vResultado = new ValidationSupport();

    GraphicValidationDecoration decorador = new GraphicValidationDecoration() {
        @Override
        public void applyValidationDecoration(ValidationMessage message) {
            super.applyValidationDecoration(message);
            message.getTarget().setStyle(
                message.getSeverity() == Severity.ERROR ?
                "-fx-border-color: red;" :
                "-fx-border-color: green;"
            );
        }
    };

    vFecha.setValidationDecorator(decorador);
    vKDA.setValidationDecorator(decorador);
    vResultado.setValidationDecorator(decorador);

    vFecha.registerValidator(dateFecha, true, Validator.createEmptyValidator("La fecha es obligatoria"));

    vResultado.registerValidator(cmbResultado, true, Validator.createEmptyValidator("Selecciona un resultado"));

    // Validación de KDA como texto con formato tipo "2/1/3"
    vKDA.registerValidator(txtKDA, true, (control, value) -> {
    if (!(value instanceof String)) {
        return ValidationResult.fromError(control, "Entrada no válida");
    }

    String texto = (String) value;

    if (texto.trim().isEmpty()) {
        return ValidationResult.fromError(control, "El KDA es obligatorio");
    }

    if (!texto.matches("\\d+/\\d+/\\d+")) {
        return ValidationResult.fromError(control, "Formato debe ser x/y/z (ej: 3/1/2)");
    }

    return ValidationResult.fromInfo(control, "Formato correcto");
});


}

    
   public void setPartida(Partida partida) {
    this.partidaSeleccionada = partida;
    this.jugadorOriginal = partida.getJugador();
    this.campeonOriginal = partida.getCampeon();

    Platform.runLater(() -> {
        txtJugador.setText(partida.getJugador());
        txtCampeon.setText(partida.getCampeon());
        dateFecha.setValue(partida.getFecha());
        txtKDA.setText(partida.getKda());
        cmbResultado.setValue(partida.getResultado());

        
        txtJugador.setEditable(false);
        txtCampeon.setEditable(false);
    });
}


    public void setTablaPartidas(TableView<Partida> tablaPartidas) {
        this.tablaPartidas = tablaPartidas;
    }

    @FXML
        private void editarPartida() {
        vFecha.revalidate();
        vKDA.revalidate();
        vResultado.revalidate();

        boolean todoOK = vFecha.getValidationResult().getErrors().isEmpty()
                && vKDA.getValidationResult().getErrors().isEmpty()
                && vResultado.getValidationResult().getErrors().isEmpty();

        if (!todoOK) {
            AlertUtils.mostrarAlerta("Error de validación", "Revisa los campos marcados con error.", Alert.AlertType.WARNING);
            return;
        }


       Partida partidaSeleccionada = this.partidaSeleccionada;
        if (partidaSeleccionada == null) {
            AlertUtils.mostrarAlerta("Error", "No se seleccionó ninguna partida.", Alert.AlertType.WARNING);
            return;
        }

        int idJuegan = partidaSeleccionada.getIdJuegan();  // ✅ Ahora obtenemos directamente el ID_juegan

        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE juegan SET Fecha_jugada=?, KDA=?, Resultado=? WHERE ID_juegan=?")) {

            stmt.setObject(1, dateFecha.getValue() != null ? dateFecha.getValue() : null);
            stmt.setString(2, txtKDA.getText());
            stmt.setString(3, cmbResultado.getValue());
            stmt.setInt(4, idJuegan);  // ✅ Se usa ID único

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                // ✅ Actualizamos los valores en el TableView
                partidaSeleccionada.setFecha(dateFecha.getValue());
                partidaSeleccionada.setKda(txtKDA.getText());
                partidaSeleccionada.setResultado(cmbResultado.getValue());
                tablaPartidas.refresh();
                AlertUtils.mostrarAlerta("Edición exitosa", "Los datos de la partida han sido actualizados correctamente.", Alert.AlertType.INFORMATION);
            } else {
                AlertUtils.mostrarAlerta("Error", "No se encontró la partida en la base de datos para actualizar.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarAlerta("Error", "Ocurrió un error al actualizar la partida: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

}