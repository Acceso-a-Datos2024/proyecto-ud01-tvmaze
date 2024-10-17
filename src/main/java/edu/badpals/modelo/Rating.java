package edu.badpals.modelo;


import java.io.Serializable;

public class Rating {
    private double average;

    public Rating(){}

    public Rating(String rating){
        this.average = Double.parseDouble(rating);
    }

    public Rating(double rating) {
        this.average = rating;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    @Override
    public String toString() {
        return String.valueOf(average);
    }
}