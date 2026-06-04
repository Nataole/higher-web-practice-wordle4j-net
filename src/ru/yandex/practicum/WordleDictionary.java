package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {
    private final List<String> words = new ArrayList<>();

    public void addWord(String word) {
        String normalized = normalizeWord(word);

        if (normalized.matches("[а-я]{5}") && !words.contains(normalized)) {
            words.add(normalized);
        }
    }

    public void addAll(List<String> newWords) {
        for (String word : newWords) {
            addWord(word);
        }
    }

    public boolean contains(String word) {
        return words.contains(normalizeWord(word));
    }

    public List<String> getWords() {
        return new ArrayList<>(words);
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new EmptyDictionaryException("Словарь пуст.");
        }

        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    public static String normalizeWord(String word) {
        if (word == null) {
            return "";
        }

        return word
                .toLowerCase()
                .replace('ё', 'е')
                .trim();
    }

    public static boolean isCorrectWord(String word) {
        return normalizeWord(word).matches("[а-я]{5}");
    }
}
