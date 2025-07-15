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

package com.balugaq.jeg.api.interfaces;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to indicate that a class should not be displayed in the cheat mode menu.
 * Priority higher than {@link DisplayInCheatMode}
 * <p>
 * Usage:
 * <p>
 * &#064;NotDisplayInCheatMode
 * public class MyGroup extends ItemGroup {
 * //...
 * }
 *
 * @author balugaq
 * @since 1.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NotDisplayInCheatMode {
    class Checker {
        public static boolean contains(@NotNull ItemGroup group) {
            String namespace = group.getKey().getNamespace();
            String key = group.getKey().getKey();
            String className = group.getClass().getName();

            // @formatter:off
            return className.equals("io.github.sefiraat.networks.slimefun.groups.DummyItemGroup")
                    || className.startsWith("com.balugaq.netex.api.groups")
                    || className.startsWith("io.github.ytdd9527.mobengineering.implementation.slimefun.groups")
                    || className.startsWith("io.taraxacum.finaltech.core.group")
                    || className.equals("me.matl114.logitech.utils.UtilClass.MenuClass.DummyItemGroup")
                    || className.equals("me.matl114.logitech.Utils.UtilClass.MenuClass.DummyItemGroup")
                    || className.equals("me.lucasgithuber.obsidianexpansion.utils.ObsidianForgeGroup")
                    || className.equals("me.char321.nexcavate.slimefun.NEItemGroup")
                    || className.equals("io.github.mooy1.infinityexpansion.categories.InfinityGroup")
                    || className.equals("io.github.mooy1.infinityexpansion.infinitylib.groups.SubGroup")
                    || className.equals("me.lucasgithuber.obsidianexpansion.infinitylib.groups.SubGroup")
                    || className.equals("io.github.slimefunguguproject.bump.implementation.groups.AppraiseInfoGroup")
                    || className.equals("dev.sefiraat.netheopoiesis.implementation.groups.DummyItemGroup")
                    || className.equals("io.github.addoncommunity.galactifun.infinitylib.groups.SubGroup")
                    || className.equals("io.github.sefiraat.crystamaehistoria.slimefun.itemgroups.DummyItemGroup")
                    || className.equals(
                            "io.github.slimefunguguproject.bump.libs.sefilib.slimefun.itemgroup.DummyItemGroup")
                    || className.equals("me.voper.slimeframe.implementation.groups.ChildGroup")
                    || className.equals("me.voper.slimeframe.implementation.groups.MasterGroup")
                    || className.equals("io.github.sefiraat.emctech.slimefun.groups.DummyItemGroup")
                    || className.equals("dev.sefiraat.sefilib.slimefun.itemgroup.DummyItemGroup")
                    || (namespace.equals("logitech")
                            && (key.equals("info") || key.equals("tools") || key.equals("tools-functional")))
                    || (namespace.equals("nexcavate") && key.equals("dummy"))
                    || (namespace.equals("slimefun") && key.equals("rick"))
                    || (group instanceof SubItemGroup
                                    && (namespace.equals("networks") && key.startsWith("ntw_expansion_"))
                            || (namespace.equals("mobengineering")
                                    && (key.startsWith("mod_engineering_") || key.startsWith("mob_engineering_")))
                            || (namespace.equals("finaltech-changed") && (key.startsWith("_finaltech_")))
                            || (namespace.equals("finaltech") && (key.startsWith("finaltech_")))
                            || namespace.equals("danktech2"));
            // @formatter:on
        }
    }
}
