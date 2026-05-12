/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de manejar la conexion con la base de datos MySQL.
 * Usa un patron singleton simple: si ya existe una conexion abierta la reutiliza,
 * y si se cerro por algun motivo la vuelve a crear.
 */
public class DataBaseMain {

    // Datos de conexion a la base de datos app_prototipo
    private static final String URL = "jdbc:mysql://localhost:3306/app_prototipo";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Conexion compartida que se usa en toda la aplicacion
    private static Connection conexion;

    /**
     * Devuelve la conexion a la BD. Si no existe o se cerro, la crea de nuevo.
     * Todos los DAO usan este metodo para obtener la conexion.
     */
    public static Connection getConnection() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return conexion;
    }

    /**
     * Cierra la conexion cuando se cierra la aplicacion.
     * Se llama desde App.java en el setOnCloseRequest del Stage principal.
     */
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
