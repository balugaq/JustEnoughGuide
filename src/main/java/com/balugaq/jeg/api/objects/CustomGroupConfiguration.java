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

package com.balugaq.jeg.api.objects;

import com.balugaq.jeg.api.cfgparse.annotations.IParsable;
import com.balugaq.jeg.api.cfgparse.annotations.Key;
import com.balugaq.jeg.api.cfgparse.annotations.Required;
import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"unused", "ConstantValue"})
@NoArgsConstructor
@Data
@NullMarked
public class CustomGroupConfiguration implements IParsable {
    @Required
    @Key("enabled")
    boolean enabled;
    @Required
    @Key("tier")
    int tier;
    @Required
    @Key("id")
    String id;
    @Required
    @Key("display")
    Display display;
    @Required
    @Key("mode")
    Mode mode;
    @Key("items")
    String[] items;
    @Key("groups")
    String[] groups;
    @Required
    @Key("formats")
    String[] formats;
    private Format format;
    private List<Object> objects;

    @UnknownNullability
    public static String[] fieldNames() {
        return IParsable.fieldNames(CustomGroupConfiguration.class);
    }

    public boolean enabled() {
        return this.enabled;
    }

    @Range(from = Integer.MIN_VALUE, to = Integer.MAX_VALUE)
    public int tier() {
        return this.tier;
    }

    public String id() {
        return this.id;
    }

    public Display display() {
        return this.display;
    }

    public Mode mode() {
        return this.mode;
    }

    public String[] items() {
        return this.items;
    }

    public String[] groups() {
        return this.groups;
    }

    public Format format() {
        if (this.format != null) return format;
        this.format = new Format() {
            @Override
            public void loadMapping() {
                loadMapping(Arrays.stream(formats()).toList());
            }
        };
        Formats.addCustomFormat(this.id, this.format);

        return this.format;
    }

    public String[] formats() {
        return this.formats;
    }

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public List<Object> objects() {
        if (this.objects != null) return this.objects;

        // ItemGroup first, SlimefunItem then.
        List<Object> objects = new ArrayList<>(
            Arrays.stream(groups)
                .map(s -> {
                    for (ItemGroup itemGroup :
                        new ArrayList<>(Slimefun.getRegistry().getAllItemGroups()))
                        if (itemGroup.getKey().toString().equals(s))
                            return itemGroup;
                    return null;
                })
                .filter(Objects::nonNull)
                .map(s -> (Object) s)
                .toList());
        objects.addAll(Arrays.stream(items)
            .map(s -> SlimefunItem.getById(s.toUpperCase()))
            .filter(Objects::nonNull)
            .map(s -> (Object) s)
            .toList());
        this.objects = objects;
        return objects;
    }

    public NamespacedKey key() {
        return KeyUtil.newKey(id);
    }

    public ItemStack item() {
        return display.item();
    }

    /**
     * @author balugaq
     * @since 1.9
     */
    @SuppressWarnings("unused")
    public enum Mode {
        TRANSFER,
        MERGE
    }

    /**
     * @author balugaq
     * @since 1.9
     */
    @SuppressWarnings("unused")
    @Data
    @NullMarked
    public static class Display implements IParsable {
        @Required
        @Key("material")
        String material;

        @Required
        @Key("name")
        String name;

        @Nullable ItemStack itemStack;

        @UnknownNullability
        public static String[] fieldNames() {
            return IParsable.fieldNames(Display.class);
        }

        public final ItemStack item() {
            if (itemStack != null) return itemStack;
            itemStack = getHashLike(material);
            if (itemStack != null) return itemStack = Converter.getItem(itemStack, this.name);

            itemStack = getBase64Like(material);
            if (itemStack != null) return itemStack = Converter.getItem(itemStack, this.name);

            itemStack = getURLLike(material);
            if (itemStack != null) return itemStack = Converter.getItem(itemStack, this.name);

            Material material = Material.getMaterial(this.material.toUpperCase());
            if (material == null || !material.isItem() || material.isLegacy()) {
                Debug.warn("自定义物品组存在无效的 material: " + this.material);
                return itemStack = new ItemStack(Material.STONE);
            }

            return itemStack = Converter.getItem(material, this.name);
        }

        @Nullable
        private ItemStack getHashLike(String material) {
            if (!isHashcodeLike(material)) {
                return null;
            }

            try {
                return PlayerHead.getItemStack(PlayerSkin.fromHashCode(material));
            } catch (Throwable ignored) {
            }

            return null;
        }

        @Nullable
        private ItemStack getBase64Like(String material) {
            if (!isBase64Like(material)) {
                return null;
            }

            try {
                return PlayerHead.getItemStack(PlayerSkin.fromBase64(material));
            } catch (Throwable ignored) {
            }

            return null;
        }

        @Nullable
        private ItemStack getURLLike(String material) {
            if (!isURLLike(material)) {
                return null;
            }

            try {
                return PlayerHead.getItemStack(PlayerSkin.fromURL(material));
            } catch (Throwable ignored) {
            }

            return null;
        }

        public boolean isHashcodeLike(String value) {
            return value.matches("^[a-fA-F0-9]{32,}$");
        }

        public boolean isBase64Like(String value) {
            return value.length() > 32
                && value.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");
        }

        public boolean isURLLike(String value) {
            return value.matches(
                "^https?://(?:[-\\w]+\\.)?[-\\w]+(?:\\.[a-zA-Z]{2,5}|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})" +
                    "(?::\\d{1,5})?(/[-\\w./]*)*(\\?[-\\w.&=]*)?(#[-\\w]*)?$");
        }
    }
}
