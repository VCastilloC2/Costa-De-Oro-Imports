package com.application.utils;

import java.util.HashMap;
import java.util.Map;

public class TextSimilarityUtil {

    public static double cosineSimilarity(String text1, String text2) {
        Map<String, Integer> vector1 = toVector(text1);
        Map<String, Integer> vector2 = toVector(text2);

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String word : vector1.keySet()) {
            dotProduct += vector1.get(word) * vector2.getOrDefault(word, 0);
        }

        for (Integer value : vector1.values()) {
            norm1 += Math.pow(value, 2);
        }

        for (Integer value : vector2.values()) {
            norm2 += Math.pow(value, 2);
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private static Map<String, Integer> toVector(String text) {
        Map<String, Integer> vector = new HashMap<>();

        if (text == null || text.isBlank()) {
            return vector;
        }

        String normalized = text.toLowerCase()
                .replaceAll("[^a-záéíóúñü0-9 ]", " ");

        String[] words = normalized.split("\\s+");

        for (String word : words) {
            if (word.length() <= 2) continue;
            vector.put(word, vector.getOrDefault(word, 0) + 1);
        }

        return vector;
    }
}