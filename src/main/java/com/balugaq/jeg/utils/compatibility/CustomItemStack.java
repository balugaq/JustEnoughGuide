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

package com.balugaq.jeg.utils.compatibility;

import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author balugaq
 * @since 1.4
 */
@SuppressWarnings({"deprecation", "unused"})
@ApiStatus.Experimental
@NullMarked
public class CustomItemStack implements Cloneable {
    private final ItemStack delegate;

    /**
     * Creates a CustomItemStack from a Bukkit ItemStack.
     *
     * @param item
     *         the Bukkit ItemStack to create from
     */
    public CustomItemStack(ItemStack item) {
        this.delegate = item.clone();
    }

    /**
     * Creates a CustomItemStack from a Material.
     *
     * @param material
     *         the Material to create from
     */
    public CustomItemStack(Material material) {
        this.delegate = new ItemStack(material);
    }

    /**
     * Creates a CustomItemStack from a Material with custom metadata.
     *
     * @param material
     *         the Material to create from
     * @param meta
     *         the consumer to modify the item metadata
     */
    public CustomItemStack(Material material, Consumer<ItemMeta> meta) {
        this(new ItemStack(material), meta);
    }

    /**
     * Creates a CustomItemStack from a Bukkit ItemStack with custom metadata.
     *
     * @param itemStack
     *         the Bukkit ItemStack to create from
     * @param itemMetaConsumer
     *         the consumer to modify the item metadata
     */
    public CustomItemStack(ItemStack itemStack, Consumer<ItemMeta> itemMetaConsumer) {
        this.delegate = itemStack.clone();
        Preconditions.checkNotNull(itemMetaConsumer, "ItemMeta consumer cannot be null");
        editItemMeta(itemMetaConsumer);
    }

