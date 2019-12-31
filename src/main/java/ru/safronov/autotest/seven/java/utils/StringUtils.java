package ru.safronov.autotest.seven.java.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String capitalize(String s){
        if (s==null )
            return s;
        Matcher matcher=Pattern.compile("(\\W+)?(\\w)(.*)", Pattern.UNICODE_CHARACTER_CLASS).matcher(s);
        if (!matcher.find())
            return s;
        String before=matcher.group(1);
        return before ==null
            ? matcher.group(2).toUpperCase() + matcher.group(3)
            : matcher.group(1) + matcher.group(2).toUpperCase() + matcher.group(3);
    }
}
