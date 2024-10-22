package edu.badpals.modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZ78 {

    // Método para decodificar una cadena codificada con el algoritmo LZ78
    public static String decode(String encodedData) {
        List<DictionaryEntry> entries = new ArrayList<>();
        // Iterar sobre los datos codificados en pasos de 2
        for (int i = 0; i < encodedData.length(); i += 2) {
            int index = java.lang.Character.getNumericValue(encodedData.charAt(i));
            char character = encodedData.charAt(i + 1);
            entries.add(new DictionaryEntry(index, character));
        }

        List<String> dictionary = new ArrayList<>();
        StringBuilder decodedString = new StringBuilder();

        // Reconstruir la cadena original usando las entradas del diccionario
        for (DictionaryEntry entry : entries) {
            String prefix = entry.index == 0 ? "" : dictionary.get(entry.index - 1);
            String currentString = prefix + entry.character;
            decodedString.append(currentString);
            dictionary.add(currentString);
        }

        return decodedString.toString();
    }

    // Método para codificar una cadena usando el algoritmo LZ78
    public static String encode(String input) {
        Map<String, Integer> dictionary = new HashMap<>();
        StringBuilder encodedString = new StringBuilder();
        StringBuilder currentString = new StringBuilder();
        int dictSize = 1;

        // Iterar sobre cada carácter en la cadena de entrada
        for (char c : input.toCharArray()) {
            currentString.append(c);
            // Si la cadena actual no está en el diccionario, agregarla
            if (!dictionary.containsKey(currentString.toString())) {
                dictionary.put(currentString.toString(), dictSize++);
                String prefix = currentString.length() > 1 ? currentString.substring(0, currentString.length() - 1) : "";
                int index = dictionary.getOrDefault(prefix, 0);
                encodedString.append(index).append(c);
                currentString.setLength(0);
            }
        }

        // Manejar cualquier carácter restante en la cadena actual
        if (!currentString.isEmpty()) {
            String prefix = currentString.length() > 1 ? currentString.substring(0, currentString.length() - 1) : "";
            int index = dictionary.getOrDefault(prefix, 0);
            encodedString.append(index).append(currentString.charAt(currentString.length() - 1));
        }

        return encodedString.toString();
    }

    // Clase interna para representar una entrada del diccionario
    private static class DictionaryEntry {
        int index;
        char character;

        DictionaryEntry(int index, char character) {
            this.index = index;
            this.character = character;
        }
    }
}