 package controller;

import dao.DataBaseMain;
import dao.PartidaDAO;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import modelos.Campeon;
import modelos.Partida;
import partidas.AddControllerPartida;
import partidas.EditarControllerPartida;
import partidas.ListaControllerPartida;
import utils.AlertUtils;
import utils.ImportExportService;
import utils.TooltipUtils;

/**
 * Handler (controlador) de la tabla de partidas (juegan).
 * Igual que JugadorHandler y CampeonHandler pero con la particularidad
 * de que las partidas usan nombres en la interfaz que se convierten
 * a IDs cuando se guardan en la BD.
 * 
 * @author npauc
 */
public class PartidaHandler {
     // Componentes FXML de la tabla de partidas
     private TableView<Partida> tablaPartidas;
    private TextField txtBuscarPartida;
    private TableColumn<Partida, String> colJugadorPartida;
    private TableColumn<Partida, String> colCampeonesPartida;
    private TableColumn<Partida, LocalDate> colFechaPartida;
    private TableColumn<Partida, String> colKDA;
    private TableColumn<Partida, String> colResultadoPartida;
    private TableColumn<Partida, Void> columnaAccionesPartida;
         
    // Referencia al controlador de filtros para poder borrarlos
    private ListaControllerPartida listaControllerPartida;
    
    // Conexion compartida a la BD
    private Connection connection;
    
    // Lista completa y copia para filtrar
    private ObservableList<Partida> partidasList = FXCollections.observableArrayList();
    private ObservableList<Partida> listaOriginalPartidas = FXCollections.observableArrayList();
    
    
    /**
     * Inicializa la tabla: carga datos, vincula columnas, configura buscador y botones.
     */
    public void initialize(){

    cargarPartidas();
    txtBuscarPartida.textProperty().addListener((observable, oldValue, newValue) -> buscarPartida(newValue));

colJugadorPartida.setCellValueFactory(
             cellData -> cellData.getValue().jugadorProperty()
     );
     colCampeonesPartida.setCellValueFactory(
             cellData -> cellData.getValue().campeonProperty()
     );
     colFechaPartida.setCellValueFactory(cellData -> cellData.getValue().fechaProperty()); 
     colKDA.setCellValueFactory(cellData -> cellData.getValue().kdaProperty());
     colResultadoPartida.setCellValueFactory(cellData -> cellData.getValue().resultadoProperty());
      
         configurarColumnaAccionesPartida();  
}
    
    /**
     * Recibe los componentes FXML desde MainController. Saca la conexion e inicializa.
     */
    public void configurar(
            
    TableView<Partida> tablaPartidas,
    TextField txtBuscarPartida,
    TableColumn<Partida, String> colJugadorPartida,
    TableColumn<Partida, String> colCampeonesPartida,
    TableColumn<Partida, LocalDate> colFechaPartida,
    TableColumn<Partida, String> colKDA,
    TableColumn<Partida, String> colResultadoPartida,
    TableColumn<Partida, Void> columnaAccionesPartida
    
    ){
    this.tablaPartidas = tablaPartidas;
    this.txtBuscarPartida = txtBuscarPartida;
    this.colJugadorPartida = colJugadorPartida;
    this.colCampeonesPartida = colCampeonesPartida;
    this.colFechaPartida = colFechaPartida;
    this.colKDA = colKDA;
    this.colResultadoPartida = colResultadoPartida;
    this.columnaAccionesPartida = columnaAccionesPartida;

    try {
        this.connection = DataBaseMain.getConnection();
    } catch (SQLException e) {
        e.printStackTrace();
    }

    initialize();
    }
    
    /**
     * Carga (o recarga) todas las partidas desde la BD usando LEFT JOIN
     * para obtener los nombres de jugador y campeon en vez de IDs.
     */
    public void cargarPartidas() {
    PartidaDAO partidaDAO = new PartidaDAO();
    
    partidasList.clear();
    listaOriginalPartidas.clear();
    partidasList.addAll(partidaDAO.obtenerTodos(connection));
    
    listaOriginalPartidas.setAll(partidasList);
    tablaPartidas.setItems(listaOriginalPartidas);
    tablaPartidas.refresh();
    
}

    /**
     * Filtra la tabla de partidas buscando por nombre de jugador.
     */
    private void buscarPartida(String filtro) {
    if (filtro == null || filtro.isEmpty()) {
        tablaPartidas.setItems(FXCollections.observableArrayList(listaOriginalPartidas));
    } else {
        ObservableList<Partida> partidasFiltradas = FXCollections.observableArrayList(
            listaOriginalPartidas.stream()
                .filter(partida -> partida.getJugador().toLowerCase().contains(filtro.toLowerCase()))
                .collect(Collectors.toList())
        );
        tablaPartidas.setItems(partidasFiltradas);
    }
    tablaPartidas.refresh();
}
    
