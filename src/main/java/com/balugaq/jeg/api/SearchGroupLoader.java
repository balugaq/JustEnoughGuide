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

package com.balugaq.jeg.api;

import com.balugaq.jeg.api.interfaces.DontShowInSearch;
import com.balugaq.jeg.api.objects.Timer;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.RandomizedSet;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.balugaq.jeg.api.groups.SearchGroup.*;

@SuppressWarnings("removal")
public class SearchGroupLoader {
    private static void initInfinityExpansionStoneworksFactory() {
        // InfinityExpansion StoneworksFactory
        Set<Material> materials = new HashSet<>();
        materials.add(Material.COBBLESTONE);
        materials.add(Material.STONE);
        materials.add(Material.SAND);
        materials.add(Material.STONE_BRICKS);
        materials.add(Material.SMOOTH_STONE);
        materials.add(Material.GLASS);
        materials.add(Material.CRACKED_STONE_BRICKS);
        materials.add(Material.GRAVEL);
        materials.add(Material.GRANITE);
        materials.add(Material.DIORITE);
        materials.add(Material.ANDESITE);
        materials.add(Material.POLISHED_GRANITE);
        materials.add(Material.POLISHED_DIORITE);
        materials.add(Material.POLISHED_ANDESITE);
        materials.add(Material.SANDSTONE);
        Set<String> cache = new HashSet<>();
        for (Material material : materials) {
            String s = ItemStackHelper.getDisplayName(new ItemStack(material));
            if (!inBanlist(s)) {
                cache.add(s);
            }
        }
        SPECIAL_CACHE.put("STONEWORKS_FACTORY", cache);
    }

    private static void checkBan(String s, Consumer<String> consumer) {
        if (!inBanlist(s)) consumer.accept(s);
    }

    private static void initFluffyMachinesSmartFactory() {
        // FluffyMachines SmartFactory
        Set<SlimefunItemStack> ACCEPTED_ITEMS = new HashSet<>(Arrays.asList(
            SlimefunItems.BILLON_INGOT,
            SlimefunItems.SOLDER_INGOT,
            SlimefunItems.NICKEL_INGOT,
            SlimefunItems.COBALT_INGOT,
            SlimefunItems.DURALUMIN_INGOT,
            SlimefunItems.BRONZE_INGOT,
            SlimefunItems.BRASS_INGOT,
            SlimefunItems.ALUMINUM_BRASS_INGOT,
            SlimefunItems.STEEL_INGOT,
            SlimefunItems.DAMASCUS_STEEL_INGOT,
            SlimefunItems.ALUMINUM_BRONZE_INGOT,
            SlimefunItems.CORINTHIAN_BRONZE_INGOT,
            SlimefunItems.GILDED_IRON,
            SlimefunItems.REDSTONE_ALLOY,
            SlimefunItems.HARDENED_METAL_INGOT,
            SlimefunItems.REINFORCED_ALLOY_INGOT,
            SlimefunItems.FERROSILICON,
            SlimefunItems.ELECTRO_MAGNET,
            SlimefunItems.ELECTRIC_MOTOR,
            SlimefunItems.HEATING_COIL,
            SlimefunItems.SYNTHETIC_EMERALD,
            SlimefunItems.GOLD_4K,
            SlimefunItems.GOLD_6K,
            SlimefunItems.GOLD_8K,
            SlimefunItems.GOLD_10K,
            SlimefunItems.GOLD_12K,
            SlimefunItems.GOLD_14K,
            SlimefunItems.GOLD_16K,
            SlimefunItems.GOLD_18K,
            SlimefunItems.GOLD_20K,
            SlimefunItems.GOLD_22K,
            SlimefunItems.GOLD_24K
        ));
        Set<String> items = new HashSet<>();
        for (SlimefunItemStack slimefunItemStack : ACCEPTED_ITEMS) {
            SlimefunItem slimefunItem = slimefunItemStack.getItem();
            if (slimefunItem != null) {
                String s = slimefunItem.getItemName();
                if (!inBanlist(s)) {
                    items.add(s);
                }
            }
        }
        SPECIAL_CACHE.put("SMART_FACTORY", items);
    }

