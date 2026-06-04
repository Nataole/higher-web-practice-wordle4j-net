package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WordleServerStatisticLoaderTest {

    @Test
    public void shouldExtractValueFromJson() {
        String json = "{"
                + "\"nickname\":\"Аня\","
                + "\"stepsUsed\":4,"
                + "\"usedHints\":true,"
                + "\"hintsCount\":2"
                + "}";

        assertEquals(
                "Аня",
                WordleServerStatisticLoader.getValueFromJson(json, "nickname")
        );

        assertEquals(
                "4",
                WordleServerStatisticLoader.getValueFromJson(json, "stepsUsed")
        );

        assertEquals(
                "true",
                WordleServerStatisticLoader.getValueFromJson(json, "usedHints")
        );

        assertEquals(
                "2",
                WordleServerStatisticLoader.getValueFromJson(json, "hintsCount")
        );
    }

    @Test
    public void shouldEscapeJson() {
        String text = "Ан\"на";

        String escaped = WordleServerStatisticLoader.escapeJson(text);

        assertEquals("Ан\\\"на", escaped);
    }

    @Test
    public void shouldReturnEmptyStringIfFieldNotFound() {
        String json = "{\"nickname\":\"Аня\"}";

        String result = WordleServerStatisticLoader.getValueFromJson(
                json,
                "unknown"
        );

        assertEquals("", result);

    }
}
