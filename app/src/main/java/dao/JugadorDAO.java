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
 * DAO (Data Access Object) para la tabla jugadores.
 * Se encarga de todas las operaciones CRUD sobre los jugadores de la base de datos.
 * La conexion se pasa como parametro para poder compartirla entre los distintos DAO.
 * 
 * @author npauc
 */
public class JugadorDAO {
    
    /**
     * Obtiene todos los jugadores de la tabla y los convierte en objetos Jugador.
     * Se usa al cargar la tabla principal y al refrescar despues de cambios.
     */
    public List<Jugador> obtenerTodos(Connection connection) {
        List<Jugador> jugadores = new ArrayList<>();
        String query = "SELECT * FROM jugadores";
    
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Mapeo de cada fila del ResultSet a un objeto Jugador
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
    
    /**
     * Inserta un jugador nuevo en la tabla.
     * Usamos RETURN_GENERATED_KEYS para recuperar el ID que le asigna el auto_increment,
     * asi podemos actualizar la tabla en la interfaz sin tener que recargar todo.
     * Devuelve -1 si falla.
     */
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
    
            // Sacamos el ID autogenerado de la fila recien insertada
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
     * Actualiza un jugador existente. Busca por idJugadores y modifica todos los campos.
     * Retorna true si se modifico al menos una fila.
     */
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
    
    
    /**
     * Elimina un jugador por su ID. OJO: como jugadores tiene una relacion con la tabla
     * juegan (foreign key), primero hay que borrar todas las partidas de ese jugador
     * y luego ya se puede borrar el jugador. Si no lo hacemos asi, salta error de
     * integridad referencial. Por eso usamos una transaccion: o se borra todo o no se borra nada.
     */
    public boolean eliminar(int id, Connection connection) {
        String eliminarPartidas = "DELETE FROM juegan WHERE ID_Jugador = ?";
        String eliminarJugador = "DELETE FROM jugadores WHERE idJugadores = ?";
    
        try {
            // Iniciamos transaccion manual para que ambos DELETE se hagan juntos
            connection.setAutoCommit(false);
            
            try (PreparedStatement stmt1 = connection.prepareStatement(eliminarPartidas);
                 PreparedStatement stmt2 = connection.prepareStatement(eliminarJugador)) {
                
                // Primero borramos las partidas del jugador
                stmt1.setInt(1, id);
                stmt1.executeUpdate();
                
                // Luego borramos al jugador
                stmt2.setInt(1, id);
                int filasEliminadas = stmt2.executeUpdate();
                
                // Si llegamos aqui sin excepcion, confirmamos los cambios
                connection.commit();
                return filasEliminadas > 0;
                
            } catch (SQLException e) {
                // Si falla algo, deshacemos todo para no dejar la BD en estado inconsistente
                connection.rollback();
                e.printStackTrace();
                return false;
            } finally {
                // Volvemos a poner el autoCommit como estaba
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene solo los nombres de los jugadores. Se usa para cargar los ComboBox
     * en el formulario de partidas (para elegir que jugador jugo la partida).
     */
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

    /**
     * Busca el ID de un jugador a partir de su nombre.
     * Lo usa PartidaDAO cuando inserta una partida: en la interfaz se selecciona
     * el nombre pero en la BD necesitamos el ID numerico.
     * Si no existe lanza SQLException.
     */
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