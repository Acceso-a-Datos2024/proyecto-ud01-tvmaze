package edu.badpals.modelo;

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