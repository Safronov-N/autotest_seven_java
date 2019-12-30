package ru.safronov.autotest.seven.java.config;


public enum Driver {
    Chrome,
    Firefox,
    IE;

    public static Driver of(String browser) {
        switch (browser.toUpperCase()){
            case "CHROME": return Chrome;
            case "FIREFOX": return Firefox;
            case "IE": return IE;
        }
        return null;
    }
}
