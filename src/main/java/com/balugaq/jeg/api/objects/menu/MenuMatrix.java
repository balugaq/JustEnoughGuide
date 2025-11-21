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

package com.balugaq.jeg.api.objects.menu;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author balugaq
 * @since 1.8
 */
@SuppressWarnings({"deprecation", "unused"})
@Getter
@NullMarked
public class MenuMatrix {
    private final List<String> labels = new ArrayList<>();
    private final Map<Character, ItemStack> itemStackMap = new HashMap<>();
    private final Map<Character, ChestMenu.MenuClickHandler> handlerMap = new HashMap<>();

    public MenuMatrix() {
    }

    @CanIgnoreReturnValue
    public MenuMatrix addLine(String label) {
        labels.add(label);
        return this;
    }

    @CanIgnoreReturnValue
    public MenuMatrix addLine(String... labels) {
        for (String label : labels) {
            addLine(label);
        }
        return this;
    }

    @CanIgnoreReturnValue
    public MenuMatrix addLine(List<String> labels) {
        for (String label : labels) {
            addLine(label);
        }
        return this;
    }

    @CanIgnoreReturnValue
    public MenuMatrix addItem(Character label, ItemStack item, ChestMenu.MenuClickHandler handler) {
        this.itemStackMap.put(label, item);
        this.handlerMap.put(label, handler);
        return this;
    }

    @CanIgnoreReturnValue
    public MenuMatrix addItem(Character label, ItemStack item) {
        return addItem(label, item, (p, s, i, a) -> false);
    }

    @CanIgnoreReturnValue
    public MenuMatrix addHandler(Character label, ChestMenu.MenuClickHandler handler) {
        this.handlerMap.put(label, handler);
        return this;
    }

    @CanIgnoreReturnValue
    public MenuMatrix addItem(String label, ItemStack item, ChestMenu.MenuClickHandler handler) {
        return addItem(label.charAt(0), item, handler);
    }

    @CanIgnoreReturnValue
    public MenuMatrix addItem(String label, ItemStack item) {
        return addItem(label.charAt(0), item, (p, s, i, a) -> false);
    }

    @CanIgnoreReturnValue
    public MenuMatrix addHandler(String label, ChestMenu.MenuClickHandler handler) {
        return addHandler(label.charAt(0), handler);
    }

    public void build(BlockMenuPreset preset) {
        int index = 0;
        for (String label : labels) {
            for (int j = 0; j < label.length(); j++) {
                if (index >= 54) {
                    break;
                }
                char c = label.charAt(j);
                if (itemStackMap.containsKey(c)) {
                    preset.addItem(index, itemStackMap.get(c), handlerMap.get(c));
                }
                index++;
            }
        }
    }

    public int getChar(char label) {
        for (int i = 0; i < labels.size(); i++) {
            String line = labels.get(i);
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == label) {
                    return i * 9 + j;
                }
            }
        }

        return -1;
    }

    public int getChar(String label) {
        return getChar(label.charAt(0));
    }

    public int[] getChars(char label) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            String line = labels.get(i);
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == label) {
                    result.add(i * 9 + j);
                }
            }
        }

        int[] array = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            array[i] = result.get(i);
        }

        return array;
    }

    public int[] getChars(String label) {
        return getChars(label.charAt(0));
    }
}
