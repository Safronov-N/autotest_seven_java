package ru.safronov.autotest.seven.java.elementModels;

import org.openqa.selenium.WebElement;
import ru.safronov.autotest.seven.java.interfaces.Clickable;
import ru.safronov.autotest.seven.java.utils.CustomLogger;
import ru.safronov.autotest.seven.java.utils.ElementFuture;

public class Button extends AbstractElement implements Clickable {

    public Button(ElementFuture future) {
        super(future);
    }

    @Override
    public void click() {
        CustomLogger.info(String.format("Клик по элементу '%s'", getInitialElement().getText()));
        for (int i = 0;i<10;i++){
            try {
                future.resolve().click();
                return;
            }catch (Exception ignored){

            }
        }

    }
}
