/*
 * Copyright (c) 2024-2026 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Debug.trace(e);
            return false;
        }
        return true;
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
        return null;
    }

    public static @Nullable Class<?> getClass(Class<?> clazz, String className) {
        while (clazz != Object.class) {
            if (clazz.getSimpleName().equals(className)) {
                return clazz;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static <T> @Nullable T getValue(Object object, String fieldName, Class<T> cast) {
        try {
            Field field = getField(object.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                return (T) field.get(object);
            }
        } catch (IllegalAccessException e) {
            Debug.trace(e);
            return null;
        }

        return null;
    }

    public static @Nullable Object getValue(Object object, String fieldName) {
        try {
            Field field = getField(object.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(object);
            }
        } catch (IllegalAccessException e) {
            Debug.trace(e);
            return null;
        }

        return null;
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
        }

        return null;
    }

    public static @Nullable Pair<Field, Class<?>> getDeclaredFieldsRecursively(
            Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return new Pair<>(field, clazz);
        } catch (Exception e) {
            clazz = clazz.getSuperclass();
            if (clazz == null) {
                return null;
            } else {
                return getDeclaredFieldsRecursively(clazz, fieldName);
            }
        }
    }

    public static @Nullable Constructor<?> getConstructor(
            Class<?> clazz, @Nullable Class<?> @Nullable ... parameterTypes) {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            Debug.trace(e);
            return null;
        }
    }

    @Nullable
    public static Object invokeMethod(
            Object object, String methodName, @Nullable Object @Nullable ... args) {
        try {
            Method method;
            if (args == null) {
                method = getMethod(object.getClass(), methodName, 1);
            } else {
                boolean containsNull = false;
                for (Object arg : args) {
                    if (arg == null) {
                        containsNull = true;
                        break;
                    }
                }

                if (containsNull) {
                    method = getMethod(object.getClass(), methodName, args.length);
                } else {
                    method = getMethod(
                            object.getClass(),
                            methodName,
                            Arrays.stream(args)
                                    .filter(Objects::nonNull)
                                    .map(Object::getClass)
                                    .toArray(Class[]::new)
                    );
                }
            }

            if (method != null) {
                method.setAccessible(true);
                return method.invoke(object, args);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            Debug.trace(e);
        }
        return null;
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
                        if (method.getParameterTypes()[i] != parameterTypes[i]) {
                            match = false;
                            break;
                        }
                    }
                    // normal match, find an adaptable method, which args are adaptable
                    if (!match) {
                        match = true;
                        for (int i = 0; i < parameterTypes.length; i++) {
                            if (!method.getParameterTypes()[i].isAssignableFrom(parameterTypes[i])) {
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
        return null;
    }

    @Nullable
    public static Object invokeStaticMethod(
            Class<?> clazz, String methodName, @Nullable Object @Nullable ... args) {
        try {
            Method method;
            if (args == null) {
                method = getMethod(clazz, methodName, 1);
            } else {
                boolean containsNull = false;
                for (Object arg : args) {
                    if (arg == null) {
                        containsNull = true;
                        break;
                    }
                }

                if (containsNull) {
                    method = getMethod(clazz, methodName, args.length);
                } else {
                    method = getMethod(
                            clazz,
                            methodName,
                            Arrays.stream(args)
                                    .filter(Objects::nonNull)
                                    .map(Object::getClass)
                                    .toArray(Class[]::new)
                    );
                }
            }
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(null, args);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            Debug.trace(e);
        }
        return null;
    }
}
