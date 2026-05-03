/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package campeones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import utils.AlertUtils;


/**
 *
 * @author nestor
 */

public class AddControllerCampeon {

    // Controles FXML
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> comboRol;
    @FXML private ComboBox<String> comboDificultad;
    @FXML private Button btnAnadir;
    @FXML private Button btnCancelar;

    // Datos y tabla principal
    private ObservableList<Campeon> listaCampeones;
    private ObservableList<Campeon> listaOriginalCampeones;
    private TableView<Campeon> tablaCampeones;

    // Estado y validaciones
    private boolean campeonAgregado = false;
    private ValidationSupport vNombre, vDescripcion, vRol, vDificultad;
    private ImageView iconoOk, iconoErr;

    /**
     * Se ejecuta al cargar la ventana.
     */
    
    @FXML
    public void initialize() {
        // ComboBox roles y dificultad
        comboRol.getItems().addAll("Asesino", "Tanque", "Mago", "Tirador", "Luchador", "Soporte");
        comboRol.setPromptText("Selecciona un Rol");

        comboDificultad.getItems().addAll("Baja", "Media", "Alta");
        comboDificultad.setPromptText("Selecciona una Dificultad");

        // Configuraciones
        inicializarValidaciones();
    }

    /**
     * Inicializa validaciones visuales y lógicas.
     */
    
    private void inicializarValidaciones() {
        // Cargar iconos
        iconoOk = new ImageView(new Image(getClass().getResourceAsStream("/icons/ok_icon.png")));
        iconoErr = new ImageView(new Image(getClass().getResourceAsStream("/icons/error_icon.png")));
        iconoOk.setFitWidth(16); iconoOk.setFitHeight(16);
        iconoErr.setFitWidth(16); iconoErr.setFitHeight(16);

        // Crear validadores
        vNombre = new ValidationSupport();
        vDescripcion = new ValidationSupport();
        vRol = new ValidationSupport();
        vDificultad = new ValidationSupport();

        // Decorador de validación personalizado
        GraphicValidationDecoration decorador = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                Control control = message.getTarget();
                control.setStyle(
                    message.getSeverity() == Severity.ERROR ? "-fx-border-color: red;" :
                    message.getSeverity() == Severity.INFO ? "-fx-border-color: green;" : null
                );
            }
        };

        // Aplicar decorador
        vNombre.setValidationDecorator(decorador);
        vDescripcion.setValidationDecorator(decorador);
        vRol.setValidationDecorator(decorador);
        vDificultad.setValidationDecorator(decorador);

        // Validaciones por campo
        vNombre.registerValidator(txtNombre, true, (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) return ValidationResult.fromError(c, "El nombre no puede estar vacío");
            if (!valor.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+$")) return ValidationResult.fromError(c, "Solo letras permitidas");
            return ValidationResult.fromInfo(c, "Nombre válido");
        });

        vDescripcion.registerValidator(txtDescripcion, true,
            Validator.createEmptyValidator("La descripción no puede estar vacía"));

        vRol.registerValidator(comboRol, true,
            Validator.createEmptyValidator("Selecciona un rol"));

        vDificultad.registerValidator(comboDificultad, true,
            Validator.createEmptyValidator("Selecciona una dificultad"));
    }

    

    /**
     * Acción del botón "Añadir".
     */
    @FXML
    private void anadirCampeon() {
        
        if (!validarFormulario()) return;
        
        // Captura de datos
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();
        String rol = comboRol.getValue();
        String dificultad = comboDificultad.getValue();
    

        // Inserción en la base de datos
        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO campeones (nombre_campeon, descripcion_campeon, rol_mapa, dificultad) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, descripcion);
            preparedStatement.setString(3, rol);
            preparedStatement.setString(4, dificultad);
            

            if (preparedStatement.executeUpdate() > 0) {
                AlertUtils.mostrarAlerta("Éxito", "Campeón añadido correctamente.", Alert.AlertType.INFORMATION);
            }
            //Cada objeto que creas sabe cuál es su ID único en la base de datos.
            int idNuevo = 0;                             
            ResultSet rs = preparedStatement.getGeneratedKeys();       
            if (rs.next()) {                               
                idNuevo = rs.getInt(1);                    
            } 

        

        // Añadir a lista y refrescar tabla
        Campeon nuevoCampeon = new Campeon(idNuevo, nombre, descripcion, rol, dificultad, false);

        if (listaOriginalCampeones != null) {
            listaOriginalCampeones.add(nuevoCampeon);
        }

        if (tablaCampeones != null) {
            tablaCampeones.setItems(FXCollections.observableArrayList(listaOriginalCampeones));
            tablaCampeones.refresh();
        }
        
        } catch (Exception e) {
            e.printStackTrace();
           AlertUtils.mostrarAlerta("Error", "No se pudo añadir el campeón: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        // Estado y cierre
        campeonAgregado = true;
        cerrarVentana();
    }

    /**
     * Validaciones manuales del formulario.
     */
    private boolean validarFormulario() {
            boolean valido = true;

            // Fuerza revalidación (por si no ha cambiado el valor)
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

    // Setters y getters
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
}