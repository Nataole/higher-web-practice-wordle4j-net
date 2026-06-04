package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private static final int WORD_LENGTH = 5;
    private static final int MAX_STEPS = 6;

    private final WordleDictionary dictionary;
    private final String answer;

    private int stepsLeft = MAX_STEPS;
    private boolean gameOver = false;
    private boolean win = false;

    private final List<String> enteredWords = new ArrayList<>();
    private final List<String> usedHints = new ArrayList<>();

    public WordleGame(WordleDictionary dictionary) {
        this.dictionary = dictionary;
        this.answer = dictionary.getRandomWord();
    }

    public WordleGame(WordleDictionary dictionary, String answer) {
        this.dictionary = dictionary;
        this.answer = normalizeWord(answer);

        if (!dictionary.contains(this.answer)) {
            throw new WordNotFoundInDictionary("Загаданное слово не найдено в словаре.");
        }
    }

    public String makeMove(String word) {
        if (gameOver) {
            throw new RuntimeException("Игра уже завершена.");
        }

        String normalizedWord = normalizeWord(word);

        validateWord(normalizedWord);

        enteredWords.add(normalizedWord);
        stepsLeft--;

        String result = analyzeWord(normalizedWord);

        if (normalizedWord.equals(answer)) {
            win = true;
            gameOver = true;
            return result;
        }

        if (stepsLeft == 0) {
            gameOver = true;
        }

        return result;
    }

    public String analyzeWord(String word) {
        return analyze(answer, normalizeWord(word));
    }

    public String getHint() {
        for (String word : dictionary.getWords()) {
            if (enteredWords.contains(word)) {
                continue;
            }

            if (usedHints.contains(word)) {
                continue;
            }

            if (isWordSuitableAsHint(word)) {
                usedHints.add(word);
                return word;
            }
        }

        return "Нет подходящих слов";
    }

    private boolean isWordSuitableAsHint(String candidate) {
        for (String enteredWord : enteredWords) {
            String realResult = analyzeWord(enteredWord);
            String candidateResult = analyzeWordForCandidate(candidate, enteredWord);

            if (!realResult.equals(candidateResult)) {
                return false;
            }
        }

        return true;
    }

    private String analyzeWordForCandidate(String candidateAnswer, String enteredWord) {
        return analyze(candidateAnswer, enteredWord);
    }

    private String analyze(String answerWord, String userWord) {
        if (answerWord.length() != WORD_LENGTH || userWord.length() != WORD_LENGTH) {
            throw new InvalidWordLengthException("Слова для анализа должны состоять из 5 букв.");
        }

        char[] result = new char[WORD_LENGTH];
        int[] letterCounts = new int[Character.MAX_VALUE + 1];

        for (int i = 0; i < WORD_LENGTH; i++) {
            char answerLetter = answerWord.charAt(i);
            char userLetter = userWord.charAt(i);

            if (answerLetter == userLetter) {
                result[i] = '+';
            } else {
                letterCounts[answerLetter]++;
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (result[i] == '+') {
                continue;
            }

            char userLetter = userWord.charAt(i);

            if (letterCounts[userLetter] > 0) {
                result[i] = '^';
                letterCounts[userLetter]--;
            } else {
                result[i] = '-';
            }
        }

        return new String(result);
    }

    private void validateWord(String word) {
        if (word == null || word.isBlank()) {
            throw new EmptyWordException("Слово не может быть пустым.");
        }

        if (word.length() != WORD_LENGTH) {
            throw new InvalidWordLengthException("Слово должно состоять из 5 букв.");
        }

        if (!word.matches("[а-я]+")) {
            throw new InvalidWordFormatException("Слово должно содержать только русские буквы.");
        }

        if (!dictionary.contains(word)) {
            throw new WordNotFoundInDictionary("Слово не найдено в словаре.");
        }
    }

    private String normalizeWord(String word) {
        if (word == null) {
            return "";
        }

        return word
                .toLowerCase()
                .replace('ё', 'е')
                .trim();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWin(String word) {
        return normalizeWord(word).equals(answer);
    }

    public boolean isWin() {
        return win;
    }

    public String getAnswer() {
        return answer;
    }

    public int getStepsUsed() {
        return MAX_STEPS - stepsLeft;
    }

    public boolean usedHints() {
        return !usedHints.isEmpty();
    }

    public int getHintsCount() {
        return usedHints.size();
    }
}
