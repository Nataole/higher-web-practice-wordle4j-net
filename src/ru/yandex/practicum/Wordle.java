package ru.yandex.practicum;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    public static void main(String[] args) {

        try (PrintWriter log = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream("game.log"),
                        StandardCharsets.UTF_8
                )
        );
             Scanner scanner = new Scanner(System.in)) {

            WordleDictionaryLoader loader =
                    new WordleDictionaryLoader();

            WordleDictionary dictionary =
                    loader.loadDictionary("words_ru.txt", log);

            WordleGame game =
                    new WordleGame(dictionary);

            System.out.println("Добро пожаловать в Wordle!");
            System.out.println("Угадайте слово из 5 букв.");
            System.out.println("У вас 6 попыток.");
            System.out.println();
            System.out.println("Правила подсказок:");
            System.out.println("+ — буква есть в слове и стоит на правильном месте.");
            System.out.println("^ — буква есть в слове, но стоит не на своём месте.");
            System.out.println("- — такой буквы нет в загаданном слове.");
            System.out.println();
            System.out.println("Нажмите Enter для подсказки.");

            while (!game.isGameOver()) {

                System.out.print("> ");
                String word = scanner.nextLine()
                        .toLowerCase()
                        .replace('ё', 'е')
                        .trim();

                try {
                    if (word.isBlank()) {
                        String hint = game.getHint();

                        System.out.println("Подсказка: " + hint);
                        log.println("Пользователь запросил подсказку: " + hint);

                        continue;
                    }

                    String result = game.makeMove(word);

                    System.out.println(result);
                    log.println("Ход: " + word + " результат: " + result);

                    if (game.isWin(word)) {
                        System.out.println("Победа!");
                        System.out.println("Загаданное слово: " + game.getAnswer());

                        sendStatsAfterWin(scanner, game, log);
                        return;
                    }

                } catch (EmptyWordException
                         | InvalidWordLengthException
                         | InvalidWordFormatException
                         | WordNotFoundInDictionary e) {

                    System.out.println("Ошибка: " + e.getMessage());
                    log.println("Ошибка игрового ввода: " + e.getMessage());
                }
            }

            System.out.println("Вы проиграли.");
            System.out.println("Загаданное слово: " + game.getAnswer());
            log.println("Игра завершена поражением. Ответ: " + game.getAnswer());

        } catch (DictionaryLoadException | EmptyDictionaryException e) {
            System.out.println("Не удалось загрузить словарь.");
        } catch (Exception e) {
            System.out.println("Неизвестная ошибка: " + e.getMessage());
        }
    }public static String buildStatsJson(
            String nickname,
            int stepsUsed,
            boolean usedHints,
            int hintsCount
    ) {
        if (nickname == null || nickname.isBlank()) {
            nickname = "anonymous";
        }

        return "{"
                + "\"nickname\":\""
                + escapeJson(nickname.trim())
                + "\","
                + "\"stepsUsed\":"
                + stepsUsed
                + ","
                + "\"usedHints\":"
                + usedHints
                + ","
                + "\"hintsCount\":"
                + hintsCount
                + "}";
    }

    private static void sendStatsAfterWin(
            Scanner scanner,
            WordleGame game,
            PrintWriter log
    ) {
        System.out.print("Введите ваш никнейм: ");
        String nickname = scanner.nextLine().trim();

        if (nickname.isBlank()) {
            nickname = "anonymous";
        }

        String json = buildStatsJson(
                nickname,
                game.getStepsUsed(),
                game.usedHints(),
                game.getHintsCount()
        );

        try {
            sendPost("http://localhost:8080/stats", json);

            System.out.println("Статистика отправлена на сервер.");

            String rating = sendGet("http://localhost:8080/rating");

            System.out.println("Топ игроков:");
            System.out.println(rating);

            log.println("Статистика победы отправлена: " + json);

        } catch (IOException e) {
            System.out.println("Не удалось отправить статистику.");
            System.out.println("Проверьте, что WordleServer запущен.");
            log.println("Ошибка отправки статистики: " + e.getMessage());
        }
    }

    private static void sendPost(String urlText, String json)
            throws IOException {

        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlText);

            connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=utf-8"
            );
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int code = connection.getResponseCode();

            if (code != 200) {
                throw new IOException("Сервер вернул код: " + code);
            }

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String sendGet(String urlText)
            throws IOException {

        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlText);

            connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            int code = connection.getResponseCode();

            if (code != 200) {
                throw new IOException("Сервер вернул код: " + code);
            }

            StringBuilder result = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            )) {

                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }

            return result.toString();

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String escapeJson(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
