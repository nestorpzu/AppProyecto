/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Modelo de datos para la tabla campeones de la BD.
 * Representa un campeon del juego con su nombre,descripcion,rol y dificultad.
 * Igual que Jugador, usa JavaFX Properties para que la TableView se actualice
 * automaticamente cuando se cambian los valores.
 *
 * @author nestor
 */
public class Campeon {

    private IntegerProperty id;               // idCampeones (PK auto_increment)
    private final StringProperty nombre;      // nombre_campeon
    private final StringProperty descripcion;  // descripcion_campeon
    private final StringProperty rol;          // rol_mapa (Luchador, Mago, Tanque, etc.)
    private final StringProperty dificultad;    // dificultad (Facil, Media, Alta)


    // Constructor
    public Campeon(int id, String nombre, String descripcion, String rol, String dificultad) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.rol = new SimpleStringProperty(rol);
        this.dificultad = new SimpleStringProperty(dificultad);

    }

    // Getters para propiedades (devuelven String)
    public String getNombre() {
        return nombre.get();
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public String getRol() {
        return rol.get();
    }

    public String getDificultad() {
        return dificultad.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public StringProperty rolProperty() {
        return rol;
    }

    public StringProperty dificultadProperty() {
        return dificultad;
    }


    // Setters para modificar los valores
    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public void setRol(String rol) {
        this.rol.set(rol);
    }

    public void setDificultad(String dificultad) {
        this.dificultad.set(dificultad);
    }
    public int getId() { 
        return id.get(); 
    }
    public void setId(int id) { 
        this.id.set(id); 
    }
    public IntegerProperty idProperty() { 
        return id; 
    }
    
}
