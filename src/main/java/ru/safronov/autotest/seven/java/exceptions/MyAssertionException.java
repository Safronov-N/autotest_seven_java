package ru.safronov.autotest.seven.java.exceptions;

public class MyAssertionException extends MyException {
    @Override
    public void resolve(){
        throw new AssertionError();
    }
}
