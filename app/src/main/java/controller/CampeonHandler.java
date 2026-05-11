/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import campeones.AddControllerCampeon;
import campeones.EditarControllerCampeon;
import campeones.ListaControllerCampeones;
import dao.CampeonDAO;
import dao.DataBaseMain;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import modelos.Jugador;
import utils.AlertUtils;
import utils.TooltipUtils;

/**
 *
 * @author npauc
 */
public class CampeonHandler {
    
    
     private TableView<Campeon> tablaCampeones;
    private TextField txtBuscarCampeon;
    private TableColumn<Campeon, String> colNombreCampeon;
    private TableColumn<Campeon, String> colDescripcionCampeon;
    private TableColumn<Campeon, String> colRolCampeon;
    private TableColumn<Campeon, String> colDificultadCampeon;
    private TableColumn<Campeon, Void> columnaAccionesCampeones;
    
    private ListaControllerCampeones listaControllerCampeones;
    
    private Connection connection;
    
    private ObservableList<Campeon> campeonesList = FXCollections.observableArrayList();
    private ObservableList<Campeon> listaOriginalCampeones = FXCollections.observableArrayList();
    
    private void initialize() {
        
    cargarCampeones();
    
    
    colNombreCampeon.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
     colDescripcionCampeon.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
     colRolCampeon.setCellValueFactory(cellData -> cellData.getValue().rolProperty());
     colDificultadCampeon.setCellValueFactory(cellData -> cellData.getValue().dificultadProperty());
      txtBuscarCampeon.textProperty().addListener((observable, oldValue, newValue) -> buscarCampeon(newValue));
    
     
    

    configurarColumnaAccionesCampeones();
    
    } 
    
    public void configurar(
    TableView<Campeon> tablaCampeones,
    TextField txtBuscarCampeon,
    TableColumn<Campeon, String> colNombreCampeon,
    TableColumn<Campeon, String> colDescripcionCampeon,
    TableColumn<Campeon, String> colRolCampeon,
    TableColumn<Campeon, String> colDificultadCampeon,
    TableColumn<Campeon, Void> columnaAccionesCampeones
    
    ){
    this.tablaCampeones = tablaCampeones;
    this.txtBuscarCampeon = txtBuscarCampeon;
    this.colNombreCampeon = colNombreCampeon;
    this.colDescripcionCampeon = colDescripcionCampeon;
    this.colRolCampeon = colRolCampeon;
    this.colDificultadCampeon = colDificultadCampeon;
    this.columnaAccionesCampeones = columnaAccionesCampeones;

    try {
        this.connection = DataBaseMain.getConnection();
    } catch (SQLException e) {
        e.printStackTrace();
    }

    initialize();
    }
    
    private void cargarCampeones() {
    CampeonDAO campeonDAO = new CampeonDAO();
    
    campeonesList.clear();
    listaOriginalCampeones.clear();
    campeonesList.addAll(campeonDAO.obtenerTodos(connection));
    
    listaOriginalCampeones.setAll(campeonesList);
    tablaCampeones.setItems(listaOriginalCampeones);
    tablaCampeones.refresh();
}
    
