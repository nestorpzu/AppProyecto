package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelos.Partida;

/**
 * DAO para la tabla juegan (partidas). Esta es la tabla intermedia que relaciona
 * jugadores con campeones: registra que jugador uso que campeon en que fecha,
 * con que resultado y KDA.
 * 
 * A diferencia de JugadorDAO y CampeonDAO, aqui usamos JOINs al hacer select
 * porque en la interfaz queremos mostrar los nombres, no los IDs numericos.
 * Y al insertar, hacemos lo contrario: el usuario elige nombres en los ComboBox
 * pero nosotros los convertimos a IDs para guardarlos en la BD.
 * 
 * @author npauc
 */
public class PartidaDAO {

    /**
     * Obtiene todas las partidas haciendo LEFT JOIN con jugadores y campeones
     * para mostrar los nombres en vez de los IDs. Si un jugador o campeon fue
     * borrado y la partida quedo huerfana, LEFT JOIN evita que desaparezca.
     */
    public List<Partida> obtenerTodos(Connection connection) {
        List<Partida> partidas = new ArrayList<>();
        String query = "SELECT j.ID_juegan, jug.nombre_jugador, c.nombre_campeon, "
                + "j.Fecha_jugada, j.KDA, j.Resultado "
                + "FROM juegan j "
                + "LEFT JOIN jugadores jug ON j.ID_Jugador = jug.idJugadores "
                + "LEFT JOIN campeones c ON j.ID_Campeon = c.idCampeones";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Con el JOIN ya tenemos los nombres directamente, no los IDs
                int idJuegan = rs.getInt("ID_juegan");
                String nombreJugador = rs.getString("nombre_jugador");
                String nombreCampeon = rs.getString("nombre_campeon");

                // La fecha puede ser null en la BD, hay que controlar eso
                LocalDate fecha = rs.getDate("Fecha_jugada") != null
                        ? rs.getDate("Fecha_jugada").toLocalDate() : null;

                // Si el KDA o resultado son null, ponemos valores por defecto
                String kda = rs.getString("KDA") != null ? rs.getString("KDA") : "0/0/0";
                String resultado = rs.getString("Resultado") != null ? rs.getString("Resultado") : "Desconocido";

                Partida partida = new Partida(idJuegan, nombreJugador, nombreCampeon, fecha, kda, resultado);
                partidas.add(partida);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return partidas;
    }


    /**
     * Insertar una partida. Conversion de nombres a IDs:
     * la interfaz trabaja con nombres (ComboBox) pero la BD necesita IDs.
     * Si el jugador o campeon no existen, lanza SQLException.
     */
    public int insertar(Partida partida, Connection connection) throws SQLException {
        // Convertimos los nombres seleccionados en ComboBox a IDs para la BD
        JugadorDAO jugadorDAO = new JugadorDAO();
        CampeonDAO campeonDAO = new CampeonDAO();
        int idJugador = jugadorDAO.obtenerIDPorNombre(partida.getJugador(), connection);
        int idCampeon = campeonDAO.obtenerIDPorNombre(partida.getCampeon(), connection);

        String query = "INSERT INTO juegan (ID_Jugador, ID_Campeon, Fecha_jugada, KDA, Resultado) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, idJugador);
            stmt.setInt(2, idCampeon);
            // La fecha puede ser null si el usuario no la rellena
            if (partida.getFecha() != null) {
                stmt.setDate(3, java.sql.Date.valueOf(partida.getFecha()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }
            stmt.setString(4, partida.getKda());
            stmt.setString(5, partida.getResultado());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return -1;
    }

    /**
     * Actualiza los datos de una partida. Solo se pueden modificar
     * la fecha, el KDA y el resultado. No se puede cambiar el jugador o campeon.
     */
    public boolean actualizar(Partida partida, Connection connection) {
        String query = "UPDATE juegan SET Fecha_jugada=?, KDA=?, Resultado=? WHERE ID_juegan=?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            if (partida.getFecha() != null) {
                stmt.setDate(1, java.sql.Date.valueOf(partida.getFecha()));
            } else {
                stmt.setNull(1, java.sql.Types.DATE);
            }
            stmt.setString(2, partida.getKda());
            stmt.setString(3, partida.getResultado());
            stmt.setInt(4, partida.getIdJuegan());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Elimina una partida por su ID. No necesita transaccion porque no hay
     * ninguna tabla que dependa de juegan (es la ultima en la cadena de FK).
     */
    public boolean eliminar(int id, Connection connection) {
        String query = "DELETE FROM juegan WHERE ID_juegan = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, id);
            int filasEliminadas = stmt.executeUpdate();
            return filasEliminadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
}
