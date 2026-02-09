/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package partidas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import org.controlsfx.control.textfield.TextFields;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
/**
 *
 * @author nestor
 */

public class AddControllerPartida {

    // Referencias a los elementos en el FXML
    @FXML private CheckBox checkJugador;
    @FXML private TextField txtJugador;
    @FXML private CheckBox checkCampeon;
    @FXML private TextField txtCampeon;
    @FXML private CheckBox checkFecha;
    @FXML private DatePicker dateFecha;
    @FXML private CheckBox checkKDA;
    @FXML private TextField txtKDA;
    @FXML private CheckBox checkResultado;
    @FXML private ComboBox<String> comboResultado;
    @FXML private Button btnAnadir;
    @FXML private Button btnCancelar;

    // Listas y tabla para la gestión de partidas
    private ObservableList<Partida> listaPartidas;
    private ObservableList<Partida> listaOriginalPartidas;
    private TableView<Partida> tablaPartidas;
    private boolean partidaAgregada = false;

    // Validaciones visuales
    private ValidationSupport vJugador, vCampeon, vFecha, vKDA, vResultado;

    /**
     * Método que se ejecuta al cargar la ventana.
     */
    @FXML
    public void initialize() {
        comboResultado.setItems(FXCollections.observableArrayList("Victoria", "Derrota"));
        dateFecha.getEditor().setDisable(true); // Evitar que el usuario escriba directamente

        configurarCheckBoxes();
        inicializarValidaciones();
        configurarAutocompletado();
    }

