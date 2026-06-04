package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {
    public WordleDictionary loadDictionary(String filename, PrintWriter log) {
        List<String> words = new ArrayList<>();

        log.println("Начинаем загрузку словаря из файла: " + filename);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filename),
                        StandardCharsets.UTF_8
                )
        )) {
            String line;

            while ((line = reader.readLine()) != null) {
                String word = WordleDictionary.normalizeWord(line);

                if (WordleDictionary.isCorrectWord(word)) {
                    words.add(word);
                }
            }

        } catch (IOException e) {
            log.println("Ошибка при загрузке словаря: " + e.getMessage());

            throw new DictionaryLoadException(
                    "Не удалось загрузить словарь: " + filename,
                    e
            );
        }

        if (words.isEmpty()) {
            log.println("В словаре нет подходящих слов.");
            throw new EmptyDictionaryException(
                    "В словаре нет слов из 5 русских букв."
            );
        }

        WordleDictionary dictionary = new WordleDictionary();
        dictionary.addAll(words);

        log.println("Загружено слов: " + dictionary.getWords().size());

        return dictionary;
    }
}
