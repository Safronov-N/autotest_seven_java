package ru.safronov.autotest.seven.java.interfaces;

import ru.safronov.autotest.seven.java.elementModels.AbstractElement;

public interface IElementCollection<T extends AbstractElement> extends Iterable<T> {
    boolean exists(String itemDescription);
    T get(String itemDescription);
    void select(String itemDescription);
}
