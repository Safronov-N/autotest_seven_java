package ru.safronov.autotest.seven.java.builder;

import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.commons.io.FileUtils;
import ru.safronov.autotest.seven.java.elementModels.ElementPattern;
import ru.safronov.autotest.seven.java.references.Definition;
import ru.safronov.autotest.seven.java.references.Namespace;
import ru.safronov.autotest.seven.java.utils.CustomClassLoader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import ru.safronov.autotest.seven.java.utils.StringUtils;

public class Builder {
    public static synchronized Class buildPage(String driverType, String pageName) {
        File file = new File(String.format("./src/main/java/ru/safronov/autotest/seven/java/page/%s", driverType.replaceAll("Driver", "")));
        file.mkdir();
        String basicPage = null;
        try {
            basicPage = FileUtils.readFileToString(new File("./src/main/resources/PAgePattern.pattern"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        basicPage = Objects.requireNonNull(basicPage)
                .replaceFirst("@Page\\(\"(.*)\"\\)", "@Page\\(\"" + pageName + "\"\\)")
                .replaceAll("driverType", driverType)
                .replaceAll("TYPEPATH", driverType.replaceAll("Driver", ""));
        basicPage = basicPage.replaceAll("PageName", pageName);
        int indexOfSelectableElement = basicPage.indexOf('{');
        StringBuilder stringBuilder = new StringBuilder(basicPage);
        HashMap<String, Definition> elementList = Namespace.instance.getPage(pageName).getAttribute("Elements");
        String element = "\n\r@Element(\"elementBindHolder\")\n\rpublic typeHolder elementNameHolder(){\n\r return elementFactory(typeHolder.class, By.xpath(\"pathHolder\"));\n\r}\n\r";
        for (Entry<String,Definition> elementPattern : elementList.entrySet()) {
            Definition definition=elementPattern.getValue();
            String xpath=definition.getAttribute(String.format("%sPath",
                driverType.replaceAll("Driver", "").toLowerCase()));
            if (xpath==null)
                xpath=definition.getAttribute("defaultXpath");
            stringBuilder.insert(indexOfSelectableElement + 1, element.replaceAll("elementNameHolder",
                StringUtils.capitalize(elementPattern.getValue().<String>getAttribute("name")))
                    .replaceAll("pathHolder", xpath)
                    .replaceAll("typeHolder", StringUtils.capitalize(elementPattern.getValue().<String>getAttribute("type")))
                    .replaceAll("elementBindHolder", StringUtils.capitalize(elementPattern.getKey()))
                    .replaceAll("driverType", driverType));
        }
        String pageClassname = String.format("./src/main/java/ru/safronov/autotest/seven/java/page/%s/%s.java", driverType.replaceAll("Driver", ""), pageName);
        File pageFile = new File(pageClassname);
        try {
            FileUtils.write(pageFile, stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Properties properties = System.getProperties();
        String sep = properties.getProperty("file.separator");
        String jrePath = properties.getProperty("java.home");
        String classFileDirectory = String.format("target%sclasses%s", sep, sep);
        String format = System.getProperty("os.name").toLowerCase().contains("windows")
                ? "\"%s\""
                : "%s";
        String javac = jrePath + sep + "bin" + sep + "javac";
        javac = String.format(format, javac);
        javac += " -J-Dfile.encoding=windows1251 -encoding utf8";
        String classPath = System.getProperty("java.class.path").replaceAll("\\s", "\\\\ ");
        String command = String.format("%s -d %s -sourcepath src -classpath \"%s\" %s", javac, classFileDirectory, classPath,
            pageFile.getPath());
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Objects.requireNonNull(process).waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CustomClassLoader customClassLoader = new CustomClassLoader();
        try {
            return customClassLoader.findClass(String.format("%s%sru%ssafronov%sautotest%sseven%sjava%spage%s%s%s%s", classFileDirectory, sep, sep, sep,sep, sep, sep, sep, driverType.replaceAll("Driver", ""), sep, pageName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Удаляет все созданные страницы
     */
    public static void destroyPages() throws IOException, XMLStreamException {
        try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PAgeXmlSources.xml")))) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    String tagName = reader.getLocalName();
                    if (tagName.equals("Page")) {
                        String pageName = reader.getAttributeValue(null, "name");
                        File javaFile = new File(String.format("./src/main/java/ru/safronov/autotest/seven/java/page/%s.java", pageName));
                        javaFile.deleteOnExit();
                    }
                }
            }
        }
    }
}



