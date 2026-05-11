/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package partidas;

import modelos.Partida;
import java.time.LocalDate;
import java.util.stream.Collectors;
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

public class ListaControllerPartida {

    // Referencias a los elementos en el FXML
    @FXML
    private TextField txtJugador;
    @FXML
    private TextField txtCampeon;
    @FXML
    private DatePicker dateFecha;
    @FXML
    private TextField txtKDA;
    @FXML
    private ComboBox<String> cmbResultado;
    @FXML
    private Button btnAplicarFiltros;
    @FXML
    private Button btnCancelar;

    private ObservableList<Partida> listaOriginal = FXCollections.observableArrayList();
    private TableView<Partida> tablaPartidas;
    
    private ValidationSupport vJugador, vCampeon, vFecha, vKDA, vResultado;


    @FXML
    public void initialize() {
        cmbResultado.getItems().addAll("Victoria", "Derrota", "Empate");
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

    vJugador.registerValidator(txtJugador, true, ValidationUtils.soloLetrasFiltro("Solo letras permitidas"));
    vCampeon.registerValidator(txtCampeon, true, ValidationUtils.soloLetrasFiltro("Solo letras permitidas"));
    vFecha.registerValidator(dateFecha, true, ValidationUtils.obligatorio("Debes seleccionar una fecha"));
    vKDA.registerValidator(txtKDA, true, ValidationUtils.kdaFiltro("Formato inválido (Ej: 3/1/2)"));
    vResultado.registerValidator(cmbResultado, true, ValidationUtils.obligatorio("Selecciona un resultado"));
}
    
@FXML
    private void aplicarFiltros() {
        if (listaOriginal == null || listaOriginal.isEmpty()) {
            AlertUtils.mostrarAlerta("Sin datos", "No hay partidas disponibles para filtrar.", Alert.AlertType.WARNING);
            return;
        }

        String jugador = txtJugador.getText() == null ? "" : txtJugador.getText().trim();
        String campeon = txtCampeon.getText() == null ? "" : txtCampeon.getText().trim();
        LocalDate fecha = dateFecha.getValue();
        String kda = txtKDA.getText() == null ? "" : txtKDA.getText().trim();
        String resultado = cmbResultado.getValue() == null ? "" : cmbResultado.getValue().trim();

        boolean algunFiltro = !jugador.isEmpty() || !campeon.isEmpty() || fecha != null || !kda.isEmpty() || !resultado.isEmpty();
        if (!algunFiltro) {
            AlertUtils.mostrarAlerta("Sin filtros", "Rellena al menos un campo para filtrar.", Alert.AlertType.WARNING);
            return;
        }

        if (!kda.isEmpty() && !kda.matches("\\d+/\\d+/\\d+")) {
            AlertUtils.mostrarAlerta("KDA inválido", "Formato debe ser n/n/n (Ej: 10/3/5).", Alert.AlertType.WARNING);
            return;
        }

        ObservableList<Partida> filtradas = listaOriginal.stream()
            .filter(p -> {
                boolean ok = true;
                if (!jugador.isEmpty())   ok &= p.getJugador() != null && p.getJugador().toLowerCase().contains(jugador.toLowerCase());
                if (!campeon.isEmpty())   ok &= p.getCampeon() != null && p.getCampeon().toLowerCase().contains(campeon.toLowerCase());
                if (fecha != null)        ok &= p.getFecha() != null && p.getFecha().equals(fecha);
                if (!kda.isEmpty())       ok &= p.getKda() != null && p.getKda().equals(kda);
                if (!resultado.isEmpty()) ok &= p.getResultado() != null && p.getResultado().equals(resultado);
                return ok;
            })
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        if (filtradas.isEmpty()) {
            AlertUtils.mostrarAlerta("Sin resultados", "No se encontraron partidas con esos filtros.", Alert.AlertType.INFORMATION);
            return;
        }

        tablaPartidas.setItems(filtradas);
        tablaPartidas.refresh();
        cerrarVentana();
    }
 
    @FXML
public void borrarFiltrosPartida() {
    if (tablaPartidas.getItems().size() == listaOriginal.size()) {
        AlertUtils.mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
        return;
    }

    tablaPartidas.setItems(FXCollections.observableArrayList(listaOriginal));
    tablaPartidas.refresh();

    AlertUtils.mostrarAlerta("Filtros eliminados", "Se han eliminado los filtros y restaurado todas las partidas.", Alert.AlertType.INFORMATION);
}

@FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
public void setListaOriginal(ObservableList<Partida> listaOriginal) {
    this.listaOriginal = listaOriginal;
}


public void setTablaPartidas(TableView<Partida> tablaPartidas) {
        this.tablaPartidas = tablaPartidas;
    }
}