    /**
     * Abre la ventana modal para editar una partida.
     */
    private void editarPartida(Partida partida) {
    if (partida == null) {
        AlertUtils.mostrarAlerta("Error", "No se ha seleccionado ninguna partida para editar.", Alert.AlertType.WARNING);
        return;
    }
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/EditarPartida.fxml"));
        Parent root = loader.load();

        EditarControllerPartida controladorEdicion = loader.getController();
        controladorEdicion.setPartida(partida);
        controladorEdicion.setTablaPartidas(tablaPartidas);
        controladorEdicion.setConnection(connection);

        Stage stage = new Stage();
        stage.setTitle("Editar Partida");
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/estilos/editarPartida.css").toExternalForm());

        stage.setScene(scene);

        stage.setWidth(350);
        stage.setHeight(470);


        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

    } catch (IOException e) {
        e.printStackTrace();
    }
}
    
    
    /**
     * Pide confirmacion y elimina la partida de la BD.
     * Verifica que el ID sea valido antes de intentar borrar.
     */
    private void eliminarPartida(Partida partida) {
        if (partida == null) {
            AlertUtils.mostrarAlerta("Selección requerida", "Por favor, selecciona una partida antes de eliminar.", Alert.AlertType.WARNING);
            return;
        }

        int idJuegan = partida.getIdJuegan();

        if (idJuegan == -1) {
            AlertUtils.mostrarAlerta("Error", "No se encontró la partida en la base de datos.", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar esta partida?\n\n");

        alertaEliminar(confirmacion);
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {


                PartidaDAO partidaDAO = new PartidaDAO();
                boolean eliminado = partidaDAO.eliminar(partida.getIdJuegan(), connection);

                if (eliminado) {
                        partidasList.remove(partida);
                        listaOriginalPartidas.remove(partida);
                        tablaPartidas.getSelectionModel().clearSelection();
                        tablaPartidas.setItems(listaOriginalPartidas); 
                        tablaPartidas.refresh();   

                    AlertUtils.mostrarAlerta("Éxito", "La partida ha sido eliminada correctamente.", Alert.AlertType.INFORMATION);
                } else {
                    AlertUtils.mostrarAlerta("Error", "No se encontró la partida en la base de datos.", Alert.AlertType.ERROR);
                }
        }
    }


    /**
     * Crea los botones de editar y eliminar en cada fila de la tabla de partidas.
     */
    private void configurarColumnaAccionesPartida() {
        Callback<TableColumn<Partida, Void>, TableCell<Partida, Void>> cellFactory = param -> new TableCell<>() {
            private final HBox contenedor = new HBox();
            private final Button btnEditar = new Button();
            private final Button btnEliminar = new Button();

            {
                // Configurar ícono del botón Editar
                ImageView iconoEditar = new ImageView(new Image(getClass().getResourceAsStream("/icons/lapiz.png")));
                iconoEditar.setFitWidth(16);
                iconoEditar.setFitHeight(16);
                btnEditar.setGraphic(iconoEditar);

                btnEditar.setOnAction(event -> {
                    Partida partidaSeleccionada = getTableView().getItems().get(getIndex());

                    if (partidaSeleccionada != null) {
                        Partida filaMarcada = tablaPartidas.getSelectionModel().getSelectedItem();

                        if (filaMarcada !=null && !filaMarcada.equals(partidaSeleccionada)) {
                            mostrarAlertaFilaMarcadaEditarPartida(filaMarcada);
                            return;
                        }

                        editarPartida(partidaSeleccionada);
                    }
                });

                ImageView iconoEliminar = new ImageView(new Image(getClass().getResourceAsStream("/icons/papelera.png")));
                iconoEliminar.setFitWidth(16);
                iconoEliminar.setFitHeight(16);
                btnEliminar.setGraphic(iconoEliminar);

                btnEliminar.setOnAction(event -> {
                    Partida partidaSeleccionada = getTableView().getItems().get(getIndex());

                    if (partidaSeleccionada != null) {
                        Partida filaMarcada = tablaPartidas.getSelectionModel().getSelectedItem();

                         if (filaMarcada !=null && !filaMarcada.equals(partidaSeleccionada)) {
                            mostrarAlertaFilaMarcadaBorrarPartida(filaMarcada);
                            return;
                        }

                        eliminarPartida(partidaSeleccionada);
                    }
                });

                btnEditar.setTooltip(TooltipUtils.crear("Editar Partida"));
                btnEliminar.setTooltip(TooltipUtils.crear("Eliminar Partida"));
                
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

        columnaAccionesPartida.setCellFactory(cellFactory);
    }


    private void mostrarAlertaFilaMarcadaEditarPartida(Partida partidaMarcada) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Edición restringida");
        alerta.setHeaderText("Ya hay una fila marcada");
        alerta.setContentText("Tienes marcada la fila de \"" + partidaMarcada.getJugador() + "\". \n"
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

    private void mostrarAlertaFilaMarcadaBorrarPartida(Partida partidaMarcada) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Borrado restringido");
        alerta.setHeaderText("Ya hay una fila marcada");
        alerta.setContentText("Tienes marcada la fila de \"" + partidaMarcada.getJugador() + "\". \n"
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


    /**
     * Borra los filtros activos y vuelve a mostrar todas las partidas.
     */
    @FXML
    public void borrarFiltroPartidas() {
        if (listaControllerPartida == null) {
            AlertUtils.mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
            return; 
        }

        listaControllerPartida.borrarFiltrosPartida();
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
     * Abre la ventana de filtros avanzados para partidas.
     */
    @FXML
    public void abrirFiltroPartidas() {
        try {   
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/ListaFiltrosPartida.fxml"));
            Parent root = loader.load();

            ListaControllerPartida controlador = loader.getController();

            controlador.setListaOriginal(listaOriginalPartidas);
            controlador.setTablaPartidas(tablaPartidas);

            this.listaControllerPartida = controlador;

            Stage stage = new Stage();
            stage.setTitle("Filtrar Partidas");
            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/estilos/filtroPartida.css").toExternalForm());

            stage.setScene(scene);

            stage.setWidth(350);
            stage.setHeight(470);


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
     * Abre la ventana modal para anadir una partida nueva.
     */
    @FXML
    public void abrirBtnAnadirPartida() throws IOException {
        try {   
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/AddPartida.fxml"));
            Parent root = loader.load();

            AddControllerPartida controlador = loader.getController();
            controlador.setListaOriginalPartidas(listaOriginalPartidas);
            controlador.setListaYTablaPartidas(partidasList, tablaPartidas);
            controlador.setConnection(connection);

            Stage stage = new Stage();
            stage.setTitle("Añadir Partida");
            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/estilos/anadirPartida.css").toExternalForm());

            stage.setScene(scene);

            stage.setWidth(350);
            stage.setHeight(470);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
            double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;

            stage.setX(centerX);
            stage.setY(centerY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controlador.isPartidaAgregada()) {
                tablaPartidas.refresh();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
        // ===================== IMPORTAR / EXPORTAR =====================

    @FXML
    public void importarCSVPartidas() {
        File archivo = seleccionarArchivo("CSV", "*.csv");
        if (archivo == null) return;

        try {
            String resultado = ImportExportService.importarPartidasCSV(archivo, connection);
            cargarPartidas();
            AlertUtils.mostrarAlerta("Importación completada", resultado, Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            AlertUtils.mostrarAlerta("Error", "Error al leer el archivo CSV: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void importarJSONPartidas() {
        File archivo = seleccionarArchivo("JSON", "*.json");
        if (archivo == null) return;

        try {
            String resultado = ImportExportService.importarPartidasJSON(archivo, connection);
            cargarPartidas();
            AlertUtils.mostrarAlerta("Importación completada", resultado, Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            AlertUtils.mostrarAlerta("Error", "Error al leer el archivo JSON: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void exportarCSVPartidas() {
        File archivo = seleccionarArchivoGuardar("partidas", ".csv");
        if (archivo == null) return;

        try {
            String resultado = ImportExportService.exportarPartidasCSV(
                    new ArrayList<>(listaOriginalPartidas), archivo);
            AlertUtils.mostrarAlerta("Exportación completada", resultado, Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            AlertUtils.mostrarAlerta("Error", "Error al escribir el archivo CSV: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void exportarJSONPartidas() {
        File archivo = seleccionarArchivoGuardar("partidas", ".json");
        if (archivo == null) return;

        try {
            String resultado = ImportExportService.exportarPartidasJSON(
                    new ArrayList<>(listaOriginalPartidas), archivo);
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
        return fileChooser.showOpenDialog(tablaPartidas.getScene().getWindow());
    }

    private File seleccionarArchivoGuardar(String nombrePorDefecto, String extension) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar archivo");
        fileChooser.setInitialFileName(nombrePorDefecto + extension);
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Archivos " + extension, "*" + extension));
        return fileChooser.showSaveDialog(tablaPartidas.getScene().getWindow());
    }
    
    }
