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

@ApiStatus.Experimental
public class Converter {
    public static final ItemStack AIR = new ItemStack(Material.AIR);
    public static final ItemGetter methodHandleSlimefunItemStack_item = createItemGetter();

    public static @NotNull ItemStack getItem(SlimefunItemStack slimefunItemStack) {
        return asBukkit(slimefunItemStack);
    }

    public static @NotNull ItemStack getItem(ItemStack itemStack) {
        return new CustomItemStack(itemStack).asBukkit();
    }

    public static @NotNull ItemStack getItem(Material material) {
        return new CustomItemStack(material).asBukkit();
    }

    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack, @NotNull Consumer<ItemMeta> itemMetaConsumer) {
        return new CustomItemStack(itemStack, itemMetaConsumer).asBukkit();
    }

    public static @NotNull ItemStack getItem(@NotNull Material material, @NotNull Consumer<ItemMeta> meta) {
        return new CustomItemStack(material, meta).asBukkit();
    }

    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack, @Nullable String name, @NotNull String @NotNull ... lore) {
        return new CustomItemStack(itemStack, name, lore).asBukkit();
    }

    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack, Color color, @Nullable String name, String @NotNull ... lore) {
        return new CustomItemStack(itemStack, color, name, lore).asBukkit();
    }

    public static @NotNull ItemStack getItem(@NotNull Material material, String name, String... lore) {
        return new CustomItemStack(material, name, lore).asBukkit();
    }

    public static @NotNull ItemStack getItem(@NotNull Material material, String name, @NotNull List<String> lore) {
        return new CustomItemStack(material, name, lore).asBukkit();
    }

    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack, @NotNull List<String> list) {
        return new CustomItemStack(itemStack, list).asBukkit();
    }

    public static @NotNull ItemStack getItem(@NotNull Material material, @NotNull List<String> list) {
        return new CustomItemStack(material, list).asBukkit();
    }

    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack, @Range(from = 1, to = Integer.MAX_VALUE) int amount) {
        return new CustomItemStack(itemStack, amount).asBukkit();
    }

    public static @NotNull ItemStack getItem(@NotNull ItemStack itemStack, @NotNull Material material) {
        return new CustomItemStack(itemStack, material).asBukkit();
    }

    private static ItemGetter createItemGetter() {
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
