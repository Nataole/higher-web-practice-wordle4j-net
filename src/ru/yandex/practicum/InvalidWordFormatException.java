package ru.yandex.practicum;

public class InvalidWordFormatException extends RuntimeException {
    public InvalidWordFormatException(String message) {
        super(message);
    }
}
