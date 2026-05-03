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
import utils.AlertUtils;
/**
 *
 * @author nestor
 */

public class AddControllerPartida {

    // Referencias a los elementos en el FXML
    @FXML private TextField txtJugador;
    @FXML private TextField txtCampeon;
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtKDA;
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

        // Revalidar todo
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

        // Leer datos
        String jugador = txtJugador.getText().trim();
        String campeon = txtCampeon.getText().trim();
        LocalDate fecha = dateFecha.getValue();
        String kda = txtKDA.getText().trim();
        String resultado = comboResultado.getValue();

        // Buscar IDs
        final int idJugador;
        final int idCampeon;
        try {
            idJugador = obtenerIDJugador(jugador);
            idCampeon = obtenerIDCampeon(campeon);
        } catch (SQLException e) {
            AlertUtils.mostrarAlerta("Error", e.getMessage(), Alert.AlertType.WARNING);
            return;
        }

        // Insertar en BD y recuperar ID autogenerado
        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO juegan (ID_Jugador, ID_Campeon, Fecha_jugada, KDA, Resultado) VALUES (?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, idJugador);
            stmt.setInt(2, idCampeon);
            stmt.setDate(3, java.sql.Date.valueOf(fecha));
            stmt.setString(4, kda);
            stmt.setString(5, resultado);

            int filasInsertadas = stmt.executeUpdate();
            if (filasInsertadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    int idJuegan = generatedKeys.next() ? generatedKeys.getInt(1) : -1;

                    Partida nueva = new Partida(idJuegan, jugador, campeon, fecha, kda, resultado);

                    if (listaPartidas != null) listaPartidas.add(nueva);
                    if (tablaPartidas != null) {
                        tablaPartidas.getItems().add(nueva);
                        tablaPartidas.refresh();
                    }
                }
            }

            partidaAgregada = true;
            cerrarVentana();

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
