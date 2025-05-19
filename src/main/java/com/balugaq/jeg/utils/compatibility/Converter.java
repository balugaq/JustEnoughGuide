/*
 * Copyright (c) 2024-2025 balugaq
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

package com.balugaq.jeg.utils.compatibility;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author balugaq
 * @since 1.4
 */
@ApiStatus.Experimental
public class Converter {
    public static final ItemStack AIR = new ItemStack(Material.AIR);
    public static final @Nullable ItemGetter methodHandleSlimefunItemStack_item = createItemGetter();

    /**
     * Converts a SlimefunItemStack to a Bukkit ItemStack.
     *
     * @param slimefunItemStack the SlimefunItemStack to convert
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(SlimefunItemStack slimefunItemStack) {
        return asBukkit(slimefunItemStack);
    }

    /**
     * Converts a Bukkit ItemStack to another Bukkit ItemStack.
     *
     * @param itemStack the Bukkit ItemStack to convert
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack) {
        return new CustomItemStack(itemStack).asBukkit();
    }

    /**
     * Converts a Material to a Bukkit ItemStack.
     *
     * @param material the Material to convert
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull Material material) {
        return new CustomItemStack(material).asBukkit();
    }

    /**
     * Converts a Bukkit ItemStack to another Bukkit ItemStack with custom metadata.
     *
     * @param itemStack        the Bukkit ItemStack to convert
     * @param itemMetaConsumer the consumer to modify the item metadata
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack, @NotNull Consumer<ItemMeta> itemMetaConsumer) {
        return new CustomItemStack(itemStack, itemMetaConsumer).asBukkit();
    }

    /**
     * Converts a Material to a Bukkit ItemStack with custom metadata.
     *
     * @param material the Material to convert
     * @param meta     the consumer to modify the item metadata
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull Material material, @NotNull Consumer<ItemMeta> meta) {
        return new CustomItemStack(material, meta).asBukkit();
    }

    /**
     * Converts a Bukkit ItemStack to another Bukkit ItemStack with a name and lore.
     *
     * @param itemStack the Bukkit ItemStack to convert
     * @param name      the name of the item
     * @param lore      the lore of the item
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack, @Nullable String name, @NotNull String @NotNull ... lore) {
        return new CustomItemStack(itemStack, name, lore).asBukkit();
    }

    /**
     * Converts a Bukkit ItemStack to another Bukkit ItemStack with a color, name, and lore.
     *
     * @param itemStack the Bukkit ItemStack to convert
     * @param color     the color of the item
     * @param name      the name of the item
     * @param lore      the lore of the item
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack, Color color, @Nullable String name, String @NotNull ... lore) {
        return new CustomItemStack(itemStack, color, name, lore).asBukkit();
    }

    /**
     * Converts a Material to a Bukkit ItemStack with a name and lore.
     *
     * @param material the Material to convert
     * @param name     the name of the item
     * @param lore     the lore of the item
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull Material material, String name, String... lore) {
        return new CustomItemStack(material, name, lore).asBukkit();
    }

    /**
     * Converts a Material to a Bukkit ItemStack with a name and lore.
     *
     * @param material the Material to convert
     * @param name     the name of the item
     * @param lore     the lore of the item
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull Material material, String name, @NotNull List<String> lore) {
        return new CustomItemStack(material, name, lore).asBukkit();
    }

    /**
     * Converts a Bukkit ItemStack to another Bukkit ItemStack with a list of lore.
     *
     * @param itemStack the Bukkit ItemStack to convert
     * @param list      the list of lore
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack, @NotNull List<String> list) {
        return new CustomItemStack(itemStack, list).asBukkit();
    }

    /**
     * Converts a Material to a Bukkit ItemStack with a list of lore.
     *
     * @param material the Material to convert
     * @param list     the list of lore
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull Material material, @NotNull List<String> list) {
        return new CustomItemStack(material, list).asBukkit();
    }

    /**
     * Converts a Bukkit ItemStack to another Bukkit ItemStack with a specified amount.
     *
     * @param itemStack the Bukkit ItemStack to convert
     * @param amount    the amount of the item
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack, @Range(from = 1, to = Integer.MAX_VALUE) int amount) {
        return new CustomItemStack(itemStack, amount).asBukkit();
    }

    /**
     * Converts a Bukkit ItemStack to another Bukkit ItemStack with a specified Material.
     *
     * @param itemStack the Bukkit ItemStack to convert
     * @param material  the Material of the item
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack, @NotNull Material material) {
        return new CustomItemStack(itemStack, material).asBukkit();
    }

    public static @NotNull ItemStack getItem(@NotNull Material material, @NotNull String name, Consumer<ItemMeta> consumer) {
        return new CustomItemStack(new CustomItemStack(material, name).asBukkit(), consumer).asBukkit();
    }

    /**
     * Creates an ItemGetter using MethodHandle.
     *
     * @return the created ItemGetter
     */
    private static @Nullable ItemGetter createItemGetter() {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodType mt = MethodType.methodType(ItemStack.class);
            MethodHandle handle = lookup.findVirtual(SlimefunItemStack.class, "item", mt);
            return (ItemGetter) LambdaMetafactory.metafactory(
                    lookup, "getItem", MethodType.methodType(ItemGetter.class),
                    handle.type().generic(), handle, handle.type()
            ).getTarget().invokeExact();
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Converts a SlimefunItemStack to a Bukkit ItemStack.
     *
     * @param item the SlimefunItemStack to convert
     * @return the converted Bukkit ItemStack
     */
    @NotNull
    public static ItemStack asBukkit(@Nullable SlimefunItemStack item) {
        if (item == null) {
            return AIR.clone();
        }

        ItemStack itemStack;
        if (ItemStack.class.isInstance(item)) {
            itemStack = ItemStack.class.cast(item);
        } else {
            if (methodHandleSlimefunItemStack_item != null) {
                try {
                    itemStack = ItemStack.class.cast(methodHandleSlimefunItemStack_item.getItem(item));
                } catch (Throwable e) {
                    return AIR.clone();
                }
            } else {
                return AIR.clone();
            }
        }

        ItemStack bukkitItem = new ItemStack(itemStack.getType());
        bukkitItem.setAmount(item.getAmount());
        if (item.hasItemMeta()) {
            bukkitItem.setItemMeta(item.getItemMeta());
        }

        return bukkitItem;
    }

    @FunctionalInterface
    public interface ItemGetter {
        ItemStack getItem(SlimefunItemStack item);
    }
}
