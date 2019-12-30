package ru.safronov.autotest.seven.java.utils;


import ru.safronov.autotest.seven.java.interfaces.BiPredicate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Кастамный класс для работы с рефлексией
 */
public class CustomReflection {


    public static Set<Method> getMethods(Class clazz) {
        Set<Method> methods = new HashSet<>();
        if (clazz.getSuperclass() != Object.class) {
            methods.addAll(getMethods(clazz.getSuperclass()));
        }
        Set<Method> set = new HashSet<>();
        for (Method method : clazz.getDeclaredMethods()) {
            set.add(method);
        }
        methods.addAll(set);
        for (Method o : methods) {
            o.setAccessible(true);
        }
        return methods;
    }

    public static Set<Constructor> getConstructors(Class clazz) {
        Set<Constructor> constructors = new HashSet<>();
        Set<Constructor> set = new HashSet<>();
        for (Constructor constructor : clazz.getConstructors()) {
            set.add(constructor);
        }
        constructors.addAll(set);
        for (Constructor o : constructors) {
            o.setAccessible(true);
        }
        return constructors;
    }

    private static Optional<Method> getOptionalMethod(Class clazz, String methodName, Class... argTypes) {
        BiPredicate<Class[], Class[]> argMathcer = new BiPredicate<Class[], Class[]>() {
            @Override
            public boolean test(Class[] a1, Class[] a2) {
                if (a1 == a2) {
                    return true;
                }
                if ((a1 == null || a2 == null) || (a1.length != a2.length)) {
                    return false;
                }
                for (int i = 0; i < a1.length; i++) {
                    if (!a1[i].equals(a2[i])) {
                        return false;
                    }
                }
                return true;
            }
        };
        for (Method m : getMethods(clazz)) {
            if (m.getName().equals(methodName) && argMathcer.test(argTypes, m.getParameterTypes())) {
                return Optional.of(m);
            }
        }
        return Optional.ofNullable(null);
    }

    public static Set<Field> getFields(Class clazz) {
        Set<Field> result = new HashSet<>();
        if (clazz.getSuperclass() != Object.class) {
            result.addAll(getFields(clazz.getSuperclass()));
        }
        List<Field> list = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            list.add(field);
        }
        result.addAll(list);
        for (Field o : result) {
            o.setAccessible(true);
        }
        return result;
    }

    public static Method getMethod(Class clazz, String methodName, Class... argTypes) {
        Method result = getOptionalMethod(clazz, methodName, argTypes).get();
        if (result == null)
            throw new NullPointerException();
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeOr(Object object, String methodName, T defaultResult, Object... args) {
        try {
            Class[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
            return args.length == 0 ? (T) getMethod(object.getClass(), methodName).invoke(object) :
                    (T) getMethod(object.getClass(), methodName, argTypes).invoke(object, args);
        } catch (Exception e) {
            return defaultResult;
        }
    }

    private static Optional<Constructor> getOptionalConstructor(Class clazz, Class... argTypes) {
        BiPredicate<Class[], Class[]> argMatcher = new BiPredicate<Class[], Class[]>() {
            @Override
            public boolean test(Class[] a1, Class[] a2) {
                if (a1 == a2) {
                    return true;
                }
                if ((a1 == null || a2 == null) || (a1.length != a2.length)) {
                    return false;
                }
                for (int i = 0; i < a1.length; i++) {
                    if (!a1[i].equals(a2[i]) && !a2[i].isAssignableFrom(a1[i])) {
                        return false;
                    }
                }
                return true;
            }
        };
        for (Constructor m : getConstructors(clazz)) {
            if (argMatcher.test(argTypes, m.getParameterTypes())) {
                return Optional.of(m);
            }
        }
        return Optional.ofNullable(null);
    }


    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
    public static <T> T createNewInstanceOr(Class<T> clazz, T defaultValue, Object... args) {
        try {
            if (args.length == 0) {
                return (T) getOptionalConstructor(clazz).get().newInstance();
            }
            Class[] types = new Class[args.length];
            List<Class<? extends Object>> list = new ArrayList<>();
            for (Object arg : args) {
                Class<?> aClass = arg.getClass();
                list.add(aClass);
            }
            list.toArray(types);
            return (T) getOptionalConstructor(clazz, types).get().newInstance(args);
        } catch (Exception e) {
            if (defaultValue != null) {
                return defaultValue;
            }
            throw new RuntimeException(e);
        }
    }

    public static Class getClazz(Class clazz, String className) {
        Set<Class> classes = new HashSet<>();

        for (Class setClass : clazz.getDeclaredClasses()) {
            classes.add(setClass);
        }
        classes.addAll(classes);
        for (Class o : classes) {
            if (o.getSimpleName().equals(className))
                return o;
        }
        return null;
    }

    public static Field getField(Class clazz, String fieldName) {
        Field result = getOptionalField(clazz, fieldName).get();
        if (result == null)
            throw new NullPointerException();
        return result;
    }

    private static Optional<Field> getOptionalField(Class clazz, String fieldName) {
        BiPredicate<Class[], Class[]> argMatcher = new BiPredicate<Class[], Class[]>() {
            @Override
            public boolean test(Class[] a1, Class[] a2) {
                if (a1 == a2) {
                    return true;
                }
                if ((a1 == null || a2 == null) || (a1.length != a2.length)) {
                    return false;
                }
                for (int i = 0; i < a1.length; i++) {
                    if (!a1[i].equals(a2[i])) {
                        return false;
                    }
                }
                return true;
            }
        };
        for (Field o : getFields(clazz)) {
            if (o.getName().equals(fieldName)) {
                return Optional.of(o);
            }
        }
        return Optional.ofNullable(null);
    }


    @SuppressWarnings("unchecked")
    public static <T> T getFieldValueOr(Object object, String fieldName, T defaultResult) {
        try {
            return (T) getField(object.getClass(), fieldName).get(object);
        } catch (Exception e) {
            return defaultResult;
        }
    }
}
