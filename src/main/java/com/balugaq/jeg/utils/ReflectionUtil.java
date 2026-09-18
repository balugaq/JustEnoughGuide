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

import com.balugaq.jeg.api.objects.menu.VanillaInventoryWrapper;
import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.api.recipe_complete.source.ConsumeResult;
import com.balugaq.jeg.api.recipe_complete.source.ContainerConsumer;
import com.balugaq.jeg.implementation.option.RecipeFillingWithNearbyContainerGuideOption;
import com.balugaq.jeg.implementation.option.RecursiveRecipeFillingGuideOption;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.experimental.UtilityClass;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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

    // @formatter:off
    public static final int[] PLAYER_INVENTORY_AVAILABLE_SLOTS = new int[] {
            0,  1,  2,  3,  4,  5,  6,  7,  8, // storage slots
            9,  10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            40 // offhand slot
    };
    // @formatter:on
    public static final int RECIPE_DEPTH_THRESHOLD = 8;
    public static final ConcurrentHashMap<UUID, Map<ItemStack, Long>> missingMaterials = new ConcurrentHashMap<>();

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

    public static String getAmountString(Map.Entry<ItemStack, Long> entry, ItemStack itemStack) {
        long amount = entry.getValue();
        long stacks = amount / Math.max(1, itemStack.getMaxStackSize());
        long left = amount - stacks * Math.max(1, itemStack.getMaxStackSize());
        String amountString = "" + amount;
        if (amount > itemStack.getMaxStackSize()) {
            amountString += " ( " + stacks + " 组";
            if (left > 0) {
                amountString += " + " + left + " 个";
            }
            amountString += ")";
        }
        return amountString;
    }

    public static int[] mergeSlots(int[]... slots) {
        IntOpenHashSet set = new IntOpenHashSet();
        for (int[] slot : slots)
            for (int i : slot) set.add(i);
        return set.toIntArray();
    }

    public static void handleMissingMaterial(RecipeCompleteSession session) {
        var p = session.getPlayer();
        if (!p.isOnline()) {
            missingMaterials.clear();
            return;
        }

        var uuid = p.getUniqueId();
        var v = missingMaterials.get(uuid);
        if (v == null || v.isEmpty()) return;

        for (var entry : v.entrySet()) {
            ItemStack itemStack = entry.getKey();
            String amountString = getAmountString(entry, itemStack);
            var builder = Component.text().color(NamedTextColor.RED).append(Component.text("缺少 "));
            var itemBuilder = Component.text(ItemStackHelper.getDisplayName(itemStack));
            SlimefunItem sf = SlimefunItem.getByItem(itemStack);
            if (sf != null) {
                itemBuilder = itemBuilder
                    .hoverEvent(HoverEvent.showText(Component.text().color(NamedTextColor.YELLOW).append(Component.text("点击查看"))))
                    .clickEvent(ClickEvent.runCommand("/jeg viewitem " + sf.getId()));
            }
            builder.color(NamedTextColor.GRAY).append(itemBuilder);
            builder.append(Component.text().color(NamedTextColor.GREEN).append(Component.text(" x")).append(Component.text(amountString)));
            p.sendMessage(builder);
        }
    }

    public static boolean depthInRange(Player player, int depth) {
        return depth <= RecursiveRecipeFillingGuideOption.getDepth(player) && depth <= RECIPE_DEPTH_THRESHOLD;
    }

    public static @NonNegative long getItemStackFromPlayerInventory(RecipeCompleteSession session, ItemStack target, long need) {
        Player player = session.getPlayer();
        long total = need;
        long gotten = 0;

        // get from player inventory
        for (int i : PLAYER_INVENTORY_AVAILABLE_SLOTS) {
            ItemStack existingStack = player.getInventory().getItem(i);

            if (existingStack != null && existingStack.getType() != Material.AIR) {
                if (StackUtils.itemsMatch(existingStack, target)) {

                    int existingAmount = existingStack.getAmount();

                    if (existingAmount <= need) {
                        need -= existingAmount;
                        gotten += existingAmount;
                        player.getInventory().clear(i);
                    } else {
                        gotten += need;
                        existingStack.setAmount((int) (existingAmount - need));
                        player.getInventory().setItem(i, existingStack);
                        need = 0;
                    }

                    if (need <= 0) {
                        return gotten;
                    }
                } else {
                    for (var itemGetter : RecipeCompletableRegistry.getPlayerInventoryItemGetters()) {
                        long gt = itemGetter.getItemStack(session, target, existingStack, need);
                        need -= gt;
                        if (need <= 0) {
                            return gotten;
                        }
                    }
                }
            }
        }

        return gotten;
    }

    public static long countAmountFromPlayerInventory(RecipeCompleteSession session, ItemStack target) {
        Player player = session.getPlayer();
        long total = 0;

        // get from player inventory
        for (int i : PLAYER_INVENTORY_AVAILABLE_SLOTS) {
            ItemStack existingStack = player.getInventory().getItem(i);

            if (existingStack != null && existingStack.getType() != Material.AIR) {
                if (StackUtils.itemsMatch(existingStack, target)) {
                    int existingAmount = existingStack.getAmount();
                    total += existingAmount;
                } else {
                    for (var itemGetter : RecipeCompletableRegistry.getPlayerInventoryItemGetters()) {
                        long gotten = itemGetter.countItemStack(session, target, existingStack);
                        total += gotten;
                    }
                }
            }
        }

        return total;
    }

    public static @Nullable ItemStack getItemStackFromNearbyContainer(Player player, Location target, ItemStack itemStack) {
        return getItemStackFromNearbyContainer(player, target, itemStack, ItemStackUtil.getValidItemAmountAtLeastOne(itemStack));
    }

    public static <T> @Nullable T forEachNearbyContainer(Player player, Location target, int d, ItemStack itemStack, ContainerConsumer<T> consumer) {
        // get from nearby container
        for (int x = -d; x <= d; x++) {
            for (int y = -d; y <= d; y++) {
                for (int z = -d; z <= d; z++) {
                    Location bloc = player.getLocation().clone().add(x, y, z);
                    if (bloc.getBlockX() == target.getBlockX() && bloc.getBlockY() == target.getBlockY() && bloc.getBlockZ() == target.getBlockZ())
                        continue; // never include itself

                    if (!Slimefun.getProtectionManager().hasPermission(player, bloc, Interaction.INTERACT_BLOCK))
                        continue;

                    BlockMenu menu = StorageCacheUtils.getMenu(bloc);
                    if (menu == null) {
                        // check if it is vanilla container
                        BlockState state = bloc.getBlock().getState();
                        if (state instanceof Container container) {
                            menu = new VanillaInventoryWrapper(container.getInventory(), state);
                        } else {
                            continue;
                        }
                    }
                    int[] slots = mergeSlots(
                        menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.WITHDRAW, itemStack),
                        menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.INSERT, itemStack)
                    );
                    if (slots.length == 0) {
                        try {
                            var handlers = getValue(menu, "handlers", Map.class);
                            if (handlers == null) continue;
                            var set = handlers.keySet();
                            if (set.isEmpty()) continue;
                            var list = new IntArrayList(menu.getSize());
                            for (int i = 0; i < menu.getSize(); i++) list.add(i);
                            list.removeAll((Set<Integer>) set);
                            slots = list.toIntArray();
                        } catch (Exception e) {
                            continue;
                        }
                    }
                    for (int slot : slots) {
                        ItemStack itemStack1 = menu.getItemInSlot(slot);

                        if (itemStack1 != null
                            && itemStack1.getType() != Material.AIR
                            && StackUtils.itemsMatch(itemStack1, itemStack)) {
                            var rs = consumer.accept(slot, itemStack1, menu);
                            if (rs.breakLoop()) {
                                return rs.result();
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public static @Nullable ItemStack getItemStackFromNearbyContainer(Player player, Location target, ItemStack itemStack, int total) {
        AtomicInteger atomicAmount = new AtomicInteger(total);

        int d = RecipeFillingWithNearbyContainerGuideOption.getRadiusDistance(player);
        if (d <= 0) return null;

        var rs = forEachNearbyContainer(player, target, d, itemStack, (slot, itemStack1, menu) -> {
            int existing = itemStack1.getAmount();

            if (existing <= atomicAmount.get()) {
                atomicAmount.updateAndGet(a -> a - existing);
                menu.replaceExistingItem(slot, null);
            } else {
                itemStack1.setAmount(existing - atomicAmount.get());
                menu.replaceExistingItem(slot, itemStack1);
                atomicAmount.set(0);
            }

            if (atomicAmount.get() <= 0) {
                ItemStack clone = itemStack.clone();
                clone.setAmount(total);
                return new ConsumeResult<>(true, clone);
            }

            return ConsumeResult.continueIt();
        });

        if (rs != null) return rs;
        if (atomicAmount.get() == total) return null;
        ItemStack clone = itemStack.clone();
        clone.setAmount(total - atomicAmount.get());
        return clone;
    }

    @NonNegative
    public static long countAmountFromNearbyContainer(Player player, Location target, ItemStack itemStack) {
        int d = RecipeFillingWithNearbyContainerGuideOption.getRadiusDistance(player);
        if (d <= 0) return 0;

        AtomicLong total = new AtomicLong(0);
        var rs = forEachNearbyContainer(player, target, d, itemStack, (slot, itemStack1, menu) -> {
            int existing = itemStack1.getAmount();
            total.addAndGet(existing);

            return ConsumeResult.continueIt();
        });

        return total.get();
    }
}
