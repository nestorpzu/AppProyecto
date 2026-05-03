package com.example.appProyecto;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.util.Callback;
import jugadores.Jugador;
import campeones.Campeon;
import partidas.Partida;
import jugadores.ListaControllerJugadores;
import campeones.ListaControllerCampeones;
import partidas.ListaControllerPartida;
import jugadores.EditarControllerJugador;
import campeones.AddControllerCampeon;
import campeones.EditarControllerCampeon;
import jugadores.AddControllerJugador;
import partidas.EditarControllerPartida;
import partidas.AddControllerPartida;


public class Controller {

   // -------------------- CONTROLADORES SECUNDARIOS --------------------
    private ListaControllerJugadores listaControllerJugadores;
    private ListaControllerCampeones listaControllerCampeones;
    private ListaControllerPartida listaControllerPartida;

    // -------------------- ELEMENTOS FXML - JUGADORES --------------------
    @FXML private TableView<Jugador> tablaJugadores;
    @FXML private TextField txtBuscarJugador;
    @FXML private TableColumn<Jugador, String> colNombreJugador;
    @FXML private TableColumn<Jugador, String> colDescripcionJugador;
    @FXML private TableColumn<Jugador, Integer> colEdadJugador;
    @FXML private TableColumn<Jugador, String> colEmailJugador;
    @FXML private TableColumn<Jugador, String> colNacionalidadJugador;
    @FXML private TableColumn<Jugador, String> colPosicionJugador;
    @FXML private TableColumn<Jugador, Void> columnaAcciones1;
   

    // -------------------- ELEMENTOS FXML - CAMPEONES --------------------
    @FXML private TableView<Campeon> tablaCampeones;
    @FXML private TextField txtBuscarCampeon;
    @FXML private TableColumn<Campeon, String> colNombreCampeon;
    @FXML private TableColumn<Campeon, String> colDescripcionCampeon;
    @FXML private TableColumn<Campeon, String> colRolCampeon;
    @FXML private TableColumn<Campeon, String> colDificultadCampeon;
    @FXML private TableColumn<Campeon, Void> columnaAccionesCampeones;
   

    // -------------------- ELEMENTOS FXML - PARTIDAS --------------------
    @FXML private TableView<Partida> tablaPartidas;
    @FXML private TextField txtBuscarPartida;
    @FXML private TableColumn<Partida, String> colJugadorPartida;
    @FXML private TableColumn<Partida, String> colCampeonesPartida;
    @FXML private TableColumn<Partida, LocalDate> colFechaPartida;
    @FXML private TableColumn<Partida, String> colKDA;
    @FXML private TableColumn<Partida, String> colResultadoPartida;
    @FXML private TableColumn<Partida, Void> columnaAccionesPartida;

    // -------------------- BOTONES DE ACCIÓN --------------------
    @FXML private Button btnListaFiltros;
    @FXML private Button btnBorrar;
    @FXML private Button btnAnadir;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    // -------------------- LISTAS DE DATOS --------------------
    private ObservableList<Jugador> jugadoresList = FXCollections.observableArrayList();
    private ObservableList<Campeon> campeonesList = FXCollections.observableArrayList();
    private ObservableList<Partida> partidasList = FXCollections.observableArrayList();
    private ObservableList<Jugador> listaOriginal = FXCollections.observableArrayList();
    private ObservableList<Campeon> listaOriginalCampeones = FXCollections.observableArrayList();
    private ObservableList<Partida> listaOriginalPartidas = FXCollections.observableArrayList();

