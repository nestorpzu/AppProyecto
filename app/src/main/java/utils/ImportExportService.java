/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import com.google.gson.reflect.TypeToken;
import dao.CampeonDAO;
import dao.JugadorDAO;
import dao.PartidaDAO;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import modelos.Campeon;
import modelos.Jugador;
import modelos.Partida;
import java.lang.reflect.Type;

/**
 *
 * @author npauc
 */
public class ImportExportService {

    // ===================== JUGADORES =====================

    private static final String[] CABECERAS_JUGADOR = {"ID", "Nombre", "Descripcion", "Edad", "Email", "Nacionalidad", "Posicion"};

    public static String exportarJugadoresCSV(List<Jugador> jugadores, File archivo) throws IOException {
        List<String[]> filas = new ArrayList<>();
        for (Jugador j : jugadores) {
            filas.add(new String[]{
                    String.valueOf(j.getId()), j.getNombre(), j.getDescripcion(),
                    String.valueOf(j.getEdad()), j.getEmail(), j.getNacionalidad(), j.getPosicion()
            });
        }
        CSVUtils.exportarCSV(CABECERAS_JUGADOR, filas, archivo);
        return "Exportados " + jugadores.size() + " jugadores a CSV.";
    }

    public static String exportarJugadoresJSON(List<Jugador> jugadores, File archivo) throws IOException {
        JSONUtils.exportarJSON(jugadores, archivo);
        return "Exportados " + jugadores.size() + " jugadores a JSON.";
    }

    public static String importarJugadoresCSV(File archivo, Connection conn) throws IOException {
        List<String[]> filas = CSVUtils.importarCSV(archivo);
        return importarJugadores(filas, conn);
    }

    public static String importarJugadoresJSON(File archivo, Connection conn) throws IOException {
        Type tipo = new TypeToken<List<Jugador>>() {}.getType();
        List<Jugador> jugadores = JSONUtils.importarJSON(archivo,Jugador.class);

        List<String[]> filas = new ArrayList<>();
        for (Jugador j : jugadores) {
            filas.add(new String[]{
                    String.valueOf(j.getId()), j.getNombre(), j.getDescripcion(),
                    String.valueOf(j.getEdad()), j.getEmail(), j.getNacionalidad(), j.getPosicion()
            });
        }
        return importarJugadores(filas, conn);
    }

    private static String importarJugadores(List<String[]> filas, Connection conn) {
        JugadorDAO dao = new JugadorDAO();
        int importados = 0, actualizados = 0, errores = 0;

        for (String[] fila : filas) {
            try {
                if (fila.length < 6) { errores++; continue; }

                String nombre = fila[1].trim();
                String descripcion = fila.length > 2 ? fila[2].trim() : "";
                int edad = parseEntero(fila.length > 3 ? fila[3].trim() : "0");
                String email = fila.length > 4 ? fila[4].trim() : "";
                String nacionalidad = fila.length > 5 ? fila[5].trim() : "";
                String posicion = fila.length > 6 ? fila[6].trim() : "";

                Jugador jugador = new Jugador(0, nombre, descripcion, edad, email, nacionalidad, posicion);

                int idExistente = dao.obtenerIDPorNombre(nombre, conn);
                if (idExistente > 0) {
                    jugador.setId(idExistente);
                    dao.actualizar(jugador, conn);
                    actualizados++;
                } else {
                    dao.insertar(jugador, conn);
                    importados++;
                }
            } catch (SQLException e) {
                // jugador no encontrado -> insertar
                try {
                    String nombre = fila[1].trim();
                    String descripcion = fila.length > 2 ? fila[2].trim() : "";
                    int edad = parseEntero(fila.length > 3 ? fila[3].trim() : "0");
                    String email = fila.length > 4 ? fila[4].trim() : "";
                    String nacionalidad = fila.length > 5 ? fila[5].trim() : "";
                    String posicion = fila.length > 6 ? fila[6].trim() : "";

                    Jugador jugador = new Jugador(0, nombre, descripcion, edad, email, nacionalidad, posicion);
                    dao.insertar(jugador, conn);
                    importados++;
                } catch (Exception ex) {
                    errores++;
                }
            } catch (Exception e) {
                errores++;
            }
        }

        return "Importación jugadores: " + importados + " nuevos, " + actualizados + " actualizados, " + errores + " errores.";
    }

    // ===================== CAMPEONES =====================

    private static final String[] CABECERAS_CAMPEON = {"ID", "Nombre", "Descripcion", "Rol", "Dificultad"};

    public static String exportarCampeonesCSV(List<Campeon> campeones, File archivo) throws IOException {
        List<String[]> filas = new ArrayList<>();
        for (Campeon c : campeones) {
            filas.add(new String[]{
                    String.valueOf(c.getId()), c.getNombre(), c.getDescripcion(),
                    c.getRol(), c.getDificultad()
            });
        }
        CSVUtils.exportarCSV(CABECERAS_CAMPEON, filas, archivo);
        return "Exportados " + campeones.size() + " campeones a CSV.";
    }

    public static String exportarCampeonesJSON(List<Campeon> campeones, File archivo) throws IOException {
        JSONUtils.exportarJSON(campeones, archivo);
        return "Exportados " + campeones.size() + " campeones a JSON.";
    }

    public static String importarCampeonesCSV(File archivo, Connection conn) throws IOException {
        List<String[]> filas = CSVUtils.importarCSV(archivo);
        return importarCampeones(filas, conn);
    }

