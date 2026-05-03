package jugadores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import utils.AlertUtils;
/**
 *
 * @author nestor
 */

public class AddControllerJugador {

    // Referencias a los elementos en el FXML
  
    @FXML private TextField txtNombre;
    @FXML private Spinner<Integer> spinnerEdad;
    @FXML private TextField txtEmail;
    @FXML private TextField txtNacionalidad;
    @FXML private ComboBox<String> comboPosicion;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnAplicarFiltros;
    @FXML private Button btnCancelar;
    @FXML private TableColumn<Jugador, Boolean> colSeleccionar;

    // Datos y referencias a tabla principal
    private ObservableList<Jugador> listaJugadores;
    private ObservableList<Jugador> listaOriginal;
    private TableView<Jugador> tablaJugadores;

    // Bandera para saber si se ha añadido un jugador
    private boolean jugadorAgregado = false;

    // Soporte de validación para cada campo
    private ValidationSupport vNombre, vEdad, vEmail, vNacionalidad, vPosicion, vDescripcion;

    /**
     * Se ejecuta al cargar la ventana.
     */
    @FXML
    public void initialize() {
        // Inicializar ComboBox de posiciones
        comboPosicion.getItems().addAll("Top", "Jungla", "Mid", "ADC", "Soporte");
        comboPosicion.setPromptText("Selecciona una Posición");

        // Inicializar Spinner de edad con rango 18-99
        spinnerEdad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(18, 99, 18));

        // Restringir entrada del Spinner a máximo 2 dígitos numéricos
        spinnerEdad.getEditor().setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d{0,2}") ? change : null;
        }));

        inicializarValidaciones();
    }

    /**
     * Configura validaciones visuales con ControlsFX.
     */
    private void inicializarValidaciones() {
        GraphicValidationDecoration decorador = new GraphicValidationDecoration();

        vNombre = new ValidationSupport();
        vEdad = new ValidationSupport();
        vEmail = new ValidationSupport();
        vNacionalidad = new ValidationSupport();
        vPosicion = new ValidationSupport();
        vDescripcion = new ValidationSupport();

        // Aplicar decorador visual a cada campo
        vNombre.setValidationDecorator(decorador);
        vEdad.setValidationDecorator(decorador);
        vEmail.setValidationDecorator(decorador);
        vNacionalidad.setValidationDecorator(decorador);
        vPosicion.setValidationDecorator(decorador);
        vDescripcion.setValidationDecorator(decorador);

        // Validaciones específicas
        vNombre.registerValidator(txtNombre, true, (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) return ValidationResult.fromError(c, "El nombre no puede estar vacío");
            if (!valor.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+$")) return ValidationResult.fromError(c, "Solo letras permitidas");
            return ValidationResult.fromInfo(c, "Nombre válido");
        });

        vEdad.registerValidator(spinnerEdad.getEditor(), true, (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) return ValidationResult.fromError(c, "La edad no puede estar vacía");
            try {
                int edad = Integer.parseInt(valor.trim());
                if (edad < 18 || edad > 99) return ValidationResult.fromError(c, "Edad debe estar entre 18 y 99");
            } catch (NumberFormatException e) {
                return ValidationResult.fromError(c, "Solo números válidos");
            }
            return ValidationResult.fromInfo(c, "Edad válida");
        });

        vEmail.registerValidator(txtEmail, true,
            Validator.createRegexValidator("Formato de email inválido", "^(.+)@(.+)\\.(.+)$", Severity.ERROR));

        vNacionalidad.registerValidator(txtNacionalidad, true, (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) return ValidationResult.fromError(c, "La nacionalidad es obligatoria");
            if (!valor.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+$")) return ValidationResult.fromError(c, "Solo letras permitidas");
            return ValidationResult.fromInfo(c, "Correcto");
        });

        vPosicion.registerValidator(comboPosicion, true,
            Validator.createEmptyValidator("Selecciona una posición"));

        vDescripcion.registerValidator(txtDescripcion, true,
            Validator.createEmptyValidator("La descripción no puede estar vacía"));
    }

    /**
     * Recibe lista original y tabla principal para actualizar datos.
     */
    public void setListaYTablaJugadores(ObservableList<Jugador> listaJugadores, TableView<Jugador> tablaJugadores) {
        this.listaJugadores = listaJugadores;
        this.tablaJugadores = tablaJugadores;
    }

    /**
     * Acción al pulsar "Añadir Jugador".
     */
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

            // Capturar datos (ya sin defaults por checkbox)
            String nombre = txtNombre.getText().trim();
            int edad = spinnerEdad.getValue();
            String email = txtEmail.getText().trim();
            String nacionalidad = txtNacionalidad.getText().trim();
            String posicion = comboPosicion.getValue();
            String descripcion = txtDescripcion.getText().trim();

        // Insertar en base de datos
        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO jugadores (nombre_jugador, descripcion_jugador, edad, email, nacionalidad, posicion_jugador) VALUES (?, ?, ?, ?, ?, ?)", 
                     Statement.RETURN_GENERATED_KEYS)) { //Pedir que devuelva el ID generado           
            
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, descripcion);
            preparedStatement.setInt(3, edad);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, nacionalidad);
            preparedStatement.setString(6, posicion);
            
            preparedStatement.executeUpdate();
            
            //Cada objeto que creas sabe cuál es su ID único en la base de datos.
            int idNuevo = 0;
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                idNuevo = rs.getInt(1);  
            }
        System.out.println("Jugador añadido correctamente.");

        // Agregar a lista y refrescar tabla
        Jugador nuevoJugador = new Jugador(idNuevo, nombre, descripcion, edad, email, nacionalidad, posicion, false);
        if (listaOriginal != null) {
            listaOriginal.add(nuevoJugador);
        }
        if (tablaJugadores != null) {
            tablaJugadores.setItems(FXCollections.observableArrayList(listaOriginal));
            tablaJugadores.refresh();
        }

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarAlerta("Error", "Error al guardar el jugador: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }
        jugadorAgregado = true;
        cerrarVentana();
    }

    /**
     * Cierra la ventana sin hacer cambios.
     */
    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    /**
     * Cierra la ventana actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // Getter para saber si se agregó el jugador correctamente
    public boolean isJugadorAgregado() {
        return jugadorAgregado;
    }

    // Setters para lista original y tabla
    public void setListaOriginal(ObservableList<Jugador> listaOriginal) {
        this.listaOriginal = listaOriginal;
    }

    public void setTablaJugadores(TableView<Jugador> tablaJugadores) {
        this.tablaJugadores = tablaJugadores;
    }
}
