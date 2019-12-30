package ru.safronov.autotest.seven.java.elementModels;

import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ElementPattern {
    private Map<String, String> attributes;

    public ElementPattern() {
        attributes = new HashMap<>();
    }

    public static ElementPattern of(XMLStreamReader reader) {
        ElementPattern result = new ElementPattern();
        int attributesCount = reader.getAttributeCount();
        for (int i = 0; i < attributesCount; i++) {
            String attributeName = reader.getAttributeLocalName(i);
            result.attributes.put(attributeName, reader.getAttributeValue(null, attributeName));
        }
        return result;
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

}
