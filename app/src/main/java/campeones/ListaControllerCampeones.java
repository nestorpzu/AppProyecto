/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package campeones;

import modelos.Campeon;
import java.util.stream.Collectors;
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

public class ListaControllerCampeones {

    @FXML private TextField txtNombreCampeon;
    @FXML private TextField  txtDescripcion;
    @FXML private ComboBox<String> cmbRol;
    @FXML private ComboBox<String> cmbDificultad;
    @FXML private Button btnAplicarFiltros;
    @FXML private Button btnCancelar;

    private ObservableList<Campeon> listaOriginal = FXCollections.observableArrayList();
    private TableView<Campeon> tablaCampeones;
    private ValidationSupport vNombre, vDescripcion, vRol, vDificultad;

    

    @FXML
    public void initialize() {
        if (cmbRol != null) {
        cmbRol.getItems().addAll("Tanque", "Luchador", "Asesino", "Mago", "Tirador", "Soporte");
    } else {
        System.err.println(" Error: cmbRol es null. Revisa el FXML.");
    }
        cmbRol.setPromptText("Selecciona un Rol");

        cmbDificultad.getItems().addAll("Baja", "Media", "Alta");
        cmbDificultad.setPromptText("Selecciona una Dificultad");

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

    vNombre.registerValidator(txtNombreCampeon, true, ValidationUtils.soloLetrasFiltro("Solo letras permitidas"));
    vDescripcion.registerValidator(txtDescripcion, true, ValidationUtils.obligatorio("La descripción no puede estar vacía"));
    vRol.registerValidator(cmbRol, true, ValidationUtils.obligatorio("Selecciona un rol"));
    vDificultad.registerValidator(cmbDificultad, true, ValidationUtils.obligatorio("Selecciona una dificultad"));
}

    
    @FXML
    private void aplicarFiltros() {

        String nombre = txtNombreCampeon.getText() == null ? "" : txtNombreCampeon.getText().trim();
        String desc   = txtDescripcion.getText() == null ? "" : txtDescripcion.getText().trim();
        String rol    = (cmbRol.getValue() == null) ? "" : cmbRol.getValue().trim();
        String dif    = (cmbDificultad.getValue() == null) ? "" : cmbDificultad.getValue().trim();

        boolean algunFiltro = !nombre.isEmpty() || !desc.isEmpty() || !rol.isEmpty() || !dif.isEmpty();
        if (!algunFiltro) {
            AlertUtils.mostrarAlerta("Sin filtros", "Rellena al menos un campo para filtrar.", Alert.AlertType.WARNING);
            return;
        }

        ObservableList<Campeon> filtrados = FXCollections.observableArrayList(
            listaOriginal.stream()
                .filter(c -> {
                    boolean ok = true;
                    if (!nombre.isEmpty()) ok &= c.getNombre() != null && c.getNombre().toLowerCase().contains(nombre.toLowerCase());
                    if (!desc.isEmpty())   ok &= c.getDescripcion() != null && c.getDescripcion().toLowerCase().contains(desc.toLowerCase());
                    if (!rol.isEmpty())    ok &= c.getRol() != null && c.getRol().equals(rol);
                    if (!dif.isEmpty())    ok &= c.getDificultad() != null && c.getDificultad().equals(dif);
                    return ok;
                })
                .collect(Collectors.toList())
        );

        if (filtrados.isEmpty()) {
            AlertUtils.mostrarAlerta("Sin resultados", "No se encontraron campeones con esos filtros.", Alert.AlertType.INFORMATION);
            return;
        }

        tablaCampeones.setItems(filtrados);
        tablaCampeones.refresh();
        cerrarVentana();
    }

    @FXML
    public void borrarFiltrosCampeones() {
        if (listaOriginal == null) {
            AlertUtils.mostrarAlerta("Error", "La lista original de campeones no está inicializada.", Alert.AlertType.ERROR);
            return;
        }

        if (listaOriginal.isEmpty()) {
            AlertUtils.mostrarAlerta("Error", "No hay datos originales disponibles para restaurar.", Alert.AlertType.WARNING);
            return;
        }

        if (tablaCampeones.getItems().size() == listaOriginal.size() &&
            tablaCampeones.getItems().containsAll(listaOriginal)) {
            AlertUtils.mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
            return;
        }

        tablaCampeones.getItems().clear();
        tablaCampeones.setItems(FXCollections.observableArrayList(listaOriginal));
        tablaCampeones.refresh();

        AlertUtils.mostrarAlerta("Filtros eliminados", "Se han eliminado los filtros y restaurado todos los campeones.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public void setListaOriginal(ObservableList<Campeon> listaOriginal) {
        this.listaOriginal = listaOriginal;
    }

    public void setTablaCampeones(TableView<Campeon> tablaCampeones) {
        this.tablaCampeones = tablaCampeones;
    }
}