    private static void initInfinityExpansionMobDataCard() {
        // InfinityExpansion MobDataCard
        try {
            Class<?> MobDataCardClass = Class.forName("io.github.mooy1.infinityexpansion.items.mobdata.MobDataCard");
            @SuppressWarnings("unchecked")
            Map<String, Object> cards = (Map<String, Object>) ReflectionUtil.getStaticValue(MobDataCardClass, "CARDS");
            if (cards == null) {
                return;
            }
            cards.values().forEach(card -> {
                @SuppressWarnings("unchecked")
                RandomizedSet<ItemStack> drops =
                    (RandomizedSet<ItemStack>) ReflectionUtil.getValue(card, "drops");
                if (drops == null) {
                    return;
                }
                Set<String> cache2 = new HashSet<>();
                for (ItemStack itemStack :
                    drops.toMap().keySet()) {
                    String s = ItemStackHelper.getDisplayName(itemStack);
                    if (!inBanlist(s)) {
                        cache2.add(s);
                    }
                }
                SPECIAL_CACHE.put(((SlimefunItem) card).getId(), cache2);
            });
        } catch (Exception ignored) {
        }
    }

    private static void addToCache(Map<Character, Set<SlimefunItem>> cache, String s, SlimefunItem slimefunItem) {
        for (char d : s.toCharArray()) {
            addToCache(cache, Character.toLowerCase(d), slimefunItem);
        }
    }

    private static void addToCache(Map<Character, Set<SlimefunItem>> cache, char d, SlimefunItem slimefunItem) {
        cache.putIfAbsent(d, new HashSet<>());
        Set<SlimefunItem> set = cache.get(d);
        if (!inBanlist(slimefunItem) && !inBlacklist(slimefunItem)) {
            set.add(slimefunItem);
        }
    }

    private static void initInfinityExpansionVoidHarvester() {
        // InfinityExpansion VoidHarvester
        SlimefunItem item2 = SlimefunItem.getById("VOID_BIT");
        if (item2 != null) {
            Set<String> cache2 = new HashSet<>();
            checkBan(item2.getItemName(), s -> {
                cache2.add(s);
                SPECIAL_CACHE.put("VOID_HARVESTER", cache2);
                SPECIAL_CACHE.put("INFINITY_VOID_HARVESTER", cache2);
            });
        }
    }

