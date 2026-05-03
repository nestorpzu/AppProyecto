/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package partidas;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.sql.Date;
import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author nestor
 */

public class Partida {

    private final IntegerProperty idJuegan; 
    private final StringProperty jugador;
    private final StringProperty campeon;
    private final ObjectProperty<LocalDate> fecha;
    private final StringProperty kda;
    private final StringProperty resultado;
    private final SimpleBooleanProperty seleccionado;

    // Constructor
    public Partida(int idJuegan,String jugador, String campeon, LocalDate fecha, String kda, String resultado) {
        this.idJuegan = new SimpleIntegerProperty(idJuegan);
        this.jugador = new SimpleStringProperty(jugador);
        this.campeon = new SimpleStringProperty(campeon);
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.kda = new SimpleStringProperty(kda);
        this.resultado = new SimpleStringProperty(resultado);
        this.seleccionado = new SimpleBooleanProperty(false);
    }

    public Partida(String jugador, String campeon, LocalDate fecha, String kda, String resultado) {
        this(-1, jugador, campeon, fecha, kda, resultado);
    }
    
     // Getters y Setters para ID_juegan
    public IntegerProperty idJueganProperty() {
        return idJuegan;
    }

    public int getIdJuegan() {
        return idJuegan.get();
    }

    public void setIdJuegan(int idJuegan) {
        this.idJuegan.set(idJuegan);
    }
    
    // Getters y Setters
    public BooleanProperty seleccionadoProperty() {
        return seleccionado;
    }

    public boolean isSeleccionado() {
        return seleccionado.get();
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado.set(seleccionado);
    }
    
    // Getters y Setters con Property para JavaFX
    public StringProperty jugadorProperty() {
        return jugador;
    }

    public String getJugador() {
        return jugador.get();
    }

    public void setJugador(String jugador) {
        this.jugador.set(jugador);
    }

    public StringProperty campeonProperty() {
        return campeon;
    }

    public String getCampeon() {
        return campeon.get();
    }

    public void setCampeon(String campeon) {
        this.campeon.set(campeon);
    }

    public ObjectProperty<LocalDate> fechaProperty() {
        return fecha;
    }

    public LocalDate getFecha() {
        return fecha.get();
    }

    public void setFecha(LocalDate fecha) {
        this.fecha.set(fecha);
    }

    public StringProperty kdaProperty() {
        return kda;
    }

    public String getKda() {
        return kda.get();
    }

    public void setKda(String kda) {
        this.kda.set(kda);
    }

    public StringProperty resultadoProperty() {
        return resultado;
    }

    public String getResultado() {
        return resultado.get();
    }

    public void setResultado(String resultado) {
        this.resultado.set(resultado);
    }

    @Override
    public String toString() {
        return "Partida{" +
                "jugador='" + jugador.get() + '\'' +
                ", campeon='" + campeon.get() + '\'' +
                ", fecha=" + fecha.get() +
                ", kda='" + kda.get() + '\'' +
                ", resultado='" + resultado.get() + '\'' +
                '}';
    }
}