     private void buscarCampeon(String filtro) {
    if (filtro == null || filtro.trim().isEmpty()) {
        tablaCampeones.setItems(FXCollections.observableArrayList(listaOriginalCampeones));
    } else {
        ObservableList<Campeon> campeonesFiltrados = FXCollections.observableArrayList(
            listaOriginalCampeones.stream()
                .filter(campeon -> campeon.getNombre().toLowerCase().contains(filtro.toLowerCase()))
                .collect(Collectors.toList())
        );

        tablaCampeones.setItems(campeonesFiltrados);
    }
    tablaCampeones.refresh();
}
    
private void mostrarAlertaFilaMarcadaEditarCampeon(Campeon campeonMarcado) {
    Alert alerta = new Alert(Alert.AlertType.WARNING);
    alerta.setTitle("Edición restringida");
    alerta.setHeaderText("Ya hay una fila marcada");
    alerta.setContentText(" Tienes marcada la fila de \"" + campeonMarcado.getNombre() + "\". \n"
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


private void mostrarAlertaFilaMarcadaBorrarCampeon(Campeon campeonMarcado) {
    Alert alerta = new Alert(Alert.AlertType.WARNING);
    alerta.setTitle("Borrado restringido");
    alerta.setHeaderText("Ya hay una fila marcada");
    alerta.setContentText(" Tienes marcada la fila de \"" + campeonMarcado.getNombre() + "\". \n"
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


    private void editarCampeon(Campeon campeon) {
    if (campeon == null) {
        AlertUtils.mostrarAlerta("Error", "No se ha seleccionado ningún campeón para editar.", Alert.AlertType.WARNING);
        return;
    }
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/EditarCampeon.fxml"));
        Parent root = loader.load();

        EditarControllerCampeon controladorEdicion = loader.getController();
        controladorEdicion.setCampeon(campeon);
        controladorEdicion.setTablaCampeones(tablaCampeones);
        controladorEdicion.setConnection(connection);

        Stage stage = new Stage();
        stage.setTitle("Editar Campeón");
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/estilos/editarCampeon.css").toExternalForm());

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
}  private void alertaEliminar(Alert alerta) {
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
    
    private void eliminarCampeon(Campeon campeon) {
    if (campeon == null) {
        AlertUtils.mostrarAlerta("Selección requerida", "Por favor, selecciona un campeón antes de eliminar.", Alert.AlertType.WARNING);
        return;
    }

    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText(null);
    confirmacion.setContentText("¿Estás seguro de que deseas eliminar a " + campeon.getNombre() + "?");

    alertaEliminar(confirmacion);

    Optional<ButtonType> resultado = confirmacion.showAndWait();

    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
        CampeonDAO campeonDAO = new CampeonDAO();
        boolean eliminado = campeonDAO.eliminar(campeon.getId(), connection);

        if (eliminado) {
            campeonesList.remove(campeon);
            listaOriginalCampeones.remove(campeon);
            tablaCampeones.getSelectionModel().clearSelection();
            tablaCampeones.setItems(listaOriginalCampeones);
            tablaCampeones.refresh();
            AlertUtils.mostrarAlerta("Éxito", "El campeón ha sido eliminado correctamente.", Alert.AlertType.INFORMATION);
        } else {
            AlertUtils.mostrarAlerta("Error", "No se encontró el campeón en la base de datos.", Alert.AlertType.ERROR);
        }
    }
}

private void configurarColumnaAccionesCampeones() {
    Callback<TableColumn<Campeon, Void>, TableCell<Campeon, Void>> cellFactory = param -> new TableCell<>() {
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
                Campeon campeonSeleccionado = getTableView().getItems().get(getIndex());

                if (campeonSeleccionado != null) {
                    Campeon filaMarcada = tablaCampeones.getSelectionModel().getSelectedItem();

                    if (filaMarcada != null && !filaMarcada.equals(campeonSeleccionado)) {
                        mostrarAlertaFilaMarcadaEditarCampeon(filaMarcada);
                        return;
                    }

                    editarCampeon(campeonSeleccionado);
                }
            });

            // Configurar ícono del botón Eliminar
            ImageView iconoEliminar = new ImageView(new Image(getClass().getResourceAsStream("/icons/papelera.png")));
            iconoEliminar.setFitWidth(16);
            iconoEliminar.setFitHeight(16);
            btnEliminar.setGraphic(iconoEliminar);

            btnEliminar.setOnAction(event -> {
                Campeon campeonSeleccionado = getTableView().getItems().get(getIndex());

                if (campeonSeleccionado != null) {
                    Campeon filaMarcada = tablaCampeones.getSelectionModel().getSelectedItem();

                if (filaMarcada != null && !filaMarcada.equals(campeonSeleccionado)) {
                    mostrarAlertaFilaMarcadaBorrarCampeon(filaMarcada);
                    return;
                }

                eliminarCampeon(campeonSeleccionado);
                }
            });

            btnEditar.setTooltip(TooltipUtils.crear("Editar Campeon"));
            btnEliminar.setTooltip(TooltipUtils.crear("Eliminar Campeon"));
            
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

    columnaAccionesCampeones.setCellFactory(cellFactory);
}


    @FXML
    public void abrirFiltroCampeones() {
    try {   
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/ListaFiltrosCampeones.fxml"));
        Parent root = loader.load();

        ListaControllerCampeones controlador = loader.getController();
        
        controlador.setTablaCampeones(tablaCampeones);
        controlador.setListaOriginal(listaOriginalCampeones);
        
        this.listaControllerCampeones = controlador;

        Stage stage = new Stage();
        stage.setTitle("Filtrar Campeones");
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/estilos/filtroCampeon.css").toExternalForm());

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

    @FXML
public void borrarFiltroCampeones() {
    if (listaControllerCampeones == null) {
        AlertUtils.mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
        return; 
    }

    listaControllerCampeones.borrarFiltrosCampeones();
}

    @FXML
public void abrirBtnAnadirCampeon() throws IOException {
    try {   
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/AddCampeon.fxml"));
        Parent root = loader.load();

        AddControllerCampeon controlador = loader.getController();
        controlador.setListaOriginalCampeones(listaOriginalCampeones);
        controlador.setListaYTablaCampeones(campeonesList, tablaCampeones);
        controlador.setConnection(connection);

        Stage stage = new Stage();
        stage.setTitle("Añadir Campeón");
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/estilos/anadirCampeon.css").toExternalForm());

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

        if (controlador.isCampeonAgregado()) {
            tablaCampeones.refresh();
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}
    
    
}
