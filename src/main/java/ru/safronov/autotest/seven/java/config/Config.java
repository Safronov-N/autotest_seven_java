package ru.safronov.autotest.seven.java.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties CONFIG_PROPERTIES;

    static {
        CONFIG_PROPERTIES = new Properties();
        try(InputStream configInputStream = new FileInputStream("src/main/resources/config.properties")){
            CONFIG_PROPERTIES.load(configInputStream);
        }catch (IOException e){
            throw new IllegalStateException("Файла нет", e);
        }
    }
    private Config(){

    }
    public static String getUrl(){return CONFIG_PROPERTIES.getProperty("url");}
}
