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
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static com.balugaq.jeg.api.groups.SearchGroup.AVAILABLE_ITEMS;
import static com.balugaq.jeg.api.groups.SearchGroup.DISPLAY_ITEM_NAMES_CACHE;
import static com.balugaq.jeg.api.groups.SearchGroup.DISPLAY_RECIPES_CACHE;
import static com.balugaq.jeg.api.groups.SearchGroup.ENABLED_ITEMS;
import static com.balugaq.jeg.api.groups.SearchGroup.KEYWORD_CACHE;
import static com.balugaq.jeg.api.groups.SearchGroup.SPECIAL_CACHE;
import static com.balugaq.jeg.api.groups.SearchGroup.inBanlist;
import static com.balugaq.jeg.api.groups.SearchGroup.inBlacklist;

@SuppressWarnings({"removal", "deprecation"})
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

    private static void addToCache(Set<String> cache, String s) {
        if (!inBanlist(s)) cache.add(s);
    }

    private static void addToCache(Map<Character, Set<SlimefunItem>> cache, String s, SlimefunItem slimefunItem) {
        for (char d : s.toCharArray()) {
            addToCache(cache, Character.toLowerCase(d), slimefunItem);
        }
    }

    private static void addToCache(Map<Character, Set<SlimefunItem>> cache, char d, SlimefunItem slimefunItem) {
        cache.computeIfAbsent(d, k -> new HashSet<>());
        Set<SlimefunItem> set = cache.get(d);
        if (!inBanlist(slimefunItem)) {
            if (cache == KEYWORD_CACHE || cache == DISPLAY_RECIPES_CACHE && !inBlacklist(slimefunItem)) {
                set.add(slimefunItem);
            }
        }
    }

    private static void initInfinityExpansionVoidHarvester() {
        // InfinityExpansion VoidHarvester
        SlimefunItem item2 = SlimefunItem.getById("VOID_BIT");
        if (item2 == null) return;
        Set<String> cache2 = new HashSet<>();
        checkBan(item2.getItemName(), s -> {
            cache2.add(s);
            SPECIAL_CACHE.put("VOID_HARVESTER", cache2);
            SPECIAL_CACHE.put("INFINITY_VOID_HARVESTER", cache2);
        });
    }

    private static boolean loadDynatechAbstractElectricMachine(SlimefunItem item, Set<String> cache) {
        if (!ItemStackUtil.isInstanceSimple(item, "AbstractElectricMachine")) return false;
        if (!(ReflectionUtil.getValue(item, "recipes") instanceof List<?> recipes)) return false;
        // DynaTech - AbstractElectricMachine
        // recipes -> List<MachineRecipe>
        for (var recipe : recipes) {
            if (recipe instanceof MachineRecipe machineRecipe) {
                for (ItemStack input : machineRecipe.getInput()) {
                    String s = ItemStackHelper.getDisplayName(input);
                    if (!inBanlist(s)) {
                        cache.add(s);
                    }
                }
                for (ItemStack output : machineRecipe.getOutput()) {
                    String s = ItemStackHelper.getDisplayName(output);
                    addToCache(cache, s);
                }
            }
        }
        return true;
    }

    private static boolean loadInfinityLibMachineBlock(SlimefunItem item, Set<String> cache) {
        if (!ItemStackUtil.isInstanceSimple(item, "MachineBlock")) return false;
        if (!(ReflectionUtil.getValue(item, "recipes") instanceof List<?> recipes)) return false;
        // InfinityLib - MachineBlock
        for (var recipe : recipes) {
            String[] strings = (String[]) ReflectionUtil.getValue(recipe, "strings");
            if (strings == null) continue;

            for (String string : strings) {
                SlimefunItem slimefunItem = SlimefunItem.getById(string);
                if (slimefunItem != null) {
                    addToCache(cache, slimefunItem.getItemName());
                    continue;
                }

                Material material = Material.getMaterial(string);
                if (material == null) continue;
                addToCache(cache, ItemStackHelper.getDisplayName(new ItemStack(material)));
            }

            ItemStack output = (ItemStack) ReflectionUtil.getValue(recipe, "output");
            if (output == null) continue;
            addToCache(cache, ItemStackHelper.getDisplayName(output));
        }
        return true;
    }

    private static boolean loadInfinityExpansionGrowingMachine(SlimefunItem item, Set<String> cache) {
        if (!ItemStackUtil.isInstanceSimple(item, "GrowingMachine")) return false;
        if (!(ReflectionUtil.getValue(item, "recipes") instanceof EnumMap<?,?> map)) return false;
        for (var obj : map.values()) {
            if (!(obj instanceof ItemStack[] items)) return false;
            for (ItemStack itemStack : items) {
                addToCache(cache, ItemStackHelper.getDisplayName(itemStack));
            }
        }
        return true;
    }

    private static boolean loadInfinityExpansionResourceSynthesizer(SlimefunItem item, Set<String> cache) {
        // InfinityExpansion ResourceSynthesizer
        if (!ItemStackUtil.isInstanceSimple(item, "ResourceSynthesizer")) return false;
        if (!(ReflectionUtil.getValue(item, "recipes") instanceof SlimefunItemStack[] recipes)) return false;
        for (SlimefunItemStack slimefunItemStack : recipes) {
            SlimefunItem slimefunItem = slimefunItemStack.getItem();
            if (slimefunItem == null) continue;
            addToCache(cache, slimefunItem.getItemName());
        }
        return true;
    }

    private static boolean loadInfinityExpansionMaterialGenerator(SlimefunItem item, Set<String> cache) {
        // InfinityExpansion MaterialGenerator
        if (!ItemStackUtil.isInstanceSimple(item, "MaterialGenerator")) return false;
        if (!(ReflectionUtil.getValue(item, "material") instanceof Material material)) return false;
        addToCache(cache, ItemStackHelper.getDisplayName(new ItemStack(material)));
        return true;
    }

    private static boolean loadInfinityExpansionSingularityConstructor(SlimefunItem item, Set<String> cache) {
        // InfinityExpansion SingularityConstructor
        if (!ItemStackUtil.isInstanceSimple(item, "SingularityConstructor")) return false;
        if (!(ReflectionUtil.getValue(item, "RECIPE_LIST") instanceof List<?> recipes)) return false;
        for (Object recipe : recipes) {
            if (!(ReflectionUtil.getValue(recipe, "input") instanceof ItemStack input)) return false;
            addToCache(cache, ItemStackHelper.getDisplayName(input));
            if (!(ReflectionUtil.getValue(recipe, "output") instanceof SlimefunItemStack output)) return false;
            SlimefunItem slimefunItem = output.getItem();
            if (slimefunItem != null) addToCache(cache, slimefunItem.getItemName());
        }
        return true;
    }

    private static boolean loadRykenCustomTemplateMachine(SlimefunItem item, Set<String> cache) {
        // RykenSlimefunCustomizer - CustomTemplateMachine
        if (!ItemStackUtil.isInstanceSimple(item, "CustomTemplateMachine")) return false;
        if (!(ReflectionUtil.getValue(item, "templates") instanceof List<?> templates)) return false;

        for (var template : templates) {
            Object o = ReflectionUtil.getValue(template, "recipes");
            if (o == null) o = ReflectionUtil.invokeMethod(template, "recipes");
            if (!(o instanceof List<?> recipes)) continue;

            for (Object recipe : recipes) {
                if (!(recipe instanceof MachineRecipe machineRecipe)) continue;
                for (ItemStack output : machineRecipe.getOutput()) {
                    addToCache(cache, ItemStackHelper.getDisplayName(output));
                }
            }
        }
        return true;
    }

    private static boolean loadRykenCustomMaterialGenerator(SlimefunItem item, Set<String> cache) {
        // RykenSlimeCustomizer - CustomMaterialGenerator
        if (!ItemStackUtil.isInstanceSimple(item, "CustomMaterialGenerator")) return false;
        if (!(ReflectionUtil.getValue(item, "generation") instanceof List<?> generation)) return false;

        for (Object g : generation) {
            if (!(g instanceof ItemStack itemStack)) continue;
            addToCache(cache, ItemStackHelper.getDisplayName(itemStack));
        }
        return true;
    }

    private static boolean loadChineseLocalizedCustomMaterialGenerator(SlimefunItem item, Set<String> cache) {
        // Chinese Localized SlimeCustomizer - CustomMaterialGenerator
        if (!ItemStackUtil.isInstanceSimple(item, "CustomMaterialGenerator")) return false;
        if (!(ReflectionUtil.getValue(item, "output") instanceof ItemStack output)) return false;

        addToCache(cache, ItemStackHelper.getDisplayName(output));
        return true;
    }

    private static boolean loadInfinityExpansionStrainerBase(SlimefunItem item, Set<String> cache) {
        // InfinityExpansion - StrainerBase
        if (!ItemStackUtil.isInstanceSimple(item, "StrainerBase")) return false;
        if (!(ReflectionUtil.getValue(item, "OUTPUTS") instanceof ItemStack[] outputs)) return false;

        for (ItemStack output : outputs) {
            addToCache(cache, ItemStackHelper.getDisplayName(output));
        }
        return true;
    }

    private static boolean loadInfinityExpansionQuarry(SlimefunItem item, Set<String> cache) {
        // InfinityExpansion - Quarry
        if (!ItemStackUtil.isInstanceSimple(item, "Quarry")) return false;
        if (!(ReflectionUtil.getValue(item, "outputs") instanceof Material[] outputs)) return false;

        for (Material material : outputs) {
            addToCache(cache, ItemStackHelper.getDisplayName(new ItemStack(material)));
        }
        return true;
    }

    @SafeVarargs
    private static void findAny(SlimefunItem item, Set<String> cache, BiPredicate<SlimefunItem, Set<String>>... functions) {
        for (var function : functions) {
            if (function.test(item, cache)) return;
        }
    }

    public static void load() {
        var tm = Timer.start();
        int i = 0;
        for (SlimefunItem item : new ArrayList<>(Slimefun.getRegistry().getEnabledSlimefunItems())) {
            ENABLED_ITEMS.put(item, i);
            i += 1;
            if ((item.isHidden() && !Slimefun.getConfigManager().isShowHiddenItemGroupsInSearch())
                || item.getItemGroup().getClass().isAnnotationPresent(DontShowInSearch.class)
                || item.isDisabled()
                || item.getRecipe() == null) {
                continue;
            }

            AVAILABLE_ITEMS.add(item);

            String id = item.getId();
            if (SPECIAL_CACHE.containsKey(id)) continue;

            Set<String> cache = new HashSet<>();

            findAny(item, cache,
                SearchGroupLoader::loadInfinityLibMachineBlock,
                SearchGroupLoader::loadDynatechAbstractElectricMachine,
                SearchGroupLoader::loadInfinityExpansionGrowingMachine,
                SearchGroupLoader::loadInfinityExpansionResourceSynthesizer,
                SearchGroupLoader::loadInfinityExpansionMaterialGenerator,
                SearchGroupLoader::loadInfinityExpansionSingularityConstructor,
                SearchGroupLoader::loadRykenCustomTemplateMachine,
                SearchGroupLoader::loadRykenCustomMaterialGenerator,
                SearchGroupLoader::loadChineseLocalizedCustomMaterialGenerator,
                SearchGroupLoader::loadInfinityExpansionStrainerBase,
                SearchGroupLoader::loadInfinityExpansionQuarry
            );

            if (!cache.isEmpty()) SPECIAL_CACHE.put(id, cache);
        }

        initInfinityExpansionStoneworksFactory();
        initInfinityExpansionVoidHarvester();
        initInfinityExpansionMobDataCard();
        initFluffyMachinesSmartFactory();
        initCommon();

        for (String s : JustEnoughGuide.getConfigManager().getSharedChars()) {
            shareCache(KEYWORD_CACHE, s);
            shareCache(DISPLAY_RECIPES_CACHE, s);
        }

        tm.logs();
        Debug.debug("Cache initialized.");
        Debug.debug("Search Group initialized.");
        Debug.debug("Enabled items: " + ENABLED_ITEMS.size());
        Debug.debug("Available items: " + AVAILABLE_ITEMS.size());
        Debug.debug("Machine blocks cache: " + SPECIAL_CACHE.size());
        Debug.debug("Shared cache: " + JustEnoughGuide.getConfigManager().getSharedChars().size());
        Debug.debug("Cache 1 (Keywords): " + KEYWORD_CACHE.size());
        Debug.debug("Cache 2 (Display Recipes): " + DISPLAY_RECIPES_CACHE.size());
    }

    private static void initCommon() {
        for (SlimefunItem slimefunItem : AVAILABLE_ITEMS) {
            String name = ChatColor.stripColor(slimefunItem.getItemName());
            addToCache(KEYWORD_CACHE, name, slimefunItem);

            if (JustEnoughGuide.getConfigManager().isPinyinSearch()) {
                addToCache(KEYWORD_CACHE, PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, ""), slimefunItem);
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
                        addToCache(DISPLAY_RECIPES_CACHE, d, slimefunItem);
                    }
                }
                if (!displayNames.isEmpty()) {
                    DISPLAY_ITEM_NAMES_CACHE.put(slimefunItem.getId(), List.copyOf(displayNames));
                }
            }

            String id = slimefunItem.getId();
            Set<String> cache = SPECIAL_CACHE.get(id);
            if (cache != null) {
                for (String s : cache) {
                    addToCache(DISPLAY_RECIPES_CACHE, s, slimefunItem);
                }
            }
        }
    }

    private static @Nullable List<ItemStack> getDisplayRecipes(SlimefunItem slimefunItem) {
        List<ItemStack> displayRecipes = null;
        switch (slimefunItem) {
            case AContainer ac -> {
                displayRecipes = ac.getDisplayRecipes();
            }
            case MultiBlockMachine mbm -> {
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
            default -> {
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
        if (foundChars.isEmpty()) return;
        for (char c : foundChars) {
            cache.put(c, allItems);  // 直接覆盖，共享同一个对象
        }
    }
}
