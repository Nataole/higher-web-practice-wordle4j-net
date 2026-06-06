package ru.yandex.practicum;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * этот класс должен запускаться отдельно и внутри себя запускать веб-сервер для получения и обработки статистики от игровых клиентов
 */
public class WordleServer {
    public static void main(String[] args) throws Exception {

            WordleServerStatisticLoader statisticLoader =
                    new WordleServerStatisticLoader();

            HttpServer server = HttpServer.create(
                    new InetSocketAddress(8080),
                    0
            );

            server.createContext("/stats", exchange -> {
                try {
                    if ("POST".equals(exchange.getRequestMethod())) {
                        handleStatsPost(exchange, statisticLoader);
                    } else {
                        sendJson(exchange, 405, "{\"error\":\"Метод не поддерживается\"}");
                    }
                } finally {
                    exchange.close();
                }
            });

            server.createContext("/rating", exchange -> {
                try {
                    if ("GET".equals(exchange.getRequestMethod())) {
                        handleRatingGet(exchange, statisticLoader);
                    } else {
                        sendJson(exchange, 405, "{\"error\":\"Метод не поддерживается\"}");
                    }
                } finally {
                    exchange.close();
                }
            });

            server.start();

            System.out.println("Сервер запущен на порту 8080");
        }

        private static void handleStatsPost(
                HttpExchange exchange,
                WordleServerStatisticLoader statisticLoader
    ) throws IOException {

            String requestBody = readRequestBody(exchange);

            String nickname = WordleServerStatisticLoader.getValueFromJson(
                    requestBody,
                    "nickname"
            );

            String stepsUsed = WordleServerStatisticLoader.getValueFromJson(
                    requestBody,
                    "stepsUsed"
            );

            String usedHints = WordleServerStatisticLoader.getValueFromJson(
                    requestBody,
                    "usedHints"
            );

            if (nickname.isBlank()
                    || stepsUsed.isBlank()
                    || usedHints.isBlank()) {

                sendJson(exchange, 400, "{\"error\":\"Некорректная статистика\"}");
                return;
            }

            statisticLoader.addWin(nickname);

            sendJson(exchange, 200, "{\"status\":\"ok\"}");
        }

        private static void handleRatingGet(
                HttpExchange exchange,
                WordleServerStatisticLoader statisticLoader
    ) throws IOException {

            String response = statisticLoader.getTop10Json();

            sendJson(exchange, 200, response);
        }

        private static String readRequestBody(HttpExchange exchange)
            throws IOException {

            StringBuilder body = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            exchange.getRequestBody(),
                            StandardCharsets.UTF_8))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }

            return body.toString();
        }

        private static void sendJson(
                HttpExchange exchange,
        int code,
        String response
    ) throws IOException {

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "application/json; charset=utf-8"
            );

            exchange.sendResponseHeaders(code, bytes.length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(bytes);
        }
    }
}
