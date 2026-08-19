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

package com.balugaq.jeg.utils;

import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.core.integrations.logitech.LogiTechSpecialMenuOpener;
import com.balugaq.jeg.core.listeners.SpecialMenuFixListener;
import com.balugaq.jeg.core.managers.IntegrationManager;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.guide.CheatGuideImplementation;
import com.balugaq.jeg.implementation.guide.SurvivalGuideImplementation;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import lombok.experimental.UtilityClass;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author balugaq
 * @see SpecialMenuFixListener
 * @see SurvivalGuideImplementation
 * @see CheatGuideImplementation
 * @since 1.3
 */
@SuppressWarnings({"unchecked", "unused", "UnusedReturnValue", "ConstantValue"})
@UtilityClass
@NullMarked
public class SpecialMenuProvider {
    public static final String PLACEHOLDER_SEARCH_TERM = "__JEG_SPECIAL_MENU_PROVIDER_UNDEFINED__";
    public static final int COMMON_RECIPE_LENGTH = 9;
    public static boolean ENABLED_FinalTECH = false;
    public static boolean ENABLED_Nexcavate = false;
    public static boolean ENABLED_LogiTech = false;
    public static boolean ENABLED_InfinityExpansion = false;
    public static boolean ENABLED_ObsidianExpansion = false;
    public static boolean ENABLED_Galactifun = false;
    // FinalTECH | FinalTECH-Changed
    public static @Nullable Method methodRecipeItemGroup_getBySlimefunItem = null;
    // Nexcavate
    public static @Nullable Method methodPlayerProgress_get = null;
    public static @Nullable Method methodNEGUI_openRecipe = null;
    public static @Nullable Method methodNEGUI_openResearchScreen = null;
    public static @Nullable Method methodNexcavateRegistry_getResearchMap = null;
    public static @Nullable Object objectNexcavate_registry = null;
    // InfinityExpansion
    public static @Nullable Method methodInfinityGroup_openInfinityRecipe = null;
    public static @Nullable Object objectInfinityExpansion_INFINITY = null;
    public static @Nullable Constructor<?> constructorInfinityExpansion_BackEntry = null;
    public static @Nullable Class<?> classInfinityExpansion_Singularity = null;
    // ObsidianExpansion
    public static @Nullable Method methodObsidianExpansion_openFORGERecipe = null; // check research
    public static @Nullable Constructor<?> constructorObsidianExpansion_BackEntry = null;
    // Galactifun
    public static @Nullable Object objectGalactifun_ASSEMBLY_CATEGORY = null;
    public static @Nullable Method methodGalactifun_displayItem = null;

    public static void load() {
        IntegrationManager.scheduleRun(SpecialMenuProvider::loadConfigurationInternal);
    }

