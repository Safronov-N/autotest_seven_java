package ru.safronov.autotest.seven.java.page.Chrome;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.safronov.autotest.seven.java.annotations.Element;
import ru.safronov.autotest.seven.java.annotations.Page;
import ru.safronov.autotest.seven.java.config.DriverManager;
import ru.safronov.autotest.seven.java.elementModels.Label;
import ru.safronov.autotest.seven.java.elementModels.TextField;
import ru.safronov.autotest.seven.java.page.AbstractPage;
import ru.safronov.autotest.seven.java.elementModels.Button;
@Page("LoginPage")
public class LoginPage extends AbstractPage {
@Element("Продолжить")
public Button nextBtnXpath(){
 return elementFactory(Button.class, By.xpath("//*[@id='identifierNext' or @id='passwordNext']"));
}

@Element("Пароль")
public TextField passwordXpath(){
 return elementFactory(TextField.class, By.xpath("//input[@type='password']"));
}

@Element("Логин")
public TextField emailXpath(){
 return elementFactory(TextField.class, By.xpath("//input[@type='email']"));
}

    public LoginPage() {
    }
}
