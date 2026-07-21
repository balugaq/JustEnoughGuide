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

package com.balugaq.jeg.utils.formatter;

import com.balugaq.jeg.api.groups.GuideGroup;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import it.unimi.dsi.fastutil.chars.Char2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2CharLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2CharMap;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author balugaq
 * @since 1.6
 */
@SuppressWarnings("unused")
@Getter
@NullMarked
public abstract class Format {
    public static final Char2ObjectMap<ItemStack> customMapping = new Char2ObjectLinkedOpenHashMap<>();
    public final Int2CharMap mapping = new Int2CharLinkedOpenHashMap();

    @ToString.Exclude
    public final Char2ObjectMap<List<Integer>> cached = new Char2ObjectLinkedOpenHashMap<>();

    @Setter
    public int size = 54;

    public abstract void loadMapping();

    @ApiStatus.Obsolete
    public void loadMapping(List<String> format) {
        int index = -1;
        for (String string : format) {
            for (char c : string.toCharArray()) {
                index++;
                if (c != ' ') {
                    mapping.put(index, c);
                }
            }
        }
    }

    @ApiStatus.Obsolete
    public List<Integer> getChars(String s) {
        return getChars(s.toCharArray()[0]);
    }

    @ApiStatus.Obsolete
    public List<Integer> getChars(char c) {
        if (cached.containsKey(c)) {
            return cached.get(c);
        }

        List<Integer> list = new ArrayList<>();
        for (Map.Entry<Integer, Character> entry : mapping.int2CharEntrySet()) {
            if (entry.getValue() == c) {
                list.add(entry.getKey());
            }
        }

        cached.put(c, list);
        return list;
    }

    @SuppressWarnings("deprecation")
    public void renderCustom(ChestMenu menu) {
        for (Map.Entry<Character, ItemStack> entry : customMapping.entrySet()) {
            for (int slot : getChars(entry.getKey())) {
                menu.addItem(slot, entry.getValue());
                if (menu.getMenuClickHandler(slot) == null) {
                    menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
                }
            }
        }
    }

    public void renderCustom(GuideGroup menu) {
        for (Map.Entry<Character, ItemStack> entry : customMapping.char2ObjectEntrySet()) {
            for (int slot : getChars(entry.getKey())) {
                menu.addGuide(slot, entry.getValue());
                if (menu.getMenuClickHandler(slot) == null) {
                    menu.addGuide(slot, ChestMenuUtils.getEmptyClickHandler());
                }
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