    @CallTimeSensitive(CallTimeSensitive.AfterIntegrationsLoaded)
    private static void loadConfigurationInternal() {
        ENABLED_FinalTECH = JustEnoughGuide.getIntegrationManager().isEnabledFinalTECH();
        ENABLED_Nexcavate = JustEnoughGuide.getIntegrationManager().isEnabledNexcavate();
        ENABLED_LogiTech = JustEnoughGuide.getIntegrationManager().isEnabledLogiTech();
        ENABLED_InfinityExpansion = JustEnoughGuide.getIntegrationManager().isEnabledInfinityExpansion();
        ENABLED_ObsidianExpansion = JustEnoughGuide.getIntegrationManager().isEnabledObsidianExpansion();
        ENABLED_Galactifun = JustEnoughGuide.getIntegrationManager().isEnabledGalactifun();
        // FinalTECH | FinalTECH-Changed
        try {
            Method method = ReflectionUtil.getMethod(
                Class.forName("io.taraxacum.finaltech.core.group.RecipeItemGroup"),
                "getBySlimefunItem",
                Player.class,
                PlayerProfile.class,
                SlimefunGuideMode.class,
                SlimefunItem.class,
                int.class
            );
            if (method != null) {
                method.setAccessible(true);
                methodRecipeItemGroup_getBySlimefunItem = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        // Nexcavate
        try {
            Method method = ReflectionUtil.getMethod(
                Class.forName("me.char321.nexcavate.research.progress.PlayerProgress"), "get", Player.class);
            if (method != null) {
                method.setAccessible(true);
                methodPlayerProgress_get = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Method method = ReflectionUtil.getMethod(Class.forName("me.char321.nexcavate.gui.NEGUI"), "openRecipe");
            if (method != null) {
                method.setAccessible(true);
                methodNEGUI_openRecipe = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Method method =
                ReflectionUtil.getMethod(Class.forName("me.char321.nexcavate.gui.NEGUI"), "openResearchScreen");
            if (method != null) {
                method.setAccessible(true);
                methodNEGUI_openResearchScreen = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Method method =
                ReflectionUtil.getMethod(Class.forName("me.char321.nexcavate.NexcavateRegistry"), "getResearchMap");
            if (method != null) {
                method.setAccessible(true);
                methodNexcavateRegistry_getResearchMap = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Object instance =
                ReflectionUtil.getStaticValue(Class.forName("me.char321.nexcavate.Nexcavate"), "instance");
            if (instance != null) {
                objectNexcavate_registry = ReflectionUtil.getValue(instance, "registry");
            }
        } catch (ClassNotFoundException ignored) {
        }

        // InfinityExpansion
        try {
            Constructor<?> constructor = ReflectionUtil.getConstructor(
                Class.forName("io.github.mooy1.infinityexpansion.categories.InfinityGroup$BackEntry"),
                BlockMenu.class,
                PlayerProfile.class,
                SlimefunGuideImplementation.class
            );
            if (constructor != null) {
                constructor.setAccessible(true);
                constructorInfinityExpansion_BackEntry = constructor;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Object object = ReflectionUtil.getStaticValue(
                Class.forName("io.github.mooy1.infinityexpansion.categories.Groups"), "INFINITY");
            if (object != null) {
                objectInfinityExpansion_INFINITY = object;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Method method = ReflectionUtil.getMethod(
                Class.forName("io.github.mooy1.infinityexpansion.categories.InfinityGroup"), "openInfinityRecipe");
            if (method != null) {
                method.setAccessible(true);
                methodInfinityGroup_openInfinityRecipe = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Class<?> clazz = Class.forName("io.github.mooy1.infinityexpansion.items.materials.Singularity");
            if (clazz != null) {
                classInfinityExpansion_Singularity = clazz;
            }
        } catch (ClassNotFoundException ignored) {
            classInfinityExpansion_Singularity = null;
        }
        // ObsidianExpansion
        try {
            Constructor<?> constructor = ReflectionUtil.getConstructor(
                Class.forName("me.lucasgithuber.obsidianexpansion.utils.ObsidianForgeGroup$BackEntry"),
                BlockMenu.class,
                PlayerProfile.class,
                SlimefunGuideImplementation.class
            );
            if (constructor != null) {
                constructor.setAccessible(true);
                constructorObsidianExpansion_BackEntry = constructor;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Method method = ReflectionUtil.getMethod(
                Class.forName("me.lucasgithuber.obsidianexpansion.utils.ObsidianForgeGroup"), "openFORGERecipe");
            if (method != null) {
                method.setAccessible(true);
                methodObsidianExpansion_openFORGERecipe = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        // Galactifun
        try {
            Object ASSEMBLY_CATEGORY = ReflectionUtil.getStaticValue(Class.forName("io.github.addoncommunity.galactifun.core.CoreItemGroup"), "ASSEMBLY_CATEGORY");
            if (ASSEMBLY_CATEGORY != null) {
                objectGalactifun_ASSEMBLY_CATEGORY = ASSEMBLY_CATEGORY;
                Method method = ReflectionUtil.getMethod(ASSEMBLY_CATEGORY.getClass(), "displayItem");
                if (method != null) {
                    methodGalactifun_displayItem = method;
                    methodGalactifun_displayItem.setAccessible(true);
                }
            }
        } catch (ClassNotFoundException ignored) {
        }

        Debug.debug("SpecialMenuProvider initialized");
        Debug.debug("ENABLED_FinalTECH: " + ENABLED_FinalTECH);
        Debug.debug("ENABLED_Nexcavate: " + ENABLED_Nexcavate);
        Debug.debug("ENABLED_LogiTech: " + ENABLED_LogiTech);
        Debug.debug("ENABLED_InfinityExpansion: " + ENABLED_InfinityExpansion);
        Debug.debug("ENABLED_ObsidianExpansion: " + ENABLED_ObsidianExpansion);
        Debug.debug("-------------FinalTECH | FinalTECH-Changed-------------");
        Debug.debug("methodRecipeItemGroup_getBySlimefunItem: " + (methodRecipeItemGroup_getBySlimefunItem != null));
        Debug.debug("-------------Nexcavate------------");
        Debug.debug("methodPlayerProgress_get: " + (methodPlayerProgress_get != null));
        Debug.debug("methodNEGUI_openRecipe: " + (methodNEGUI_openRecipe != null));
        Debug.debug("methodNEGUI_openResearchScreen: " + (methodNEGUI_openResearchScreen != null));
        Debug.debug("methodNexcavateRegistry_getResearchMap: " + (methodNexcavateRegistry_getResearchMap != null));
        Debug.debug("objectNexcavate_registry: " + (objectNexcavate_registry != null));
        Debug.debug("-------------InfinityExpansion----------");
        Debug.debug("methodInfinityGroup_openInfinityRecipe: " + (methodInfinityGroup_openInfinityRecipe != null));
        Debug.debug("objectInfinityExpansion_INFINITY: " + (objectInfinityExpansion_INFINITY != null));
        Debug.debug("constructorInfinityExpansion_BackEntry: " + (constructorInfinityExpansion_BackEntry != null));
        Debug.debug("classInfinityExpansion_Singularity: " + (classInfinityExpansion_Singularity != null));
        Debug.debug("-------------ObsidianExpansion----------");
        Debug.debug("methodObsidianExpansion_openFORGERecipe: " + (methodObsidianExpansion_openFORGERecipe != null));
        Debug.debug("constructorObsidianExpansion_BackEntry: " + (constructorObsidianExpansion_BackEntry != null));
        Debug.debug("-------------Galactifun----------");
        Debug.debug("objectGalactifun_ASSEMBLY_CATEGORY: " + (objectGalactifun_ASSEMBLY_CATEGORY != null));
        Debug.debug("methodGalactifun_displayItem: " + (methodGalactifun_displayItem != null));
    }

    public static boolean isSpecialItem(SlimefunItem slimefunItem) {
        return isFinalTECHItem(slimefunItem)
            || isNexcavateItem(slimefunItem)
            || isLogiTechItem(slimefunItem)
            || isInfinityItem(slimefunItem)
            || isObsidianForgeItem(slimefunItem)
            || isGalactifunItem(slimefunItem);
    }

    public static boolean isFinalTECHItem(SlimefunItem slimefunItem) {
        if (!ENABLED_FinalTECH) return false;

        String addonName = slimefunItem.getAddon().getName();
        return addonName.startsWith("FinalTECH") && slimefunItem.getRecipe().length > COMMON_RECIPE_LENGTH;
    }

    public static boolean isNexcavateItem(SlimefunItem slimefunItem) {
        if (!ENABLED_Nexcavate) return false;

        String addonName = slimefunItem.getAddon().getName();
        if ("Nexcavate".equals(addonName)) {
            for (ItemStack itemStack : slimefunItem.getRecipe()) {
                if (itemStack != null) {
                    // Go to fallback
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isLogiTechItem(SlimefunItem slimefunItem) {
        if (!ENABLED_LogiTech) return false;

        String addonName = slimefunItem.getAddon().getName();
        return addonName.startsWith("LogiTech") && slimefunItem.getRecipe().length > COMMON_RECIPE_LENGTH;
    }

    public static boolean isInfinityItem(SlimefunItem slimefunItem) {
        if (!ENABLED_InfinityExpansion) return false;

        String addonName = slimefunItem.getAddon().getName();
        return addonName.startsWith("InfinityExpansion") && slimefunItem.getRecipe().length > COMMON_RECIPE_LENGTH;
    }

    public static boolean isObsidianForgeItem(SlimefunItem slimefunItem) {
        if (!ENABLED_ObsidianExpansion) return false;

        String addonName = slimefunItem.getAddon().getName();
        return "ObsidianExpansion".equals(addonName) && slimefunItem.getRecipe().length > COMMON_RECIPE_LENGTH;
    }

    public static boolean isGalactifunItem(SlimefunItem slimefunItem) {
        if (!ENABLED_Galactifun) return false;

        String addonName = slimefunItem.getAddon().getName();
        return "Galactifun".equals(addonName) && slimefunItem.getRecipe().length > COMMON_RECIPE_LENGTH;
    }

    public static boolean open(
        Player player,
        PlayerProfile profile,
        SlimefunGuideMode slimefunGuideMode,
        SlimefunItem slimefunItem)
        throws IllegalAccessException, InstantiationException, InvocationTargetException {
        GuideUtil.getProfile(profile);
        if (isFinalTECHItem(slimefunItem)) {
            FlexItemGroup flexItemGroup = getFinalTECHRecipeItemGroup(player, profile, slimefunGuideMode, slimefunItem);
            if (flexItemGroup != null) {
                flexItemGroup.open(player, profile, slimefunGuideMode);
                Debug.debug("Opened FinalTECH special menu");
                return true;
            }
        } else if (isNexcavateItem(slimefunItem)) {
            openNexcavateGuide(player, slimefunItem);
            Debug.debug("Opened Nexcavate special menu");
            return true;
        } else if (isLogiTechItem(slimefunItem)) {
            LogiTechSpecialMenuOpener.openLogiTechMenu(player, profile, slimefunItem);
            Debug.debug("Opened LogiTech special menu");
            return true;
        } else if (isInfinityExpansionSingularityItem(slimefunItem)) {
            openInfinityExpansionSingularityMenu(player, profile, slimefunItem);
            Debug.debug("Opened InfinityExpansion Singularity special menu");
            return true;
        } else if (isInfinityItem(slimefunItem)) {
            openInfinityMenu(player, profile, slimefunItem, slimefunGuideMode);
            Debug.debug("Opened InfinityExpansion special menu");
            return true;
        } else if (isObsidianForgeItem(slimefunItem)) {
            openObsidianForgeMenu(player, profile, slimefunItem, slimefunGuideMode);
            Debug.debug("Opened ObsidianExpansion special menu");
            return true;
        } else if (isGalactifunItem(slimefunItem)) {
            openGalactifunMenu(player, profile, slimefunItem, slimefunGuideMode);
            Debug.debug("Opened Galactifun special menu");
            return true;
        }
        return false;
    }

    @Nullable
    public static FlexItemGroup getFinalTECHRecipeItemGroup(
        Player player,
        PlayerProfile profile,
        SlimefunGuideMode slimefunGuideMode,
        SlimefunItem slimefunItem)
        throws InvocationTargetException, IllegalAccessException {
        if (!ENABLED_FinalTECH) return null;

        if (methodRecipeItemGroup_getBySlimefunItem == null) return null;

        methodRecipeItemGroup_getBySlimefunItem.setAccessible(true);
        return (FlexItemGroup) methodRecipeItemGroup_getBySlimefunItem.invoke(
            null, player, profile, slimefunGuideMode, slimefunItem, 1);
    }

    public static void openNexcavateGuide(Player player, SlimefunItem slimefunItem)
        throws IllegalAccessException, InvocationTargetException {
        if (!isNexcavateItem(slimefunItem)) return;

        ItemStack item = slimefunItem.getItem();
        if (methodNexcavateRegistry_getResearchMap == null) {
            return;
        }

        Object research = null;
        if (objectNexcavate_registry == null) {
            return;
        }
        Map<NamespacedKey, Object> researchMap =
            (Map<NamespacedKey, Object>) methodNexcavateRegistry_getResearchMap.invoke(objectNexcavate_registry);
        for (Object lresearch : researchMap.values()) {
            SlimefunItem NEItem = (SlimefunItem) ReflectionUtil.getValue(lresearch, "item");
            if (NEItem == null) {
                continue;
            }
            // No material conflict in Nexcavate
            if (NEItem.getItem().getType() == item.getType()) {
                research = lresearch;
                break;
            }
        }

        if (research == null) {
            return;
        }

        if (isPlayerResearchedNexcavate(player, research)) {
            if (methodNEGUI_openRecipe == null) {
                return;
            }
            methodNEGUI_openRecipe.invoke(null, player, research);
        } else {
            if (methodNEGUI_openResearchScreen == null) {
                return;
            }
            methodNEGUI_openResearchScreen.invoke(null, player);
        }
    }

    public static boolean isInfinityExpansionSingularityItem(SlimefunItem slimefunItem) {
        return classInfinityExpansion_Singularity != null
            && slimefunItem.getClass() == classInfinityExpansion_Singularity;
    }

    // Use LogiTech's menu to display its recipe :)
    public static void openInfinityExpansionSingularityMenu(Player player, PlayerProfile profile, SlimefunItem slimefunItem) {
        if (!ENABLED_InfinityExpansion || !ENABLED_LogiTech) return;

        if (isInfinityExpansionSingularityItem(slimefunItem)) {
            LogiTechSpecialMenuOpener.openLogiTechMenu(player, profile, slimefunItem);
        }
    }

    public static void openInfinityMenu(
        Player player,
        PlayerProfile profile,
        SlimefunItem slimefunItem,
        SlimefunGuideMode slimefunGuideMode)
        throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!ENABLED_InfinityExpansion) return;

        if (isInfinityItem(slimefunItem)) {
            if (isPlayerResearchedInfinity(player, profile, slimefunItem)) {
                if (constructorInfinityExpansion_BackEntry == null || methodInfinityGroup_openInfinityRecipe == null) {
                    return;
                }
                Object backEntry = constructorInfinityExpansion_BackEntry.newInstance(
                    null, profile, GuideUtil.getSlimefunGuide(slimefunGuideMode));
                methodInfinityGroup_openInfinityRecipe.invoke(null, player, slimefunItem.getId(), backEntry);
                /**
                 * Intentionally insert useless history twice to fix Back Button of InfinityGroup
                 * fixed in {@link SpecialMenuFixListener}
                 * @author balugaq
                 * @since 1.3
                 */
                insertUselessHistory(profile);
                insertUselessHistory(profile);
            } else {
                if (objectInfinityExpansion_INFINITY instanceof FlexItemGroup flexItemGroup) {
                    flexItemGroup.open(player, profile, slimefunGuideMode);
                }
            }
        }
    }

    public static void openObsidianForgeMenu(
        Player player,
        PlayerProfile profile,
        SlimefunItem slimefunItem,
        SlimefunGuideMode slimefunGuideMode)
        throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!ENABLED_ObsidianExpansion) return;

        if (isObsidianForgeItem(slimefunItem)) {
            if (constructorObsidianExpansion_BackEntry == null || methodObsidianExpansion_openFORGERecipe == null) {
                return;
            }
            Object backEntry = constructorObsidianExpansion_BackEntry.newInstance(
                null, profile, GuideUtil.getSlimefunGuide(slimefunGuideMode));
            methodObsidianExpansion_openFORGERecipe.invoke(null, player, slimefunItem.getId(), backEntry);
            insertUselessHistory(profile);
        }
    }

    public static void openGalactifunMenu(
        Player player,
        PlayerProfile profile,
        SlimefunItem slimefunItem,
        SlimefunGuideMode slimefunGuideMode) throws InvocationTargetException, IllegalAccessException {
        if (!ENABLED_Galactifun) return;

        if (isGalactifunItem(slimefunItem)) {
            if (methodGalactifun_displayItem == null) {
                return;
            }

            SlimefunItemStack sfis;
            if (slimefunItem.getItem() instanceof SlimefunItemStack is) {
                sfis = is;
            } else {
                sfis = new SlimefunItemStack(slimefunItem.getId(), slimefunItem.getItem());
            }
            Map.Entry<SlimefunItemStack, ItemStack[]> entry = Map.entry(
                sfis,
                slimefunItem.getRecipe()
            );
            methodGalactifun_displayItem.invoke(objectGalactifun_ASSEMBLY_CATEGORY, player, profile, entry);
        }
    }

    public static boolean isPlayerResearchedNexcavate(Player player, Object research)
        throws InvocationTargetException, IllegalAccessException {
        if (!ENABLED_Nexcavate) return false;

        if (methodPlayerProgress_get == null) return false;

        methodPlayerProgress_get.setAccessible(true);
        Object playerProgress = methodPlayerProgress_get.invoke(null, player);
        if (playerProgress == null) return false;

        Method method = ReflectionUtil.getMethod(playerProgress.getClass(), "isResearched", NamespacedKey.class);
        if (method == null) return false;

        method.setAccessible(true);
        NamespacedKey key = (NamespacedKey) ReflectionUtil.getValue(research, "key");
        return (boolean) method.invoke(playerProgress, key);
    }

    /**
     * This method is used to insert useless history into the player profile. It is used to fix the bug of the special
     * menu not working in some cases.
     *
     * @param profile The player profile to insert useless history
     * @author balugaq
     * @see SpecialMenuFixListener
     * @since 1.3
     */
    public static void insertUselessHistory(PlayerProfile profile) {
        profile.getGuideHistory().add(PLACEHOLDER_SEARCH_TERM);
    }

    public static boolean isPlayerResearchedInfinity(Player player, PlayerProfile profile, SlimefunItem slimefunItem) {
        if (!ENABLED_InfinityExpansion) return false;

        if (isInfinityItem(slimefunItem)) {
            Research research = slimefunItem.getResearch();
            if (research == null) {
                return true;
            }

            return profile.hasUnlocked(research);
        }

        return false;
    }

    public static void fallbackOpen(
        Player player,
        PlayerProfile profile,
        SlimefunGuideMode slimefunGuideMode,
        SlimefunItem slimefunItem) {
        SlimefunGuideImplementation implementation = GuideUtil.getSlimefunGuide(slimefunGuideMode);
        if (implementation instanceof JEGSlimefunGuideImplementation jeg) {
            jeg.displayItem(profile, slimefunItem, true);
        } else {
            implementation.displayItem(profile, slimefunItem, true);
        }
    }

}
