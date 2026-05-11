package jugadores;

import modelos.Jugador;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.stream.Collectors;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import utils.AlertUtils;
import utils.ValidationUtils;

public class ListaControllerJugadores {

    @FXML
    private TextField txtNombreJugador;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private Spinner<Integer> spinnerEdad;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtNacionalidad;
    @FXML
    private ComboBox<String> cmbPosicion;
    @FXML
    private Button btnAplicarFiltros;
    @FXML
    private Button btnCancelar;
    private ObservableList<Jugador> listaOriginal = FXCollections.observableArrayList();    
    private TableView<Jugador> tablaJugadores;
    private ValidationSupport vNombre, vEdad, vEmail, vNacionalidad, vPosicion;
@FXML
    public void initialize() {
        cmbPosicion.getItems().addAll("Top", "Jungla", "Mid", "ADC", "Soporte");
        cmbPosicion.setPromptText("Selecciona una Posición");
    SpinnerValueFactory.IntegerSpinnerValueFactory vf =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(18, 99, 18){
             @Override
    public void increment(int steps) {
        if (getValue() == null) {
            setValue(18);
            return;
        }
        super.increment(steps);
    }

    @Override
    public void decrement(int steps) {
        if (getValue() == null) {
            setValue(18);
            return;
        }
        super.decrement(steps);
    }
            };

    spinnerEdad.setValueFactory(vf);

    vf.setValue(null);
    spinnerEdad.getEditor().setText("");
    spinnerEdad.setEditable(true);
    spinnerEdad.getEditor().setTextFormatter(new TextFormatter<>(c ->
        c.getControlNewText().matches("\\d*") ? c : null
    ));
         inicializarValidaciones();
    }
    
    
    private void inicializarValidaciones() {
    GraphicValidationDecoration decorador = ValidationUtils.crearDecorador();

    vNombre = new ValidationSupport();
    vEdad = new ValidationSupport();
    vEmail = new ValidationSupport();
    vNacionalidad = new ValidationSupport();
    vPosicion = new ValidationSupport();

    vNombre.setValidationDecorator(decorador);
    vEdad.setValidationDecorator(decorador);
    vEmail.setValidationDecorator(decorador);
    vNacionalidad.setValidationDecorator(decorador);
    vPosicion.setValidationDecorator(decorador);

    vNombre.registerValidator(txtNombreJugador, true, ValidationUtils.soloLetrasFiltro("Solo letras permitidas"));
    
    vEmail.registerValidator(txtEmail, true, ValidationUtils.emailFiltro("Formato de email inválido"));
    vNacionalidad.registerValidator(txtNacionalidad, true, ValidationUtils.soloLetrasFiltro("Solo letras permitidas"));
    vPosicion.registerValidator(cmbPosicion, true, ValidationUtils.obligatorio("Selecciona una posición"));
}
@FXML
    private void aplicarFiltros() {
        spinnerEdad.commitValue();       
        String edadTxt = spinnerEdad.getEditor().getText().trim();
        
        boolean usarEdad = !edadTxt.isEmpty();
        Integer edadCalculada = null;
        try {
            edadCalculada = usarEdad ? Integer.parseInt(edadTxt) : null;
        } catch (NumberFormatException e) {
            edadCalculada = null;
        }
        final Integer edad = edadCalculada; 
        
                                       
        String nombre = txtNombreJugador.getText() == null ? "" : txtNombreJugador.getText().trim();
        String desc   = txtDescripcion.getText() == null ? "" : txtDescripcion.getText().trim();
        String email  = txtEmail.getText() == null ? "" : txtEmail.getText().trim();
        String nac    = txtNacionalidad.getText() == null ? "" : txtNacionalidad.getText().trim();      
        String pos    = cmbPosicion.getValue() == null ? "" : cmbPosicion.getValue().trim();
        
        
        boolean algunFiltro =
            !nombre.isEmpty() || !desc.isEmpty() || !email.isEmpty() || !nac.isEmpty() || !pos.isEmpty() || edad != null;

        if (!algunFiltro) {
            AlertUtils.mostrarAlerta("Sin filtros", "Rellena al menos un campo para filtrar.", Alert.AlertType.WARNING);
            return;
        }

        ObservableList<Jugador> filtrados = FXCollections.observableArrayList(
            listaOriginal.stream()
                .filter(j -> {
                    boolean ok = true;
                    if (!nombre.isEmpty()) ok &= j.getNombre() != null && j.getNombre().toLowerCase().contains(nombre.toLowerCase());
                    if (!desc.isEmpty())   ok &= j.getDescripcion() != null && j.getDescripcion().toLowerCase().contains(desc.toLowerCase());
                    if (!email.isEmpty())  ok &= j.getEmail() != null && j.getEmail().toLowerCase().contains(email.toLowerCase());
                    if (!nac.isEmpty())    ok &= j.getNacionalidad() != null && j.getNacionalidad().toLowerCase().contains(nac.toLowerCase());
                    if (!pos.isEmpty())    ok &= j.getPosicion() != null && j.getPosicion().equals(pos);
                    if (edad != null) ok &= j.getEdad() == edad;
                    return ok;
                })
                .collect(Collectors.toList())
        );

        if (filtrados.isEmpty()) {
            AlertUtils.mostrarAlerta("Sin resultados", "No se encontraron jugadores con esos filtros.", Alert.AlertType.INFORMATION);
            return;
        }

        tablaJugadores.setItems(filtrados);
        tablaJugadores.refresh();
        cerrarVentana();
    }
  
@FXML
    public void borrarFiltros() {
        if (listaOriginal == null || listaOriginal.isEmpty()) {
            AlertUtils.mostrarAlerta("Error", "No hay datos originales disponibles para restaurar.", Alert.AlertType.WARNING);
            return;
        }

        if (tablaJugadores.getItems().size() == listaOriginal.size()) {
            AlertUtils.mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
            return;
        }

        tablaJugadores.setItems(FXCollections.observableArrayList(listaOriginal));
        spinnerEdad.getEditor().setText("");
        tablaJugadores.refresh();

        AlertUtils.mostrarAlerta("Filtros eliminados", "Se han eliminado los filtros y restaurado todos los jugadores.", Alert.AlertType.INFORMATION);
    }

@FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

public void setListaOriginal(ObservableList<Jugador> listaOriginal) {
        this.listaOriginal = listaOriginal;
    }

public void setTablaJugadores(TableView<Jugador> tablaJugadores) {
        this.tablaJugadores = tablaJugadores;
    }
}
