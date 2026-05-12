package controller;

import dao.DataBaseMain;
import dao.JugadorDAO;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import jugadores.AddControllerJugador;
import jugadores.EditarControllerJugador;
import jugadores.ListaControllerJugadores;
import modelos.Jugador;
import utils.AlertUtils;
import utils.ImportExportService;
import utils.TooltipUtils;

/**
 * Handler (controlador) de la tabla de jugadores.
 * Se encarga de cargar los datos, buscar, anadir, editar y eliminar jugadores.
 * Tambien gestiona los botones de accion (editar/eliminar) que aparecen en cada fila.
 * 
 * @author npauc
 */
public class JugadorHandler {
    
     // Componentes FXML que se enlazan desde MainController
     private TableView<Jugador> tablaJugadores;
    private TextField txtBuscarJugador;
    private TableColumn<Jugador, String> colNombreJugador;
    private TableColumn<Jugador, String> colDescripcionJugador;
    private TableColumn<Jugador, Integer> colEdadJugador;
    private TableColumn<Jugador, String> colEmailJugador;
    private TableColumn<Jugador, String> colNacionalidadJugador;
    private TableColumn<Jugador, String> colPosicionJugador;
    private TableColumn<Jugador, Void> columnaAccionesJugadores;
    
    // Lista completa de jugadores y la copia original para poder filtrar
    private ObservableList<Jugador> jugadoresList = FXCollections.observableArrayList();
    private ObservableList<Jugador> listaOriginal = FXCollections.observableArrayList();
    
    // Conexion a la BD compartida
    private Connection connection;
    
