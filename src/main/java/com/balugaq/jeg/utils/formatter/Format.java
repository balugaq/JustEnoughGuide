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

package com.balugaq.jeg.utils.formatter;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author balugaq
 * @since 1.6
 */
@Getter
public abstract class Format {
    public final Map<Integer, Character> mapping = new HashMap<>();
    @Deprecated
    public final Map<Character, ItemStackFormat> formats = new HashMap<>();
    public final Map<Character, List<Integer>> cached = new HashMap<>();
    @Setter
    public int size = 54;

    public Format() {
        formats.put('B', new Background());
        formats.put('R', new RealTimeSearch());
        formats.put('C', new BookMark());
        formats.put('c', new ItemMark());
        //formats.put('E', new BigRecipe());

        formats.put('b', new Back());
        formats.put('T', new Settings());
        formats.put('S', new Search());

        formats.put('P', new PagePrevious());
        formats.put('N', new PageNext());
        loadMapping();
    }

    public abstract void loadMapping();

    @Deprecated
    @OverridingMethodsMustInvokeSuper
    public void decorate(ChestMenu menu, Player player) {
        for (var entry : mapping.entrySet()) {
            var format = formats.get(entry.getValue());
            if (format instanceof ItemStackSupplier supplier) {
                menu.addItem(entry.getKey(), supplier.get());
            } else if (format instanceof Back function) {
                menu.addItem(entry.getKey(), function.apply(player));
            } else if (format instanceof Settings function) {
                menu.addItem(entry.getKey(), function.apply(player));
            } else if (format instanceof Search function) {
                menu.addItem(entry.getKey(), function.apply(player));
            }
        }
    }

    @Deprecated
    @OverridingMethodsMustInvokeSuper
    public void decoratePage(ChestMenu menu, Player player, int page, int maxPage) {
        for (var entry : mapping.entrySet()) {
            var format = formats.get(entry.getValue());
            if (format instanceof PagePrevious function) {
                menu.addItem(entry.getKey(), function.apply(player, page, maxPage));
            } else if (format instanceof PageNext function) {
                menu.addItem(entry.getKey(), function.apply(player, page, maxPage));
            }
        }
    }

    @ApiStatus.Obsolete
    public void loadMapping(List<String> format) {
        int index = -1;
        for (var string : format) {
            for (var c : string.toCharArray()) {
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
        for (var entry : mapping.entrySet()) {
            if (entry.getValue() == c) {
                list.add(entry.getKey());
            }
        }

        cached.put(c, list);
        return list;
    }

    @Deprecated
    public interface ItemStackCiFunction<A, B, C> extends CiFunction<A, B, C, ItemStack>, ItemStackFormat {

    }

    @Deprecated
    public interface ItemStackBiFunction<A, B> extends BiFunction<A, B, ItemStack>, ItemStackFormat {
        ItemStack apply(A a, B b);
    }

    @Deprecated
    public interface ItemStackFunction<A> extends Function<A, ItemStack>, ItemStackFormat {
        ItemStack apply(A a);
    }

    @Deprecated
    public interface ItemStackSupplier extends Supplier<ItemStack>, ItemStackFormat {
        ItemStack get();
    }

    @Deprecated
    public interface ItemStackFormat {
    }

    @Deprecated
    public interface CiFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    @Deprecated
    public static class Background implements ItemStackSupplier {
        @Override
        public ItemStack get() {
            return ChestMenuUtils.getBackground();
        }
    }

    @Deprecated
    public static class Back implements ItemStackFunction<Player> {
        @Override
        public ItemStack apply(Player player) {
            return SlimefunOfficialSupporter.getBackButton(player);
        }
    }

    @Deprecated
    public static class PagePrevious implements ItemStackCiFunction<Player, Integer, Integer> {
        @Override
        public ItemStack apply(Player player, Integer page, Integer maxPage) {
            return ChestMenuUtils.getPreviousButton(player, page, maxPage);
        }
    }

    @Deprecated
    public static class PageNext implements ItemStackCiFunction<Player, Integer, Integer> {
        @Override
        public ItemStack apply(Player player, Integer page, Integer maxPage) {
            return ChestMenuUtils.getNextButton(player, page, maxPage);
        }
    }

    @Deprecated
    public static class BookMark implements ItemStackSupplier {
        @Override
        public ItemStack get() {
            return JustEnoughGuide.getConfigManager().isBookmark() ?
                    GuideUtil.getBookMarkMenuButton() :
                    ChestMenuUtils.getBackground();
        }
    }

    @Deprecated
    public static class ItemMark implements ItemStackSupplier {
        @Override
        public ItemStack get() {
            return JustEnoughGuide.getConfigManager().isBookmark() ?
                    GuideUtil.getItemMarkMenuButton() :
                    ChestMenuUtils.getBackground();
        }
    }

    @Deprecated
    public static class Settings implements ItemStackFunction<Player> {
        @Override
        public ItemStack apply(Player p) {
            return ChestMenuUtils.getMenuButton(p);
        }
    }

    @Deprecated
    public static class Search implements ItemStackFunction<Player> {
        @Override
        public ItemStack apply(Player p) {
            return ChestMenuUtils.getSearchButton(p);
        }
    }

    @Deprecated
    public static class RealTimeSearch implements ItemStackSupplier {
        @Override
        public ItemStack get() {
            return JustEnoughGuide.getConfigManager().isRTSSearch() ?
                    Lang.RTS_ITEM :
                    ChestMenuUtils.getBackground();
        }
    }

    @Deprecated
    public static class BigRecipe implements ItemStackSupplier {
        @Override
        public ItemStack get() {
            return Lang.SPECIAL_MENU_ITEM;
        }
    }
}
