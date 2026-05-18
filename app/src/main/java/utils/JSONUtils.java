/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import modelos.Campeon;
import modelos.Jugador;
import modelos.Partida;

/**
 *
 * @author npauc
 */
/**
 * Utilidades para leer y escribir archivos JSON usando Gson.
 */
public class JSONUtils {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public static <T> List<T> importarJSON(File archivo, Class<T> clazz) throws IOException {
    Type tipoLista = TypeToken.getParameterized(List.class, Map.class).getType();
    
    try (Reader reader = new BufferedReader(new FileReader(archivo))) {
        List<Map<String, Object>> maps = gson.fromJson(reader, tipoLista);
        
        List<T> resultado = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            resultado.add((T) convertirMapAObjeto(map, clazz));
        }
        return resultado;
    }
    }

private static Object convertirMapAObjeto(Map<String, Object> map, Class<?> clase) {
    if (clase == Campeon.class) {
        return new Campeon(
            ((Number) map.get("id")).intValue(),
            (String) map.get("nombre"),
            (String) map.get("descripcion"),
            (String) map.get("rol"),
            (String) map.get("dificultad")
        );
    } else if (clase == Jugador.class) {
        return new Jugador(
            ((Number) map.get("id")).intValue(),
            (String) map.get("nombre"),
            (String) map.get("descripcion"),
            ((Number) map.get("edad")).intValue(),
            (String) map.get("email"),
            (String) map.get("nacionalidad"),
            (String) map.get("posicion")
        );
    } else if (clase == Partida.class) {
        return new Partida(
            ((Number) map.get("idJuegan")).intValue(),
            (String) map.get("jugador"),
            (String) map.get("campeon"),
            (LocalDate) map.get("fecha"),
            (String) map.get("kda"),
            (String) map.get("resultado")
        );
    }
    return null;
}

public static void exportarJSON(Object objeto, File archivo) throws IOException {
    Object serializable = convertirSiEsNecesario(objeto); 
    try (Writer writer = new BufferedWriter(new FileWriter(archivo))) {
        gson.toJson(serializable, writer);  
    }
}

private static Object convertirSiEsNecesario(Object obj) {
    if (obj instanceof List) {
        List<?> lista = (List<?>) obj;
        if (!lista.isEmpty()) {
            Object primero = lista.get(0);
            if (primero instanceof Campeon) {
                return convertirCampeones(lista);
            } else if (primero instanceof Jugador) {
                return convertirJugadores(lista);
            } else if (primero instanceof Partida) {
                return convertirPartidas(lista);
            }
        }
    }
    return obj;
}

private static List<Map<String, Object>> convertirCampeones(List<?> lista) {
    List<Map<String, Object>> resultado = new ArrayList<>();
    for (Object item : lista) {
        Campeon c = (Campeon) item;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.getId());
        map.put("nombre", c.getNombre());
        map.put("descripcion", c.getDescripcion());
        map.put("rol", c.getRol());
        map.put("dificultad", c.getDificultad());
        resultado.add(map);
    }
    return resultado;
}


private static List<Map<String, Object>> convertirJugadores(List<?> lista) {
    List<Map<String, Object>> resultado = new ArrayList<>();
    for (Object item : lista) {
        Jugador c = (Jugador) item;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.getId());
        map.put("nombre", c.getNombre());
        map.put("descripcion", c.getDescripcion());
        map.put("edad", c.getEdad());
        map.put("email", c.getEmail());
        map.put("nacionalidad", c.getNacionalidad());
        map.put("posicion", c.getPosicion());
        resultado.add(map);
    }
    return resultado;
}


private static List<Map<String, Object>> convertirPartidas(List<?> lista) {
    List<Map<String, Object>> resultado = new ArrayList<>();
    for (Object item : lista) {
        Partida c = (Partida) item;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idJuegan", c.getIdJuegan());
        map.put("jugador", c.getJugador());
        map.put("campeon", c.getCampeon());
        map.put("fecha", c.getFecha());
        map.put("kda", c.getKda());
        map.put("resultado", c.getResultado());
        resultado.add(map);
    }
    return resultado;
}

}