    // -------------------- BANDERAS --------------------
    private boolean jugadoresCargados = false;

    

    
    @FXML
 public void initialize() {
     // Cargar jugadores desde la base de datos
     cargarJugadores();
     cargarCampeones();
     cargarPartidas();

     // Configurar columnas de la tabla de jugadores
     colNombreJugador.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
     colDescripcionJugador.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
     colEdadJugador.setCellValueFactory(cellData -> cellData.getValue().edadProperty().asObject());
     colEmailJugador.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
     colNacionalidadJugador.setCellValueFactory(cellData -> cellData.getValue().nacionalidadProperty());
     colPosicionJugador.setCellValueFactory(cellData -> cellData.getValue().posicionProperty());

     // Configurar la tabla de campeones
     colNombreCampeon.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
     colDescripcionCampeon.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
     colRolCampeon.setCellValueFactory(cellData -> cellData.getValue().rolProperty());
     colDificultadCampeon.setCellValueFactory(cellData -> cellData.getValue().dificultadProperty());

     // Configurar columnas de la tabla de partidas
     colJugadorPartida.setCellValueFactory(cellData -> cellData.getValue().jugadorProperty());
     colCampeonesPartida.setCellValueFactory(cellData -> cellData.getValue().campeonProperty());
     colFechaPartida.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFecha())); 
     colKDA.setCellValueFactory(cellData -> cellData.getValue().kdaProperty());
     colResultadoPartida.setCellValueFactory(cellData -> cellData.getValue().resultadoProperty());




     txtBuscarCampeon.textProperty().addListener((observable, oldValue, newValue) -> buscarCampeon(newValue));
     txtBuscarJugador.textProperty().addListener((observable, oldValue, newValue) -> buscarJugador(newValue));
     txtBuscarPartida.textProperty().addListener((observable, oldValue, newValue) -> buscarPartida(newValue));

     // Evitar que se puedan seleccionar varias filas a la vez en Campeones
     tablaCampeones.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
         if (newSelection != null) {
             newSelection.setSeleccionado(true);
             for (Campeon campeon : campeonesList) {
                 if (campeon != newSelection) {
                     campeon.setSeleccionado(false);
                 }
             }
         }
         tablaCampeones.refresh();
     });

     // Evitar que se puedan seleccionar varias filas a la vez en Jugadores
     tablaJugadores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
         if (newSelection != null) {
             newSelection.setSeleccionado(true);
             for (Jugador jugador : jugadoresList) {
                 if (jugador != newSelection) {
                     jugador.setSeleccionado(false);
                 }
             }
         }
         tablaJugadores.refresh();
     });

        // Evitar que se puedan seleccionar varias filas a la vez en Partidas
   tablaPartidas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
       if (newSelection != null) {
           newSelection.setSeleccionado(true);
           for (Partida partida : tablaPartidas.getItems()) {
               if (partida != newSelection) {
                   partida.setSeleccionado(false);
               }
           }
       }
       tablaPartidas.refresh();
   });

     // Asignar la lista original a la tabla si está disponible
     if (listaOriginal != null && !listaOriginal.isEmpty()) {
         tablaJugadores.setItems(listaOriginal);
     } else {
         System.out.println("listaOriginal está vacía o no se pudo cargar.");
     }

     // Configurar la columna de acciones en la tabla
     configurarColumnaAcciones();
      // Configurar la columna de acciones para Campeones
     configurarColumnaAccionesCampeones();
     configurarColumnaAccionesPartida();   
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
                    // Verificar si hay una fila ya marcada y es distinta a la que queremos editar
                    Optional<Campeon> filaMarcada = tablaCampeones.getItems().stream()
                            .filter(Campeon::isSeleccionado)
                            .findFirst();

                    if (filaMarcada.isPresent() && !filaMarcada.get().equals(campeonSeleccionado)) {
                        // Mostrar la alerta y salir sin abrir la edición
                        mostrarAlertaFilaMarcadaEditarCampeon(filaMarcada.get());
                        return;
                    }

                    // Si no hay otra fila marcada o es la misma, abrir la edición
                    editarCampeonC(campeonSeleccionado);
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
                    // Verificar si hay una fila ya marcada y es distinta a la que queremos eliminar
                    Optional<Campeon> filaMarcada = tablaCampeones.getItems().stream()
                            .filter(Campeon::isSeleccionado)
                            .findFirst();

                    if (filaMarcada.isPresent() && !filaMarcada.get().equals(campeonSeleccionado)) {
                        // Mostrar la alerta y salir sin eliminar
                        mostrarAlertaFilaMarcadaBorrarCampeon(filaMarcada.get());
                        return;
                    }

                    // Si no hay otra fila marcada o es la misma, proceder con la eliminación
                    eliminarCampeon(campeonSeleccionado);
                }
            });

            contenedor.getChildren().addAll(btnEditar, btnEliminar);
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
                    // Verificar si hay una fila ya marcada y es distinta a la que queremos editar
                    Optional<Partida> filaMarcada = tablaPartidas.getItems().stream()
                            .filter(Partida::isSeleccionado)
                            .findFirst();

                    if (filaMarcada.isPresent() && !filaMarcada.get().equals(partidaSeleccionada)) {
                        // Mostrar alerta y salir sin abrir la edición
                        mostrarAlertaFilaMarcadaEditarPartida(filaMarcada.get());
                        return;
                    }

                    // Si no hay otra fila marcada o es la misma, abrir la edición
                    editarPartida(partidaSeleccionada);
                }
            });

            // Configurar ícono del botón Eliminar
            ImageView iconoEliminar = new ImageView(new Image(getClass().getResourceAsStream("/icons/papelera.png")));
            iconoEliminar.setFitWidth(16);
            iconoEliminar.setFitHeight(16);
            btnEliminar.setGraphic(iconoEliminar);

            btnEliminar.setOnAction(event -> {
                Partida partidaSeleccionada = getTableView().getItems().get(getIndex());

                if (partidaSeleccionada != null) {
                    // Verificar si hay una fila ya marcada y es distinta a la que queremos eliminar
                    Optional<Partida> filaMarcada = tablaPartidas.getItems().stream()
                            .filter(Partida::isSeleccionado)
                            .findFirst();

                    if (filaMarcada.isPresent() && !filaMarcada.get().equals(partidaSeleccionada)) {
                        // Mostrar alerta y salir sin borrar la partida
                        mostrarAlertaFilaMarcadaBorrarPartida(filaMarcada.get());
                        return;
                    }

                    // Si no hay otra fila marcada o es la misma, eliminar la partida
                    eliminarPartida(partidaSeleccionada);
                }
            });

            contenedor.getChildren().addAll(btnEditar, btnEliminar);
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


