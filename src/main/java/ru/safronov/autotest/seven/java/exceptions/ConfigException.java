package ru.safronov.autotest.seven.java.exceptions;

public class ConfigException extends MyUncheckedException {

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable t) {
        super(message, t);
    }
}
