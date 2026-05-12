/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author npauc
 */
/**
 * Utilidades para leer y escribir archivos CSV.
 * Usa OpenCSV para manejar correctamente comillas, comas dentro de campos, etc.
 */
public class CSVUtils {

    /**
     * Escribe un CSV con cabeceras y filas de datos.
     */
    public static void exportarCSV(String[] cabeceras, List<String[]> filas, File archivo) throws IOException {
        try (ICSVWriter writer = new CSVWriterBuilder(new BufferedWriter(new FileWriter(archivo)))
                .withSeparator(',')
                .withQuoteChar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .build()) {

            writer.writeNext(cabeceras);

            for (String[] fila : filas) {
                writer.writeNext(fila);
            }
        }
    }

    /**
     * Lee un CSV y devuelve una lista de filas (cada fila es un array de Strings).
     * La primera fila se omite si es cabecera.
     */
    public static List<String[]> importarCSV(File archivo) throws IOException {
        List<String[]> filas = new ArrayList<>();

        try (CSVReader reader = new CSVReaderBuilder(new BufferedReader(new FileReader(archivo)))
                .build()) {

            String[] linea;
            boolean primeraLinea = true;
        try {
            while ((linea = reader.readNext()) != null) {
                if (primeraLinea) {
                    // Detectar si la primera fila es cabecera (no numérica)
                    try {
                        Integer.parseInt(linea[0]);
                    } catch (NumberFormatException e) {
                        primeraLinea = false;
                        continue; // Saltar cabecera
                    }
                    primeraLinea = false;
                }
                filas.add(linea);
            }
        }catch (com.opencsv.exceptions.CsvValidationException e) {
            throw new IOException("Error al validar el CSV", e);
        }
        }

        return filas;
    }
}
