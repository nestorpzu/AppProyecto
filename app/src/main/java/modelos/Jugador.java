package modelos;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modelo de datos para la tabla jugadores de la BD.
 * Usa JavaFX Properties (StringProperty, IntegerProperty) en vez de String e int
 * normales porque asi la TableView puede escuchar los cambios y actualizarse sola.
 * Si usaramos String normales, la tabla no se enteraria de los cambios.
 */
public class Jugador {

    private IntegerProperty id;
    private final StringProperty nombre;       // nombre_jugador
    private final StringProperty descripcion;  // descripcion_jugador
    private final IntegerProperty edad;         // edad
    private final StringProperty email;         // email
    private final StringProperty nacionalidad;   // nacionalidad
    private final StringProperty posicion;      // posicion_jugador

    
    public Jugador(int id, String nombre, String descripcion, int edad, String email, String nacionalidad, String posicion) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.edad = new SimpleIntegerProperty(edad);
        this.email = new SimpleStringProperty(email);
        this.nacionalidad = new SimpleStringProperty(nacionalidad);
        this.posicion = new SimpleStringProperty(posicion);
        
    }
     

    public StringProperty nombreProperty() {
        return nombre;
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public IntegerProperty edadProperty() {
        return edad;
    }

    public int getEdad() {
        return edad.get();
    }

    public void setEdad(int edad) {
        this.edad.set(edad);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty nacionalidadProperty() {
        return nacionalidad;
    }

    public String getNacionalidad() {
        return nacionalidad.get();
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad.set(nacionalidad);
    }

    public StringProperty posicionProperty() {
        return posicion;
    }

    public String getPosicion() {
        return posicion.get();
    }

    public void setPosicion(String posicion) {
        this.posicion.set(posicion);
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
