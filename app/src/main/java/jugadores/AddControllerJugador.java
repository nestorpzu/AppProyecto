package jugadores;

import dao.JugadorDAO;
import modelos.Jugador;
import java.sql.Connection;
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

public class AddControllerJugador {

@FXML private TextField txtNombre;
    @FXML private Spinner<Integer> spinnerEdad;
    @FXML private TextField txtEmail;
    @FXML private TextField txtNacionalidad;
    @FXML private ComboBox<String> comboPosicion;
    @FXML private TextField  txtDescripcion;
    @FXML private Button btnAnadirJugador;
    @FXML private Button btnCancelar;


    private ObservableList<Jugador> listaJugadores;
    private ObservableList<Jugador> listaOriginal;
    private final JugadorDAO jugadorDAO = new JugadorDAO();
    private Connection connection;
    private TableView<Jugador> tablaJugadores;

    private boolean jugadorAgregado = false;

    private ValidationSupport vNombre, vEdad, vEmail, vNacionalidad, vPosicion, vDescripcion;

@FXML
    public void initialize() {
        comboPosicion.getItems().addAll("Top", "Jungla", "Mid", "ADC", "Soporte");
        comboPosicion.setPromptText("Selecciona una Posición");

        spinnerEdad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(18, 99, 18));

        spinnerEdad.getEditor().setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d{0,2}") ? change : null;
        }));
        

        inicializarValidaciones();
    }

private void inicializarValidaciones() {
    GraphicValidationDecoration decorador = ValidationUtils.crearDecorador();

    vNombre = new ValidationSupport();
    vEdad = new ValidationSupport();
    vEmail = new ValidationSupport();
    vNacionalidad = new ValidationSupport();
    vPosicion = new ValidationSupport();
    vDescripcion = new ValidationSupport();

    vNombre.setValidationDecorator(decorador);
    vEdad.setValidationDecorator(decorador);
    vEmail.setValidationDecorator(decorador);
    vNacionalidad.setValidationDecorator(decorador);
    vPosicion.setValidationDecorator(decorador);
    vDescripcion.setValidationDecorator(decorador);

    vNombre.registerValidator(txtNombre, true, ValidationUtils.soloLetras(
            "El nombre no puede estar vacío", "Solo letras permitidas"));
    vEdad.registerValidator(spinnerEdad.getEditor(), true, ValidationUtils.edad(18, 99, "Edad fuera de rango (18-99)", "Introduce un número válido"));
    vEmail.registerValidator(txtEmail, true, ValidationUtils.email("Formato de email inválido"));
    vNacionalidad.registerValidator(txtNacionalidad, true, ValidationUtils.soloLetras(
            "La nacionalidad es obligatoria", "Solo letras permitidas"));
    vPosicion.registerValidator(comboPosicion, true, ValidationUtils.obligatorio("Selecciona una posición"));
    vDescripcion.registerValidator(txtDescripcion, true, ValidationUtils.obligatorio("La descripción no puede estar vacía"));
}

   

public void setListaYTablaJugadores(ObservableList<Jugador> listaJugadores, TableView<Jugador> tablaJugadores) {
        this.listaJugadores = listaJugadores;
        this.tablaJugadores = tablaJugadores;
    }

@FXML
    private void anadirJugador() {
            vNombre.revalidate();
            vEdad.revalidate();
            vEmail.revalidate();
            vNacionalidad.revalidate();
            vPosicion.revalidate();
            vDescripcion.revalidate();

            boolean valido = vNombre.getValidationResult().getErrors().isEmpty()
                    && vEdad.getValidationResult().getErrors().isEmpty()
                    && vEmail.getValidationResult().getErrors().isEmpty()
                    && vNacionalidad.getValidationResult().getErrors().isEmpty()
                    && vPosicion.getValidationResult().getErrors().isEmpty()
                    && vDescripcion.getValidationResult().getErrors().isEmpty();

            if (!valido) {
                AlertUtils.mostrarAlerta("Error de validación", "Revisa los campos marcados con error.", Alert.AlertType.WARNING);
                return;
            }

            String nombre = txtNombre.getText().trim();
            int edad = spinnerEdad.getValue();
            String email = txtEmail.getText().trim();
            String nacionalidad = txtNacionalidad.getText().trim();
            String posicion = comboPosicion.getValue();
            String descripcion = txtDescripcion.getText().trim();

        try {
            Jugador nuevoJugador = new Jugador(0, nombre, descripcion, edad, email, nacionalidad, posicion);
            int idNuevo = jugadorDAO.insertar(nuevoJugador, connection);

            if (idNuevo > 0) {
                nuevoJugador.setId(idNuevo);
                System.out.println("Jugador añadido correctamente.");

                if (listaOriginal != null) {
                    listaOriginal.add(nuevoJugador);
                }
                if (tablaJugadores != null) {
                    tablaJugadores.setItems(FXCollections.observableArrayList(listaOriginal));
                    tablaJugadores.refresh();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarAlerta("Error", "Error al guardar el jugador: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        jugadorAgregado = true;
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

    // Getter para saber si se agregó el jugador correctamente
    public boolean isJugadorAgregado() {
        return jugadorAgregado;
    }

    public void setListaOriginal(ObservableList<Jugador> listaOriginal) {
        this.listaOriginal = listaOriginal;
    }

    public void setTablaJugadores(TableView<Jugador> tablaJugadores) {
        this.tablaJugadores = tablaJugadores;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
