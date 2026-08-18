/*
 * Copyright (c) 2024-2026 balugaq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.balugaq.jeg.utils;

import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Final_ROOT
 * @author balugaq
 * @author m1919810
 * @since 1.0
 */
@SuppressWarnings({"unchecked", "unused"})
@UtilityClass
@NullMarked
public class ReflectionUtil {

    @SuppressWarnings("UnusedReturnValue")
    public static boolean setValue(Object object, String field, @Nullable Object value) {
        try {
            Field declaredField = getField(object.getClass(), field);
            if (declaredField == null) {
                throw new NoSuchFieldException(field);
            }
            declaredField.setAccessible(true);
            declaredField.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Debug.trace(e);
            return false;
        }
        return true;
    }

    public static @Nullable Field getField(Class<?> clazz, String fieldName) {
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
        }
        Debug.debug("Field " + fieldName + " not found in " + clazz.getSimpleName());
        return null;
    }

    public static <T> boolean setStaticValue(Class<T> clazz, String field, @Nullable Object value) {
        try {
            Field declaredField = getField(clazz, field);
            if (declaredField == null) {
                throw new NoSuchFieldException(field);
            }
            declaredField.setAccessible(true);
            declaredField.set(null, value);
            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Debug.trace(e);
            return false;
        }
    }

    public static @Nullable Object getStaticValue(Class<?> clazz, String field) {
        try {
            Field declaredField = getField(clazz, field);
            if (declaredField == null) {
                throw new NoSuchFieldException(field);
            }
            declaredField.setAccessible(true);
            return declaredField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Debug.trace(e);
            return null;
        }
    }

    public static <T> @Nullable T getStaticValue(
        Class<?> clazz, String field, Class<T> cast) {
        try {
            Field declaredField = getField(clazz, field);
            if (declaredField == null) {
                throw new NoSuchFieldException(field);
            }
            declaredField.setAccessible(true);
            return (T) declaredField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Debug.trace(e);
            return null;
        }
    }

    public static @Nullable Method getMethod(Class<?> clazz, String methodName, boolean noargs) {
        while (clazz != Object.class) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && (!noargs || method.getParameterTypes().length == 0)) {
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        // noargs failed, try to find a method which has arguments
        return getMethod(clazz, methodName);
    }

    public static @Nullable Method getMethod(Class<?> clazz, String methodName) {
        while (clazz != Object.class) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        Debug.debug("Method " + methodName + " not found in " + clazz.getSimpleName());
        return null;
    }

    public static @Nullable Class<?> getClass(Class<?> clazz, String className) {
        while (clazz != Object.class) {
            if (clazz.getSimpleName().equals(className)) {
                return clazz;
            }
            clazz = clazz.getSuperclass();
        }
        Debug.debug("Superclass " + className + " not found in " + clazz.getSimpleName());
        return null;
    }

    public static <T> @Nullable T getValue(Object object, String fieldName, Class<T> cast) {
        try {
            Field field = getField(object.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                return (T) field.get(object);
            } else {
                Debug.debug("Value not found: field=" + fieldName + " class=" + object.getClass().getSimpleName());
                return null;
            }
        } catch (IllegalAccessException e) {
            Debug.trace(e);
            return null;
        }
    }

    public static @Nullable Object getValue(Object object, String fieldName) {
        try {
            Field field = getField(object.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(object);
            } else {
                Debug.debug("Value not found: field=" + fieldName + ", class=" + object.getClass().getSimpleName());
                return null;
            }
        } catch (IllegalAccessException e) {
            Debug.trace(e);
            return null;
        }
    }

    public static <T, V> @Nullable T getProperty(Object o, Class<V> clazz, String fieldName)
        throws IllegalAccessException {
        Field field = getField(clazz, fieldName);
        if (field != null) {
            boolean b = field.canAccess(o);
            field.setAccessible(true);
            Object result = field.get(o);
            field.setAccessible(b);
            return (T) result;
        } else {
            Debug.debug("Cannot get property: field=" + fieldName + ", clazz=" + clazz.getSimpleName());
            return null;
        }
    }

    /**
     * @author m1919810
     */
    public static @Nullable Pair<Field, Class<?>> getDeclaredFieldsRecursively(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return new Pair<>(field, clazz);
        } catch (Exception e) {
            clazz = clazz.getSuperclass();
            if (clazz == null) {
                Debug.debug("Field not found: field=" + fieldName + " class=" + clazz);
                return null;
            } else {
                return getDeclaredFieldsRecursively(clazz, fieldName);
            }
        }
    }

    public static @Nullable Constructor<?> getConstructor(Class<?> clazz, @Nullable Class<?> @Nullable ... parameterTypes) {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            Debug.trace(e);
            return null;
        }
    }

    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object instance, @Nullable Object @Nullable ... args) {
        try {
            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            Debug.trace(e);
        }
        return null;
    }

    @Nullable
    public static Object invokeStaticMethod(Method method, @Nullable Object @Nullable ... args) {
        return invokeMethod(method, (Object) null, args);
    }

    @Nullable
    public static Method findMethod(Class<?> clazz, String methodName, @Nullable Object @Nullable ... args) {
        if (args == null) {
            // it is called like: `findMethod(clazz, methodName, null)`, null refers to a null argument
            return getMethod(clazz, methodName, 1);
        }

        boolean containsNull = false;
        for (Object arg : args) {
            if (arg == null) {
                containsNull = true;
                break;
            }
        }

        if (containsNull) {
            // find by parameter count
            return getMethod(clazz, methodName, args.length);
        }

        // find by parameter types
        return getMethod(
            clazz,
            methodName,
            Arrays.stream(args)
                .filter(Objects::nonNull)
                .map(Object::getClass)
                .toArray(Class[]::new)
        );
    }

    @Nullable
    public static Object invokeMethod(
        Object object, String methodName, @Nullable Object @Nullable ... args) {
        Method method = findMethod(object.getClass(), methodName, args);

        if (method == null) {
            Debug.debug("Method not found: " + methodName);
            return null;
        }

        return invokeMethod(method, object, args);
    }

    public static @Nullable Method getMethod(
        Class<?> clazz,
        String methodName,
        @Range(from = 0, to = Short.MAX_VALUE) int parameterCount) {
        while (clazz != Object.class) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.getParameterTypes().length == parameterCount) {
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        Debug.debug("Method not found: name=" + methodName + ", class=" + clazz + ", parameterCount=" + parameterCount);
        return null;
    }

    public static @Nullable Method getMethod(
        Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        while (clazz != Object.class) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.getParameterTypes().length == parameterTypes.length) {
                    boolean match = true;
                    // exact match
                    for (int i = 0; i < parameterTypes.length; i++) {
                        if (wrapClass(method.getParameterTypes()[i]) != wrapClass(parameterTypes[i])) {
                            match = false;
                            break;
                        }
                    }
                    // normal match, find an adaptable method, which args are adaptable
                    if (!match) {
                        match = true;
                        for (int i = 0; i < parameterTypes.length; i++) {
                            if (!wrapClass(method.getParameterTypes()[i]).isAssignableFrom(wrapClass(parameterTypes[i]))) {
                                match = false;
                                break;
                            }
                        }
                    }

                    if (match) {
                        return method;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        Debug.debug("Method not found: name=" + methodName + ", class=" + clazz + ", parameterTypes=" + Arrays.toString(parameterTypes));
        return null;
    }

    public static Class<?> wrapClass(Class<?> clazz) {
        return !clazz.isPrimitive()
            ? clazz
            : switch (clazz.getName()) {
            case "boolean" -> Boolean.class;
            case "byte" -> Byte.class;
            case "char" -> Character.class;
            case "double" -> Double.class;
            case "float" -> Float.class;
            case "int" -> Integer.class;
            case "long" -> Long.class;
            case "short" -> Short.class;
            default -> clazz;
        };
    }

    @Nullable
    public static Object invokeStaticMethod(
        Class<?> clazz, String methodName, @Nullable Object @Nullable ... args) {

        Method method = findMethod(clazz, methodName, args);
        if (method == null) {
            return null;
        }

        return invokeStaticMethod(method, args);
    }

    public static Class<?> getCallerClass() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < 4) return Object.class;
        try {
            return Class.forName(stackTrace[3].getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCallerClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace.length < 4 ? "null" : stackTrace[3].getClassName();
    }
}
