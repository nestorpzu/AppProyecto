/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package partidas;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
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
    private TextField txtKDA; // Ahora es un campo de texto en lugar de un ComboBox
    @FXML
    private ComboBox<String> cmbResultado;
    @FXML
    private Button btnAplicarFiltros;
    @FXML
    private Button btnCancelar;

    private ObservableList<Partida> listaOriginal = FXCollections.observableArrayList();
    private TableView<Partida> tablaPartidas; // Referencia a la tabla principal
    
    private ValidationSupport vJugador, vCampeon, vFecha, vKDA, vResultado;
    private ImageView iconoOk, iconoErr;


    /**
     * Inicialización de la ventana
     */
    @FXML
    public void initialize() {
        // Llenar el ComboBox de Resultado
        cmbResultado.getItems().addAll("Victoria", "Derrota", "Empate");
        dateFecha.getEditor().setDisable(true);

        // Configurar la lógica de los CheckBox
        inicializarValidaciones();

    }

    private void inicializarValidaciones() {
    iconoOk = new ImageView(new Image(getClass().getResourceAsStream("/icons/ok_icon.png")));
    iconoErr = new ImageView(new Image(getClass().getResourceAsStream("/icons/error_icon.png")));
    iconoOk.setFitHeight(16); iconoOk.setFitWidth(16);
    iconoErr.setFitHeight(16); iconoErr.setFitWidth(16);

    vJugador = new ValidationSupport();
    vCampeon = new ValidationSupport();
    vFecha = new ValidationSupport();
    vKDA = new ValidationSupport();
    vResultado = new ValidationSupport();

    GraphicValidationDecoration decorador = new GraphicValidationDecoration() {
        @Override
        public void applyValidationDecoration(ValidationMessage message) {
            super.applyValidationDecoration(message);
            message.getTarget().setStyle(
                message.getSeverity() == Severity.ERROR ?
                "-fx-border-color: red;" :
                "-fx-border-color: green;"
            );
        }
    };

    vJugador.setValidationDecorator(decorador);
    vCampeon.setValidationDecorator(decorador);
    vFecha.setValidationDecorator(decorador);
    vKDA.setValidationDecorator(decorador);
    vResultado.setValidationDecorator(decorador);

    vJugador.registerValidator(txtJugador, true, (Control c, String valor) -> {
        if (valor == null || valor.trim().isEmpty()) return ValidationResult.fromError(c, "Jugador vacío");
        if (!valor.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+$")) return ValidationResult.fromError(c, "Solo letras");
        return ValidationResult.fromInfo(c, "Correcto");
    });

    vCampeon.registerValidator(txtCampeon, true, (Control c, String valor) -> {
        if (valor == null || valor.trim().isEmpty()) return ValidationResult.fromError(c, "Campeón vacío");
        if (!valor.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+$")) return ValidationResult.fromError(c, "Solo letras");
        return ValidationResult.fromInfo(c, "Correcto");
    });

    vFecha.registerValidator(dateFecha, true,
        Validator.createEmptyValidator("Debes seleccionar una fecha"));

    vKDA.registerValidator(txtKDA, true, (Control c, String valor) -> {
        if (valor == null || valor.trim().isEmpty()) return ValidationResult.fromError(c, "KDA vacío");
        if (!valor.matches("\\d+/\\d+/\\d+")) return ValidationResult.fromError(c, "Formato inválido (Ej: 3/1/2)");
        return ValidationResult.fromInfo(c, "Formato correcto");
    });

    vResultado.registerValidator(cmbResultado, true,
        Validator.createEmptyValidator("Selecciona un resultado"));
}

    
    /**
     * Método para aplicar filtros
     */
    @FXML
    private void aplicarFiltros() {
        if (listaOriginal == null || listaOriginal.isEmpty()) {
            mostrarAlerta("Sin datos", "No hay partidas disponibles para filtrar.", Alert.AlertType.WARNING);
            return;
        }

        String jugador = txtJugador.getText() == null ? "" : txtJugador.getText().trim();
        String campeon = txtCampeon.getText() == null ? "" : txtCampeon.getText().trim();
        LocalDate fecha = dateFecha.getValue();              // null = no filtra por fecha
        String kda = txtKDA.getText() == null ? "" : txtKDA.getText().trim();
        String resultado = cmbResultado.getValue() == null ? "" : cmbResultado.getValue().trim();

        boolean algunFiltro = !jugador.isEmpty() || !campeon.isEmpty() || fecha != null || !kda.isEmpty() || !resultado.isEmpty();
        if (!algunFiltro) {
            mostrarAlerta("Sin filtros", "Rellena al menos un campo para filtrar.", Alert.AlertType.WARNING);
            return;
        }

        // (Opcional) validar KDA solo si se ha escrito algo:
        if (!kda.isEmpty() && !kda.matches("\\d+/\\d+/\\d+")) {
            mostrarAlerta("KDA inválido", "Formato debe ser n/n/n (Ej: 10/3/5).", Alert.AlertType.WARNING);
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
            mostrarAlerta("Sin resultados", "No se encontraron partidas con esos filtros.", Alert.AlertType.INFORMATION);
            return;
        }

        tablaPartidas.setItems(filtradas);
        tablaPartidas.refresh();
        cerrarVentana();
    }



    /**
     * Método para borrar los filtros y restaurar la tabla con los datos originales.
     */
    
    @FXML
public void borrarFiltrosPartida() {
    if (listaOriginal == null || listaOriginal.isEmpty()) {
        mostrarAlerta("Error", "No hay datos originales disponibles para restaurar.", Alert.AlertType.WARNING);
        return;
    }

    System.out.println("🔄 Restaurando la tabla con todas las partidas...");
    
    // Restaurar la tabla con la lista original
    tablaPartidas.setItems(FXCollections.observableArrayList(listaOriginal));
    tablaPartidas.refresh();

    mostrarAlerta("Filtros eliminados", "Se han eliminado los filtros y restaurado todas las partidas.", Alert.AlertType.INFORMATION);
}

    /**
     * Método para cerrar la ventana sin aplicar filtros
     */
    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Mostrar una alerta
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Platform.runLater(() -> {
            Alert alerta = new Alert(tipo);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(mensaje);

            alerta.setOnShown(event -> {
                Platform.runLater(() -> {
                    Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
                    if (stage != null) {
                        Screen screen = Screen.getPrimary();
                        Rectangle2D bounds = screen.getVisualBounds();
                        stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
                        stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
                    }
                });
            });

            alerta.showAndWait();
        });
    }

    /**
     * Setter para listaOriginal
     */
    public void setListaOriginal(ObservableList<Partida> listaOriginal) {
    if (listaOriginal == null || listaOriginal.isEmpty()) {
        System.out.println("⚠️ Error: Intentando asignar una lista vacía o nula a listaOriginal.");
    } else {
        System.out.println("✅ listaOriginal asignada con " + listaOriginal.size() + " partidas.");
    }
    this.listaOriginal = listaOriginal;
}


    /**
     * Setter para la tabla principal
     */
    public void setTablaPartidas(TableView<Partida> tablaPartidas) {
        this.tablaPartidas = tablaPartidas;
        System.out.println("tablaPartidas configurada correctamente.");
    }
}
