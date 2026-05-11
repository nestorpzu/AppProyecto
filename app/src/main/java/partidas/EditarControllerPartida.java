/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package partidas;

import dao.PartidaDAO;
import modelos.Partida;
import java.sql.Connection;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import utils.AlertUtils;
import utils.ValidationUtils;
/**
 *
 * @author nestor
 */


public class EditarControllerPartida {

    @FXML private TextField txtJugador, txtCampeon, txtKDA;
    @FXML private DatePicker dateFecha;
    @FXML private ComboBox<String> cmbResultado;
    @FXML private Button btnGuardar, btnCancelar;
    private TableView<Partida> tablaPartidas;

    private Partida partidaSeleccionada;
    private String jugadorOriginal, campeonOriginal;
    private ValidationSupport vFecha, vKDA, vResultado;
    
    private final PartidaDAO partidaDAO = new PartidaDAO();
    private Connection connection;


    @FXML
    public void initialize() {
        cmbResultado.getItems().addAll("Victoria", "Derrota", "Empate");
        cmbResultado.setPromptText("Selecciona un resultado");

        inicializarValidaciones();

        txtJugador.setEditable(false);
        txtCampeon.setEditable(false);
    }

    private void inicializarValidaciones() {
    GraphicValidationDecoration decorador = ValidationUtils.crearDecorador();

    vFecha = new ValidationSupport();
    vKDA = new ValidationSupport();
    vResultado = new ValidationSupport();

    vFecha.setValidationDecorator(decorador);
    vKDA.setValidationDecorator(decorador);
    vResultado.setValidationDecorator(decorador);

    vFecha.registerValidator(dateFecha, true, ValidationUtils.obligatorio("La fecha es obligatoria"));
    vKDA.registerValidator(txtKDA, true, ValidationUtils.kda("El KDA es obligatorio", "Formato debe ser x/y/z (ej: 3/1/2)"));
    vResultado.registerValidator(cmbResultado, true, ValidationUtils.obligatorio("Selecciona un resultado"));
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

    public void setConnection(Connection connection) {
        this.connection = connection;
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

        int idJuegan = partidaSeleccionada.getIdJuegan();
        try {
            LocalDate fecha = dateFecha.getValue();
            String kda = txtKDA.getText();
            String resultado = cmbResultado.getValue();

            Partida partidaActualizada = new Partida(idJuegan, "", "", fecha, kda, resultado);
            boolean exito = partidaDAO.actualizar(partidaActualizada, connection);

            if (exito) {
                partidaSeleccionada.setFecha(fecha);
                partidaSeleccionada.setKda(kda);
                partidaSeleccionada.setResultado(resultado);
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