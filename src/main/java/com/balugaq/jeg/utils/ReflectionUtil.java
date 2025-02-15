package com.balugaq.jeg.utils;

import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author Final_ROOT
 */
@SuppressWarnings({"unchecked", "unused"})
@UtilityClass
public class ReflectionUtil {

    public static boolean setValue(@NotNull Object object, @NotNull String field, Object value) {
        try {
            Field declaredField = object.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static <T> boolean setStaticValue(@NotNull Class<T> clazz, @NotNull String field, Object value) {
        try {
            Field declaredField = clazz.getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(null, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static <T> @Nullable T getStaticValue(@NotNull Class<T> clazz, @NotNull String field) {
        try {
            Field declaredField = clazz.getDeclaredField(field);
            declaredField.setAccessible(true);
            return (T) declaredField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static @Nullable Method getMethod(@NotNull Class<?> clazz, String methodName, boolean noargs) {
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

    public static @Nullable Method getMethod(@NotNull Class<?> clazz, String methodName) {
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

    public static @Nullable Method getMethod(@NotNull Class<?> clazz, String methodName, int parameterCount) {
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

    public static @Nullable Method getMethod(@NotNull Class<?> clazz, String methodName, Class<?> @NotNull ... parameterTypes) {
        while (clazz != Object.class) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.getParameterTypes().length == parameterTypes.length) {
                    boolean match = true;
                    for (int i = 0; i < parameterTypes.length; i++) {
                        if (method.getParameterTypes()[i] != parameterTypes[i]) {
                            match = false;
                            break;
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

    public static @Nullable Field getField(@NotNull Class<?> clazz, String fieldName) {
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

    public static @Nullable Object getValue(@NotNull Object object, @NotNull String fieldName) {
        try {
            Field field = getField(object.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(object);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public static <T, V> @Nullable T getProperty(Object o, @NotNull Class<V> clazz, String fieldName) throws IllegalAccessException {
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

    public static @Nullable Pair<Field, Class<?>> getDeclaredFieldsRecursively(@NotNull Class<?> clazz, @NotNull String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return new Pair<>(field, clazz);
        } catch (Throwable e) {
            clazz = clazz.getSuperclass();
            if (clazz == null) {
                return null;
            } else {
                return getDeclaredFieldsRecursively(clazz, fieldName);
            }
        }
    }

    public static @Nullable Constructor<?> getConstructor(@NotNull Class<?> clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static Object invokeMethod(@NotNull Object object, @NotNull String methodName, Object @NotNull ... args) {
        try {
            Method method = getMethod(object.getClass(), methodName, args.length);
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(object, args);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Object invokeStaticMethod(@NotNull Class<?> clazz, @NotNull String methodName, Object @NotNull ... args) {
        try {
            Method method = getMethod(clazz, methodName, args.length);
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(null, args);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
