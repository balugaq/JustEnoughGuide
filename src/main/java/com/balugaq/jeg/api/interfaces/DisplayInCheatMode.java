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

package com.balugaq.jeg.api.interfaces;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to indicate that a class should be displayed in the cheat mode menu. Priority lower than
 * {@link NotDisplayInCheatMode}
 * <p>
 * Usage:
 * <p>
 * &#064;DisplayInCheatMode public class MyGroup extends ItemGroup { //... }
 *
 * @author balugaq
 * @since 1.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@NullMarked
public @interface DisplayInCheatMode {
    /**
     * @author balugaq
     * @since 1.8
     */
    @NullMarked
    class Checker {
        /**
         * Check if the {@link ItemGroup} should be forced to display
         *
         * @param group The {@link ItemGroup} to check
         * @return true if the {@link ItemGroup} should be forced to display, false otherwise
         */
        public static boolean contains(ItemGroup group) {
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
        public static boolean isSpecial(ItemGroup group) {
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
