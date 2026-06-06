package ru.yandex.practicum;

public class DictionaryLoadException extends RuntimeException {
    public DictionaryLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
