package edu.badpals.modelo;

public class Image {
    private String medium;
    private String original;

    public Image() {
    }

    public Image(String medium) {
        this.medium = medium;
    }

    // Getters and setters
    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}