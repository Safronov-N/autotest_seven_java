package ru.safronov.autotest.seven.java.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.safronov.autotest.seven.java.builder.ProcessingThread;
import ru.safronov.autotest.seven.java.config.DriverManager;

import java.util.List;

public class ElementFuture {
    protected ElementFuture(){}

    private By by;
    private long time;

    public ElementFuture(long time, By by){
        this.time = time;
        this.by = by;
    }

    public WebElement resolve(){
        return WaitElement.instance.waitForElement(time,by);
    }

    public List<WebElement> resolveMany(){
        return DriverManager.getWD(ProcessingThread.getSessionId()).findElements(by);
    }
}
