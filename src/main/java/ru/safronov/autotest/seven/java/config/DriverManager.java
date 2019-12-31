package ru.safronov.autotest.seven.java.config;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.service.DriverService;
import ru.safronov.autotest.seven.java.builder.StaxStreamProcessor;
import ru.safronov.autotest.seven.java.references.Definition;
import ru.safronov.autotest.seven.java.references.Namespace;
import ru.safronov.autotest.seven.java.utils.CustomLogger;
import ru.safronov.autotest.seven.java.utils.CustomReflection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public final class DriverManager {

    private static volatile Map<String, WebDriver> local;

    static {
        local = new ConcurrentHashMap<>();
    }

    public static synchronized WebDriver createWD(String driverType) {
        try {
            Definition definition = Namespace.instance.getDriver(driverType);
            String driverTypeName = definition.getAttribute("type");
            String packageName = definition.getAttribute("package-name");
            String shortDriverType = driverTypeName.substring(0, driverTypeName.indexOf("Driver"));
            if (packageName == null) {
                packageName = shortDriverType.toLowerCase();
            }
            String driverOptionsName = "org.openqa.selenium." + packageName.toLowerCase() + "." + shortDriverType + "Options";
            Class<?> driverOptionsClass = Class.forName(driverOptionsName);
            Object driverOptions = CustomReflection.createNewInstanceOr(driverOptionsClass, null);
            CustomLogger.debug(String.format("Created driverOptions '%s'", driverOptionsName));//ChromeOptions или FireFoxOptions
            String driverServiceName = "org.openqa.selenium." + packageName.toLowerCase() + "." + definition.getAttribute(
                "serviceType");
            Class<?> driverServiceClass = Class.forName(driverServiceName);
            Class driverBuilderClass = CustomReflection.getClazz(driverServiceClass, "Builder");
            Object driverBuilder = CustomReflection.createNewInstanceOr(driverBuilderClass, null);
            CustomReflection.invokeOr(driverBuilder, "usingDriverExecutable", null, new File(definition.<String>getAttribute(
                "filePath")));
            Object driverService = ((DriverService.Builder) driverBuilder).build();
            CustomLogger.debug(String.format("Created driverService '%s'", driverServiceName));
            CustomReflection.invokeOr(driverOptions, "setPageLoadStrategy", null, PageLoadStrategy.NORMAL);
            CustomReflection.invokeOr(driverOptions, "addArguments", null, "--start-maximized");
            String className = String.format("org.openqa.selenium.%s.%s", packageName
                .replaceAll("driver", ""), driverTypeName);
            Class<?> clazz = Class.forName(className);
            WebDriver result = (WebDriver) CustomReflection.createNewInstanceOr(clazz, null, driverService, driverOptions);
            result.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            return result;
        } catch (ClassNotFoundException e) {
            CustomLogger.fail("Не удалось создать драйвер", e);
        }

        return null;
    }

    public static synchronized WebDriver getWD(String sessionId) {
        return local.get(sessionId);
    }

    public static synchronized void setWD(String sessionId, WebDriver driver) {
        local.put(sessionId, Objects.requireNonNull(driver));
    }

    public static void drop() {
        for (WebDriver webDriver : local.values()) {
            webDriver.quit();
        }
    }


}
