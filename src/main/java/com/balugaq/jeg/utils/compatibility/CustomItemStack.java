package com.balugaq.jeg.utils.compatibility;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Experimental
public class CustomItemStack implements Cloneable {
    private final ItemStack delegate;

    public CustomItemStack(@NotNull ItemStack item) {
        this.delegate = item.clone();
    }

    public CustomItemStack(@NotNull Material material) {
        this.delegate = new ItemStack(material);
    }

    public CustomItemStack(@NotNull ItemStack itemStack, @NotNull Consumer<ItemMeta> itemMetaConsumer) {
        this.delegate = itemStack.clone();
        Preconditions.checkNotNull(itemMetaConsumer, "ItemMeta consumer cannot be null");
        editItemMeta(itemMetaConsumer);
    }

    public CustomItemStack(@NotNull Material material, @NotNull Consumer<ItemMeta> meta) {
        this(new ItemStack(material), meta);
    }

    public CustomItemStack(@NotNull ItemStack itemStack, @Nullable String name, @NotNull String @NotNull ... lore) {
        this(itemStack, itemMeta -> {
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
        });
    }

    public CustomItemStack(@NotNull ItemStack itemStack, Color color, @Nullable String name, String @NotNull ... lore) {
        this(itemStack, itemMeta -> {
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
        });
    }

    public CustomItemStack(@NotNull Material material, String name, String... lore) {
        this(new ItemStack(material), name, lore);
    }

    public CustomItemStack(@NotNull Material material, String name, @NotNull List<String> lore) {
        this(new ItemStack(material), name, lore.toArray(new String[0]));
    }

    public CustomItemStack(@NotNull ItemStack itemStack, @NotNull List<String> list) {
        this(itemStack, list.get(0), list.subList(1, list.size()).toArray(new String[0]));
    }

    public CustomItemStack(@NotNull Material material, @NotNull List<String> list) {
        this(new ItemStack(material), list);
    }

    public CustomItemStack(@NotNull ItemStack itemStack, @Range(from = 1, to = Integer.MAX_VALUE) int amount) {
        this.delegate = itemStack.clone();
        this.delegate.setAmount(amount);
    }

    public CustomItemStack(@NotNull ItemStack itemStack, @NotNull Material material) {
        this.delegate = itemStack.clone();
        this.delegate.setType(material);
    }

    public static @NotNull String color(@NotNull String raw) {
        return ChatColor.translateAlternateColorCodes('&', Preconditions.checkNotNull(raw, "raw cannot be null"));
    }

    // 委托方法封装
    public ItemStack getDelegate() {
        return delegate.clone();
    }

    public Material getType() {
        return delegate.getType();
    }

    public void setType(Material material) {
        delegate.setType(material);
    }

    public int getAmount() {
        return delegate.getAmount();
    }

    public void setAmount(int amount) {
        delegate.setAmount(amount);
    }

    public boolean hasItemMeta() {
        return delegate.hasItemMeta();
    }

    public ItemMeta getItemMeta() {
        return delegate.getItemMeta();
    }

    public boolean setItemMeta(ItemMeta meta) {
        return delegate.setItemMeta(meta);
    }

    public @NotNull CustomItemStack addFlags(@NotNull ItemFlag @NotNull ... flags) {
        Preconditions.checkNotNull(flags, "flags cannot be null");
        Preconditions.checkArgument(flags.length > 0, "flags cannot be empty");
        return editItemMeta(meta -> meta.addItemFlags(flags));
    }

    public @NotNull ItemStack asBukkit() {
        return delegate.clone();
    }

    public @NotNull CustomItemStack editItemMeta(@NotNull Consumer<ItemMeta> itemMetaConsumer) {
        Preconditions.checkNotNull(itemMetaConsumer, "ItemMeta consumer cannot be null");

        ItemMeta meta = delegate.getItemMeta();
        if (meta != null) {
            itemMetaConsumer.accept(meta);
            delegate.setItemMeta(meta);
        }
        return this;
    }

    public @NotNull CustomItemStack editItemStack(@NotNull Consumer<ItemStack> itemStackConsumer) {
        Preconditions.checkNotNull(itemStackConsumer, "ItemStack consumer cannot be null");

        itemStackConsumer.accept(delegate);
        return this;
    }

    public @NotNull CustomItemStack setCustomModelData(@Range(from = 0, to = Integer.MAX_VALUE) int data) {
        return editItemMeta(meta -> meta.setCustomModelData(data == 0 ? null : data));
    }

    public @NotNull CustomItemStack clone() {
        return new CustomItemStack(getDelegate());
    }
}