    public static void load() {
        var tm = Timer.start();
        int i = 0;
        for (SlimefunItem item : new ArrayList<>(Slimefun.getRegistry().getEnabledSlimefunItems())) {
            ENABLED_ITEMS.put(item, i);
            i += 1;
            if ((item.isHidden() && !SHOW_HIDDEN_ITEM_GROUPS)
                || item.getItemGroup().getClass().isAnnotationPresent(DontShowInSearch.class)
                || item.isDisabled()
                || item.getRecipe() == null) {
                continue;
            }

            AVAILABLE_ITEMS.add(item);

            String id = item.getId();
            if (SPECIAL_CACHE.containsKey(id)) continue;

            // <editor-fold desc="反射">
            try {
                Set<String> cache = new HashSet<>();

                // init cache
                Object Orecipes = ReflectionUtil.getValue(item, "recipes");
                if (Orecipes == null) {
                    Object Omaterial = ReflectionUtil.getValue(item, "material");
                    if (Omaterial == null) {
                        Object ORECIPE_LIST = ReflectionUtil.getValue(item, "RECIPE_LIST");
                        if (ORECIPE_LIST == null) {
                            Object Ooutputs = ReflectionUtil.getValue(item, "outputs");
                            if (Ooutputs == null) {
                                Object OOUTPUTS = ReflectionUtil.getValue(item, "OUTPUTS");
                                if (OOUTPUTS == null) {
                                    Object Ooutput = ReflectionUtil.getValue(item, "output");
                                    if (Ooutput == null) {
                                        Object Ogeneration = ReflectionUtil.getValue(item, "generation");
                                        if (Ogeneration == null) {
                                            Object Otemplates = ReflectionUtil.getValue(item, "templates");
                                            if (Otemplates == null) {
                                                continue;
                                            }

                                            // RykenSlimefunCustomizer
                                            // CustomTemplateMachine
                                            else if (Otemplates instanceof List<?> templates) {
                                                for (Object template : templates) {
                                                    Object _Orecipes =
                                                        ReflectionUtil.getValue(
                                                            template,
                                                            "recipes"
                                                        );
                                                    if (_Orecipes == null) {
                                                        Method method =
                                                            ReflectionUtil.getMethod(template.getClass(), "recipes");
                                                        if (method != null) {
                                                            try {
                                                                method.setAccessible(true);
                                                                _Orecipes = method.invoke(template);
                                                            } catch (Exception ignored) {
                                                            }
                                                        }
                                                    }

                                                    if (_Orecipes instanceof List<?> _recipes) {
                                                        for (Object _recipe : _recipes) {
                                                            if (_recipe instanceof MachineRecipe machineRecipe) {
                                                                ItemStack[] _output = machineRecipe.getOutput();
                                                                for (ItemStack __output : _output) {
                                                                    String s = ItemStackHelper.getDisplayName(__output);
                                                                    if (!inBanlist(s)) {
                                                                        cache.add(s);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        // RykenSlimeCustomizer CustomMaterialGenerator
                                        else if (Ogeneration instanceof List<?> generation) {
                                            for (Object g : generation) {
                                                if (g instanceof ItemStack itemStack) {
                                                    String s =
                                                        ItemStackHelper.getDisplayName(itemStack);
                                                    if (!inBanlist(s)) {
                                                        cache.add(s);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    // Chinese Localized SlimeCustomizer
                                    // CustomMaterialGenerator
                                    else if (Ooutput instanceof ItemStack output) {
                                        String s = ItemStackHelper.getDisplayName(output);
                                        if (!inBanlist(s)) {
                                            cache.add(s);
                                        }
                                    }
                                }
                                // InfinityExpansion StrainerBase
                                if (OOUTPUTS instanceof ItemStack[] outputs) {
                                    if (!ItemStackUtil.isInstanceSimple(item, "StrainerBase")) {
                                        continue;
                                    }
                                    for (ItemStack output : outputs) {
                                        String s = ItemStackHelper.getDisplayName(output);
                                        if (!inBanlist(s)) {
                                            cache.add(s);
                                        }
                                    }
                                }
                            }
                            // InfinityExpansion Quarry
                            else if (Ooutputs instanceof Material[] outputs) {
                                if (!ItemStackUtil.isInstanceSimple(item, "Quarry")) {
                                    continue;
                                }
                                for (Material material : outputs) {
                                    String s = ItemStackHelper.getDisplayName(
                                        new ItemStack(material));
                                    if (!inBanlist(s)) {
                                        cache.add(s);
                                    }
                                }
                            }
                        }
                        // InfinityExpansion SingularityConstructor
                        else if (ORECIPE_LIST instanceof List<?> recipes) {
                            if (!ItemStackUtil.isInstanceSimple(item, "SingularityConstructor")) {
                                continue;
                            }
                            for (Object recipe : recipes) {
                                ItemStack input = (ItemStack)
                                    ReflectionUtil.getValue(recipe, "input");
                                if (input != null) {
                                    String s = ItemStackHelper.getDisplayName(input);
                                    if (!inBanlist(s)) {
                                        cache.add(s);
                                    }
                                }
                                SlimefunItemStack output = (SlimefunItemStack)
                                    ReflectionUtil.getValue(recipe, "output");
                                if (output != null) {
                                    SlimefunItem slimefunItem = output.getItem();
                                    if (slimefunItem != null) {
                                        String s = slimefunItem.getItemName();
                                        if (!inBanlist(s)) {
                                            cache.add(s);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // InfinityExpansion MaterialGenerator
                        if (!ItemStackUtil.isInstanceSimple(item, "MaterialGenerator")) {
                            continue;
                        }
                        String s =
                            ItemStackHelper.getDisplayName(new ItemStack((Material) Omaterial));
                        if (!inBanlist(s)) {
                            cache.add(s);
                        }
                    }
                }
                // InfinityExpansion ResourceSynthesizer
                else if (Orecipes instanceof SlimefunItemStack[] recipes) {
                    if (!ItemStackUtil.isInstanceSimple(item, "ResourceSynthesizer")) {
                        continue;
                    }
                    for (SlimefunItemStack slimefunItemStack : recipes) {
                        SlimefunItem slimefunItem = slimefunItemStack.getItem();
                        if (slimefunItem != null) {
                            String s = slimefunItem.getItemName();
                            if (!inBanlist(s)) {
                                cache.add(s);
                            }
                        }
                    }
                }
                // InfinityExpansion GrowingMachine
                else if (Orecipes instanceof EnumMap<?, ?> recipes) {
                    if (!ItemStackUtil.isInstanceSimple(item, "GrowingMachine")) {
                        continue;
                    }
                    recipes.values().forEach(obj -> {
                        ItemStack[] items = (ItemStack[]) obj;
                        for (ItemStack itemStack : items) {
                            String s = ItemStackHelper.getDisplayName(itemStack);
                            if (!inBanlist(s)) {
                                cache.add(s);
                            }
                        }
                    });
                }
                // InfinityExpansion MachineBlock
                else if (Orecipes instanceof List<?> recipes) {
                    if (ItemStackUtil.isInstanceSimple(item, "MachineBlock")) {
                        // InfinityLib - MachineBlock
                        for (Object recipe : recipes) {
                            String[] strings = (String[])
                                ReflectionUtil.getValue(recipe, "strings");
                            if (strings == null) {
                                continue;
                            }
                            for (String string : strings) {
                                SlimefunItem slimefunItem =
                                    SlimefunItem.getById(string);
                                if (slimefunItem != null) {
                                    String s = slimefunItem.getItemName();
                                    if (!inBanlist(s)) {
                                        cache.add(s);
                                    }
                                } else {
                                    Material material = Material.getMaterial(string);
                                    if (material != null) {
                                        String s = ItemStackHelper.getDisplayName(
                                            new ItemStack(material));
                                        if (!inBanlist(s)) {
                                            cache.add(s);
                                        }
                                    }
                                }
                            }

                            ItemStack output = (ItemStack)
                                ReflectionUtil.getValue(recipe, "output");
                            if (output != null) {
                                String s = ItemStackHelper.getDisplayName(output);
                                if (!inBanlist(s)) {
                                    cache.add(s);
                                }
                            }
                        }
                    } else if (ItemStackUtil.isInstanceSimple(item, "AbstractElectricMachine")) {
                        // DynaTech - AbstractElectricMachine
                        // recipes -> List<MachineRecipe>
                        for (Object recipe : recipes) {
                            if (recipe instanceof MachineRecipe machineRecipe) {
                                for (ItemStack input : machineRecipe.getInput()) {
                                    String s = ItemStackHelper.getDisplayName(input);
                                    if (!inBanlist(s)) {
                                        cache.add(s);
                                    }
                                }
                                for (ItemStack output : machineRecipe.getOutput()) {
                                    String s = ItemStackHelper.getDisplayName(output);
                                    if (!inBanlist(s)) {
                                        cache.add(s);
                                    }
                                }
                            }
                        }
                    }
                }

                if (!cache.isEmpty()) {
                    SPECIAL_CACHE.put(id, cache);
                }
            } catch (Exception ignored) {
            }
            // </editor-fold>
        }

        initInfinityExpansionStoneworksFactory();
        initInfinityExpansionVoidHarvester();
        initInfinityExpansionMobDataCard();

        for (SlimefunItem slimefunItem : AVAILABLE_ITEMS) {
            String name = ChatColor.stripColor(slimefunItem.getItemName());
            addToCache(CACHE, name, slimefunItem);

            if (JustEnoughGuide.getConfigManager().isPinyinSearch()) {
                addToCache(CACHE, PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, ""), slimefunItem);
            }

            List<ItemStack> displayRecipes = getDisplayRecipes(slimefunItem);
            if (displayRecipes != null) {
                List<String> displayNames = new ArrayList<>();
                for (ItemStack itemStack : displayRecipes) {
                    if (itemStack == null) continue;

                    String name2 = ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack));
                    if (name2.isEmpty()) continue;

                    // Populate DISPLAY_ITEM_NAMES_CACHE for fast string-only lookup.
                    displayNames.add(name2.toLowerCase(Locale.ROOT));
                    // Also populate the character-index CACHE2 as before.
                    for (char c : name2.toCharArray()) {
                        char d = Character.toLowerCase(c);
                        addToCache(CACHE2, d, slimefunItem);
                    }
                }
                if (!displayNames.isEmpty()) {
                    DISPLAY_ITEM_NAMES_CACHE.put(slimefunItem.getId(), List.copyOf(displayNames));
                }
            }

            String id = slimefunItem.getId();
            Set<String> cache2 = SPECIAL_CACHE.get(id);
            if (cache2 != null) {
                for (String s : cache2) {
                    addToCache(CACHE2, s, slimefunItem);
                }
            }
        }

        initFluffyMachinesSmartFactory();

        for (String s : JustEnoughGuide.getConfigManager().getSharedChars()) {
            shareCache(CACHE, s);
            shareCache(CACHE2, s);
        }

        tm.logs();
        Debug.debug("Cache initialized.");
        Debug.debug("Search Group initialized.");
        Debug.debug("Enabled items: " + ENABLED_ITEMS.size());
        Debug.debug("Available items: " + AVAILABLE_ITEMS.size());
        Debug.debug("Machine blocks cache: " + SPECIAL_CACHE.size());
        Debug.debug("Shared cache: " + JustEnoughGuide.getConfigManager().getSharedChars().size());
        Debug.debug("Cache 1 (Keywords): " + CACHE.size());
        Debug.debug("Cache 2 (Display Recipes): " + CACHE2.size());
    }

    private static @Nullable List<ItemStack> getDisplayRecipes(SlimefunItem slimefunItem) {
        List<ItemStack> displayRecipes = null;
        switch(slimefunItem) {
            case AContainer ac-> {
                displayRecipes = ac.getDisplayRecipes();
            }
            case MultiBlockMachine mbm-> {
                try {
                    displayRecipes = mbm.getDisplayRecipes();
                } catch (Exception ignored) {
                }
            }
            case RecipeDisplayItem rdi -> {
                if (SpecialMenuProvider.ENABLED_LogiTech && SpecialMenuProvider.classLogiTech_CustomSlimefunItem != null && SpecialMenuProvider.classLogiTech_CustomSlimefunItem.isInstance(slimefunItem)) {
                    try {
                        displayRecipes = rdi.getDisplayRecipes();
                    } catch (Exception ignored) {
                    }
                }
            }
            default->{
            }
        }
        return displayRecipes;
    }

    private static void shareCache(Map<Character, Set<SlimefunItem>> cache, String s) {
        // 收集所有关联的 SlimefunItem
        Set<SlimefunItem> allItems = new HashSet<>();
        List<Character> foundChars = new ArrayList<>();

        for (char c : s.toCharArray()) {
            Set<SlimefunItem> set = cache.get(c);
            if (set != null && !set.isEmpty()) {
                foundChars.add(c);
                allItems.addAll(set);
            }
        }

        // 所有字符共享同一个 Set 引用
        if (!foundChars.isEmpty() && !allItems.isEmpty()) {
            for (char c : foundChars) {
                cache.put(c, allItems);  // 直接覆盖，共享同一个对象
            }
        }
    }
}
