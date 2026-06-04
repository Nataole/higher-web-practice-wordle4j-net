package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleGameTest {
    private WordleDictionary dictionary;

    @BeforeEach
    public void setUp() {
        dictionary = new WordleDictionary();

        dictionary.addAll(List.of(
                "герой",
                "гонец",
                "город",
                "океан",
                "берег",
                "ежики",
                "потоп",
                "топот"
        ));
    }

    @Test
    public void shouldAnalyzeWordCorrectly() {
        WordleGame game = new WordleGame(dictionary, "герой");

        String result = game.analyzeWord("гонец");

        assertEquals("+^-^-", result);
    }

    @Test
    public void shouldAnalyzeRepeatedLettersCorrectly() {
        WordleGame game = new WordleGame(dictionary, "потоп");

        String result = game.analyzeWord("топот");

        assertEquals("^+^+-", result);
    }

    @Test
    public void shouldWinGame() {
        WordleGame game = new WordleGame(dictionary, "герой");

        String result = game.makeMove("герой");

        assertEquals("+++++", result);
        assertTrue(game.isGameOver());
        assertTrue(game.isWin());
        assertEquals(1, game.getStepsUsed());
    }

    @Test
    public void shouldLoseAfterSixWrongMoves() {
        WordleGame game = new WordleGame(dictionary, "герой");

        game.makeMove("океан");
        game.makeMove("океан");
        game.makeMove("океан");
        game.makeMove("океан");
        game.makeMove("океан");
        game.makeMove("океан");

        assertTrue(game.isGameOver());
        assertFalse(game.isWin());
        assertEquals(6, game.getStepsUsed());
    }

    @Test
    public void shouldNotSpendStepForInvalidWord() {
        WordleGame game = new WordleGame(dictionary, "герой");

        assertThrows(
                InvalidWordLengthException.class,
                () -> game.makeMove("море")
        );

        assertEquals(0, game.getStepsUsed());
        assertFalse(game.isGameOver());
    }

    @Test
    public void shouldRejectEnglishLetters() {
        WordleGame game = new WordleGame(dictionary, "герой");

        assertThrows(
                InvalidWordFormatException.class,
                () -> game.makeMove("abcde")
        );
    }

    @Test
    public void shouldRejectWordNotFromDictionary() {
        WordleGame game = new WordleGame(dictionary, "герой");

        assertThrows(
                WordNotFoundInDictionary.class,
                () -> game.makeMove("банан")
        );
    }

    @Test
    public void shouldGiveHint() {
        WordleGame game = new WordleGame(dictionary, "герой");

        String hint = game.getHint();

        assertNotNull(hint);
        assertEquals(5, hint.length());
        assertTrue(dictionary.contains(hint));
        assertTrue(game.usedHints());
        assertEquals(1, game.getHintsCount());
    }

    @Test
    public void shouldNotRepeatHints() {
        WordleGame game = new WordleGame(dictionary, "герой");

        String firstHint = game.getHint();
        String secondHint = game.getHint();

        assertNotEquals(firstHint, secondHint);
        assertEquals(2, game.getHintsCount());
    }
}
