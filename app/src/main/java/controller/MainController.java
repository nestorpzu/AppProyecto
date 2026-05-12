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
import javafx.scene.control.Button;
import modelos.Jugador;
import modelos.Campeon;
import modelos.Partida;
import javafx.scene.control.TextField;
import utils.TooltipUtils;

/**
 * Controlador principal de la vista Main.fxml.
 * Gestiona las tres tablas de la aplicacion (jugadores, campeones, partidas)
 * mediante sus respectivos Handlers. Se encarga de inicializar los handlers
 * con los componentes FXML y aplicar la animacion de carga a las tablas.
 */
public class MainController {
    
    // Handlers que gestionan la logica de cada tabla
    private JugadorHandler jugadorHandler;
    private CampeonHandler campeonHandler;
    private PartidaHandler partidaHandler;

    // --- Componentes FXML de la tabla de Jugadores ---
    @FXML private TableView<Jugador> tablaJugadores;
    @FXML private TextField txtBuscarJugador;
    @FXML private TableColumn<Jugador, String> colNombreJugador;
    @FXML private TableColumn<Jugador, String> colDescripcionJugador;
    @FXML private TableColumn<Jugador, Integer> colEdadJugador;
    @FXML private TableColumn<Jugador, String> colEmailJugador;
    @FXML private TableColumn<Jugador, String> colNacionalidadJugador;
    @FXML private TableColumn<Jugador, String> colPosicionJugador;
    @FXML private TableColumn<Jugador, Void> columnaAccionesJugadores;
   
    // --- Componentes FXML de la tabla de Campeones ---
    @FXML private TableView<Campeon> tablaCampeones;
    @FXML private TextField txtBuscarCampeon;
    @FXML private TableColumn<Campeon, String> colNombreCampeon;
    @FXML private TableColumn<Campeon, String> colDescripcionCampeon;
    @FXML private TableColumn<Campeon, String> colRolCampeon;
    @FXML private TableColumn<Campeon, String> colDificultadCampeon;
    @FXML private TableColumn<Campeon, Void> columnaAccionesCampeones;
    @FXML private TableColumn<Campeon, Void> colImagenCampeon;
   
    // --- Componentes FXML de la tabla de Partidas ---
    @FXML private TableView<Partida> tablaPartidas;
    @FXML private TextField txtBuscarPartida;
    @FXML private TableColumn<Partida, String> colJugadorPartida;
    @FXML private TableColumn<Partida, String> colCampeonesPartida;
    @FXML private TableColumn<Partida, LocalDate> colFechaPartida;
    @FXML private TableColumn<Partida, String> colKDA;
    @FXML private TableColumn<Partida, String> colResultadoPartida;
    @FXML private TableColumn<Partida, Void> columnaAccionesPartida;
    
      // Metodos FXML que delegan a los handlers
    @FXML private void abrirListaDeFiltros1() { jugadorHandler.abrirFiltroJugadores(); }
    @FXML private void borrarFiltros1() { jugadorHandler.borrarFiltroJugadores(); }
    @FXML private void abrirBtnAnadir() throws IOException { jugadorHandler.abrirBtnAnadir(); }

    @FXML private void abrirListaDeFiltros2() { campeonHandler.abrirFiltroCampeones(); }
    @FXML private void borrarFiltros2() { campeonHandler.borrarFiltroCampeones(); }
    @FXML private void abrirBtnAnadirCampeon() throws IOException { campeonHandler.abrirBtnAnadirCampeon(); }

    @FXML private void abrirListaDeFiltros3() { partidaHandler.abrirFiltroPartidas(); }
    @FXML private void borrarFiltros3() { partidaHandler.borrarFiltroPartidas(); }
    @FXML private void abrirBtnAnadirPartida() throws IOException { partidaHandler.abrirBtnAnadirPartida(); }
    
        // --- Importar / Exportar Jugadores ---
    @FXML private void importarCSVJugadores() { jugadorHandler.importarCSVJugadores(); }
    @FXML private void importarJSONJugadores() { jugadorHandler.importarJSONJugadores(); }
    @FXML private void exportarCSVJugadores() { jugadorHandler.exportarCSVJugadores(); }
    @FXML private void exportarJSONJugadores() { jugadorHandler.exportarJSONJugadores(); }

    // --- Importar / Exportar Campeones ---
    @FXML private void importarCSVCampeones() { campeonHandler.importarCSVCampeones(); }
    @FXML private void importarJSONCampeones() { campeonHandler.importarJSONCampeones(); }
    @FXML private void exportarCSVCampeones() { campeonHandler.exportarCSVCampeones(); }
    @FXML private void exportarJSONCampeones() { campeonHandler.exportarJSONCampeones(); }

    // --- Importar / Exportar Partidas ---
    @FXML private void importarCSVPartidas() { partidaHandler.importarCSVPartidas(); }
    @FXML private void importarJSONPartidas() { partidaHandler.importarJSONPartidas(); }
    @FXML private void exportarCSVPartidas() { partidaHandler.exportarCSVPartidas(); }
    @FXML private void exportarJSONPartidas() { partidaHandler.exportarJSONPartidas(); }
    
    //tooltip css
    
    @FXML private Button btnAnadir;
    @FXML private Button btnListaFiltros;
    @FXML private Button btnBorrar;
    @FXML private Button btnAnadir2;
    @FXML private Button btnListaFiltro2;
    @FXML private Button btnBorrar2;
    @FXML private Button btnAnadir3;
    @FXML private Button btnListaFiltros3;
    @FXML private Button btnBorrar3;
    
    /**
     * Metodo que se ejecuta al cargar la vista FXML. Inicializa los tres handlers
     * (uno por tabla) y les pasa los componentes FXML que necesitan para funcionar.
     * Tambien aplica la animacion de fade-in a las tablas.
     */
    @FXML
 public void initialize() {

     jugadorHandler = new JugadorHandler();
     campeonHandler = new CampeonHandler();
     partidaHandler = new PartidaHandler();
     
    // Pasamos los componentes FXML a cada handler para que los gestione
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
        colImagenCampeon,
        colRolCampeon,colDificultadCampeon, 
        columnaAccionesCampeones
     );
     
     partidaHandler.configurar(
        tablaPartidas, txtBuscarPartida, 
        colJugadorPartida, colCampeonesPartida, 
        colFechaPartida, colKDA, 
        colResultadoPartida, columnaAccionesPartida
     );

     //tooltip css
     
     btnAnadir.setTooltip(TooltipUtils.crear("Añadir nuevo jugador"));
    btnListaFiltros.setTooltip(TooltipUtils.crear("Abrir filtros de búsqueda de jugadores"));
    btnBorrar.setTooltip(TooltipUtils.crear("Borrar todos los filtros de jugadores"));
    btnAnadir2.setTooltip(TooltipUtils.crear("Añadir nuevo campeón"));
    btnListaFiltro2.setTooltip(TooltipUtils.crear("Abrir filtros de búsqueda de campeones"));
    btnBorrar2.setTooltip(TooltipUtils.crear("Borrar todos los filtros de campeones"));
    btnAnadir3.setTooltip(TooltipUtils.crear("Añadir nueva partida"));
    btnListaFiltros3.setTooltip(TooltipUtils.crear("Abrir filtros de búsqueda de partidas"));
    btnBorrar3.setTooltip(TooltipUtils.crear("Borrar todos los filtros de partidas"));
     
     animarTabla(tablaJugadores);
     animarTabla(tablaCampeones);
     animarTabla(tablaPartidas);
 }   

    // Animacion: cuando se anade una fila nueva a la tabla, aparece con fade-in
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
