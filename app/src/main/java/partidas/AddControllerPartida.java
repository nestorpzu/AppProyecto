/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package partidas;

import dao.CampeonDAO;
import dao.JugadorDAO;
import dao.PartidaDAO;
import modelos.Partida;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.controlsfx.control.textfield.TextFields;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import utils.AlertUtils;
import utils.ValidationUtils;
/**
 *
 * @author nestor
 */

public class AddControllerPartida {

    @FXML private TextField txtJugador;
    @FXML private TextField txtCampeon;
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtKDA;
    @FXML private ComboBox<String> comboResultado;
    @FXML private Button btnAnadir;
    @FXML private Button btnCancelar;
    
    private final PartidaDAO dao = new PartidaDAO();
    private Connection connection;

    private ObservableList<Partida> listaPartidas;
    private ObservableList<Partida> listaOriginalPartidas;
    private TableView<Partida> tablaPartidas;
    private boolean partidaAgregada = false;

    private ValidationSupport vJugador, vCampeon, vFecha, vKDA, vResultado;

    @FXML
    public void initialize() {
        comboResultado.setItems(FXCollections.observableArrayList("Victoria", "Derrota", "Empate"));
        dateFecha.getEditor().setDisable(true); 
        
        inicializarValidaciones();
    }

private void inicializarValidaciones() {
        GraphicValidationDecoration decorador = ValidationUtils.crearDecorador();

    vJugador = new ValidationSupport();
    vCampeon = new ValidationSupport();
    vFecha = new ValidationSupport();
    vKDA = new ValidationSupport();
    vResultado = new ValidationSupport();

    vJugador.setValidationDecorator(decorador);
    vCampeon.setValidationDecorator(decorador);
    vFecha.setValidationDecorator(decorador);
    vKDA.setValidationDecorator(decorador);
    vResultado.setValidationDecorator(decorador);

    vJugador.registerValidator(txtJugador, true, ValidationUtils.soloLetras("Jugador requerido", "Solo letras permitidas"));
    vCampeon.registerValidator(txtCampeon, true, ValidationUtils.soloLetras("Campeón requerido", "Solo letras permitidas"));
    vFecha.registerValidator(dateFecha, true, ValidationUtils.obligatorio("Selecciona una fecha"));
    vKDA.registerValidator(txtKDA, true, ValidationUtils.kda("KDA requerido", "Formato inválido (ej. 10/2/5)"));
    vResultado.registerValidator(comboResultado, true, ValidationUtils.obligatorio("Selecciona un resultado"));
    }

public void setListaOriginalPartidas(ObservableList<Partida> listaOriginalPartidas) {
        this.listaOriginalPartidas = listaOriginalPartidas;
    }

public void setListaYTablaPartidas(ObservableList<Partida> listaPartidas, TableView<Partida> tablaPartidas) {
        this.listaPartidas = listaPartidas;
        this.tablaPartidas = tablaPartidas;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
        configurarAutocompletado();
    }


private void configurarAutocompletado() {
        JugadorDAO jugadorDAO = new JugadorDAO();
        CampeonDAO campeonDAO = new CampeonDAO();
        List<String> nombresJugadores = jugadorDAO.obtenerNombres(connection);
        List<String> nombresCampeones = campeonDAO.obtenerNombres(connection);
        TextFields.bindAutoCompletion(txtJugador, nombresJugadores);
        TextFields.bindAutoCompletion(txtCampeon, nombresCampeones);
    }

    
@FXML
    private void anadirPartida() {

        vJugador.revalidate();
        vCampeon.revalidate();
        vFecha.revalidate();
        vKDA.revalidate();
        vResultado.revalidate();

        boolean valido = vJugador.getValidationResult().getErrors().isEmpty()
                && vCampeon.getValidationResult().getErrors().isEmpty()
                && vFecha.getValidationResult().getErrors().isEmpty()
                && vKDA.getValidationResult().getErrors().isEmpty()
                && vResultado.getValidationResult().getErrors().isEmpty();

        if (!valido) {
            AlertUtils.mostrarAlerta("Error de validación", "Revisa los campos marcados con errores.", Alert.AlertType.WARNING);
            return;
        }

        String jugador = txtJugador.getText().trim();
        String campeon = txtCampeon.getText().trim();
        LocalDate fecha = dateFecha.getValue();
        String kda = txtKDA.getText().trim();
        String resultado = comboResultado.getValue();

        try  {

            int idJuegan = dao.insertar(new Partida(0, jugador, campeon, fecha, kda, resultado), connection);
           
            if (idJuegan > 0) {
                Partida nueva = new Partida(idJuegan, jugador, campeon, fecha, kda, resultado);
                if (listaPartidas != null) listaPartidas.add(nueva);
                if (tablaPartidas != null) {
                    tablaPartidas.getItems().add(nueva);
                    tablaPartidas.refresh();
                }
                partidaAgregada = true;
                cerrarVentana();
                }           

        } catch (SQLException e) {
            AlertUtils.mostrarAlerta("Error", "Error en base de datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }


    public boolean isPartidaAgregada() {
        return partidaAgregada;
    }
}