    /**
     * Inicializa las validaciones visuales con colores e iconos.
     */
    private void inicializarValidaciones() {
        GraphicValidationDecoration decorador = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                message.getTarget().setStyle(
                    message.getSeverity() == Severity.ERROR
                        ? "-fx-border-color: red; -fx-border-width: 2px;"
                        : "-fx-border-color: green; -fx-border-width: 2px;"
                );
            }
        };

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

        vJugador.registerValidator(txtJugador, true, Validator.createEmptyValidator("Jugador requerido"));
        vCampeon.registerValidator(txtCampeon, true, Validator.createEmptyValidator("Campeón requerido"));
        vFecha.registerValidator(dateFecha, true, Validator.createEmptyValidator("Selecciona una fecha"));

        vKDA.registerValidator(txtKDA, true, (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty())
                return ValidationResult.fromError(c, "KDA requerido");
            if (!valor.matches("\\d+/\\d+/\\d+"))
                return ValidationResult.fromError(c, "Formato inválido (ej. 10/2/5)");
            return ValidationResult.fromInfo(c, "Correcto");
        });

        vResultado.registerValidator(comboResultado, true, Validator.createEmptyValidator("Selecciona un resultado"));
    }

    /**
     * Deshabilita campos si no están seleccionados sus respectivos checkboxes.
     */
    private void configurarCheckBoxes() {
        txtJugador.disableProperty().bind(checkJugador.selectedProperty().not());
        txtCampeon.disableProperty().bind(checkCampeon.selectedProperty().not());
        dateFecha.disableProperty().bind(checkFecha.selectedProperty().not());
        txtKDA.disableProperty().bind(checkKDA.selectedProperty().not());
        comboResultado.disableProperty().bind(checkResultado.selectedProperty().not());
    }

    /**
     * Establece la lista original para restaurar los datos si se cancela.
     */
    public void setListaOriginalPartidas(ObservableList<Partida> listaOriginalPartidas) {
        this.listaOriginalPartidas = listaOriginalPartidas;
    }

    /**
     * Establece la lista y la tabla principal para añadir partidas.
     */
    public void setListaYTablaPartidas(ObservableList<Partida> listaPartidas, TableView<Partida> tablaPartidas) {
        this.listaPartidas = listaPartidas;
        this.tablaPartidas = tablaPartidas;
    }

    /**
     * Realiza consulta a la base de datos para obtener nombres (jugadores o campeones).
     */
    private List<String> obtenerNombres(String tabla, String columna) {
        List<String> nombres = new ArrayList<>();
        String query = "SELECT " + columna + " FROM " + tabla;

        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                nombres.add(rs.getString(columna));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nombres;
    }

    /**
     * Activa el autocompletado en los campos de texto.
     */
    private void configurarAutocompletado() {
        List<String> nombresJugadores = obtenerNombres("jugadores", "nombre_jugador");
        List<String> nombresCampeones = obtenerNombres("campeones", "nombre_campeon");

        TextFields.bindAutoCompletion(txtJugador, nombresJugadores);
        TextFields.bindAutoCompletion(txtCampeon, nombresCampeones);
    }

    /**
     * Busca el ID de un jugador por nombre.
     */
    private int obtenerIDJugador(String nombreJugador) throws SQLException {
        String query = "SELECT idJugadores FROM jugadores WHERE nombre_jugador = ?";

        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nombreJugador);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("idJugadores");
            } else {
                System.out.println("Jugador no encontrado: " + nombreJugador);
                throw new SQLException("Jugador no encontrado en la base de datos: " + nombreJugador);
            }
        }
    }

    /**
     * Busca el ID de un campeón por nombre.
     */
    private int obtenerIDCampeon(String nombreCampeon) throws SQLException {
        if (nombreCampeon == null || nombreCampeon.trim().isEmpty()) {
            return -1;
        }

        String query = "SELECT idCampeones FROM campeones WHERE nombre_campeon = ?";
        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, nombreCampeon);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("idCampeones");
            } else {
                throw new SQLException("Campeón no existente: " + nombreCampeon);
            }
        }
    }

    /**
     * Acción del botón "Añadir Partida".
     */
    @FXML
    private void anadirPartida() {
        StringBuilder mensajeError = new StringBuilder();

        // Validación de campos seleccionados
        if (checkJugador.isSelected() && (txtJugador.getText() == null || txtJugador.getText().trim().isEmpty())) {
            mensajeError.append("- Debe ingresar un nombre de jugador.\n");
        }
        if (checkCampeon.isSelected() && (txtCampeon.getText() == null || txtCampeon.getText().trim().isEmpty())) {
            mensajeError.append("- Debe ingresar un campeón.\n");
        }
        if (checkFecha.isSelected() && (dateFecha.getValue() == null)) {
            mensajeError.append("- Debe seleccionar una fecha.\n");
        }
        if (checkKDA.isSelected() && (txtKDA.getText() == null || txtKDA.getText().trim().isEmpty())) {
            mensajeError.append("- Debe ingresar un KDA válido.\n");
        }
        if (checkResultado.isSelected() && (comboResultado.getValue() == null || comboResultado.getValue().trim().isEmpty())) {
            mensajeError.append("- Debe seleccionar un resultado.\n");
        }

        // Validaciones visuales
        boolean valido = true;
        if (checkJugador.isSelected()) valido &= vJugador.getValidationResult().getErrors().isEmpty();
        if (checkCampeon.isSelected()) valido &= vCampeon.getValidationResult().getErrors().isEmpty();
        if (checkFecha.isSelected()) valido &= vFecha.getValidationResult().getErrors().isEmpty();
        if (checkKDA.isSelected()) valido &= vKDA.getValidationResult().getErrors().isEmpty();
        if (checkResultado.isSelected()) valido &= vResultado.getValidationResult().getErrors().isEmpty();

        if (!valido) {
            mostrarAlerta("Error de validación", "Revisa los campos marcados con errores.", Alert.AlertType.WARNING);
            return;
        }

        if (!checkJugador.isSelected() && !checkCampeon.isSelected() && !checkFecha.isSelected() &&
            !checkKDA.isSelected() && !checkResultado.isSelected()) {
            mostrarAlerta("Sin selección", "No hay ningún campo seleccionado. Por favor, marque al menos uno.", Alert.AlertType.WARNING);
            return;
        }

        // Captura de datos
        String jugador = checkJugador.isSelected() ? txtJugador.getText().trim() : "Sin Jugador";
        String campeon = checkCampeon.isSelected() ? txtCampeon.getText().trim() : null;
        LocalDate fecha = checkFecha.isSelected() ? dateFecha.getValue() : null;
        String kda = checkKDA.isSelected() ? txtKDA.getText().trim() : "0/0/0";
        String resultado = checkResultado.isSelected() ? comboResultado.getValue() : "Desconocido";

        try {
            int idJugador = -1;
            int idCampeon = -1;
            StringBuilder errores = new StringBuilder();

            // Buscar IDs
            try {
                if (checkJugador.isSelected()) {
                    idJugador = obtenerIDJugador(jugador);
                }
            } catch (SQLException e) {
                errores.append("❌ Jugador no encontrado: ").append(jugador).append("\n");
            }

            try {
                if (checkCampeon.isSelected()) {
                    idCampeon = obtenerIDCampeon(campeon);
                }
            } catch (SQLException e) {
                errores.append("❌ Campeón no encontrado: ").append(campeon).append("\n");
            }

            if (errores.length() > 0) {
                mostrarAlerta("Error de búsqueda", errores.toString() + "Por favor, escoge otro jugador o campeón.", Alert.AlertType.WARNING);
                return;
            }

            // Inserción en BD
            try (Connection connection = baseDatos.DataBaseMain.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO juegan (ID_Jugador, ID_Campeon, Fecha_jugada, KDA, Resultado) VALUES (?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

                stmt.setInt(1, idJugador);
                if (idCampeon == -1) {
                    stmt.setNull(2, java.sql.Types.INTEGER);
                } else {
                    stmt.setInt(2, idCampeon);
                }
                stmt.setDate(3, fecha != null ? java.sql.Date.valueOf(fecha) : null);
                stmt.setString(4, kda);
                stmt.setString(5, resultado);

                int filasInsertadas = stmt.executeUpdate();
                if (filasInsertadas > 0) {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    int idJuegan = generatedKeys.next() ? generatedKeys.getInt(1) : -1;

                    Partida nueva = new Partida(idJuegan, jugador, campeon != null ? campeon : "Sin Campeón", fecha, kda, resultado);
                    listaPartidas.add(nueva);
                    tablaPartidas.getItems().add(nueva);
                    tablaPartidas.refresh();
                }
            }

            cerrarVentana();

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en base de datos: " + e.getMessage(), Alert.AlertType.ERROR);
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

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.setOnShown(event -> Platform.runLater(() -> {
            Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
        }));

        alerta.showAndWait();
    }

    public boolean isPartidaAgregada() {
        return partidaAgregada;
    }
}