    // Referencia al controlador de filtros para poder borrarlos despues
    private ListaControllerJugadores listaControllerJugadores;
    
    
 /**
     * Inicializa la tabla: carga los datos de la BD, vincula cada columna
     * con su propiedad del modelo, configura el buscador en tiempo real
     * y los botones de editar/eliminar en cada fila.
     */
 public void initialize() {
     cargarJugadores();
     
     // Vinculamos cada columna con la propiedad correspondiente del modelo Jugador
colNombreJugador.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
     colDescripcionJugador.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
     colEdadJugador.setCellValueFactory(cellData -> cellData.getValue().edadProperty().asObject());
     colEmailJugador.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
     colNacionalidadJugador.setCellValueFactory(cellData -> cellData.getValue().nacionalidadProperty());
     colPosicionJugador.setCellValueFactory(cellData -> cellData.getValue().posicionProperty());
     
     // Listener para el buscador: cada vez que se escribe algo, filtra la tabla
     txtBuscarJugador.textProperty().addListener((observable, oldValue, newValue) -> buscarJugador(newValue));
     
     configurarColumnaAccionesJugadores();
          
 }
 
 
 /**
     * Recibe los componentes FXML desde MainController y los guarda.
     * Tambien saca la conexion a la BD e inicializa la tabla.
     */
 public void configurar(
    TableView<Jugador> tablaJugadores,
    TextField txtBuscarJugador,
    TableColumn<Jugador, String> colNombreJugador,
    TableColumn<Jugador, String> colDescripcionJugador,
    TableColumn<Jugador, Integer> colEdadJugador,
    TableColumn<Jugador, String> colEmailJugador,
    TableColumn<Jugador, String> colNacionalidadJugador,
    TableColumn<Jugador, String> colPosicionJugador,
    TableColumn<Jugador, Void> columnaAccionesJugadores
) {
    this.tablaJugadores = tablaJugadores;
    this.txtBuscarJugador = txtBuscarJugador;
    this.colNombreJugador = colNombreJugador;
    this.colDescripcionJugador = colDescripcionJugador;
    this.colEdadJugador = colEdadJugador;
    this.colEmailJugador = colEmailJugador;
    this.colNacionalidadJugador = colNacionalidadJugador;
    this.colPosicionJugador = colPosicionJugador;
    this.columnaAccionesJugadores = columnaAccionesJugadores;
    
    try {
        this.connection = DataBaseMain.getConnection();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
initialize();
    }
 
 /**
     * Carga (o recarga) todos los jugadores desde la BD y los pinta en la tabla.
     * Se llama al inicio y despues de cada insert/delete para refrescar.
     */
 private void cargarJugadores() {
    JugadorDAO jugadorDAO = new JugadorDAO();
    
    jugadoresList.clear();
    listaOriginal.clear();
    jugadoresList.addAll(jugadorDAO.obtenerTodos(connection));
    
    listaOriginal.setAll(jugadoresList);
    tablaJugadores.setItems(listaOriginal);
    tablaJugadores.refresh();
}
    
    /**
     * Filtra la tabla de jugadores en tiempo real segun lo que se escriba
     * en el campo de busqueda. Si el campo esta vacio, muestra todos.
     */
    private void buscarJugador(String filtro) {
    if (filtro == null || filtro.trim().isEmpty()) {
        tablaJugadores.setItems(FXCollections.observableArrayList(listaOriginal));
    } else {
        ObservableList<Jugador> jugadoresFiltrados = FXCollections.observableArrayList(
            listaOriginal.stream()
                .filter(jugador -> jugador.getNombre().toLowerCase().contains(filtro.toLowerCase()))
                .collect(Collectors.toList())
        );
        tablaJugadores.setItems(jugadoresFiltrados);
    }
    tablaJugadores.refresh();
}
 
    /**
     * Abre una ventana modal para editar los datos del jugador seleccionado.
     * Si no hay jugador seleccionado, muestra un aviso.
     */
    private void editarJugador(Jugador jugador) {
    if (jugador == null) {
        AlertUtils.mostrarAlerta("Error", "No se ha seleccionado ningún jugador para editar.", Alert.AlertType.WARNING);
        return;
    }
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/EditarJugador.fxml"));
        Parent root = loader.load();

EditarControllerJugador controladorEdicion = loader.getController();
        controladorEdicion.setJugador(jugador);
        controladorEdicion.setTablaJugadores(tablaJugadores); 
        controladorEdicion.setConnection(connection);

        Stage stage = new Stage();
            stage.setTitle("Editar Jugador");
            Scene scene = new Scene(root);


            scene.getStylesheets().add(getClass().getResource("/estilos/editarJugador.css").toExternalForm());

            stage.setScene(scene);

            stage.setWidth(350); 
            stage.setHeight(540);

            // Obtener las dimensiones de la pantalla
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // Calcular la posición centrada después de que el tamaño del Stage se haya inicializado
            double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
            double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;

            // Configurar la posición centrada
            stage.setX(centerX);
            stage.setY(centerY);

stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
    
    /**
     * Pide confirmacion al usuario y, si acepta, elimina al jugador de la BD.
     * Tambien elimina sus partidas (juegan) por la foreign key.
     * Despues actualiza la tabla.
     */
    private void eliminarJugador(Jugador jugador) {
    if (jugador == null) {
        AlertUtils.mostrarAlerta("Selección requerida", "Por favor, selecciona un jugador antes de eliminar.", Alert.AlertType.WARNING);
        return;
    }
    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText(null);
    confirmacion.setContentText("¿Estás seguro de que deseas eliminar a " + jugador.getNombre() + "?");
    
    alertaEliminar(confirmacion);
    
    Optional<ButtonType> resultado = confirmacion.showAndWait();
    
    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
       
            JugadorDAO jugadorDAO = new JugadorDAO();
            boolean eliminado = jugadorDAO.eliminar(jugador.getId(), connection);

            if (eliminado) {             
                    jugadoresList.remove(jugador);
                    listaOriginal.remove(jugador);
                    tablaJugadores.getSelectionModel().clearSelection();
                    tablaJugadores.refresh();
                 AlertUtils.mostrarAlerta("Éxito", "El jugador ha sido eliminado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                 AlertUtils.mostrarAlerta("Error", "No se encontró el jugador en la base de datos.", Alert.AlertType.ERROR);
            }

    
    }  
}
    
     /**
     * Crea los botones de editar y eliminar en cada fila de la tabla.
     * Cuando se pulsa editar/eliminar, comprueba si la fila seleccionada
     * coincide con la fila del boton para evitar ambiguedad.
     */
 private void configurarColumnaAccionesJugadores() {
    Callback<TableColumn<Jugador, Void>, TableCell<Jugador, Void>> cellFactory = param -> new TableCell<>() {
        private final HBox contenedor = new HBox();
        private final Button btnEditar = new Button();
        private final Button btnEliminar = new Button();

        {
                ImageView iconoEditar = new ImageView(new Image(getClass().getResourceAsStream("/icons/lapiz.png")));
            iconoEditar.setFitWidth(16);
            iconoEditar.setFitHeight(16);
            btnEditar.setGraphic(iconoEditar);

            btnEditar.setOnAction(event -> {
                Jugador jugadorSeleccionado = getTableView().getItems().get(getIndex());

                if (jugadorSeleccionado != null) {
                    Jugador filaMarcada = tablaJugadores.getSelectionModel().getSelectedItem();
                    if (filaMarcada != null && !filaMarcada.equals(jugadorSeleccionado)) {
                        mostrarAlertaFilaMarcadaEditar(filaMarcada);
                        return;
                    }

                    editarJugador(jugadorSeleccionado);
                }
            });
            
            ImageView iconoEliminar = new ImageView(new Image(getClass().getResourceAsStream("/icons/papelera.png")));
            iconoEliminar.setFitWidth(16);
            iconoEliminar.setFitHeight(16);
            btnEliminar.setGraphic(iconoEliminar);

            
            btnEliminar.setOnAction(event -> {
                Jugador jugadorSeleccionado = getTableView().getItems().get(getIndex());

                if (jugadorSeleccionado != null) {
                    Jugador filaMarcada = tablaJugadores.getSelectionModel().getSelectedItem();
                    if (filaMarcada != null && !filaMarcada.equals(jugadorSeleccionado)) {
                        mostrarAlertaFilaMarcadaBorrar(filaMarcada);
                        return;
                    }

                    eliminarJugador(jugadorSeleccionado);
                }
            });

            btnEditar.setTooltip(TooltipUtils.crear("Editar Jugador"));
            btnEliminar.setTooltip(TooltipUtils.crear("Eliminar Jugador"));

            contenedor.getChildren().addAll(btnEditar, btnEliminar);
            contenedor.setAlignment(Pos.CENTER);
            contenedor.setSpacing(10);
        }

         @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(contenedor);
            }
        }
    };

    columnaAccionesJugadores.setCellFactory(cellFactory);
}


private void mostrarAlertaFilaMarcadaEditar(Jugador jugadorMarcado) {
    Alert alerta = new Alert(Alert.AlertType.WARNING);
    alerta.setTitle("Edición restringida");
    alerta.setHeaderText("Ya hay una fila marcada");
    alerta.setContentText(" Tienes marcada la fila de \"" + jugadorMarcado.getNombre() + "\". \n"
            + "Si quieres editar otra fila, primero desmarca la fila seleccionada.");

    alerta.setOnShown(event -> {
        Platform.runLater(() -> {
            Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
        });
    });
    alerta.showAndWait();
}


private void mostrarAlertaFilaMarcadaBorrar(Jugador jugadorMarcado) {
    Alert alerta = new Alert(Alert.AlertType.WARNING);
    alerta.setTitle("Borrado restringido");
    alerta.setHeaderText("Ya hay una fila marcada");
    alerta.setContentText(" Tienes marcada la fila de \"" + jugadorMarcado.getNombre() + "\". \n"
            + "Si quieres borrar otra fila, primero desmarca la fila seleccionada.");

    alerta.setOnShown(event -> {
        Platform.runLater(() -> {
            Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
        });
    });
    alerta.showAndWait();
}

