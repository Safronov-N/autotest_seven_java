package ru.safronov.autotest.seven.java.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.safronov.autotest.seven.java.builder.ProcessingThread;
import ru.safronov.autotest.seven.java.config.DriverManager;

public class WaitElement {
    public static WaitElement instance;

    static {
        instance= new WaitElement();
    }

    public void tick(){
        long current = System.currentTimeMillis() + 22;
        while (System.currentTimeMillis() < current);
    }

    public WebElement waitForElement(long time, By by){
        for(int i =0;i< time/22 + 1 ; i++){
            try{
                WebElement result = DriverManager.getWD(ProcessingThread.getSessionId()).findElement(by);
                result.isDisplayed();
                return result;
            }catch (Exception e){
                tick();
            }
        }return null;
    }

    public WebElement waitForElements(long time, By by){
        for(int i =0;i< time/22 + 1 ; i++){
            try{
                return DriverManager.getWD(ProcessingThread.getSessionId()).findElement(by);
            }catch (Exception e){
                tick();
            }
        }return null;
    }

    public WebElement waitForClick(long time, WebElement element){
        for(int i =0;i< time/22 + 1 ; i++){
            try{
                if (element.isDisplayed()){
                    element.click();
                }
            }catch (Exception e){
                tick();
            }
        }return null;
    }

    public WebElement waitIsDisplay(long time, WebElement element){
        for(int i =0;i< time/22 + 1 ; i++){
            try{
                if (element.isDisplayed()){
                    return element;
                }
            }catch (Exception e){
                tick();
            }
        }return null;
    }
}
