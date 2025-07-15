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
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to indicate that a class should be displayed in the cheat mode menu.
 * Priority lower than {@link NotDisplayInCheatMode}
 * <p>
 * Usage:
 * <p>
 * &#064;DisplayInCheatMode
 * public class MyGroup extends ItemGroup {
 * //...
 * }
 *
 * @author balugaq
 * @since 1.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DisplayInCheatMode {
    class Checker {
        /**
         * Check if the {@link ItemGroup} should be forced to display
         *
         * @param group The {@link ItemGroup} to check
         * @return true if the {@link ItemGroup} should be forced to display, false otherwise
         */
        public static boolean contains(@NotNull ItemGroup group) {
            String namespace = group.getKey().getNamespace();
            String key = group.getKey().getKey();
            // @formatter:off
            return isSpecial(group)
                    || (namespace.equals("danktech2") && key.equals("main"))
                    || (namespace.equals("slimeframe") && key.equals("wf_main"))
                    || (namespace.equals("finaltech-changed") && (key.equals("_finaltech_category_main")))
                    || (namespace.equals("finaltech") && (key.equals("finaltech_category_main")));
            // @formatter:on
        }

        /**
         * Check if the {@link ItemGroup} should be put to the last
         *
         * @param group The {@link ItemGroup} to check
         * @return true if the {@link ItemGroup} should be put to the last, false otherwise
         */
        public static boolean isSpecial(@NotNull ItemGroup group) {
            String namespace = group.getKey().getNamespace();
            String key = group.getKey().getKey();
            String className = group.getClass().getName();

            // @formatter:off
            return (className.equals("io.github.mooy1.infinityexpansion.infinitylib.groups.SubGroup")
                            && ((namespace.equals("infinityexpansion") || namespace.equals("infinityexpansion-changed"))
                                    && key.equals("infinity_cheat")))
                    || (className.equals("me.lucasgithuber.obsidianexpansion.infinitylib.groups.SubGroup")
                            && (namespace.equals("obsidianexpansion") && key.equals("omc_forge_cheat")))
                    || className.equals("io.github.sefiraat.networks.slimefun.NetworksItemGroups$HiddenItemGroup");
            // @formatter:on
        }
    }
}
