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

    public String[] formats() {
        return this.formats;
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

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public List<Object> objects() {
        if (this.objects != null) return this.objects;

        List<Object> objects = new ArrayList<>(Arrays.stream(groups)
                .map(s -> {
                    for (ItemGroup itemGroup : Slimefun.getRegistry().getAllItemGroups())
                        if (itemGroup.getKey().toString().equals(s)) return itemGroup;
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

        public boolean isHashcodeLike(String value) {
            return value.matches("^[a-fA-F0-9]{32,}$");
        }

        public boolean isBase64Like(String value) {
            return value.length() > 32
                    && value.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");
        }

        public boolean isURLLike(String value) {
            return value.matches(
                    "^https?://(?:[-\\w]+\\.)?[-\\w]+(?:\\.[a-zA-Z]{2,5}|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(?::\\d{1,5})?(/[-\\w./]*)*(\\?[-\\w.&=]*)?(#[-\\w]*)?$");
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
    }
}
