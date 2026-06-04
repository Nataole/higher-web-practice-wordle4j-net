package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * этот класс должен выполнять рутинные процедуры по хранению результатов игроков
 */
public class WordleServerStatisticLoader {
    private final Map<String, Integer> stats = new HashMap<>();
    private final String filename = "stats.json";

    public WordleServerStatisticLoader() {
        loadFromFile();
    }

    public void addWin(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            nickname = "anonymous";
        }

        nickname = nickname.trim();

        stats.put(
                nickname,
                stats.getOrDefault(nickname, 0) + 1
        );

        saveToFile();
    }

    public String getTop10Json() {
        return buildJson(true);
    }

    private String getAllStatsJson() {
        return buildJson(false);
    }

    private String buildJson(boolean onlyTop10) {
        StringBuilder result = new StringBuilder();

        result.append("[");

        List<Map.Entry<String, Integer>> players = new ArrayList<>(stats.entrySet());

        players.sort((first, second) ->
                second.getValue().compareTo(first.getValue())
        );

        int limit = players.size();

        if (onlyTop10) {
            limit = Math.min(10, players.size());
        }

        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> entry = players.get(i);

            if (i > 0) {
                result.append(",");
            }

            result.append("{")
                    .append("\"nickname\":\"")
                    .append(escapeJson(entry.getKey()))
                    .append("\",")
                    .append("\"wins\":")
                    .append(entry.getValue())
                    .append("}");
        }

        result.append("]");

        return result.toString();
    }

    private void loadFromFile() {
        File file = new File(filename);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8))) {

            String json = reader.readLine();

            if (json == null || json.isBlank()) {
                return;
            }

            parseStatsJson(json);

        } catch (IOException e) {
            stats.clear();
        }
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filename),
                        StandardCharsets.UTF_8))) {

            writer.print(getAllStatsJson());

        } catch (IOException e) {

        }
    }

    private void parseStatsJson(String json) {
        stats.clear();

        json = json.trim();

        if (json.length() < 2) {
            return;
        }

        json = json.substring(1, json.length() - 1);

        if (json.isBlank()) {
            return;
        }

        String[] players = json.split("\\},\\{");

        for (String player : players) {
            String clean = player
                    .replace("{", "")
                    .replace("}", "")
                    .replace("\"", "");

            String[] fields = clean.split(",");

            String nickname = null;
            int wins = 0;

            for (String field : fields) {
                String[] pair = field.split(":");

                if (pair.length != 2) {
                    continue;
                }

                String key = pair[0];
                String value = pair[1];

                if ("nickname".equals(key)) {
                    nickname = value;
                } else if ("wins".equals(key)) {
                    try {
                        wins = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        wins = 0;
                    }
                }
            }

            if (nickname != null && !nickname.isBlank()) {
                stats.put(nickname, wins);
            }
        }
    }

    public static String getValueFromJson(String json, String fieldName) {
        String search = "\"" + fieldName + "\":";

        int start = json.indexOf(search);

        if (start == -1) {
            return "";
        }

        start = start + search.length();

        if (start >= json.length()) {
            return "";
        }

        if (json.charAt(start) == '"') {
            start++;

            int end = json.indexOf("\"", start);

            if (end == -1) {
                return "";
            }

            return json.substring(start, end);
        }

        int end = json.indexOf(",", start);

        if (end == -1) {
            end = json.indexOf("}", start);
        }

        if (end == -1) {
            end = json.length();
        }

        return json.substring(start, end).trim();
    }

    public static String escapeJson(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
