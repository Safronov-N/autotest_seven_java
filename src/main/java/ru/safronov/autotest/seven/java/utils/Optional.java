package ru.safronov.autotest.seven.java.utils;

/**
 * Обертка над объектом позволяющая проверить его на налловость
 *
 * @param <T>
 */
public class Optional<T> {
    private T t;

    private Optional(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public T orElse(T t){return this.t == null ? t : this.t;}

    public static <T> Optional<T> of(T t){
        if(t==null)
            throw new NullPointerException();
        return new Optional<T>(t);
    }
    public static <T> Optional<T> ofNullable(T t){return new Optional<T>(t);}
}
