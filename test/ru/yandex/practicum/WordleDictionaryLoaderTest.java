package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    public void shouldLoadDictionaryFromFile() throws Exception {
        Path file = tempDir.resolve("words.txt");

        Files.write(
                file,
                List.of(
                        "Герой",
                        "гонец",
                        "море",
                        "ЁЖИКИ",
                        "abcde"
                ),
                StandardCharsets.UTF_8
        );

        WordleDictionaryLoader loader = new WordleDictionaryLoader();

        WordleDictionary dictionary = loader.loadDictionary(
                file.toString(),
                new PrintWriter(System.out)
        );

        assertTrue(dictionary.contains("герой"));
        assertTrue(dictionary.contains("гонец"));
        assertTrue(dictionary.contains("ежики"));

        assertFalse(dictionary.contains("море"));
        assertFalse(dictionary.contains("abcde"));

        assertEquals(3, dictionary.getWords().size());
    }

    @Test
    public void shouldThrowExceptionIfFileNotFound() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader();

        assertThrows(
                DictionaryLoadException.class,
                () -> loader.loadDictionary(
                        "unknown-file.txt",
                        new PrintWriter(System.out)
                )
        );
    }

    @Test
    public void shouldThrowExceptionIfNoCorrectWords() throws Exception {
        Path file = tempDir.resolve("bad_words.txt");

        Files.write(
                file,
                List.of(
                        "море",
                        "abcde",
                        "дом",
                        "12345"
                ),
                StandardCharsets.UTF_8
        );

        WordleDictionaryLoader loader = new WordleDictionaryLoader();

        assertThrows(
                EmptyDictionaryException.class,
                () -> loader.loadDictionary(
                        file.toString(),
                        new PrintWriter(System.out)
                )
        );
    }
}
