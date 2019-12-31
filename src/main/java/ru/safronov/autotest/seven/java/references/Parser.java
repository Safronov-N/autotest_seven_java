package ru.safronov.autotest.seven.java.references;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.io.FileUtils;
import ru.safronov.autotest.seven.java.builder.StaxStreamProcessor;
import ru.safronov.autotest.seven.java.exceptions.ConfigException;

public class Parser {
    private String content;
    private InputStream stream;
    private XMLStreamReader reader;

    private Parser() {

    }

    private void initReader() throws XMLStreamException, IOException {
        if (stream!=null)
            stream.reset();
        stream = new ByteArrayInputStream(content.getBytes());
        StaxStreamProcessor processor = new StaxStreamProcessor(stream);
        reader = processor.getReader();
    }

    public static Parser instance;

    static {
        instance = new Parser();
    }

    /**
     * правила - единственный элемент который не может ссылаться на другое правило
     */
    private void parseRule() throws XMLStreamException {
        if (reader.getEventType()==1 && reader.hasName() && reader.getLocalName().equals("Rule")) {
            Definition rule = new Definition();
            for (int i = 0; i < reader.getAttributeCount(); i++) {
                rule.content.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            }
            Namespace.instance.putRule(rule);
        }
        reader.next();
    }

    /**
     * читает полный список правил для текущего файла
     */
    private void parseRules() throws XMLStreamException {
        while (reader.hasNext() && reader.getEventType() != 2)
            parseRule();
    }

    private void parseInclude() throws XMLStreamException {
        if (reader.getEventType()==1 && reader.hasName() && reader.getLocalName().equals("Include")) {
            Definition include = new Definition();
            for (int i = 0; i < reader.getAttributeCount(); i++) {
                String ref=reader.getAttributeValue(null,"ref");
                if (ref!=null)
                    include.content.putAll(Namespace.instance.getRule(ref).content);
                include.content.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            }
            Namespace.instance.include(include);
        }
        reader.next();
    }

    private void parseIncludes() throws XMLStreamException {
        while (reader.getEventType() != 2) {
            parseInclude();
        }
    }

    private void parseDriver() throws XMLStreamException {
        if (reader.getEventType()==1 && reader.hasName() && reader.getLocalName().equals("Driver")) {
            Definition driver = new Definition();
            for (int i = 0; i < reader.getAttributeCount(); i++) {
                String ref=reader.getAttributeValue(null,"ref");
                if (ref!=null)
                    driver.content.putAll(Namespace.instance.getRule(ref).content);
                driver.content.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            }
            Namespace.instance.addDriver(driver);
        }
        reader.next();
    }

    private void parseDrivers() throws XMLStreamException {
        while (reader.getEventType() != 2) {
            parseDriver();
        }
    }

    private void parsePage() throws XMLStreamException {
        if (reader.hasName() && reader.getLocalName().equals("Page")) {
            Definition page = new Definition();
            for (int i = 0; i < reader.getAttributeCount(); i++) {
                String ref=reader.getAttributeValue(null,"ref");
                if (ref!=null)
                    page.content.putAll(Namespace.instance.getRule(ref).content);
                page.content.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                Namespace.instance.addPage(page);
            }
            reader.next();
            page.content.put("Elements",new HashMap<String,Definition>());
            while (reader.getEventType() != 2 || !"Page".equals(reader.getLocalName())) {
                parseElement(page);
            }
        }
        reader.next();
    }

    private void parsePages() throws XMLStreamException {
        while (reader.getEventType() != 2) {
            parsePage();
        }
    }

    private void parseElement(Definition page) throws XMLStreamException {
        if (reader.getEventType()==1 && reader.hasName() && reader.getLocalName().equals("Element")) {
            Definition element = new Definition();
            for (int i = 0; i < reader.getAttributeCount(); i++) {
                String ref=reader.getAttributeValue(null,"ref");
                if (ref!=null)
                    element.content.putAll(Namespace.instance.getRule(ref).content);
                element.content.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            }
            HashMap<String, Definition> map= (HashMap<String, Definition>) page.getAttribute("Elements");
            map.put((String) element.getAttribute("bind"),element);
        }
        reader.next();
    }

    public void parse(String filePath) {
        //инициализируем сериализатор
        try {
            File file=new File(filePath);
            content= FileUtils.readFileToString(file, (String) null);
            initReader();
        } catch (XMLStreamException | IOException e) {
            throw new ConfigException("Путь к исходному файлу указан некорректно", e);
        }
        /*
                Правила чтения:
                Сначала читаем правила, они могут влиять на прочие определения
                Затем читаем импорты
                Затем читаем все остальное
             */
        try {
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("Rules")) {
                    parseRules();
                }
            }
            content=content.replaceAll("<Rules(\\w|\\W)*?/Rules>","");
            initReader();
        } catch (XMLStreamException | IOException e) {
            throw new ConfigException("Не удалось прочитать правила",e);
        }
        try {
            while (reader.hasNext()) {
                int event = reader.next();
                if (event==XMLEvent.START_ELEMENT)
                    switch (reader.getLocalName().toLowerCase()){
                        case "includes": parseIncludes(); break;
                        case "drivers": parseDrivers(); break;
                        case "pages": parsePages(); break;
                    }
            }
        } catch (XMLStreamException e) {
            throw new ConfigException("Не удалось прочитать файл",e);
        }
        Collection<Definition>definitions=Namespace.instance.getIncludes();
        Definition[] includes=new Definition[definitions.size()];
        includes=definitions.toArray(includes);
        for (Definition include : includes) {
            if (definitions.contains(include)) {
                definitions.remove(include);
                parse("src\\main\\resources\\" + include.getAttribute("path"));
            }
        }
    }


}
