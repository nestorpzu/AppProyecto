/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package partidas;

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
    private CheckBox chkJugador;
    @FXML
    private TextField txtJugador;
    @FXML
    private CheckBox chkCampeon;
    @FXML
    private TextField txtCampeon;
    @FXML
    private CheckBox chkFecha;
    @FXML
    private DatePicker dateFecha;
    @FXML
    private CheckBox chkKDA;
    @FXML
    private TextField txtKDA; // Ahora es un campo de texto en lugar de un ComboBox
    @FXML   
    private CheckBox chkResultado;
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
        configurarCheckBoxes();
        inicializarValidaciones();

    }

    private void configurarCheckBoxes() {
        txtJugador.disableProperty().bind(chkJugador.selectedProperty().not());
        txtCampeon.disableProperty().bind(chkCampeon.selectedProperty().not());
        dateFecha.disableProperty().bind(chkFecha.selectedProperty().not());
        txtKDA.disableProperty().bind(chkKDA.selectedProperty().not()); // Se actualiza para el TextField
        cmbResultado.disableProperty().bind(chkResultado.selectedProperty().not());
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

    StringBuilder mensajeAlertas = new StringBuilder();

    // Verificar que al menos un checkbox esté seleccionado
    if (!chkJugador.isSelected() && !chkCampeon.isSelected() && !chkFecha.isSelected() &&
        !chkKDA.isSelected() && !chkResultado.isSelected()) {
        mostrarAlerta("Sin selección", "No hay ningún filtro seleccionado. Por favor, marque al menos uno.", Alert.AlertType.WARNING);
        return;
    }

    // Validaciones de los campos seleccionados
    if (chkJugador.isSelected() && (txtJugador.getText() == null || txtJugador.getText().trim().isEmpty())) {
        mensajeAlertas.append("- El campo 'Jugador' está vacío.\n");
    }
    if (chkCampeon.isSelected() && (txtCampeon.getText() == null || txtCampeon.getText().trim().isEmpty())) {
        mensajeAlertas.append("- El campo 'Campeón' está vacío.\n");
    }
    if (chkFecha.isSelected() && dateFecha.getValue() == null) {
        mensajeAlertas.append("- Debe seleccionar una fecha.\n");
    }
    if (chkKDA.isSelected()) {
        String kdaIngresado = txtKDA.getText().trim();
        if (kdaIngresado.isEmpty()) {
            mensajeAlertas.append("- El campo 'KDA' está vacío.\n");
        } else if (!kdaIngresado.matches("\\d+/\\d+/\\d+")) { 
            mensajeAlertas.append("- Formato incorrecto para 'KDA'. Debe ser en formato 'n/n/n' (Ej: 10/3/5).\n");
        }
    }
    if (chkResultado.isSelected() && (cmbResultado.getValue() == null || cmbResultado.getValue().trim().isEmpty())) {
        mensajeAlertas.append("- Debe seleccionar un valor para el campo 'Resultado'.\n");
    }

    // Si hay errores, mostrar alerta
    boolean valido = true;

    if (chkJugador.isSelected())
        valido &= vJugador.getValidationResult().getErrors().isEmpty();
    if (chkCampeon.isSelected())
        valido &= vCampeon.getValidationResult().getErrors().isEmpty();
    if (chkFecha.isSelected())
        valido &= vFecha.getValidationResult().getErrors().isEmpty();
    if (chkKDA.isSelected())
        valido &= vKDA.getValidationResult().getErrors().isEmpty();
    if (chkResultado.isSelected())
        valido &= vResultado.getValidationResult().getErrors().isEmpty();

    if (!valido) {
        mostrarAlerta("Error de validación", "Revisa los campos marcados con error.", Alert.AlertType.WARNING);
        return;
    }


    // 🚀 Aplicar filtros sobre listaOriginal sin modificarla
    System.out.println("🔍 Filtrando partidas...");
    System.out.println("Número de partidas antes del filtro: " + listaOriginal.size());

    ObservableList<Partida> filtradas = listaOriginal.stream()
            .filter(this::cumpleFiltros)
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

    System.out.println("Número de partidas después del filtro: " + filtradas.size());

    if (filtradas.isEmpty()) {
    System.out.println("⚠️ Ninguna partida coincide con los filtros.");

    StringBuilder filtrosAplicados = new StringBuilder("No se encontraron partidas con los siguientes filtros:\n\n");

    if (chkJugador.isSelected()) {
        filtrosAplicados.append("- Jugador: ").append(txtJugador.getText().trim()).append("\n");
    }
    if (chkCampeon.isSelected()) {
        filtrosAplicados.append("- Campeón: ").append(txtCampeon.getText().trim()).append("\n");
    }
    if (chkFecha.isSelected()) {
        filtrosAplicados.append("- Fecha: ").append(dateFecha.getValue()).append("\n");
    }
    if (chkKDA.isSelected()) {
        filtrosAplicados.append("- KDA: ").append(txtKDA.getText().trim()).append("\n");
    }
    if (chkResultado.isSelected()) {
        filtrosAplicados.append("- Resultado: ").append(cmbResultado.getValue()).append("\n");
    }

    mostrarAlerta("Sin resultados", filtrosAplicados.toString(), Alert.AlertType.INFORMATION);
    return; // ⛔ Detenemos el cierre de ventana
} else {
    System.out.println("✅ Filtrado exitoso, actualizando tabla.");
    tablaPartidas.setItems(filtradas);
    tablaPartidas.refresh();
}


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
     * Método para verificar si una partida cumple con los filtros seleccionados.
     */
    private boolean cumpleFiltros(Partida partida) {
    if (chkJugador.isSelected() && (partida.getJugador() == null || !partida.getJugador().equalsIgnoreCase(txtJugador.getText().trim()))) {
        return false;
    }
    if (chkCampeon.isSelected() && (partida.getCampeon() == null || !partida.getCampeon().equalsIgnoreCase(txtCampeon.getText().trim()))) {
        return false;
    }
    if (chkFecha.isSelected() && (partida.getFecha() == null || !partida.getFecha().equals(dateFecha.getValue()))) {
        return false;
    }
    if (chkKDA.isSelected() && (partida.getKda() == null || !partida.getKda().equals(txtKDA.getText().trim()))) {
        return false;
    }
    if (chkResultado.isSelected() && (partida.getResultado() == null || !partida.getResultado().trim().equalsIgnoreCase(cmbResultado.getValue().trim()))) {
    return false;
    }

    return true;
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
