package ru.safronov.autotest.seven.java.references;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Namespace {
    private Map<String,Definition> rules;
    private Set<Definition> includes;
    private Set<Definition> drivers;
    private Set<Definition> pages;
    private Namespace(){
        rules=new HashMap<>();
        includes=new HashSet<>();
        drivers=new HashSet<>();
        pages=new HashSet<>();
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
        pages.add(page);
    }

    public Collection<Definition> getIncludes() {
        return includes;
    }
}
