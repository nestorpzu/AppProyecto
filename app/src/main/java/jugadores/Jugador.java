package jugadores;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Jugador {

    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final IntegerProperty edad;
    private final StringProperty email;
    private final StringProperty nacionalidad;
    private final StringProperty posicion;
    private final BooleanProperty seleccionado;
    
    public Jugador(String nombre, String descripcion, int edad, String email, String nacionalidad, String posicion, Boolean seleccionado) {
        this.seleccionado = new SimpleBooleanProperty(seleccionado);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.edad = new SimpleIntegerProperty(edad);
        this.email = new SimpleStringProperty(email);
        this.nacionalidad = new SimpleStringProperty(nacionalidad);
        this.posicion = new SimpleStringProperty(posicion);
        
    }
    
    public Jugador(Jugador otro) {
    this.nombre = new SimpleStringProperty(otro.getNombre());
    this.descripcion = new SimpleStringProperty(otro.getDescripcion());
    this.edad = new SimpleIntegerProperty(otro.getEdad());
    this.email = new SimpleStringProperty(otro.getEmail());
    this.nacionalidad = new SimpleStringProperty(otro.getNacionalidad());
    this.posicion = new SimpleStringProperty(otro.getPosicion());
    this.seleccionado = new SimpleBooleanProperty(false);
}

    public BooleanProperty seleccionadoProperty() {
        return seleccionado;
    }
    
    public boolean isSeleccionado() {
        return seleccionado.get();
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado.set(seleccionado);
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
}
