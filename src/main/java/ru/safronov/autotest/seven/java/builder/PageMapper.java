package ru.safronov.autotest.seven.java.builder;

import org.apache.commons.io.FileUtils;
import ru.safronov.autotest.seven.java.annotations.Page;
import ru.safronov.autotest.seven.java.page.AbstractPage;
import ru.safronov.autotest.seven.java.utils.CustomReflection;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PageMapper {
    private static class Key {
        private Key(Class<? extends AbstractPage> clazz, String sessionId) {
            this.clazz = clazz;
            this.sessionId = sessionId;
        }

        Class<? extends AbstractPage> clazz;
        String sessionId;

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != this.getClass())
                return false;
            Key key = (Key) o;
            return clazz.equals(key.clazz) && sessionId.equals(key.sessionId);
        }
    }

    private static volatile Map<String, Class<? extends AbstractPage>> classMap;
    private static volatile Map<Key, Object> objectClass;
    private static volatile Map<String, Object> threadlyActivePage;

    static {
        classMap = new ConcurrentHashMap<>();
        objectClass = new ConcurrentHashMap<>();
        threadlyActivePage = new ConcurrentHashMap<>();
        try {
            populatePage();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized AbstractPage getActivePage() {
        return (AbstractPage) threadlyActivePage.get(ProcessingThread.getSessionId());
    }

    @SuppressWarnings("unchecked")
    public static synchronized AbstractPage getPage(String className) {
        if (className == null)
            return null;
        else if (!classMap.containsKey(className))
            return null;
        Class clazz = classMap.get(className);
        Object instance;
        Key key = new Key(clazz, ProcessingThread.getSessionId());
        if ((instance = objectClass.get(key)) != null) {
            threadlyActivePage.put(ProcessingThread.getSessionId(), instance);
            return (AbstractPage) instance;
        } else {
            Object page = CustomReflection.createNewInstanceOr(clazz, null);
            objectClass.put(key, page);
            threadlyActivePage.put(ProcessingThread.getSessionId(), page);
            return (AbstractPage) page;
        }
    }

    private static synchronized void populatePage() throws ClassNotFoundException {
        String separator = System.getProperty("file.separator");
        String pagesPath = System.getProperty("user.dir") + separator + "src" + separator + "main";
        Collection<File> files = FileUtils.listFiles(new File(pagesPath), new String[]{"java"}, true);
        String sep = File.separator;
        Set<String> fileNames = new HashSet<>();
        Set<Class> pageClasses = new HashSet<>();
        for (File file : files) {
            fileNames.add(file.getPath().substring(file.getPath().indexOf("ru" + sep + "safronov"))
                    .replaceAll(System.getProperty("os.name")
                            .toLowerCase()
                            .contains("mac") ? sep : sep + sep, "\\.")
                    .replaceFirst("(.+)(\\.java)","$1"));
        }
        for (String fileName : fileNames) {
            Class clazz = Class.forName(fileName);
            if (clazz.isAnnotationPresent(Page.class))
                pageClasses.add(Class.forName(fileName));
        }
        for (Class clazz : pageClasses)
            classMap.put(((Page) clazz.getAnnotation(Page.class)).value(), clazz);
    }

    public static void drop(String sessionId) {
        for (Class<? extends AbstractPage> key : classMap.values()) {
            objectClass.remove(new Key(key, sessionId));
        }
    }
}
