

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestClass {
    private ThreadLocal<Map<WebDriver,String>> local;
    @DataProvider(name = "testData")
    public Object[][] dataprovider() {
        return new Object[][]{{"ChromeDriver"}};
    }

    @BeforeTest
    public void setUp() {
        local = new ThreadLocal<>();
    }

    @AfterTest
    public void tearDown() {

    }



    @Test(dataProvider = "testData")
    public void test(String driverType) {
        System.out.println(System.getProperty("os.name"));
    }


}