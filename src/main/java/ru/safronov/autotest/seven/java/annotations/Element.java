package ru.safronov.autotest.seven.java.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для определения имени элементов.При использовании над методом необходимоБ чтобы аннотируемый метод не имел
 * параметров и возвращал значение
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface Element {
    String value();
}
