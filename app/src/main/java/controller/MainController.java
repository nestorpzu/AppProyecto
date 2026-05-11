package controller;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.util.Duration;
import java.time.LocalDate;
import modelos.Jugador;
import modelos.Campeon;
import modelos.Partida;
import javafx.scene.control.TextField;

public class MainController {
    
    private JugadorHandler jugadorHandler;
    private CampeonHandler campeonHandler;
    private PartidaHandler partidaHandler;

    @FXML private TableView<Jugador> tablaJugadores;
    @FXML private TextField txtBuscarJugador;
    @FXML private TableColumn<Jugador, String> colNombreJugador;
    @FXML private TableColumn<Jugador, String> colDescripcionJugador;
    @FXML private TableColumn<Jugador, Integer> colEdadJugador;
    @FXML private TableColumn<Jugador, String> colEmailJugador;
    @FXML private TableColumn<Jugador, String> colNacionalidadJugador;
    @FXML private TableColumn<Jugador, String> colPosicionJugador;
    @FXML private TableColumn<Jugador, Void> columnaAccionesJugadores;
   
    @FXML private TableView<Campeon> tablaCampeones;
    @FXML private TextField txtBuscarCampeon;
    @FXML private TableColumn<Campeon, String> colNombreCampeon;
    @FXML private TableColumn<Campeon, String> colDescripcionCampeon;
    @FXML private TableColumn<Campeon, String> colRolCampeon;
    @FXML private TableColumn<Campeon, String> colDificultadCampeon;
    @FXML private TableColumn<Campeon, Void> columnaAccionesCampeones;
   
    @FXML private TableView<Partida> tablaPartidas;
    @FXML private TextField txtBuscarPartida;
    @FXML private TableColumn<Partida, String> colJugadorPartida;
    @FXML private TableColumn<Partida, String> colCampeonesPartida;
    @FXML private TableColumn<Partida, LocalDate> colFechaPartida;
    @FXML private TableColumn<Partida, String> colKDA;
    @FXML private TableColumn<Partida, String> colResultadoPartida;
    @FXML private TableColumn<Partida, Void> columnaAccionesPartida;
    
      @FXML private void abrirListaDeFiltros1() { jugadorHandler.abrirFiltroJugadores(); }
    @FXML private void borrarFiltros1() { jugadorHandler.borrarFiltroJugadores(); }
    @FXML private void abrirBtnAnadir() throws IOException { jugadorHandler.abrirBtnAnadir(); }

    @FXML private void abrirListaDeFiltros2() { campeonHandler.abrirFiltroCampeones(); }
    @FXML private void borrarFiltros2() { campeonHandler.borrarFiltroCampeones(); }
    @FXML private void abrirBtnAnadirCampeon() throws IOException { campeonHandler.abrirBtnAnadirCampeon(); }

    @FXML private void abrirListaDeFiltros3() { partidaHandler.abrirFiltroPartidas(); }
    @FXML private void borrarFiltros3() { partidaHandler.borrarFiltroPartidas(); }
    @FXML private void abrirBtnAnadirPartida() throws IOException { partidaHandler.abrirBtnAnadirPartida(); }
    
    @FXML
 public void initialize() {

     jugadorHandler = new JugadorHandler();
     campeonHandler = new CampeonHandler();
     partidaHandler = new PartidaHandler();
     
    jugadorHandler.configurar(
        tablaJugadores, txtBuscarJugador,
        colNombreJugador, colDescripcionJugador,
        colEdadJugador, colEmailJugador,
        colNacionalidadJugador, colPosicionJugador,
        columnaAccionesJugadores
    );
     
     campeonHandler.configurar(
        tablaCampeones,txtBuscarCampeon,
        colNombreCampeon, colDescripcionCampeon, 
        colRolCampeon,colDificultadCampeon,
        columnaAccionesCampeones
     );
     
     partidaHandler.configurar(
        tablaPartidas, txtBuscarPartida, 
        colJugadorPartida, colCampeonesPartida, 
        colFechaPartida, colKDA, 
        colResultadoPartida, columnaAccionesPartida
     );

     animarTabla(tablaJugadores);
     animarTabla(tablaCampeones);
     animarTabla(tablaPartidas);
 }   

    private void animarTabla(TableView<?> tabla) {
        tabla.getItems().addListener((ListChangeListener<Object>) change -> {
            tabla.layout(); 
            for (Node nodo : tabla.lookupAll(".table-row-cell")) {
                FadeTransition ft = new FadeTransition(Duration.millis(500), nodo);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
            }
        });
    }
}
