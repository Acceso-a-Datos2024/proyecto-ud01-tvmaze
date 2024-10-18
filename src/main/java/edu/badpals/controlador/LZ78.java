package edu.badpals.controlador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class LZ78 {
    public static String decode(String encodedData) {
        List<DictionaryEntry> entries = new ArrayList<>();
        for (int i = 0; i < encodedData.length(); i += 2) {
            int index = Character.getNumericValue(encodedData.charAt(i));
            char character = encodedData.charAt(i + 1);
            entries.add(new DictionaryEntry(index, character));
        }

        List<String> dictionary = new ArrayList<>();
        StringBuilder decodedString = new StringBuilder();

        for (DictionaryEntry entry : entries) {
            String prefix = entry.index == 0 ? "" : dictionary.get(entry.index - 1);
            String currentString = prefix + entry.character;
            decodedString.append(currentString);
            dictionary.add(currentString);
        }

        return decodedString.toString();
    }

    public static String encode(String input) {
        Map<String, Integer> dictionary = new HashMap<>();
        StringBuilder encodedString = new StringBuilder();
        StringBuilder currentString = new StringBuilder();
        int dictSize = 1;

        for (char c : input.toCharArray()) {
            currentString.append(c);
            if (!dictionary.containsKey(currentString.toString())) {
                dictionary.put(currentString.toString(), dictSize++);
                String prefix = currentString.length() > 1 ? currentString.substring(0, currentString.length() - 1) : "";
                int index = dictionary.getOrDefault(prefix, 0);
                encodedString.append(index).append(c);
                currentString.setLength(0);
            }
        }

        if (currentString.length() > 0) {
            String prefix = currentString.length() > 1 ? currentString.substring(0, currentString.length() - 1) : "";
            int index = dictionary.getOrDefault(prefix, 0);
            encodedString.append(index).append(currentString.charAt(currentString.length() - 1));
        }

        return encodedString.toString();
    }

}