    public static String importarCampeonesJSON(File archivo, Connection conn) throws IOException {
        Type tipo = new TypeToken<List<Campeon>>() {}.getType();
        List<Campeon> campeones = JSONUtils.importarJSON(archivo, Campeon.class);

        List<String[]> filas = new ArrayList<>();
        for (Campeon c : campeones) {
            filas.add(new String[]{
                    String.valueOf(c.getId()), c.getNombre(), c.getDescripcion(),
                    c.getRol(), c.getDificultad()
            });
        }
        return importarCampeones(filas, conn);
    }

    private static String importarCampeones(List<String[]> filas, Connection conn) {
        CampeonDAO dao = new CampeonDAO();
        int importados = 0, actualizados = 0, errores = 0;

        for (String[] fila : filas) {
            try {
                if (fila.length < 4) { errores++; continue; }

                String nombre = fila[1].trim();
                String descripcion = fila.length > 2 ? fila[2].trim() : "";
                String rol = fila.length > 3 ? fila[3].trim() : "";
                String dificultad = fila.length > 4 ? fila[4].trim() : "";

                Campeon campeon = new Campeon(0, nombre, descripcion, rol, dificultad);

                int idExistente = dao.obtenerIDPorNombre(nombre, conn);
                if (idExistente > 0) {
                    campeon.setId(idExistente);
                    dao.actualizar(campeon, conn);
                    actualizados++;
                } else {
                    dao.insertar(campeon, conn);
                    importados++;
                }
            } catch (SQLException e) {
                try {
                    String nombre = fila[1].trim();
                    String descripcion = fila.length > 2 ? fila[2].trim() : "";
                    String rol = fila.length > 3 ? fila[3].trim() : "";
                    String dificultad = fila.length > 4 ? fila[4].trim() : "";

                    Campeon campeon = new Campeon(0, nombre, descripcion, rol, dificultad);
                    dao.insertar(campeon, conn);
                    importados++;
                } catch (Exception ex) {
                    errores++;
                }
            } catch (Exception e) {
                errores++;
            }
        }

        return "Importación campeones: " + importados + " nuevos, " + actualizados + " actualizados, " + errores + " errores.";
    }

    // ===================== PARTIDAS =====================

    private static final String[] CABECERAS_PARTIDA = {"ID", "Jugador", "Campeon", "Fecha", "KDA", "Resultado"};

    public static String exportarPartidasCSV(List<Partida> partidas, File archivo) throws IOException {
        List<String[]> filas = new ArrayList<>();
        for (Partida p : partidas) {
            filas.add(new String[]{
                    String.valueOf(p.getIdJuegan()), p.getJugador(), p.getCampeon(),
                    p.getFecha() != null ? p.getFecha().toString() : "",
                    p.getKda(), p.getResultado()
            });
        }
        CSVUtils.exportarCSV(CABECERAS_PARTIDA, filas, archivo);
        return "Exportadas " + partidas.size() + " partidas a CSV.";
    }

    public static String exportarPartidasJSON(List<Partida> partidas, File archivo) throws IOException {
        JSONUtils.exportarJSON(partidas, archivo);
        return "Exportadas " + partidas.size() + " partidas a JSON.";
    }

    public static String importarPartidasCSV(File archivo, Connection conn) throws IOException {
        List<String[]> filas = CSVUtils.importarCSV(archivo);
        return importarPartidas(filas, conn);
    }

    public static String importarPartidasJSON(File archivo, Connection conn) throws IOException {
        Type tipo = new TypeToken<List<Partida>>() {}.getType();
        List<Partida> partidas = JSONUtils.importarJSON(archivo, Partida.class);

        List<String[]> filas = new ArrayList<>();
        for (Partida p : partidas) {
            filas.add(new String[]{
                    String.valueOf(p.getIdJuegan()), p.getJugador(), p.getCampeon(),
                    p.getFecha() != null ? p.getFecha().toString() : "",
                    p.getKda(), p.getResultado()
            });
        }
        return importarPartidas(filas, conn);
    }

    private static String importarPartidas(List<String[]> filas, Connection conn) {
        JugadorDAO jugadorDAO = new JugadorDAO();
        CampeonDAO campeonDAO = new CampeonDAO();
        PartidaDAO partidaDAO = new PartidaDAO();
        int importadas = 0, errores = 0;

        for (String[] fila : filas) {
            try {
                if (fila.length < 5) { errores++; continue; }

                String nombreJugador = fila[1].trim();
                String nombreCampeon = fila[2].trim();
                LocalDate fecha = parseFecha(fila.length > 3 ? fila[3].trim() : "");
                String kda = fila.length > 4 ? fila[4].trim() : "0/0/0";
                String resultado = fila.length > 5 ? fila[5].trim() : "Empate";

                // Si el jugador no existe, crearlo con nombre solo
                try {
                    jugadorDAO.obtenerIDPorNombre(nombreJugador, conn);
                } catch (SQLException e) {
                    Jugador nuevo = new Jugador(0, nombreJugador, "", 0, "", "", "");
                    jugadorDAO.insertar(nuevo, conn);
                }

                // Si el campeon no existe, crearlo con nombre solo
                try {
                    campeonDAO.obtenerIDPorNombre(nombreCampeon, conn);
                } catch (SQLException e) {
                    Campeon nuevo = new Campeon(0, nombreCampeon, "", "", "");
                    campeonDAO.insertar(nuevo, conn);
                }

                Partida partida = new Partida(nombreJugador, nombreCampeon, fecha, kda, resultado);
                partidaDAO.insertar(partida, conn);
                importadas++;

            } catch (Exception e) {
                errores++;
            }
        }

        return "Importación partidas: " + importadas + " nuevas, " + errores + " errores.";
    }

    // ===================== UTILIDADES =====================

    private static int parseEntero(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static LocalDate parseFecha(String valor) {
        if (valor == null || valor.isEmpty()) return null;
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
