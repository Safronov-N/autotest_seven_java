package ru.safronov.autotest.seven.java.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.safronov.autotest.seven.java.exceptions.MyException;
import ru.safronov.autotest.seven.java.exceptions.MyUncheckedException;

public class CustomLogger {
    private static final Logger logger;
    static {
        logger = LoggerFactory.getLogger(CustomLogger.class);
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public void fail(String message) {
        logger.error(message);
        throw new AssertionError(message);
    }

    public static void fail(String message, Throwable throwable) {
        logger.error(message,throwable);
        MyException resolve=throwable instanceof MyException
                ? ((MyException) throwable)
                : new MyUncheckedException(message,throwable);
        resolve.resolve();
    }

    public static void debug(String message) {
        logger.debug(message);
        System.out.println(message);
    }
}
