/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package campeones;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
/**
 *
 * @author nestor
 */
public class Campeon {

    private IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final StringProperty rol;
    private final StringProperty dificultad;
    private final BooleanProperty seleccionado; // Nueva propiedad para selección

    // Constructor
    public Campeon(int id, String nombre, String descripcion, String rol, String dificultad, boolean seleccionado) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.rol = new SimpleStringProperty(rol);
        this.dificultad = new SimpleStringProperty(dificultad);
        this.seleccionado = new SimpleBooleanProperty(seleccionado); // Inicializar selección
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

    // Getters para las propiedades (devuelven StringProperty o BooleanProperty)
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

    public BooleanProperty seleccionadoProperty() {
        return seleccionado;
    }

    // Método para obtener el estado de seleccionado (devuelve boolean)
    public boolean isSeleccionado() {
        return seleccionado.get();
    }

    // Método para establecer la selección
    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado.set(seleccionado);
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
    //ids de nombre a ids
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