private void editarPartida(Partida partida) {
    if (partida == null) {
        mostrarAlerta("Error", "No se ha seleccionado ninguna partida para editar.", Alert.AlertType.WARNING);
        return;
    }
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/EditarPartida.fxml"));
        Parent root = loader.load();

        EditarControllerPartida controladorEdicion = loader.getController();
        controladorEdicion.setPartida(partida);
        controladorEdicion.setTablaPartidas(tablaPartidas);

        Stage stage = new Stage();
        stage.setTitle("Editar Partida");
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/estilos/editarPartida.css").toExternalForm());

        stage.setScene(scene);

        stage.setWidth(350);
        stage.setHeight(470);


        // Centrar ventana
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        System.out.println("Partida editada: " + partida.getJugador());

    } catch (IOException e) {
        e.printStackTrace();
    }
}

private void eliminarPartida(Partida partida) {
    if (partida == null) {
        mostrarAlerta("Selección requerida", "Por favor, selecciona una partida antes de eliminar.", Alert.AlertType.WARNING);
        return;
    }

    int idJuegan = partida.getIdJuegan(); // ✅ Obtener el ID único de la partida

    if (idJuegan == -1) {
        mostrarAlerta("Error", "No se encontró la partida en la base de datos.", Alert.AlertType.ERROR);
        return;
    }

    // ✅ Confirmar eliminación
    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText(null);
    confirmacion.setContentText("¿Estás seguro de que deseas eliminar esta partida?\n\n");

    AlertaEliminar(confirmacion); // Centrar la alerta en la pantalla
    Optional<ButtonType> resultado = confirmacion.showAndWait();

    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM juegan WHERE ID_juegan = ?")) {

            stmt.setInt(1, idJuegan);

            int filasEliminadas = stmt.executeUpdate();

            if (filasEliminadas > 0) {
                    partidasList.remove(partida);  //Quita de la lista
                    listaOriginalPartidas.remove(partida); //Quita de la lista original
                    tablaPartidas.getSelectionModel().clearSelection();
                    tablaPartidas.setItems(FXCollections.observableArrayList(listaOriginalPartidas)); 
                    tablaPartidas.refresh();   
                
                mostrarAlerta("Éxito", "La partida ha sido eliminada correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se encontró la partida en la base de datos.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al eliminar la partida: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}



private int obtenerIDJugador(String nombreJugador) throws SQLException {
    String query = "SELECT idJugadores FROM jugadores WHERE nombre_jugador = ?";
    try (Connection connection = baseDatos.DataBaseMain.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        preparedStatement.setString(1, nombreJugador);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("idJugadores");
        } else {
            throw new SQLException("Jugador no encontrado.");
        }
    }
}

private int obtenerIDCampeon(String nombreCampeon) throws SQLException {
    String query = "SELECT idCampeones FROM campeones WHERE nombre_campeon = ?";
    try (Connection connection = baseDatos.DataBaseMain.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        preparedStatement.setString(1, nombreCampeon);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("idCampeones");
        } else {
            throw new SQLException("Campeón no encontrado.");
        }
    }
}


// Este es el setter para asignar la referencia
public void setListaControllerJugadores(ListaControllerJugadores listaController) {
    this.listaControllerJugadores = listaController;
}

// Método para editar un campeón
private void editarCampeonC(Campeon campeon) {
    if (campeon == null) {
        mostrarAlerta("Error", "No se ha seleccionado ningún campeón para editar.", Alert.AlertType.WARNING);
        return;
    }
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/EditarCampeon.fxml"));
        Parent root = loader.load();

        EditarControllerCampeon controladorEdicion = loader.getController();
        controladorEdicion.setCampeon(campeon);
        controladorEdicion.setTablaCampeones(tablaCampeones);

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

        System.out.println("Campeón editado: " + campeon.getNombre());
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    
private void eliminarCampeon(Campeon campeon) {
    if (campeon == null) {
        mostrarAlerta("Selección requerida", "Por favor, selecciona un campeón antes de eliminar.", Alert.AlertType.WARNING);
        return;
    }

    // Confirmar eliminación
    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText(null);
    confirmacion.setContentText("¿Estás seguro de que deseas eliminar a " + campeon.getNombre() + "?");

    // Centrar la alerta en la pantalla
    AlertaEliminar(confirmacion);

    // Mostrar la alerta y esperar la respuesta del usuario
    Optional<ButtonType> resultado = confirmacion.showAndWait();

    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
        // Intentar eliminar de la base de datos
        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM campeones WHERE idCampeones = ?")) {
            preparedStatement.setInt(1, campeon.getId());
            int filasEliminadas = preparedStatement.executeUpdate();

            if (filasEliminadas > 0) {
                System.out.println("Campeón eliminado de la base de datos correctamente.");

                    campeonesList.remove(campeon);
                    listaOriginalCampeones.remove(campeon);
                    tablaCampeones.getSelectionModel().clearSelection();
                    tablaCampeones.setItems(FXCollections.observableArrayList(campeonesList)); // ⚠️ Forzar actualización
                    tablaCampeones.refresh();
                

                mostrarAlerta("Éxito", "El campeón ha sido eliminado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se encontró el campeón en la base de datos.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al eliminar el campeón de la base de datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
@FXML
private void borrarFiltros1() {
    if (listaControllerJugadores == null) {
        System.out.println("⚠️ No se ha asignado el controlador de la lista de jugadores.");
        mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
        return; 
    }

    System.out.println("🔄 Llamando a borrarFiltros en Jugadores...");
    listaControllerJugadores.borrarFiltros();
}

@FXML
private void borrarFiltros2() {
    if (listaControllerCampeones == null) {
        System.out.println("⚠️ No se ha asignado el controlador de la lista de campeones.");
        mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
        return; 
    }

    System.out.println("🔄 Llamando a borrarFiltrosCampeones...");
    listaControllerCampeones.borrarFiltrosCampeones();
}

    
       @FXML
private void borrarFiltros3() {
    if (listaControllerPartida == null) {
        System.out.println("⚠️ No se ha asignado el controlador de la lista de partidas.");
        mostrarAlerta("Filtros no aplicados", "No hay filtros activos para borrar.", Alert.AlertType.INFORMATION);
        return; 
    }

    System.out.println("🔄 Llamando a borrarFiltrosPartida...");
    listaControllerPartida.borrarFiltrosPartida();
}



    
    private void cargarJugadores() {
    String query = "SELECT * FROM jugadores"; 
    try (Connection connection = baseDatos.DataBaseMain.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query);
         ResultSet resultSet = preparedStatement.executeQuery()) {

        jugadoresList.clear(); // Asegúrate de limpiar la lista antes de cargar nuevos datos
        listaOriginal.clear();

        while (resultSet.next()) {
            int id = resultSet.getInt("idJugadores");
            String nombre = resultSet.getString("nombre_jugador");
            String descripcion = resultSet.getString("descripcion_jugador");
            int edad = resultSet.getInt("edad");
            String email = resultSet.getString("email");
            String nacionalidad = resultSet.getString("nacionalidad");
            String posicion = resultSet.getString("posicion_jugador");

            Jugador jugador = new Jugador(id, nombre, descripcion, edad, email, nacionalidad, posicion, false);
            jugadoresList.add(jugador);
        }
        
      

        listaOriginal = FXCollections.observableArrayList(jugadoresList);
        tablaJugadores.setItems(listaOriginal); // Establece los datos iniciales en la tabla
        tablaJugadores.refresh(); // Refrescar la tabla para ver la fila de estado
        
        System.out.println("Datos iniciales cargados en listaOriginal: " + listaOriginal);

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    // Método para cargar los campeones desde la base de datos
    private void cargarCampeones() {
    String query = "SELECT * FROM campeones"; 
    try (Connection connection = baseDatos.DataBaseMain.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query);
         ResultSet resultSet = preparedStatement.executeQuery()) {

        campeonesList.clear(); // Limpia la lista antes de agregar nuevos datos
        listaOriginalCampeones.clear(); // También limpia la lista original

        while (resultSet.next()) {
            int id = resultSet.getInt("idCampeones");
            String nombre = resultSet.getString("nombre_campeon");
            String descripcion = resultSet.getString("descripcion_campeon");
            String rol = resultSet.getString("rol_mapa");
            String dificultad = resultSet.getString("dificultad");

            Campeon campeon = new Campeon(id, nombre, descripcion, rol, dificultad, false);
            campeonesList.add(campeon);
            listaOriginalCampeones.add(campeon);
        }

        
        tablaCampeones.setItems(campeonesList); // Asigna la lista a la tabla
        tablaCampeones.refresh();

        System.out.println("📌 Datos cargados en listaOriginalCampeones: " + listaOriginalCampeones);

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    // Método para cargar las partidas desde la base de datos
    private void cargarPartidas() {
        if (listaOriginalPartidas == null) {
            listaOriginalPartidas = FXCollections.observableArrayList();
        } else {
            listaOriginalPartidas.clear(); 
        }

    tablaPartidas.getItems().clear(); // Limpiar la tabla antes de cargar nuevas partidas

    String query = "SELECT ID_juegan, ID_Jugador, ID_Campeon, Fecha_jugada, KDA, Resultado FROM juegan";

    try (Connection connection = baseDatos.DataBaseMain.getConnection();
         PreparedStatement stmt = connection.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
        //Lee los ids
        while (rs.next()) {
            int idJuegan = rs.getInt("ID_juegan");
            String nombreJugador = (rs.getObject("ID_Jugador") != null) 
                ? obtenerNombreJugador(rs.getInt("ID_Jugador")) 
                : "Desconocido";
            
            String nombreCampeon = (rs.getObject("ID_Campeon") != null) 
                ? obtenerNombreCampeon(rs.getInt("ID_Campeon")) 
                : "Sin Campeón";

            LocalDate fecha = (rs.getDate("Fecha_jugada") != null) 
                ? rs.getDate("Fecha_jugada").toLocalDate() 
                : null;

            String kda = rs.getString("KDA") != null ? rs.getString("KDA") : "0/0/0";
            String resultado = rs.getString("Resultado") != null ? rs.getString("Resultado") : "Desconocido";

            // Crear la instancia de Partida
            Partida partida = new Partida(idJuegan, nombreJugador, nombreCampeon, fecha, kda, resultado);

            listaOriginalPartidas.add(partida); // 🔹 Agregar a la lista original correctamente
            tablaPartidas.getItems().add(partida);

            System.out.println("✅ Partida cargada: " + partida);
        }

        // 🔹 Verificar si listaOriginalPartidas se actualizó correctamente
        System.out.println("📌 listaOriginalPartidas contiene " + listaOriginalPartidas.size() + " partidas.");
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


private String obtenerNombreJugador(int idJugador) {
    String query = "SELECT nombre_jugador FROM jugadores WHERE idJugadores = ?"; // <-- Asegúrate del nombre correcto
    try (Connection connection = baseDatos.DataBaseMain.getConnection();
         PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, idJugador);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("nombre_jugador");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return "Desconocido"; 
}

private String obtenerNombreCampeon(int idCampeon) {
    String query = "SELECT nombre_campeon FROM campeones WHERE idCampeones = ?"; // <-- Asegúrate del nombre correcto
    try (Connection connection = baseDatos.DataBaseMain.getConnection();
         PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, idCampeon);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("nombre_campeon");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return "Desconocido"; 
}

    
    //ListaFiltrosJugadores
    @FXML
    private void abrirListaDeFiltros1() {
        try {   
            // Cargar el archivo FXML de la ventana de Lista de Filtros
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/ListaFiltrosJugadores.fxml"));
            Parent root = loader.load();
             // Obtener el controlador de la ventana emergente
            ListaControllerJugadores controlador = loader.getController();
              // Pasa las referencias necesarias al controlador de la ventana emergente
        controlador.setTablaJugadores(tablaJugadores); // Pasa la tabla correctamente
        controlador.setListaOriginal(listaOriginal);  // Pasa listaOriginal

         // Guardar la referencia al controlador
        this.listaControllerJugadores = controlador;
        if (tablaJugadores == null || listaOriginal == null || listaOriginal.isEmpty()) {
            System.out.println("Error: tablaJugadores o listaOriginal no están configuradas correctamente.");
        }

            // Configurar la ventana modal
            Stage stage = new Stage();
            stage.setTitle("Filtrar Jugadores");

            // ANTES: stage.setScene(new Scene(root));
            Scene scene = new Scene(root);

           
            scene.getStylesheets().add(getClass().getResource("/estilos/filtroJugador.css").toExternalForm()); 

            stage.setScene(scene);

            stage.setTitle("Filtrar Jugadores");
            
            stage.setWidth(350); // Ancho de la ventana (ajústalo a tus necesidades)
            stage.setHeight(520); // Altura de la ventana (ajústalo a tus necesidades)

            // Obtener las dimensiones de la pantalla
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // Calcular la posición centrada después de que el tamaño del Stage se haya inicializado
            double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
            double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;

            // Configurar la posición centrada
            stage.setX(centerX);
            stage.setY(centerY);

            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal hasta que se cierre
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    private void editarJugadorC(Jugador jugador) {
    if (jugador == null) {
        mostrarAlerta("Error", "No se ha seleccionado ningún jugador para editar.", Alert.AlertType.WARNING);
        return;
    }
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/EditarJugador.fxml"));
        Parent root = loader.load();

        // Obtener el controlador y pasarle el jugador
        EditarControllerJugador controladorEdicion = loader.getController();
        controladorEdicion.setJugador(jugador); // Método para pasar el jugador
        controladorEdicion.setTablaJugadores(tablaJugadores); 

        // Configurar la ventana modal
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

            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal hasta que se cierre
            stage.showAndWait();
             System.out.println("Jugador editado: " + jugador.getNombre());
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
    
    // Método para buscar jugadores en la lista
    private void buscarJugador(String filtro) {
    if (filtro == null || filtro.isEmpty()) {
        // Si el campo de búsqueda está vacío, mostrar todos los jugadores
        tablaJugadores.setItems(FXCollections.observableArrayList(listaOriginal));
    } else {
        // Filtrar jugadores cuyo nombre contenga el texto del filtro
        ObservableList<Jugador> jugadoresFiltrados = FXCollections.observableArrayList(
            listaOriginal.stream()
                .filter(jugador -> jugador.getNombre().toLowerCase().contains(filtro.toLowerCase()))
                .collect(Collectors.toList())
        );
        tablaJugadores.setItems(jugadoresFiltrados);
    }
    tablaJugadores.refresh();
}
    
 // Método para buscar campeones en la lista
private void buscarCampeon(String filtro) {
    if (filtro == null || filtro.trim().isEmpty()) {
        // Si el campo de búsqueda está vacío, mostrar todos los campeones originales
        tablaCampeones.setItems(FXCollections.observableArrayList(listaOriginalCampeones));
    } else {
        // Filtrar campeones cuyo nombre contenga el texto del filtro
        ObservableList<Campeon> campeonesFiltrados = FXCollections.observableArrayList(
            listaOriginalCampeones.stream()
                .filter(campeon -> campeon.getNombre().toLowerCase().contains(filtro.toLowerCase()))
                .collect(Collectors.toList())
        );

        tablaCampeones.setItems(campeonesFiltrados);
    }
    tablaCampeones.refresh();
}
    
    //Método para buscar partidas en la lista
// Método para buscar jugadores en la lista
    private void buscarPartida(String filtro) {
    if (filtro == null || filtro.isEmpty()) {
        // Si el campo de búsqueda está vacío, mostrar todas las partidas
        tablaPartidas.setItems(FXCollections.observableArrayList(listaOriginalPartidas));
    } else {
        // Filtrar partidas cuyo nombre contenga el texto del filtro
        ObservableList<Partida> partidasFiltradas = FXCollections.observableArrayList(
            listaOriginalPartidas.stream()
                .filter(Partida -> Partida.getJugador().toLowerCase().contains(filtro.toLowerCase()))
                .collect(Collectors.toList())
        );
        tablaPartidas.setItems(partidasFiltradas);
    }
    tablaPartidas.refresh();
}
    
    // Método para configurar la columna de acciones
private void configurarColumnaAcciones() {
    Callback<TableColumn<Jugador, Void>, TableCell<Jugador, Void>> cellFactory = param -> new TableCell<>() {
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
                Jugador jugadorSeleccionado = getTableView().getItems().get(getIndex());

                if (jugadorSeleccionado != null) {
                    // Verificar si hay una fila ya marcada y es distinta a la que queremos editar
                    Optional<Jugador> filaMarcada = tablaJugadores.getItems().stream()
                            .filter(Jugador::isSeleccionado)
                            .findFirst();

                    if (filaMarcada.isPresent() && !filaMarcada.get().equals(jugadorSeleccionado)) {
                        // Mostrar la alerta y salir sin abrir la edición
                        mostrarAlertaFilaMarcadaEditar(filaMarcada.get());
                        return;
                    }

                    // Si no hay otra fila marcada o es la misma, abrir la edición
                    editarJugadorC(jugadorSeleccionado);
                }
            });

            // Configurar ícono del botón Eliminar
            ImageView iconoEliminar = new ImageView(new Image(getClass().getResourceAsStream("/icons/papelera.png")));
            iconoEliminar.setFitWidth(16);
            iconoEliminar.setFitHeight(16);
            btnEliminar.setGraphic(iconoEliminar);
            
            
            btnEliminar.setOnAction(event -> {
                Jugador jugadorSeleccionado = getTableView().getItems().get(getIndex());

                if (jugadorSeleccionado != null) {
                    // Verificar si hay una fila ya marcada y es distinta a la que queremos editar
                    Optional<Jugador> filaMarcada = tablaJugadores.getItems().stream()
                            .filter(Jugador::isSeleccionado)
                            .findFirst();

                    if (filaMarcada.isPresent() && !filaMarcada.get().equals(jugadorSeleccionado)) {
                        // Mostrar la alerta y salir sin abrir la edición
                        mostrarAlertaFilaMarcadaBorrar(filaMarcada.get());
                        return;
                    }

                    // Si no hay otra fila marcada o es la misma, abrir la edición
                    eliminarJugador(jugadorSeleccionado);
                }
            });


            contenedor.getChildren().addAll(btnEditar, btnEliminar);
            contenedor.setSpacing(10);
        }

         @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) { // Ocultar la columna de acciones en la primera fila
                setGraphic(null);
            } else {
                setGraphic(contenedor);
            }
        }
    };

    columnaAcciones1.setCellFactory(cellFactory);
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

  private void eliminarJugador(Jugador jugador) {
    if (jugador == null) {
        mostrarAlerta("Selección requerida", "Por favor, selecciona un jugador antes de eliminar.", Alert.AlertType.WARNING);
        return;
    }

    // Confirmar eliminación
    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText(null);
    confirmacion.setContentText("¿Estás seguro de que deseas eliminar a " + jugador.getNombre() + "?");

    // Centrar la alerta en la pantalla
    AlertaEliminar(confirmacion);

    // Mostrar la alerta y esperar la respuesta del usuario
    Optional<ButtonType> resultado = confirmacion.showAndWait();

    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
        // Intentar eliminar de la base de datos
        try (Connection connection = baseDatos.DataBaseMain.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM jugadores WHERE idJugadores = ?")) {
             preparedStatement.setInt(1, jugador.getId());
            
            int filasEliminadas = preparedStatement.executeUpdate();

            if (filasEliminadas > 0) {
                System.out.println("Jugador eliminado de la base de datos correctamente.");

                    jugadoresList.remove(jugador);
                    listaOriginal.remove(jugador);
                    tablaJugadores.getSelectionModel().clearSelection();
                    tablaJugadores.setItems(FXCollections.observableArrayList(jugadoresList)); // Actualizar el TableView correctamente
                    tablaJugadores.refresh();

                mostrarAlerta("Éxito", "El jugador ha sido eliminado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se encontró el jugador en la base de datos.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {         
            mostrarAlerta("Error", "No se puede borrar: el jugador tiene partidas registradas. "
                    + "Elimina primero la partida o partidas para poder eliminar a este jugador.", Alert.AlertType.ERROR);
        }
    }
}

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
       
        // Centrar la alerta en la pantalla después de mostrarla
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

    private void AlertaEliminar(Alert alerta) {
    // Centrar la alerta en la pantalla después de mostrarla
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
    
    //ListaFiltrosCampeones
   @FXML
    private void abrirListaDeFiltros2() {
    try {   
        // Cargar el archivo FXML de la ventana de Lista de Filtros
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/ListaFiltrosCampeones.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la ventana emergente
        ListaControllerCampeones controlador = loader.getController();
        
        // PASA LOS DATOS CORRECTAMENTE
        controlador.setTablaCampeones(tablaCampeones);
        controlador.setListaOriginal(listaOriginalCampeones);
        
        // Guardar la referencia al controlador
        this.listaControllerCampeones = controlador;

       if (listaOriginalCampeones == null || listaOriginalCampeones.isEmpty()) {
            System.out.println(" listaOriginalCampeones está vacía o no se pudo cargar.");
        } else {
            System.out.println(" listaOriginalCampeones cargada con " + listaOriginalCampeones.size() + " elementos.");
        }

        // Configurar la ventana modal
        Stage stage = new Stage();
        stage.setTitle("Filtrar Campeones");
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/estilos/filtroCampeon.css").toExternalForm());

        stage.setScene(scene);

        stage.setWidth(350);
        stage.setHeight(470);


        // Centrar la ventana en la pantalla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    
    //ListaFiltrosPartida
    
    @FXML
private void abrirListaDeFiltros3() {
    try {   
        // Cargar el archivo FXML de la ventana de Lista de Filtros para Partidas
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/ListaFiltrosPartida.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la ventana emergente
        ListaControllerPartida controlador = loader.getController();

        if (listaOriginalPartidas == null || listaOriginalPartidas.isEmpty()) {
            System.out.println(" Error: listaOriginalPartidas está vacía antes de pasarla al controlador.");
        } else {
            System.out.println(" listaOriginalPartidas tiene " + listaOriginalPartidas.size() + " partidas antes de abrir la ventana de filtros.");
        }

        controlador.setListaOriginal(listaOriginalPartidas);
        controlador.setTablaPartidas(tablaPartidas);

        // Guardar la referencia al controlador
        this.listaControllerPartida = controlador;

       
        // Configurar la ventana modal
        Stage stage = new Stage();
        stage.setTitle("Filtrar Partidas");
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/estilos/filtroPartida.css").toExternalForm());

        stage.setScene(scene);

        stage.setWidth(350);
        stage.setHeight(470);


        // Obtener las dimensiones de la pantalla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Calcular la posición centrada después de que el tamaño del Stage se haya inicializado
        double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
        double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;

        // Configurar la posición centrada
        stage.setX(centerX);
        stage.setY(centerY);

        stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal hasta que se cierre
        stage.showAndWait();

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    
    @FXML
    private void abrirBtnAnadir() throws IOException {
        
     try{   
        // Cargar el archivo FXML de la ventana de Lista de Filtros
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/AddJugador.fxml"));
        Parent root = loader.load();

        AddControllerJugador controlador = loader.getController();
        controlador.setListaOriginal(listaOriginal);
        controlador.setListaYTablaJugadores(jugadoresList, tablaJugadores);
        
        // Configurar la ventana modal
        Stage stage = new Stage();
        stage.setTitle("Añadir Jugador");
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/estilos/añadirJugador.css").toExternalForm());

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
        stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal hasta que se cierre
        stage.showAndWait();
        
         // Verificar si se agregó un jugador y refrescar la tabla
        if (controlador.isJugadorAgregado()) {
            tablaJugadores.refresh();
            System.out.println("Nuevo jugador agregado y tabla actualizada.");
        }

        
    }catch (IOException e) {
        e.printStackTrace();
    }
    } 
    
    @FXML
private void abrirBtnAnadirCampeon() throws IOException {
    try {   
        // Cargar el archivo FXML de la ventana de Añadir Campeón
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/AddCampeon.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la nueva ventana
        AddControllerCampeon controlador = loader.getController();
        controlador.setListaOriginalCampeones(listaOriginalCampeones);
        controlador.setListaYTablaCampeones(campeonesList, tablaCampeones);

        // Configurar la ventana modal
        Stage stage = new Stage();
        stage.setTitle("Añadir Campeón");
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/estilos/añadirCampeon.css").toExternalForm());

        stage.setScene(scene);

        stage.setWidth(350);
        stage.setHeight(470);

        // Obtener las dimensiones de la pantalla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Calcular la posición centrada después de que el tamaño del Stage se haya inicializado
        double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
        double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;

        // Configurar la posición centrada
        stage.setX(centerX);
        stage.setY(centerY);
        stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal hasta que se cierre
        stage.showAndWait();

        // Verificar si se agregó un campeón y refrescar la tabla
        if (controlador.isCampeonAgregado()) {
            tablaCampeones.refresh();
            System.out.println("Nuevo campeón agregado y tabla actualizada.");
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}



@FXML
private void abrirBtnAnadirPartida() throws IOException {
    try {   
        // Cargar el archivo FXML de la ventana de Añadir Partida
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/AddPartida.fxml"));
        Parent root = loader.load();

        
        // Obtener el controlador de la nueva ventana
        AddControllerPartida controlador = loader.getController();
        controlador.setListaOriginalPartidas(listaOriginalPartidas);
        controlador.setListaYTablaPartidas(partidasList, tablaPartidas);

        // Configurar la ventana modal
        Stage stage = new Stage();
        stage.setTitle("Añadir Partida");
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/estilos/añadirPartida.css").toExternalForm());

        stage.setScene(scene);

        stage.setWidth(350);
        stage.setHeight(470);


        // Obtener las dimensiones de la pantalla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Calcular la posición centrada después de que el tamaño del Stage se haya inicializado
        double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
        double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;

        // Configurar la posición centrada
        stage.setX(centerX);
        stage.setY(centerY);
        stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal hasta que se cierre
        stage.showAndWait();

        // Verificar si se agregó una partida y refrescar la tabla
        if (controlador.isPartidaAgregada()) {
            tablaPartidas.refresh();
            System.out.println("Nueva partida agregada y tabla actualizada.");
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}
    
    
}
