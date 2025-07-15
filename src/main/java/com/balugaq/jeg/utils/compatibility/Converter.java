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

import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author balugaq
 * @since 1.4
 */
@SuppressWarnings("unused")
@ApiStatus.Experimental
public class Converter {
    public static final ItemStack AIR = new ItemStack(Material.AIR);

    public static @NotNull Builder builder() {
        return new Builder();
    }

    /**
     * Converts a {@link SlimefunItemStack} to a Bukkit ItemStack.
     *
     * @param slimefunItemStack the {@link SlimefunItemStack} to convert
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull SlimefunItemStack slimefunItemStack) {
        return asBukkit(slimefunItemStack);
    }

    /**
     * Converts a {@link io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack} to a Bukkit ItemStack.
     *
     * @param cis the {@link io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack} to convert
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack cis) {
        return asBukkit(cis);
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
    public static @NotNull ItemStack getItem(
            @NotNull ItemStack itemStack, @NotNull Consumer<ItemMeta> itemMetaConsumer) {
        return new CustomItemStack(itemStack, itemMetaConsumer).asBukkit();
    }

    /**
     * Converts a Material to a Bukkit ItemStack with custom metadata.
     *
     * @param material the Material to convert
     * @param consumer the consumer to modify the item metadata
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(@NotNull Material material, @NotNull Consumer<ItemMeta> consumer) {
        return new CustomItemStack(material, consumer).asBukkit();
    }

    /**
     * Converts a Bukkit ItemStack to another Bukkit ItemStack with a name and lore.
     *
     * @param itemStack the Bukkit ItemStack to convert
     * @param name      the name of the item
     * @param lore      the lore of the item
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(
            @NotNull ItemStack itemStack, @Nullable String name, @NotNull List<String> lore) {
        return new CustomItemStack(itemStack, name, lore).asBukkit();
    }

    /**
     * Converts a Bukkit ItemStack to another Bukkit ItemStack with a name and lore.
     *
     * @param itemStack the Bukkit ItemStack to convert
     * @param name      the name of the item
     * @param lore      the lore of the item
     * @return the converted Bukkit ItemStack
     */
    public static @NotNull ItemStack getItem(
            @NotNull ItemStack itemStack, @Nullable String name, @NotNull String @NotNull ... lore) {
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
    public static @NotNull ItemStack getItem(
            @NotNull ItemStack itemStack, Color color, @Nullable String name, @NotNull String @NotNull ... lore) {
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
    public static @NotNull ItemStack getItem(
            @NotNull Material material, @Nullable String name, @NotNull String @NotNull ... lore) {
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
    public static @NotNull ItemStack getItem(
            @NotNull ItemStack itemStack, @Range(from = 1, to = Integer.MAX_VALUE) int amount) {
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

    public static @NotNull ItemStack getItem(
            @NotNull Material material, @NotNull String name, @NotNull Consumer<ItemMeta> consumer) {
        return new CustomItemStack(new CustomItemStack(material, name).asBukkit(), consumer).asBukkit();
    }

    /**
     * Converts a {@link SlimefunItemStack} to a Bukkit ItemStack.
     *
     * @param item the {@link SlimefunItemStack} to convert
     * @return the converted Bukkit ItemStack
     */
    @SuppressWarnings({"RedundantClassCall", "ConstantValue"})
    @NotNull
    public static ItemStack asBukkit(@Nullable SlimefunItemStack item) {
        if (item == null) {
            return AIR.clone();
        }

        ItemStack itemStack;
        if (ItemStack.class.isInstance(item)) {
            itemStack = ItemStack.class.cast(item);
        } else {
            try {
                itemStack = ReflectionUtil.getValue(item, "item", ItemStack.class);
                if (itemStack == null) throw new Throwable();
            } catch (Throwable ignored) {
                try {
                    itemStack = (ItemStack) ReflectionUtil.invokeMethod(item, "getItem");
                    if (itemStack == null) throw new Throwable();
                } catch (Throwable ignored2) {
                    try {
                        itemStack = (ItemStack) ReflectionUtil.invokeMethod(item, "item");
                        if (itemStack == null) throw new Throwable();
                    } catch (Throwable ignored3) {
                        return AIR.clone();
                    }
                }
            }
        }

        ItemStack bukkitItem = new ItemStack(itemStack.getType());
        bukkitItem.setAmount(item.getAmount());
        if (item.hasItemMeta()) {
            bukkitItem.setItemMeta(item.getItemMeta());
        }

        return bukkitItem;
    }

    /**
     * Converts a {@link io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack} to a Bukkit ItemStack.
     *
     * @param item the {@link io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack} to convert
     * @return the converted Bukkit ItemStack
     */
    @SuppressWarnings({"RedundantClassCall", "ConstantValue"})
    @NotNull
    public static ItemStack asBukkit(@Nullable io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack item) {
        if (item == null) {
            return AIR.clone();
        }

        ItemStack itemStack;
        if (ItemStack.class.isInstance(item)) {
            itemStack = ItemStack.class.cast(item);
        } else {
            try {
                itemStack = ReflectionUtil.getValue(item, "item", ItemStack.class);
                if (itemStack == null) throw new Throwable();
            } catch (Throwable ignored) {
                try {
                    itemStack = (ItemStack) ReflectionUtil.invokeMethod(item, "getItem");
                    if (itemStack == null) throw new Throwable();
                } catch (Throwable ignored2) {
                    try {
                        itemStack = (ItemStack) ReflectionUtil.invokeMethod(item, "item");
                        if (itemStack == null) throw new Throwable();
                    } catch (Throwable ignored3) {
                        return AIR.clone();
                    }
                }
            }
        }

        ItemStack bukkitItem = new ItemStack(itemStack.getType());
        bukkitItem.setAmount(item.getAmount());
        if (item.hasItemMeta()) {
            bukkitItem.setItemMeta(item.getItemMeta());
        }

        return bukkitItem;
    }

    @SuppressWarnings("unused")
    public static class Builder {
        public final List<ItemStack> itemStacks = new ArrayList<>();
        public boolean ifValue = true;
        public int index = 0;

        public @NotNull Builder if_(boolean expression) {
            this.ifValue = expression;
            return this;
        }

        public @NotNull Builder thenTryFirst() {
            this.index = 0;
            return this;
        }

        public @NotNull Builder thenTrySecond() {
            this.index = 1;
            return this;
        }

        public @NotNull Builder thenTry(int index) {
            this.index = index;
            return this;
        }

        public @NotNull Builder add(@NotNull SlimefunItemStack slimefunItemStack) {
            itemStacks.add(getItem(slimefunItemStack));
            return this;
        }

        public @NotNull Builder add(@NotNull ItemStack itemStack) {
            itemStacks.add(getItem(itemStack));
            return this;
        }

        public @NotNull Builder add(@NotNull Material material) {
            itemStacks.add(getItem(material));
            return this;
        }

        public @NotNull Builder add(@NotNull ItemStack itemStack, @NotNull Consumer<ItemMeta> itemMetaConsumer) {
            itemStacks.add(getItem(itemStack, itemMetaConsumer));
            return this;
        }

        public @NotNull Builder add(@NotNull Material material, @NotNull Consumer<ItemMeta> itemMetaConsumer) {
            itemStacks.add(getItem(material, itemMetaConsumer));
            return this;
        }

        public @NotNull Builder add(
                @NotNull ItemStack itemStack, @Nullable String name, @NotNull String @NotNull ... lore) {
            itemStacks.add(getItem(itemStack, name, lore));
            return this;
        }

        public @NotNull Builder add(
                @NotNull ItemStack itemStack, Color color, @Nullable String name, String @NotNull ... lore) {
            itemStacks.add(getItem(itemStack, color, name, lore));
            return this;
        }

        public @NotNull Builder add(@NotNull Material material, String name, String... lore) {
            itemStacks.add(getItem(material, name, lore));
            return this;
        }

        public @NotNull Builder add(@NotNull Material material, String name, @NotNull List<String> lore) {
            itemStacks.add(getItem(material, name, lore));
            return this;
        }

        public @NotNull Builder add(@NotNull ItemStack itemStack, @NotNull List<String> list) {
            itemStacks.add(getItem(itemStack, list));
            return this;
        }

        public @NotNull Builder add(@NotNull Material material, @NotNull List<String> list) {
            itemStacks.add(getItem(material, list));
            return this;
        }

        public @NotNull Builder add(@NotNull ItemStack itemStack, @Range(from = 1, to = Integer.MAX_VALUE) int amount) {
            itemStacks.add(getItem(itemStack, amount));
            return this;
        }

        public @NotNull Builder add(@NotNull ItemStack itemStack, @NotNull Material material) {
            itemStacks.add(getItem(itemStack, material));
            return this;
        }

        public @NotNull Builder add(
                @NotNull Material material, @NotNull String name, @NotNull Consumer<ItemMeta> consumer) {
            itemStacks.add(getItem(material, name, consumer));
            return this;
        }

        public ItemStack thenFirst() {
            return ifValue ? itemStacks.get(index) : AIR.clone();
        }

        public ItemStack thenSecond() {
            return ifValue ? itemStacks.get(index) : AIR.clone();
        }

        public ItemStack then(int index) {
            return ifValue ? itemStacks.get(index) : AIR.clone();
        }

        public ItemStack orElse() {
            return ifValue ? itemStacks.get(index) : AIR.clone();
        }

        public ItemStack orElse(int index) {
            return ifValue ? itemStacks.get(this.index) : itemStacks.get(index);
        }

        public ItemStack orElse(ItemStack itemStack) {
            return ifValue ? itemStacks.get(this.index) : itemStack;
        }

        public ItemStack orElseGet(@NotNull Supplier<ItemStack> supplier) {
            return ifValue ? itemStacks.get(this.index) : supplier.get();
        }

        public @NotNull ItemStack findFirst() {
            return itemStacks.stream()
                    .filter(itemStack -> itemStack != null && itemStack.getType() != Material.AIR)
                    .findFirst()
                    .orElse(AIR.clone());
        }
    }
}
