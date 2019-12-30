package ru.safronov.autotest.seven.java.utils;

import org.openqa.selenium.WebElement;

public class ElementFutureResolved extends ElementFuture {

    WebElement element;

    public ElementFutureResolved(WebElement element){this.element=element;}

    @Override
    public WebElement resolve() {
        return element;
    }
}
