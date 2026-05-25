/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import javafx.scene.control.Alert;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author npauc
 */
public class InformeDAO {
    public void generarInformeCampeones(Connection connection) {
        new Thread(() -> {
        try {
            // jrxml o jasper
            InputStream jasper = getClass().getResourceAsStream("/reports/InformeCampeones.jasper");
            
            if (jasper == null) {
                mostrarError("No se encontró el archivo InformeCampeones.jrxml en /reports/");
                return;
            }
            
            if (connection == null || connection.isClosed()) {
                mostrarError("La conexión a la base de datos no está disponible.");
                return;
            }
            /** lento
    JasperReport report = JasperCompileManager.compileReport(jrxml);
    JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(), connection);
            */
            //cambiar esta linea 
            JasperPrint print = JasperFillManager.fillReport(jasper, new HashMap<>(), connection);
            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("Informe de Campeones");
            viewer.setLocationRelativeTo(null);
            viewer.setVisible(true);
        } catch (Exception e) {
            mostrarError("Error al generar el informe:\n" + e.getMessage());
            e.printStackTrace();
        }
         }).start();
    }
    
    public void generarInformePartidas(Connection connection, String resultadoFiltro) {
    new Thread(() -> {
        try {
            InputStream jasper = getClass().getResourceAsStream("/reports/InformePartidas.jasper");
            
            if (jasper == null) {
                mostrarError("No se encontró el archivo InformePartidas.jasper");
                return;
            }
            
            if (connection == null || connection.isClosed()) {
                mostrarError("La conexión a la base de datos no está disponible.");
                return;
            }
            
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("ResultadoFiltro", resultadoFiltro != null ? resultadoFiltro : "");
            
            JasperPrint print = JasperFillManager.fillReport(jasper, parametros, connection);
            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("Informe de Partidas - Filtro: " + (resultadoFiltro.isEmpty() ? "Todos" : resultadoFiltro));
            viewer.setLocationRelativeTo(null);
            viewer.setVisible(true);
        } catch (Exception e) {
            mostrarError("Error al generar el informe:\n" + e.getMessage());
            e.printStackTrace();
        }
    }).start();
}
    
    private void mostrarError(String mensaje) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error en el informe");
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }
}
