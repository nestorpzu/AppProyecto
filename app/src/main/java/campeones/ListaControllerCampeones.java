/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package campeones;

import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

/**
 *
 * @author nestor
 */

public class ListaControllerCampeones {

    // Referencias a los elementos en el FXML
    @FXML private CheckBox chkNombre;
    @FXML private TextField txtNombreCampeon;
    @FXML private CheckBox chkDescripcion;
    @FXML private TextArea txtDescripcion;
    @FXML private CheckBox chkRol;
    @FXML private ComboBox<String> cmbRol;
    @FXML private CheckBox chkDificultad;
    @FXML private ComboBox<String> cmbDificultad;
    @FXML private Button btnAplicarFiltros;
    @FXML private Button btnCancelar;

    private ObservableList<Campeon> listaOriginal = FXCollections.observableArrayList();
    private TableView<Campeon> tablaCampeones; // Referencia a la tabla principal
    private ValidationSupport vNombre, vDescripcion, vRol, vDificultad;
    private ImageView iconoOk, iconoErr;
    

    /**
     * Inicialización de la ventana
     */
        @FXML
        public void initialize() {
            // Inicializar ComboBox
            if (cmbRol != null) { // Verifica que no sea null antes de acceder a getItems()
        cmbRol.getItems().addAll("Tanque", "Luchador", "Asesino", "Mago", "Tirador", "Soporte");
    } else {
        System.err.println(" Error: cmbRol es null. Revisa el FXML.");
    }
        cmbRol.setPromptText("Selecciona un Rol");

        cmbDificultad.getItems().addAll("Baja", "Media", "Alta");
        cmbDificultad.setPromptText("Selecciona una Dificultad");

        // Configurar la lógica de los CheckBox
        configurarCheckBoxes();
        inicializarValidaciones();

    }

    private void configurarCheckBoxes() {
        txtNombreCampeon.disableProperty().bind(chkNombre.selectedProperty().not());
        txtDescripcion.disableProperty().bind(chkDescripcion.selectedProperty().not());
        cmbRol.disableProperty().bind(chkRol.selectedProperty().not());
        cmbDificultad.disableProperty().bind(chkDificultad.selectedProperty().not());
    }

    private void inicializarValidaciones() {
    iconoOk = new ImageView(new Image(getClass().getResourceAsStream("/icons/ok_icon.png")));
    iconoErr = new ImageView(new Image(getClass().getResourceAsStream("/icons/error_icon.png")));
    iconoOk.setFitWidth(16); iconoOk.setFitHeight(16);
    iconoErr.setFitWidth(16); iconoErr.setFitHeight(16);

    vNombre = new ValidationSupport();
    vDescripcion = new ValidationSupport();
    vRol = new ValidationSupport();
    vDificultad = new ValidationSupport();

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

    vNombre.setValidationDecorator(decorador);
    vDescripcion.setValidationDecorator(decorador);
    vRol.setValidationDecorator(decorador);
    vDificultad.setValidationDecorator(decorador);

    vNombre.registerValidator(txtNombreCampeon, true,
        Validator.createEmptyValidator("El nombre no puede estar vacío"));

    vDescripcion.registerValidator(txtDescripcion, true,
        Validator.createEmptyValidator("La descripción no puede estar vacía"));

    vRol.registerValidator(cmbRol, true,
        Validator.createEmptyValidator("Selecciona un rol"));

    vDificultad.registerValidator(cmbDificultad, true,
        Validator.createEmptyValidator("Selecciona una dificultad"));
}

    
    /**
     * Método para aplicar filtros
    */
    
