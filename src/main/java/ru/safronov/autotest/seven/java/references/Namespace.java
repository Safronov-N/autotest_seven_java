package ru.safronov.autotest.seven.java.references;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import ru.safronov.autotest.seven.java.exceptions.ConfigException;

public class Namespace {
    private Map<String,Definition> rules;
    private Set<Definition> includes;
    private Set<Definition> drivers;
    private Map<String,Definition> pages;
    private Namespace(){
        rules=new HashMap<>();
        includes=new HashSet<>();
        drivers=new HashSet<>();
        pages=new HashMap<>();
    }
    public static Namespace instance;
    static {
        instance=new Namespace();
    }

    public void putRule(Definition rule){
        String ref= String.valueOf(rule.getAttribute("ref"));
        rules.put(ref,rule);
        rule.content.remove("ref");
    }

    public void include(Definition include){
        includes.add(include);
    }

    public Definition getRule(String ref) {
        return rules.get(ref);
    }

    public void addDriver(Definition driver){
        drivers.add(driver);
    }

    public void addPage(Definition page){
        pages.put(String.valueOf(page.getAttribute("name")),page);
    }

    public Collection<Definition> getIncludes() {
        return includes;
    }

    public Definition getPage(String pageName) {
        Definition page=pages.get(pageName);
        if (page==null)
            throw new ConfigException(String.format("Не существует страницы '%s'",pageName));
        return page;
    }

    public Definition getDriver(String driverType) {
        for (Definition definition:drivers)
            if (driverType.equals(definition.getAttribute("type")))
                return definition;
            throw new ConfigException(String.format("Не существует драйвера с типом '%s'",driverType));
    }
}
