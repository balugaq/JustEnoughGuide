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
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to indicate that a class should not be displayed in the cheat mode menu. Priority higher than
 * {@link DisplayInCheatMode}
 * <p>
 * Usage:
 * <p>
 * &#064;NotDisplayInCheatMode public class MyGroup extends ItemGroup { //... }
 *
 * @author balugaq
 * @since 1.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@NullMarked
public @interface NotDisplayInCheatMode {
    /**
     * @author balugaq
     * @since 1.8
     */
    @NullMarked
    class Checker {
        public static boolean contains(ItemGroup group) {
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