    @FXML
    private void aplicarFiltros() {
        StringBuilder mensajeAlertas = new StringBuilder();
        boolean algunFiltroSeleccionado = false;

        // Validar si hay filtros seleccionados
        if (chkNombre.isSelected()) algunFiltroSeleccionado = true;
        if (chkDescripcion.isSelected()) algunFiltroSeleccionado = true;
        if (chkRol.isSelected()) algunFiltroSeleccionado = true;
        if (chkDificultad.isSelected()) algunFiltroSeleccionado = true;

        if (!algunFiltroSeleccionado) {
            mostrarAlerta("Sin selección", "No hay ningún filtro seleccionado. Por favor, marca al menos uno.", Alert.AlertType.WARNING);
            return;
        }

        // Validar que los campos de los CheckBox seleccionados no estén vacíos
        if (chkNombre.isSelected() && (txtNombreCampeon.getText() == null || txtNombreCampeon.getText().trim().isEmpty())) {
            mensajeAlertas.append("- El campo 'Nombre' está vacío.\n");
        }
        if (chkDescripcion.isSelected() && (txtDescripcion.getText() == null || txtDescripcion.getText().trim().isEmpty())) {
            mensajeAlertas.append("- El campo 'Descripción' está vacío.\n");
        }
        if (chkRol.isSelected() && (cmbRol.getValue() == null || cmbRol.getValue().trim().isEmpty())) {
            mensajeAlertas.append("- Debes seleccionar un Rol.\n");
        }
        if (chkDificultad.isSelected() && (cmbDificultad.getValue() == null || cmbDificultad.getValue().trim().isEmpty())) {
            mensajeAlertas.append("- Debes seleccionar una Dificultad.\n");
        }

        boolean valido = true;

        if (chkNombre.isSelected()) {
            valido &= vNombre.getValidationResult().getErrors().isEmpty();
        }
        if (chkDescripcion.isSelected()) {
            valido &= vDescripcion.getValidationResult().getErrors().isEmpty();
        }
        if (chkRol.isSelected()) {
            valido &= vRol.getValidationResult().getErrors().isEmpty();
        }
        if (chkDificultad.isSelected()) {
            valido &= vDificultad.getValidationResult().getErrors().isEmpty();
        }

        if (!valido) {
            mostrarAlerta("Error de validación", "Revisa los campos marcados con error.", Alert.AlertType.WARNING);
            return;
        }


        // Verificar que listaOriginal no esté vacía antes de filtrar
        if (listaOriginal == null || listaOriginal.isEmpty()) {
            mostrarAlerta("Error", "No hay datos originales disponibles para filtrar.", Alert.AlertType.WARNING);
            return;
        }

        // Aplicar los filtros a la lista
        ObservableList<Campeon> filtrados = FXCollections.observableArrayList(
            listaOriginal.stream()
                .filter(this::cumpleFiltros)
                .collect(Collectors.toList())
        );

        // Si la lista filtrada está vacía, mostrar un mensaje
        if (filtrados.isEmpty()) {
    StringBuilder filtrosAplicados = new StringBuilder("No se encontraron campeones con los filtros aplicados:\n\n");

    if (chkNombre.isSelected()) {
        filtrosAplicados.append("- Nombre: ").append(txtNombreCampeon.getText().trim()).append("\n");
    }
    if (chkDescripcion.isSelected()) {
        filtrosAplicados.append("- Descripción: ").append(txtDescripcion.getText().trim()).append("\n");
    }
    if (chkRol.isSelected()) {
        filtrosAplicados.append("- Rol: ").append(cmbRol.getValue()).append("\n");
    }
    if (chkDificultad.isSelected()) {
        filtrosAplicados.append("- Dificultad: ").append(cmbDificultad.getValue()).append("\n");
    }

    mostrarAlerta("Sin resultados", filtrosAplicados.toString(), Alert.AlertType.INFORMATION);
    return; // para que no siga
}
 else {
            // Actualizar la tabla con los datos filtrados
            tablaCampeones.setItems(filtrados);
            tablaCampeones.refresh();
        }

        // Cerrar la ventana
        cerrarVentana();
    }

    /**
     * Método para borrar los filtros y restaurar la tabla con los datos originales.
     */
    
  @FXML
public void borrarFiltrosCampeones() {
    if (listaOriginal == null) {
        mostrarAlerta("Error", "La lista original de campeones no está inicializada.", Alert.AlertType.ERROR);
        System.out.println("⚠️ Error: listaOriginal es null.");
        return;
    }

    if (listaOriginal.isEmpty()) {
        mostrarAlerta("Error", "No hay datos originales disponibles para restaurar.", Alert.AlertType.WARNING);
        System.out.println("⚠️ Error: listaOriginal está vacía.");
        return;
    }

    // Verificar si la tabla ya muestra la lista original
    if (tablaCampeones.getItems().size() == listaOriginal.size() &&
        tablaCampeones.getItems().containsAll(listaOriginal)) {
        mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
        System.out.println("ℹ️ No hay filtros activos en la tabla de campeones.");
        return;
    }

    System.out.println("♻️ Restaurando listaOriginalCampeones con " + listaOriginal.size() + " elementos.");

    // Limpiar y actualizar la tabla con los datos originales
    tablaCampeones.getItems().clear();
    tablaCampeones.setItems(FXCollections.observableArrayList(listaOriginal));
    tablaCampeones.refresh();

    mostrarAlerta("Filtros eliminados", "Se han eliminado los filtros y restaurado todos los campeones.", Alert.AlertType.INFORMATION);
}



    private boolean cumpleFiltros(Campeon campeon) {
        boolean coincide = true;

        if (chkNombre.isSelected()) {
            coincide &= campeon.getNombre().toLowerCase().contains(txtNombreCampeon.getText().toLowerCase());
        }
        if (chkDescripcion.isSelected()) {
            coincide &= campeon.getDescripcion().toLowerCase().contains(txtDescripcion.getText().toLowerCase());
        }
        if (chkRol.isSelected()) {
            coincide &= campeon.getRol().equals(cmbRol.getValue());
        }
        if (chkDificultad.isSelected()) {
            coincide &= campeon.getDificultad().equals(cmbDificultad.getValue());
        }

        return coincide;
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

            // Centrar la alerta en la pantalla
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
    public void setListaOriginal(ObservableList<Campeon> listaOriginal) {
        this.listaOriginal = listaOriginal;
         System.out.println("listaOriginalCampeones recibida con " + listaOriginal.size() + " elementos.");
    }

    /**
     * Setter para la tabla principal
     */
    public void setTablaCampeones(TableView<Campeon> tablaCampeones) {
        this.tablaCampeones = tablaCampeones;
    }
}
