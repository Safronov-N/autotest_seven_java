package ru.safronov.autotest.seven.java.elementModels;

import org.openqa.selenium.WebElement;
import ru.safronov.autotest.seven.java.utils.ElementFuture;

public abstract class AbstractElement {

    protected ElementFuture future;

    public AbstractElement(ElementFuture future) {
        this.future = future;
    }

    public WebElement getInitialElement() {
        return future.resolve();
    }


}
