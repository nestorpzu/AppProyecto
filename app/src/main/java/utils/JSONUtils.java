/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

/**
 *
 * @author npauc
 */
/**
 * Utilidades para leer y escribir archivos JSON usando Gson.
 */
public class JSONUtils {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Serializa un objeto (o lista) a JSON y lo escribe en un archivo.
     */
    public static void exportarJSON(Object objeto, File archivo) throws IOException {
        try (Writer writer = new BufferedWriter(new FileWriter(archivo))) {
            gson.toJson(objeto, writer);
        }
    }

    public static <T> List<T> importarJSON(File archivo, Type tipo) throws IOException {
        try (Reader reader = new BufferedReader(new FileReader(archivo))) {
            return gson.fromJson(reader, tipo);
        }
    }
}