package edu.badpals.controlador;

public class DictionaryEntry {
    public final int index;
    public final char character;

    public DictionaryEntry(int index, char character) {
        this.index = index;
        this.character = character;
    }

    @Override
    public String toString() {
        return index + "," + character;
    }
}