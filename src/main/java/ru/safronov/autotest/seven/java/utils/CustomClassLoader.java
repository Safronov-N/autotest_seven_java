package ru.safronov.autotest.seven.java.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class CustomClassLoader extends ClassLoader {
    public CustomClassLoader(){}
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        File f = new File(name + ".class");
        if (!f.isFile())
            logger.error(String.format("Класс %s не найден", name), new ClassNotFoundException());

        InputStream ins = null;
        try {
            ins = new BufferedInputStream(new FileInputStream(f));
            byte[] b = new byte[(int) f.length()];
            int length = ins.read(b);
            int indexOf = name.lastIndexOf(File.separator)+1;
            logger.debug(String.format("Создание класса '%s'", name.substring(indexOf)));
            return defineClass(null, b, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClassNotFoundException("Проблемы с байт кодом");
        } finally {
            try {
                Objects.requireNonNull(ins).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
