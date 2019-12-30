package ru.safronov.autotest.seven.java.elementModels;

import org.openqa.selenium.WebElement;
import ru.safronov.autotest.seven.java.interfaces.TextEditable;
import ru.safronov.autotest.seven.java.utils.CustomClassLoader;
import ru.safronov.autotest.seven.java.utils.CustomLogger;
import ru.safronov.autotest.seven.java.utils.ElementFuture;

public class TextField extends AbstractElement implements TextEditable {
    public TextField(ElementFuture future) {
        super(future);
    }

    @Override
    public void setText(String text) {
        CustomLogger.info(String.format("Ввод в поле '%s'", getInitialElement().getText()));
        for (int i = 0;i<10;i++){
            try {
                future.resolve().sendKeys(text);
                return;
            }catch (Exception ignored){

            }
        }
    }
}
