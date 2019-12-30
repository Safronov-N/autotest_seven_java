package ru.safronov.autotest.seven.java.builder;

import cucumber.api.testng.TestNGCucumberRunner;

public class LocalTestNgRunner extends TestNGCucumberRunner implements Cloneable {
    public LocalTestNgRunner(Class clazz) {
        super(clazz);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }


}
