package ru.safronov.autotest.seven.java.references;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Definition {
    Map<String,Object> content;
    Definition(){
        content=new ConcurrentHashMap<>();
    }

    public Object getAttribute(String bind){
        return content.get(bind);
    }
}