    private void alertaEliminar(Alert alerta) {
        alerta.setOnShown(event -> {
        Platform.runLater(() -> {
            Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
        });
    });
}
   
    
/**
     * Abre la ventana de filtros avanzados para jugadores.
     */
    @FXML
    public void abrirFiltroJugadores() {
        try {   
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/ListaFiltrosJugadores.fxml"));
            Parent root = loader.load();

            ListaControllerJugadores controlador = loader.getController();
            controlador.setTablaJugadores(tablaJugadores);
            controlador.setListaOriginal(listaOriginal);

            this.listaControllerJugadores = controlador;

            Stage stage = new Stage();
            stage.setTitle("Filtrar Jugadores");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/estilos/filtroJugador.css").toExternalForm()); 

            stage.setScene(scene);

            stage.setTitle("Filtrar Jugadores");
            
            stage.setWidth(350);
            stage.setHeight(520);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
            double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;

            stage.setX(centerX);
            stage.setY(centerY);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Borra los filtros activos y vuelve a mostrar todos los jugadores.
     * Si no hay filtros aplicados, muestra un aviso.
     */
    @FXML
public void borrarFiltroJugadores() {
    if (listaControllerJugadores == null) {
        AlertUtils.mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
        return; 
    }

    listaControllerJugadores.borrarFiltros();
}

/**
     * Abre la ventana modal para anadir un jugador nuevo.
     * Al cerrarse la ventana, refresca la tabla si se agrego correctamente.
     */
    @FXML
    public void abrirBtnAnadir() throws IOException {
        
     try{   
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/AddJugador.fxml"));
        Parent root = loader.load();

        AddControllerJugador controlador = loader.getController();
        controlador.setListaOriginal(listaOriginal);
        controlador.setListaYTablaJugadores(jugadoresList, tablaJugadores);
        controlador.setConnection(connection);
        
        Stage stage = new Stage();
        stage.setTitle("Añadir Jugador");
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/estilos/anadirJugador.css").toExternalForm());

        stage.setScene(scene);
        
        stage.setWidth(350);
        stage.setHeight(540);
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
        double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;

        stage.setX(centerX);
        stage.setY(centerY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        
        if (controlador.isJugadorAgregado()) {
            tablaJugadores.refresh();
        }

    }catch (IOException e) {
        e.printStackTrace();
    }
    } 
       // ===================== IMPORTAR / EXPORTAR =====================

    @FXML
    public void importarCSVJugadores() {
        File archivo = seleccionarArchivo("CSV", "*.csv");
        if (archivo == null) return;

        try {
            String resultado = ImportExportService.importarJugadoresCSV(archivo, connection);
            cargarJugadores();
            AlertUtils.mostrarAlerta("Importación completada", resultado, Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            AlertUtils.mostrarAlerta("Error", "Error al leer el archivo CSV: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void importarJSONJugadores() {
        File archivo = seleccionarArchivo("JSON", "*.json");
        if (archivo == null) return;

        try {
            String resultado = ImportExportService.importarJugadoresJSON(archivo, connection);
            cargarJugadores();
            AlertUtils.mostrarAlerta("Importación completada", resultado, Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            AlertUtils.mostrarAlerta("Error", "Error al leer el archivo JSON: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void exportarCSVJugadores() {
        File archivo = seleccionarArchivoGuardar("jugadores", ".csv");
        if (archivo == null) return;

        try {
            String resultado = ImportExportService.exportarJugadoresCSV(
                    new ArrayList<>(listaOriginal), archivo);
            AlertUtils.mostrarAlerta("Exportación completada", resultado, Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            AlertUtils.mostrarAlerta("Error", "Error al escribir el archivo CSV: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void exportarJSONJugadores() {
        File archivo = seleccionarArchivoGuardar("jugadores", ".json");
        if (archivo == null) return;

        try {
            String resultado = ImportExportService.exportarJugadoresJSON(
                    new ArrayList<>(listaOriginal), archivo);
            AlertUtils.mostrarAlerta("Exportación completada", resultado, Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            AlertUtils.mostrarAlerta("Error", "Error al escribir el archivo JSON: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private File seleccionarArchivo(String titulo, String extension) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle(titulo);
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter(titulo, extension));
        return fileChooser.showOpenDialog(tablaJugadores.getScene().getWindow());
    }

    private File seleccionarArchivoGuardar(String nombrePorDefecto, String extension) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar archivo");
        fileChooser.setInitialFileName(nombrePorDefecto + extension);
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Archivos " + extension, "*" + extension));
        return fileChooser.showSaveDialog(tablaJugadores.getScene().getWindow());
    }
}
