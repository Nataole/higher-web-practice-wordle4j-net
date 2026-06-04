package ru.yandex.practicum;

public class EmptyWordException extends RuntimeException {
    public EmptyWordException(String message) {
        super(message);
    }
}
