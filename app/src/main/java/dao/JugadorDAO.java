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
import modelos.Jugador;

/**
 *
 * @author npauc
 */
public class JugadorDAO {
    
    public List<Jugador> obtenerTodos(Connection connection) {
        List<Jugador> jugadores = new ArrayList<>();
        String query = "SELECT * FROM jugadores";
    
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Jugador jugador = new Jugador(
                    rs.getInt("idJugadores"),
                    rs.getString("nombre_jugador"),
                    rs.getString("descripcion_jugador"),
                    rs.getInt("edad"),
                    rs.getString("email"),
                    rs.getString("nacionalidad"),
                    rs.getString("posicion_jugador")
                );
                jugadores.add(jugador);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return jugadores;
    }
    
    public int insertar(Jugador jugador, Connection connection) {
        String query = "INSERT INTO jugadores (nombre_jugador, descripcion_jugador, edad, email, nacionalidad, posicion_jugador) VALUES (?,?,?,?,?,?)";
    
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, jugador.getNombre());
            stmt.setString(2, jugador.getDescripcion());
            stmt.setInt(3, jugador.getEdad());
            stmt.setString(4, jugador.getEmail());
            stmt.setString(5, jugador.getNacionalidad());
            stmt.setString(6, jugador.getPosicion());
    
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
    
    public boolean actualizar(Jugador jugador, Connection connection) {
        String query = "UPDATE jugadores SET nombre_jugador=?, descripcion_jugador=?, edad=?, email=?, nacionalidad=?, posicion_jugador=? WHERE idJugadores=?";
    
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, jugador.getNombre());
            stmt.setString(2, jugador.getDescripcion());
            stmt.setInt(3, jugador.getEdad());
            stmt.setString(4, jugador.getEmail());
            stmt.setString(5, jugador.getNacionalidad());
            stmt.setString(6, jugador.getPosicion());
            stmt.setInt(7, jugador.getId());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }
    
    
    public boolean eliminar(int id, Connection connection) {
        String eliminarPartidas = "DELETE FROM juegan WHERE ID_Jugador = ?";
        String eliminarJugador = "DELETE FROM jugadores WHERE idJugadores = ?";
    
        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement stmt1 = connection.prepareStatement(eliminarPartidas);
                 PreparedStatement stmt2 = connection.prepareStatement(eliminarJugador)) {
                
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
        String query = "SELECT nombre_jugador FROM jugadores";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                nombres.add(rs.getString("nombre_jugador"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombres;
    }

    public int obtenerIDPorNombre(String nombre, Connection connection) throws SQLException {
        String query = "SELECT idJugadores FROM jugadores WHERE nombre_jugador = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idJugadores");
            } else {
                throw new SQLException("Jugador no encontrado: " + nombre);
            }
        }
    }
}
