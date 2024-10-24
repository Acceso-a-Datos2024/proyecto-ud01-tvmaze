package edu.badpals.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Episodio implements Serializable {
    private static final long serialVersionUID = 1L;
    private int season;
    private int number;
    private String name;

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Episodio{" +
                "season=" + season +
                ", number=" + number +
                ", name='" + name + '\'' +
                '}';
    }
}
