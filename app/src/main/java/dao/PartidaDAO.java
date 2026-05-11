/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 *
 * @author npauc
 */
public class PartidaDAO {

    // Obtener todas las partidas con JOIN (nombres en vez de IDs)
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
                int idJuegan = rs.getInt("ID_juegan");
                String nombreJugador = rs.getString("nombre_jugador");
                String nombreCampeon = rs.getString("nombre_campeon");

                LocalDate fecha = rs.getDate("Fecha_jugada") != null
                        ? rs.getDate("Fecha_jugada").toLocalDate() : null;

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


    // Insertar una partida (convierte nombres a IDs internamente)
    public int insertar(Partida partida, Connection connection) throws SQLException {
        JugadorDAO jugadorDAO = new JugadorDAO();
        CampeonDAO campeonDAO = new CampeonDAO();
        int idJugador = jugadorDAO.obtenerIDPorNombre(partida.getJugador(), connection);
        int idCampeon = campeonDAO.obtenerIDPorNombre(partida.getCampeon(), connection);

        String query = "INSERT INTO juegan (ID_Jugador, ID_Campeon, Fecha_jugada, KDA, Resultado) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, idJugador);
            stmt.setInt(2, idCampeon);
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

    // Actualizar partida (solo fecha, KDA y resultado)
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

    // Eliminar partida por ID
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
