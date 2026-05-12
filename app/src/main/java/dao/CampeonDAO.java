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
 * DAO para la tabla campeones. Misma estructura que JugadorDAO:
 * operaciones CRUD basicas mas busqueda por nombre para los ComboBox de partidas.
 * 
 * @author npauc
 */
public class CampeonDAO {

    /**
     * Recupera todos los campeones de la BD y los convierte en objetos Campeon.
     * Se usa para llenar la tabla principal de la interfaz.
     */
    public List<Campeon> obtenerTodos(Connection connection) {
        List<Campeon> campeones = new ArrayList<>();
        String query = "SELECT * FROM campeones";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Cada fila del resultado la convertimos en un objeto Campeon
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

    /**
     * Inserta un campeon nuevo. Igual que en JugadorDAO, usamos RETURN_GENERATED_KEYS
     * para obtener el ID que le asigna el auto_increment y poder actualizar la vista.
     * Devuelve -1 si falla.
     */
    public int insertar(Campeon campeon, Connection connection) {
        String query = "INSERT INTO campeones (nombre_campeon, descripcion_campeon, rol_mapa, dificultad) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, campeon.getNombre());
            stmt.setString(2, campeon.getDescripcion());
            stmt.setString(3, campeon.getRol());
            stmt.setString(4, campeon.getDificultad());

            stmt.executeUpdate();

            // Recuperamos el ID autogenerado
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Modifica un campeon existente buscando por su ID.
     * Retorna true si al menos una fila fue afectada.
     */
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

    /**
     * Elimina un campeon por su ID. Igual que con jugadores, primero hay que borrar
     * las partidas relacionadas (juegan) para que no salte el error de foreign key.
     * Usamos transaccion para asegurar que o se borra todo o no se borra nada.
     */
    public boolean eliminar(int id, Connection connection) {
        String eliminarPartidas = "DELETE FROM juegan WHERE ID_Campeon = ?";
        String eliminarCampeon = "DELETE FROM campeones WHERE idCampeones = ?";

        try {
            // Iniciamos transaccion manual
            connection.setAutoCommit(false);

            try (PreparedStatement stmt1 = connection.prepareStatement(eliminarPartidas);
                 PreparedStatement stmt2 = connection.prepareStatement(eliminarCampeon)) {

                // Primero las partidas donde aparece este campeon
                stmt1.setInt(1, id);
                stmt1.executeUpdate();

                // Luego el campeon en si
                stmt2.setInt(1, id);
                int filasEliminadas = stmt2.executeUpdate();

                // Todo bien, confirmamos
                connection.commit();
                return filasEliminadas > 0;

            } catch (SQLException e) {
                // Algo fallo, volvemos atras
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
    
    /**
     * Obtiene solo los nombres de los campeones para cargar el ComboBox
     * en el formulario de anadir/editar partida.
     */
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

    /**
     * Busca el ID de un campeon por su nombre. Lo usa PartidaDAO para convertir
     * el nombre seleccionado en el ComboBox al ID que necesita la BD.
     * Lanza SQLException si no lo encuentra.
     */
    public int obtenerIDPorNombre(String nombre, Connection connection) throws SQLException {
        String query = "SELECT idCampeones FROM campeones WHERE nombre_campeon = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idCampeones");
            } else {
                throw new SQLException("Campeon no encontrado: " + nombre);
            }
        }
    }
    
}