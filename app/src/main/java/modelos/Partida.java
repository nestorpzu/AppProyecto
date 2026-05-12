/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.sql.Date;
import java.time.LocalDate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Modelo de datos para la tabla juegan (partidas) de la BD.
 * Esta tabla es la intermedia que relaciona jugadores con campeones.
 * 
 * IMPORTANTE: en la interfaz guardamos nombres (jugador, campeon) en vez de IDs
 * porque asi se muestra en la tabla. Pero en la BD se guardan los IDs numericos.
 * PartidaDAO se encarga de hacer la conversion nombre↔ID.
 *
 * @author nestor
 */
public class Partida {

    private final IntegerProperty idJuegan;      // ID de la partida (PK)
    private final StringProperty jugador;        // nombre del jugador (no el ID)
    private final StringProperty campeon;         // nombre del campeon (no el ID)
    private final ObjectProperty<LocalDate> fecha; // fecha en que se jugo
    private final StringProperty kda;             // kills/deaths/assists ej: "5/2/8"
    private final StringProperty resultado;       // Victoria, Derrota o Empate

    // Constructor
    public Partida(int idJuegan,String jugador, String campeon, LocalDate fecha, String kda, String resultado) {
        this.idJuegan = new SimpleIntegerProperty(idJuegan);
        this.jugador = new SimpleStringProperty(jugador);
        this.campeon = new SimpleStringProperty(campeon);
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.kda = new SimpleStringProperty(kda);
        this.resultado = new SimpleStringProperty(resultado);
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
