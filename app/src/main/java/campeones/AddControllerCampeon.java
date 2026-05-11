/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package campeones;

import dao.DataBaseMain;
import dao.CampeonDAO;
import modelos.Campeon;
import java.sql.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class AddControllerCampeon {

    @FXML private TextField txtNombre;
    @FXML private TextField  txtDescripcion;
    @FXML private ComboBox<String> comboRol;
    @FXML private ComboBox<String> comboDificultad;
    @FXML private Button btnAnadir;
    @FXML private Button btnCancelar;

    private ObservableList<Campeon> listaCampeones;
    private ObservableList<Campeon> listaOriginalCampeones;
    private TableView<Campeon> tablaCampeones;

    private boolean campeonAgregado = false;
    private Connection connection;
    private ValidationSupport vNombre, vDescripcion, vRol, vDificultad;


    @FXML
    public void initialize() {
        comboRol.getItems().addAll("Asesino", "Tanque", "Mago", "Tirador", "Luchador", "Soporte");
        comboRol.setPromptText("Selecciona un Rol");

        comboDificultad.getItems().addAll("Baja", "Media", "Alta");
        comboDificultad.setPromptText("Selecciona una Dificultad");

        inicializarValidaciones();
    }

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
    vDescripcion.registerValidator(txtDescripcion, true, ValidationUtils.obligatorio("La descripción no puede estar vacía"));
    vRol.registerValidator(comboRol, true, ValidationUtils.obligatorio("Selecciona un rol"));
    vDificultad.registerValidator(comboDificultad, true, ValidationUtils.obligatorio("Selecciona una dificultad"));
    }

    

@FXML
    private void anadirCampeon() {
        
        if (!validarFormulario()) return;
        
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();
        String rol = comboRol.getValue();
        String dificultad = comboDificultad.getValue();
    

        try {
            if (connection == null || connection.isClosed()) {
                connection = DataBaseMain.getConnection();
            }
            CampeonDAO campeonDAO = new CampeonDAO();
            Campeon nuevoCampeon = new Campeon(0, nombre, descripcion, rol, dificultad);
            int idNuevo = campeonDAO.insertar(nuevoCampeon, connection);

            if (idNuevo > 0) {
                nuevoCampeon.setId(idNuevo);
                AlertUtils.mostrarAlerta("Éxito", "Campeón añadido correctamente.", Alert.AlertType.INFORMATION);

                if (listaOriginalCampeones != null) {
                    listaOriginalCampeones.add(nuevoCampeon);
                }

                if (tablaCampeones != null) {
                    tablaCampeones.setItems(FXCollections.observableArrayList(listaOriginalCampeones));
                    tablaCampeones.refresh();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarAlerta("Error", "No se pudo añadir el campeón: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        campeonAgregado = true;
        cerrarVentana();
    }

private boolean validarFormulario() {
            boolean valido = true;

            vNombre.revalidate();
            vDescripcion.revalidate();
            vRol.revalidate();
            vDificultad.revalidate();

            valido &= vNombre.getValidationResult().getErrors().isEmpty();
            valido &= vDescripcion.getValidationResult().getErrors().isEmpty();
            valido &= vRol.getValidationResult().getErrors().isEmpty();
            valido &= vDificultad.getValidationResult().getErrors().isEmpty();

            if (!valido) {
                AlertUtils.mostrarAlerta("Error de validación", "Revisa los campos marcados con errores.", Alert.AlertType.WARNING);
            }

            return valido;
}


    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public boolean isCampeonAgregado() {
        return campeonAgregado;
    }

    public void setListaOriginalCampeones(ObservableList<Campeon> listaOriginalCampeones) {
        this.listaOriginalCampeones = listaOriginalCampeones;
    }

    public void setTablaCampeones(TableView<Campeon> tablaCampeones) {
        this.tablaCampeones = tablaCampeones;
    }

    public void setListaYTablaCampeones(ObservableList<Campeon> listaCampeones, TableView<Campeon> tablaCampeones) {
        this.listaCampeones = listaCampeones;
        this.tablaCampeones = tablaCampeones;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}