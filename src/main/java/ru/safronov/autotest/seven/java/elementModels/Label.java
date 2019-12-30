package ru.safronov.autotest.seven.java.elementModels;

import org.openqa.selenium.WebElement;
import ru.safronov.autotest.seven.java.interfaces.Clickable;
import ru.safronov.autotest.seven.java.interfaces.HasText;
import ru.safronov.autotest.seven.java.utils.CustomLogger;
import ru.safronov.autotest.seven.java.utils.ElementFuture;

public class Label extends AbstractElement implements HasText, Clickable {
    public Label(ElementFuture future) {
        super(future);
    }

    @Override
    public String getText() {
        return future.resolve().getText();
    }

    @Override
    public void click() {
        CustomLogger.info(String.format("Клик по элементу '%s'", getInitialElement().getText()));
        future.resolve().click();
    }
}