    /**
     * Edits the item metadata.
     *
     * @param itemMetaConsumer
     *         the consumer to modify the item metadata
     *
     * @return the CustomItemStack with the edited metadata
     */
    public CustomItemStack editItemMeta(Consumer<ItemMeta> itemMetaConsumer) {
        Preconditions.checkNotNull(itemMetaConsumer, "ItemMeta consumer cannot be null");

        ItemMeta meta = delegate.getItemMeta();
        if (meta != null) {
            itemMetaConsumer.accept(meta);
            delegate.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Creates a CustomItemStack from a Bukkit ItemStack with a name and lore.
     *
     * @param itemStack
     *         the Bukkit ItemStack to create from
     * @param name
     *         the name of the item
     * @param lore
     *         the lore of the item
     */
    public CustomItemStack(ItemStack itemStack, @Nullable String name, List<String> lore) {
        this(itemStack, name, lore.toArray(new String[0]));
    }

    /**
     * Creates a CustomItemStack from a Bukkit ItemStack with a name and lore.
     *
     * @param itemStack
     *         the Bukkit ItemStack to create from
     * @param name
     *         the name of the item
     * @param lore
     *         the lore of the item
     */
    public CustomItemStack(ItemStack itemStack, @Nullable String name, String... lore) {
        this(
                itemStack, itemMeta -> {
                    if (name != null) {
                        itemMeta.setDisplayName(color(name));
                    }
                    if (lore.length > 0) {
                        List<String> lines = new ArrayList<>();
                        for (String line : lore) {
                            lines.add(color(line));
                        }
                        itemMeta.setLore(lines);
                    }
                }
        );
    }

    /**
     * Translates color codes in a string.
     *
     * @param raw
     *         the string to translate
     *
     * @return the translated string
     */
    public static String color(String raw) {
        return ChatColors.color(Preconditions.checkNotNull(raw, "raw cannot be null"));
    }

    /**
     * Creates a CustomItemStack from a Bukkit ItemStack with a color, name, and lore.
     *
     * @param itemStack
     *         the Bukkit ItemStack to create from
     * @param color
     *         the color of the item
     * @param name
     *         the name of the item
     * @param lore
     *         the lore of the item
     */
    public CustomItemStack(ItemStack itemStack, Color color, @Nullable String name, String... lore) {
        this(
                itemStack, itemMeta -> {
                    if (name != null) {
                        itemMeta.setDisplayName(color(name));
                    }
                    if (lore.length > 0) {
                        List<String> lines = new ArrayList<>();
                        for (String line : lore) {
                            lines.add(color(line));
                        }
                        itemMeta.setLore(lines);
                    }
                    if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
                        leatherArmorMeta.setColor(color);
                    }
                    if (itemMeta instanceof PotionMeta potionMeta) {
                        potionMeta.setColor(color);
                    }
                }
        );
    }

    /**
     * Creates a CustomItemStack from a Material with a name and lore.
     *
     * @param material
     *         the Material to create from
     * @param name
     *         the name of the item
     * @param lore
     *         the lore of the item
     */
    public CustomItemStack(Material material, @Nullable String name, String... lore) {
        this(new ItemStack(material), name, lore);
    }

    /**
     * Creates a CustomItemStack from a Material with a name and lore.
     *
     * @param material
     *         the Material to create from
     * @param name
     *         the name of the item
     * @param lore
     *         the lore of the item
     */
    public CustomItemStack(Material material, @Nullable String name, List<String> lore) {
        this(new ItemStack(material), name, lore.toArray(new String[0]));
    }

    /**
     * Creates a CustomItemStack from a Material with a list of lore.
     *
     * @param material
     *         the Material to create from
     * @param list
     *         the list of lore
     */
    public CustomItemStack(Material material, List<String> list) {
        this(new ItemStack(material), list);
    }

    /**
     * Creates a CustomItemStack from a Bukkit ItemStack with a list of lore.
     *
     * @param itemStack
     *         the Bukkit ItemStack to create from
     * @param list
     *         the list of name and lore
     */
    public CustomItemStack(ItemStack itemStack, List<String> list) {
        this(itemStack, list.get(0), new ArrayList<>(list.subList(1, list.size())).toArray(new String[0]));
    }

    /**
     * Creates a CustomItemStack from a Bukkit ItemStack with a specified amount.
     *
     * @param itemStack
     *         the Bukkit ItemStack to create from
     * @param amount
     *         the amount of the item
     */
    public CustomItemStack(ItemStack itemStack, @Range(from = 1, to = Integer.MAX_VALUE) int amount) {
        this.delegate = itemStack.clone();
        this.delegate.setAmount(amount);
    }

    /**
     * Creates a CustomItemStack from a Bukkit ItemStack with a specified Material.
     *
     * @param itemStack
     *         the Bukkit ItemStack to create from
     * @param material
     *         the Material of the item
     */
    public CustomItemStack(ItemStack itemStack, Material material) {
        this.delegate = itemStack.clone();
        this.delegate.setType(material);
    }

    /**
     * Returns the Material of the item.
     *
     * @return the Material of the item
     */
    public Material getType() {
        return delegate.getType();
    }

    /**
     * Sets the Material of the item.
     *
     * @param material
     *         the Material to set
     */
    public void setType(Material material) {
        delegate.setType(material);
    }

    /**
     * Returns the amount of the item.
     *
     * @return the amount of the item
     */
    public int getAmount() {
        return delegate.getAmount();
    }

    /**
     * Sets the amount of the item.
     *
     * @param amount
     *         the amount to set
     */
    public void setAmount(int amount) {
        delegate.setAmount(amount);
    }

    /**
     * Checks if the item has metadata.
     *
     * @return true if the item has metadata, false otherwise
     */
    public boolean hasItemMeta() {
        return delegate.hasItemMeta();
    }

    /**
     * Returns the item metadata.
     *
     * @return the item metadata
     */
    public ItemMeta getItemMeta() {
        return delegate.getItemMeta();
    }

    /**
     * Sets the item metadata.
     *
     * @param meta
     *         the metadata to set
     *
     * @return true if the metadata was set successfully, false otherwise
     */
    public boolean setItemMeta(ItemMeta meta) {
        return delegate.setItemMeta(meta);
    }

    /**
     * Adds item flags to the item.
     *
     * @param flags
     *         the item flags to add
     *
     * @return the CustomItemStack with the added flags
     */
    public CustomItemStack addFlags(ItemFlag... flags) {
        Preconditions.checkNotNull(flags, "flags cannot be null");
        Preconditions.checkArgument(flags.length > 0, "flags cannot be empty");
        return editItemMeta(meta -> meta.addItemFlags(flags));
    }

    /**
     * Returns the Bukkit ItemStack.
     *
     * @return the Bukkit ItemStack
     */
    public ItemStack asBukkit() {
        return delegate.clone();
    }

    /**
     * Edits the item stack.
     *
     * @param itemStackConsumer
     *         the consumer to modify the item stack
     *
     * @return the CustomItemStack with the edited stack
     */
    public CustomItemStack editItemStack(Consumer<ItemStack> itemStackConsumer) {
        Preconditions.checkNotNull(itemStackConsumer, "ItemStack consumer cannot be null");

        itemStackConsumer.accept(delegate);
        return this;
    }

    /**
     * Sets the custom model data of the item.
     *
     * @param data
     *         the custom model data to set
     *
     * @return the CustomItemStack with the set custom model data
     */
    public CustomItemStack setCustomModelData(@Range(from = 0, to = Integer.MAX_VALUE) int data) {
        return editItemMeta(meta -> meta.setCustomModelData(data == 0 ? null : data));
    }

    /**
     * Clones the CustomItemStack.
     *
     * @return the cloned CustomItemStack
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CustomItemStack clone() {
        return new CustomItemStack(getDelegate());
    }

    /**
     * Returns the delegate Bukkit ItemStack.
     *
     * @return the delegate Bukkit ItemStack
     */
    public ItemStack getDelegate() {
        return delegate.clone();
    }
}
