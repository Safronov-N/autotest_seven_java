package stepdefinitions;

import cucumber.api.java.bg.И;
import org.openqa.selenium.WebDriver;
import ru.safronov.autotest.seven.java.builder.Builder;
import ru.safronov.autotest.seven.java.builder.PageMapper;
import ru.safronov.autotest.seven.java.builder.ProcessingThread;
import ru.safronov.autotest.seven.java.config.Config;
import ru.safronov.autotest.seven.java.config.DriverManager;
import ru.safronov.autotest.seven.java.elementModels.Button;
import ru.safronov.autotest.seven.java.elementModels.MenuGroup;
import ru.safronov.autotest.seven.java.elementModels.TextField;
import ru.safronov.autotest.seven.java.interfaces.Clickable;
import ru.safronov.autotest.seven.java.utils.CustomLogger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class MyStepdefs {

    @И("открыть сайт")
    public void openURL() {
       WebDriver driver = DriverManager.getWD(ProcessingThread.getSessionId());
       driver.get(Config.getUrl());
    }

    @И("создать страницу \"(.+)\"")
    public void pageCreate(String pageName) {
        Class pageClass =  Builder.buildPage(DriverManager.getWD(ProcessingThread.getSessionId()).getClass().getSimpleName(),pageName);
        String page =  PageMapper.getPage(pageName).getClass().getName();
        CustomLogger.info(page);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @И("появилась страница \"(.+)\"")
    public void getPage(String pageName) {
        Builder.buildPage(DriverManager.getWD(ProcessingThread.getSessionId()).getClass().getSimpleName(),pageName);
        PageMapper.getPage(pageName);
    }

    @И("нажать на элемент \"(.+)\"")
    public void clickOnElement(String elementName) {
        Clickable clickable = PageMapper.getActivePage().getElement(elementName);
        clickable.click();
    }

    @И("в поле \"(.+)\" введено значение \"(.+)\"")
    public void sendText(String elementName,String text) {
        TextField element = PageMapper.getActivePage().getElement(elementName);
        element.setText(text);
    }

    @И("из списка \"(.+)\" нажать на элемент \"(.+)\"")
    public void selectCustomMenu(String listName,String nameMenu) {
        MenuGroup element = PageMapper.getActivePage().getElement(listName);
        element.select(nameMenu);
    }

}
