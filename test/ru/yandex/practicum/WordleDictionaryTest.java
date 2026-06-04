package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryTest {
    private WordleDictionary dictionary;

    @BeforeEach
    public void setUp() {
        dictionary = new WordleDictionary();
        dictionary.addAll(List.of(
                "герой",
                "гонец",
                "город",
                "море",
                "ЁЖИКИ",
                "abcde",
                "герой"
        ));
    }

    @Test
    public void shouldAddOnlyRussianFiveLetterWords() {
        List<String> words = dictionary.getWords();

        assertTrue(words.contains("герой"));
        assertTrue(words.contains("гонец"));
        assertTrue(words.contains("город"));
        assertTrue(words.contains("ежики"));

        assertFalse(words.contains("море"));
        assertFalse(words.contains("abcde"));
    }

    @Test
    public void shouldNormalizeWords() {
        assertTrue(dictionary.contains("ГЕРОЙ"));
        assertTrue(dictionary.contains("ёжики"));
        assertTrue(dictionary.contains("ЕЖИКИ"));
    }

    @Test
    public void shouldNotAddDuplicates() {
        List<String> words = dictionary.getWords();

        int count = 0;

        for (String word : words) {
            if ("герой".equals(word)) {
                count++;
            }
        }

        assertEquals(1, count);
    }

    @Test
    public void shouldReturnRandomWordFromDictionary() {
        String word = dictionary.getRandomWord();

        assertNotNull(word);
        assertEquals(5, word.length());
        assertTrue(dictionary.contains(word));
    }

    @Test
    public void shouldThrowExceptionIfDictionaryIsEmpty() {
        WordleDictionary emptyDictionary = new WordleDictionary();

        assertThrows(
                EmptyDictionaryException.class,
                emptyDictionary::getRandomWord
        );
    }
}
