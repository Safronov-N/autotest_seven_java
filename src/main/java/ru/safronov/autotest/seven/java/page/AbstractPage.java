package ru.safronov.autotest.seven.java.page;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.safronov.autotest.seven.java.annotations.Element;
import ru.safronov.autotest.seven.java.builder.ProcessingThread;
import ru.safronov.autotest.seven.java.config.DriverManager;
import ru.safronov.autotest.seven.java.elementModels.AbstractElement;
import ru.safronov.autotest.seven.java.utils.CustomReflection;
import ru.safronov.autotest.seven.java.utils.ElementFuture;

import java.lang.reflect.Method;

public class AbstractPage {
    private WebDriverWait wait;
    private WebDriver driver;

    public AbstractPage() {
        this.driver = DriverManager.getWD(ProcessingThread.getSessionId());
        wait = new WebDriverWait(this.driver, 10);
    }

    public <T> T getElement(String bind) {
        if (bind == null) throw new IllegalArgumentException("Привязка элемента не может быть null");
        for (Method m : CustomReflection.getMethods(this.getClass())) {
            Element element = m.getAnnotation(Element.class);
            if (element != null && bind.equalsIgnoreCase(element.value()))
                return CustomReflection.invokeOr(this, m.getName(), null);
        }
        throw new IllegalArgumentException("Элемент не зарегистрирован на странице");
    }

    protected WebDriver getDriver() {
        return driver;
    }

    protected <T extends AbstractElement> T elementFactory(Class<T> clazz, By by) {
        return CustomReflection.createNewInstanceOr(clazz, null,new ElementFuture(10000,by));
    }
}
