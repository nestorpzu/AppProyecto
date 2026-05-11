/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.DataBaseMain;
import dao.JugadorDAO;
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
import utils.TooltipUtils;

/**
 *
 * @author npauc
 */
public class JugadorHandler {
    
     private TableView<Jugador> tablaJugadores;
    private TextField txtBuscarJugador;
    private TableColumn<Jugador, String> colNombreJugador;
    private TableColumn<Jugador, String> colDescripcionJugador;
    private TableColumn<Jugador, Integer> colEdadJugador;
    private TableColumn<Jugador, String> colEmailJugador;
    private TableColumn<Jugador, String> colNacionalidadJugador;
    private TableColumn<Jugador, String> colPosicionJugador;
    private TableColumn<Jugador, Void> columnaAccionesJugadores;
    
    private ObservableList<Jugador> jugadoresList = FXCollections.observableArrayList();
    private ObservableList<Jugador> listaOriginal = FXCollections.observableArrayList();
    
    private Connection connection;
    
    private ListaControllerJugadores listaControllerJugadores;
    
    
 public void initialize() {
     cargarJugadores();
     
colNombreJugador.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
     colDescripcionJugador.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
     colEdadJugador.setCellValueFactory(cellData -> cellData.getValue().edadProperty().asObject());
     colEmailJugador.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
     colNacionalidadJugador.setCellValueFactory(cellData -> cellData.getValue().nacionalidadProperty());
     colPosicionJugador.setCellValueFactory(cellData -> cellData.getValue().posicionProperty());
     
     txtBuscarJugador.textProperty().addListener((observable, oldValue, newValue) -> buscarJugador(newValue));
     
     configurarColumnaAccionesJugadores();
          
 }
 
 
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
 
 private void cargarJugadores() {
    JugadorDAO jugadorDAO = new JugadorDAO();
    
    jugadoresList.clear();
    listaOriginal.clear();
    jugadoresList.addAll(jugadorDAO.obtenerTodos(connection));
    
    listaOriginal.setAll(jugadoresList);
    tablaJugadores.setItems(listaOriginal);
    tablaJugadores.refresh();
}
    
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
    
     // Método para configurar la columna de acciones
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

    
    @FXML
public void borrarFiltroJugadores() {
    if (listaControllerJugadores == null) {
        AlertUtils.mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
        return; 
    }

    listaControllerJugadores.borrarFiltros();
}

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
   
}
