package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    @Test
    public void shouldBuildStatsJson() {
        String json = Wordle.buildStatsJson(
                "Аня",
                4,
                true,
                2
        );

        assertEquals(
                "{\"nickname\":\"Аня\",\"stepsUsed\":4,\"usedHints\":true,\"hintsCount\":2}",
                json
        );
    }

    @Test
    public void shouldUseAnonymousNicknameIfNicknameIsEmpty() {
        String json = Wordle.buildStatsJson(
                "",
                6,
                false,
                0
        );

        assertEquals(
                "{\"nickname\":\"anonymous\",\"stepsUsed\":6,\"usedHints\":false,\"hintsCount\":0}",
                json
        );
    }

    @Test
    public void shouldEscapeNicknameInStatsJson() {
        String json = Wordle.buildStatsJson(
                "Ан\"на",
                3,
                false,
                0
        );

        assertEquals(
                "{\"nickname\":\"Ан\\\"на\",\"stepsUsed\":3,\"usedHints\":false,\"hintsCount\":0}",
                json
        );
    }
}
