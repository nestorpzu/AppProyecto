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
import java.util.ArrayList;
import java.util.List;
import modelos.Campeon;

/**
 *
 * @author npauc
 */

public class CampeonDAO {

    public List<Campeon> obtenerTodos(Connection connection) {
        List<Campeon> campeones = new ArrayList<>();
        String query = "SELECT * FROM campeones";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Campeon campeon = new Campeon(
                    rs.getInt("idCampeones"),
                    rs.getString("nombre_campeon"),
                    rs.getString("descripcion_campeon"),
                    rs.getString("rol_mapa"),
                    rs.getString("dificultad")
                );
                campeones.add(campeon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return campeones;
    }

    public int insertar(Campeon campeon, Connection connection) {
        String query = "INSERT INTO campeones (nombre_campeon, descripcion_campeon, rol_mapa, dificultad) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, campeon.getNombre());
            stmt.setString(2, campeon.getDescripcion());
            stmt.setString(3, campeon.getRol());
            stmt.setString(4, campeon.getDificultad());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean actualizar(Campeon campeon, Connection connection) {
        String query = "UPDATE campeones SET nombre_campeon=?, descripcion_campeon=?, rol_mapa=?, dificultad=? WHERE idCampeones=?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, campeon.getNombre());
            stmt.setString(2, campeon.getDescripcion());
            stmt.setString(3, campeon.getRol());
            stmt.setString(4, campeon.getDificultad());
            stmt.setInt(5, campeon.getId());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean eliminar(int id, Connection connection) {
        String eliminarPartidas = "DELETE FROM juegan WHERE ID_Campeon = ?";
        String eliminarCampeon = "DELETE FROM campeones WHERE idCampeones = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt1 = connection.prepareStatement(eliminarPartidas);
                 PreparedStatement stmt2 = connection.prepareStatement(eliminarCampeon)) {

                stmt1.setInt(1, id);
                stmt1.executeUpdate();

                stmt2.setInt(1, id);
                int filasEliminadas = stmt2.executeUpdate();

                connection.commit();
                return filasEliminadas > 0;

            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<String> obtenerNombres(Connection connection) {
        List<String> nombres = new ArrayList<>();
        String query = "SELECT nombre_campeon FROM campeones";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                nombres.add(rs.getString("nombre_campeon"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombres;
    }

    public int obtenerIDPorNombre(String nombre, Connection connection) throws SQLException {
        String query = "SELECT idCampeones FROM campeones WHERE nombre_campeon = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idCampeones");
            } else {
                throw new SQLException("Campeón no encontrado: " + nombre);
            }
        }
    }
    
}
