package ru.safronov.autotest.seven.java.exceptions;

public abstract class MyException extends IllegalStateException {
    MyException(){

    }

    public MyException(String message) {
        super(message);
    }

    public void resolve(){
        throw this;
    }
}
