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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.Timer;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.clickhandler.OnDisplay;
import com.balugaq.jeg.utils.formatter.Formats;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.RandomizedSet;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This group is used to display the search results of the search feature.
 * Supports Pinyin search and page turning.
 *
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"deprecation", "unused", "ConstantValue", "JavaExistingMethodCanBeUsed"})
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
@NullMarked
public class SearchGroup extends BaseGroup<SearchGroup> {
    public static final ConcurrentHashMap<UUID, String> searchTerms = new ConcurrentHashMap<>();

    public static final Char2ObjectOpenHashMap<Reference<Set<SlimefunItem>>> CACHE =
            new Char2ObjectOpenHashMap<>(); // fast way for by item name
    public static final Char2ObjectOpenHashMap<Reference<Set<SlimefunItem>>> CACHE2 =
            new Char2ObjectOpenHashMap<>(); // fast way for by display item name
    public static final Map<String, Reference<Set<String>>> SPECIAL_CACHE = new HashMap<>();

    public static final Boolean SHOW_HIDDEN_ITEM_GROUPS =
            Slimefun.getConfigManager().isShowHiddenItemGroupsInSearch();
    public static final Integer DEFAULT_HASH_SIZE = 5000;
    public static final Map<SlimefunItem, Integer> ENABLED_ITEMS = new HashMap<>(DEFAULT_HASH_SIZE);
    public static final Set<SlimefunItem> AVAILABLE_ITEMS = new HashSet<>(DEFAULT_HASH_SIZE);

    public static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();

    public static boolean LOADED = false;
    public final SlimefunGuideImplementation implementation;
    public final Player player;
    public final String searchTerm;
    public final boolean pinyin;
    public final List<SlimefunItem> slimefunItemList;
    public final boolean re_search_when_cache_failed;

    public SearchGroup(
            SlimefunGuideImplementation implementation,
            final Player player,
            final String searchTerm,
            boolean pinyin) {
        this(implementation, player, searchTerm, pinyin, true);
    }

    public SearchGroup(
            SlimefunGuideImplementation implementation,
            final Player player,
            final String searchTerm,
            boolean pinyin,
            boolean re_search_when_cache_failed) {
        super();
        if (!LOADED) {
            init();
        }
        this.page = 1;
        this.searchTerm = searchTerm;
        this.pinyin = pinyin;
        this.player = player;
        this.re_search_when_cache_failed = re_search_when_cache_failed;
        this.implementation = implementation;
        this.slimefunItemList = filterItems(player, searchTerm, pinyin);
        this.pageMap.put(1, this);
    }

    public static boolean isSearchFilterApplicable(SlimefunItem slimefunItem, String searchTerm, boolean pinyin) {
        if (slimefunItem == null) {
            return false;
        }
        String itemName = ChatColor.stripColor(slimefunItem.getItemName()).toLowerCase(Locale.ROOT);
        return isSearchFilterApplicable(itemName, searchTerm.toLowerCase(), pinyin);
    }

    public static boolean isSearchFilterApplicable(ItemStack itemStack, String searchTerm, boolean pinyin) {
        if (itemStack == null) {
            return false;
        }
        String itemName =
                ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack)).toLowerCase(Locale.ROOT);
        return isSearchFilterApplicable(itemName, searchTerm.toLowerCase(), pinyin);
    }

    public static boolean isSearchFilterApplicable(String itemName, String searchTerm, boolean pinyin) {
        if (itemName.isEmpty()) {
            return false;
        }

        // Quick escape for common cases
        boolean result = itemName.contains(searchTerm);
        if (result) {
            return true;
        }

        if (pinyin) {
            final String pinyinFirstLetter = getPinyin(itemName);
            return pinyinFirstLetter.contains(searchTerm);
        }

        return false;
    }

    public static String getPinyin(String string) {
        return getPinyin(string, PinyinStyleEnum.FIRST_LETTER);
    }

    public static String getPinyin(String string, PinyinStyleEnum style) {
        return PinyinHelper.toPinyin(string, style, "");
    }

    public static List<SlimefunItem> filterItems(
            Player player,
            FilterType filterType,
            String filterValue,
            boolean pinyin,
            List<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream()
                .filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin))
                .toList();
    }

    public static Set<SlimefunItem> filterItems(
            Player player,
            FilterType filterType,
            String filterValue,
            boolean pinyin,
            Set<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream()
                .filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin))
                .collect(Collectors.toSet());
    }

    /**
     * Initializes the search group by populating caches and preparing data.
     */
    public static void init() {
        if (LOADED) {
            return;
        }

        LOADED = true;
        Debug.debug("Initializing Search Group...");
        Timer.start();
        JustEnoughGuide.runLaterAsync(() -> {
                    // Initialize asynchronously
                    int i = 0;
                    for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
                        try {
                            ENABLED_ITEMS.put(item, i);
                            i += 1;
                            if (item.isHidden() && !SHOW_HIDDEN_ITEM_GROUPS) {
                                continue;
                            }

                            ItemStack[] r = item.getRecipe();
                            if (r == null) {
                                continue;
                            }

                            if (item.isDisabled()) {
                                continue;
                            }
                            AVAILABLE_ITEMS.add(item);
                            try {
                                String id = item.getId();
                                if (!SPECIAL_CACHE.containsKey(id)) {
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

                                                                // RykenSlimeCustomizer
                                                                // CustomTemplateMachine
                                                                else if (Otemplates instanceof List<?> templates) {
                                                                    for (Object template : templates) {
                                                                        Object _Orecipes = ReflectionUtil.getValue(template, "recipes");
                                                                        if (_Orecipes == null) {
                                                                            Method method = ReflectionUtil.getMethod(template.getClass(), "recipes");
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
                                                                        String s = ItemStackHelper.getDisplayName(itemStack);
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
                                            String s = ItemStackHelper.getDisplayName(new ItemStack((Material) Omaterial));
                                            if (!inBanlist(s)) {
                                                cache.add(s);
                                            }
                                        }
                                    }
                                    // InfinityExpansion ResourceSynthesizer
                                    if (Orecipes instanceof SlimefunItemStack[] recipes) {
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
                                        SPECIAL_CACHE.put(id, new SoftReference<>(cache));
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        } catch (Exception ignored) {
                        }
                    }

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
                    SPECIAL_CACHE.put("STONEWORKS_FACTORY", new SoftReference<>(cache));

                    // InfinityExpansion VoidHarvester
                    SlimefunItem item2 = SlimefunItem.getById("VOID_BIT");
                    if (item2 != null) {
                        Set<String> cache2 = new HashSet<>();
                        String s = item2.getItemName();
                        if (!inBanlist(s)) {
                            cache2.add(s);
                            SPECIAL_CACHE.put("VOID_HARVESTER", new SoftReference<>(cache2));
                            SPECIAL_CACHE.put("INFINITY_VOID_HARVESTER", new SoftReference<>(cache2));
                        }
                    }

                    // InfinityExpansion MobDataCard
                    label2:
                    {
                        try {
                            Class<?> MobDataCardClass = Class.forName(
                                    "io.github.mooy1.infinityexpansion.items.mobdata.MobDataCard");
                            @SuppressWarnings("unchecked")
                            Map<String, Object> cards = (Map<String, Object>)
                                    ReflectionUtil.getStaticValue(MobDataCardClass, "CARDS");
                            if (cards == null) {
                                break label2;
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
                                SPECIAL_CACHE.put(
                                        ((SlimefunItem) card).getId(), new SoftReference<>(cache2));
                            });
                        } catch (Exception ignored) {
                        }
                    }

                    for (SlimefunItem slimefunItem : AVAILABLE_ITEMS) {
                        try {
                            if (slimefunItem == null) {
                                continue;
                            }
                            String name = ChatColor.stripColor(slimefunItem.getItemName());
                            for (char c : name.toCharArray()) {
                                char d = Character.toLowerCase(c);
                                CACHE.putIfAbsent(d, new SoftReference<>(new HashSet<>()));
                                Reference<Set<SlimefunItem>> ref = CACHE.get(d);
                                if (ref != null) {
                                    Set<SlimefunItem> set = ref.get();
                                    if (set != null) {
                                        if (!inBanlist(slimefunItem)) {
                                            set.add(slimefunItem);
                                        }
                                    }
                                }
                            }

                            if (JustEnoughGuide.getConfigManager().isPinyinSearch()) {
                                final String pinyinFirstLetter =
                                        PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "");
                                for (char c : pinyinFirstLetter.toCharArray()) {
                                    char d = Character.toLowerCase(c);
                                    CACHE.putIfAbsent(d, new SoftReference<>(new HashSet<>()));
                                    Reference<Set<SlimefunItem>> ref = CACHE.get(d);
                                    if (ref != null) {
                                        Set<SlimefunItem> set = ref.get();
                                        if (set == null) {
                                            set = new HashSet<>();
                                            CACHE.put(d, new SoftReference<>(set));
                                        }
                                        if (!inBanlist(slimefunItem)) {
                                            set.add(slimefunItem);
                                        }
                                    }
                                }
                            }

                            List<ItemStack> displayRecipes = null;
                            if (slimefunItem instanceof AContainer ac) {
                                displayRecipes = ac.getDisplayRecipes();
                            } else if (slimefunItem instanceof MultiBlockMachine mb) {
                                try {
                                    displayRecipes = mb.getDisplayRecipes();
                                } catch (Exception e) {
                                    Debug.trace(e, "init searching");
                                }
                            } else if (SpecialMenuProvider.ENABLED_LogiTech
                                    && SpecialMenuProvider.classLogiTech_CustomSlimefunItem != null
                                    && SpecialMenuProvider.classLogiTech_CustomSlimefunItem.isInstance(
                                    slimefunItem)
                                    && slimefunItem instanceof RecipeDisplayItem rdi) {
                                try {
                                    displayRecipes = rdi.getDisplayRecipes();
                                } catch (Exception e) {
                                    Debug.trace(e, "init searching");
                                }
                            }
                            if (displayRecipes != null) {
                                for (ItemStack itemStack : displayRecipes) {
                                    if (itemStack != null) {
                                        String name2 = ChatColor.stripColor(
                                                ItemStackHelper.getDisplayName(itemStack));
                                        for (char c : name2.toCharArray()) {
                                            char d = Character.toLowerCase(c);
                                            CACHE2.putIfAbsent(d, new SoftReference<>(new HashSet<>()));
                                            Reference<Set<SlimefunItem>> ref = CACHE2.get(d);
                                            if (ref != null) {
                                                Set<SlimefunItem> set = ref.get();
                                                if (set == null) {
                                                    set = new HashSet<>();
                                                    CACHE2.put(d, new SoftReference<>(set));
                                                }
                                                if (!inBanlist(slimefunItem)
                                                        && !inBlacklist(slimefunItem)) {
                                                    set.add(slimefunItem);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            String id = slimefunItem.getId();
                            if (SPECIAL_CACHE.containsKey(id)) {
                                Reference<Set<String>> ref2 = SPECIAL_CACHE.get(id);
                                if (ref2 != null) {
                                    Set<String> cache2 = ref2.get();
                                    if (cache2 != null) {
                                        for (String s : cache2) {
                                            for (char c : s.toCharArray()) {
                                                char d = Character.toLowerCase(c);
                                                CACHE2.putIfAbsent(d, new SoftReference<>(new HashSet<>()));
                                                Reference<Set<SlimefunItem>> ref = CACHE2.get(d);
                                                if (ref != null) {
                                                    Set<SlimefunItem> set = ref.get();
                                                    if (set != null) {
                                                        if (!inBanlist(slimefunItem)
                                                                && !inBlacklist(slimefunItem)) {
                                                            set.add(slimefunItem);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }

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
                            SlimefunItems.GOLD_24K));
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
                    SPECIAL_CACHE.put("SMART_FACTORY", new SoftReference<>(items));

                    for (String s :
                            JustEnoughGuide.getConfigManager().getSharedChars()) {
                        Set<SlimefunItem> sharedItems = new HashSet<>();
                        for (char c : s.toCharArray()) {
                            Reference<Set<SlimefunItem>> ref = CACHE.get(c);
                            if (ref == null) {
                                continue;
                            }
                            Set<SlimefunItem> set = ref.get();
                            if (set == null) {
                                continue;
                            }
                            sharedItems.addAll(set);
                        }
                        if (!sharedItems.isEmpty()) {
                            for (char c : s.toCharArray()) {
                                Reference<Set<SlimefunItem>> ref = CACHE.get(c);
                                if (ref != null) {
                                    Set<SlimefunItem> set = ref.get();
                                    if (set != null) {
                                        set.addAll(sharedItems);
                                        Debug.debug("Shared cache added to CACHE char \"" + c + "\" ("
                                                + sharedItems.size() + " items)");
                                    }
                                }
                            }
                        }

                        Set<SlimefunItem> sharedItems2 = new HashSet<>();
                        for (char c : s.toCharArray()) {
                            Reference<Set<SlimefunItem>> ref = CACHE2.get(c);
                            if (ref == null) {
                                continue;
                            }
                            Set<SlimefunItem> set = ref.get();
                            if (set == null) {
                                continue;
                            }
                            sharedItems2.addAll(set);
                        }
                        if (!sharedItems2.isEmpty()) {
                            for (char c : s.toCharArray()) {
                                Reference<Set<SlimefunItem>> ref = CACHE2.get(c);
                                if (ref != null) {
                                    Set<SlimefunItem> set = ref.get();
                                    if (set != null) {
                                        set.addAll(sharedItems2);
                                        Debug.debug("Shared cache added to CACHE2 char \"" + c + "\" ("
                                                + sharedItems2.size() + " items)");
                                    }
                                }
                            }
                        }
                    }

                    Debug.debug("Cache initialized.");

                    Timer.log();
                    Debug.debug("Search Group initialized.");
                    Debug.debug("Enabled items: " + ENABLED_ITEMS.size());
                    Debug.debug("Available items: " + AVAILABLE_ITEMS.size());
                    Debug.debug("Machine blocks cache: " + SPECIAL_CACHE.size());
                    Debug.debug("Shared cache: "
                            + JustEnoughGuide.getConfigManager()
                            .getSharedChars()
                            .size());
                    Debug.debug("Cache 1 (Keywords): " + CACHE.size());
                    Debug.debug("Cache 2 (Display Recipes): " + CACHE2.size());
                },
                1L);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean inBanlist(SlimefunItem slimefunItem) {
        return inBanlist(slimefunItem.getItemName());
    }

    public static boolean inBanlist(String itemName) {
        for (String s : JustEnoughGuide.getConfigManager().getBanlist()) {
            if (ChatColor.stripColor(itemName).contains(s)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean inBlacklist(SlimefunItem slimefunItem) {
        return inBlacklist(slimefunItem.getItemName());
    }

    public static boolean inBlacklist(String itemName) {
        for (String s : JustEnoughGuide.getConfigManager().getBlacklist()) {
            if (ChatColor.stripColor(itemName).contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean onlyAscii(String str) {
        for (char c : str.toCharArray()) {
            if (c > 127) {
                return false;
            }
        }
        return true;
    }

    public static int levenshteinDistance(String s1, String s2) {
        if (s1.length() < s2.length()) {
            return levenshteinDistance(s2, s1);
        }

        if (s2.isEmpty()) {
            return s1.length();
        }

        int[] previousRow = new int[s2.length() + 1];
        for (int i = 0; i <= s2.length(); i++) {
            previousRow[i] = i;
        }

        for (int i = 0; i < s1.length(); i++) {
            char c1 = s1.charAt(i);
            int[] currentRow = new int[s2.length() + 1];
            currentRow[0] = i + 1;

            for (int j = 0; j < s2.length(); j++) {
                char c2 = s2.charAt(j);
                int insertions = previousRow[j + 1] + 1;
                int deletions = currentRow[j] + 1;
                int substitutions = previousRow[j] + (c1 == c2 ? 0 : 1);
                currentRow[j + 1] = Math.min(Math.min(insertions, deletions), substitutions);
            }

            previousRow = currentRow;
        }

        return previousRow[s2.length()];
    }

    /**
     * Calculates the name fit score between two strings.
     *
     * @param name       The name to calculate the name fit score for.
     * @param searchTerm The search term
     * @return The name fit score. Non-negative integer.
     */
    public static int nameFit(String name, String searchTerm) {
        int distance = levenshteinDistance(searchTerm.toLowerCase(Locale.ROOT), name.toLowerCase(Locale.ROOT));
        int maxLen = Math.max(searchTerm.length(), name.length());

        int matchScore;
        if (maxLen == 0) {
            matchScore = 100;
        } else {
            matchScore = (int) (100 * (1 - (double) distance / maxLen));
        }

        return matchScore;
    }

    public static List<SlimefunItem> sortByNameFit(
            Set<SlimefunItem> origin, String searchTerm) {
        return origin.stream()
                .sorted(Comparator.comparingInt(item ->
                        /* Intentionally negative */
                        -nameFit(ChatColor.stripColor(item.getItemName()), searchTerm)))
                .toList();
    }

    public static List<SlimefunItem> sortByPinyinContinuity(
            Set<SlimefunItem> origin, String searchTerm) {
        return origin.stream()
                .sorted(Comparator.comparingInt(item ->
                        /* Intentionally negative */
                        -nameFit(getPinyin(ChatColor.stripColor(item.getItemName())), searchTerm)))
                .toList();
    }

    @Override
    public boolean isVisible(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    @Override
    public ChestMenu generateMenu(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu =
                new ChestMenu("你正在搜索: %item%".replace("%item%", ChatUtils.crop(ChatColor.WHITE, searchTerm)));

        OnClick.preset(chestMenu);

        for (int ss : Formats.sub.getChars('b')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.Back.patch(
                            player,
                            ChestMenuUtils.getBackButton(player, "", "&f左键: &7返回上一页", "&fShift + 左键: &7返回主菜单")));
            chestMenu.addMenuClickHandler(ss, (pl, s, is, action) -> EventUtil.callEvent(
                            new GuideEvents.BackButtonClickEvent(pl, is, s, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideHistory guideHistory = playerProfile.getGuideHistory();
                        if (action.isShiftClicked()) {
                            SlimefunGuide.openMainMenu(
                                    playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
                        } else {
                            guideHistory.goBack(implementation);
                        }
                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('S')) {
            chestMenu.addItem(ss, PatchScope.Search.patch(player, ChestMenuUtils.getSearchButton(player)));
            chestMenu.addMenuClickHandler(ss, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.SearchButtonClickEvent(pl, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        pl.closeInventory();

                        Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                        ChatInput.waitForPlayer(
                                JAVA_PLUGIN,
                                pl,
                                msg -> implementation.openSearch(
                                        playerProfile,
                                        msg,
                                        implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE));

                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('P')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.PreviousPage.patch(
                            player,
                            ChestMenuUtils.getPreviousButton(
                                    player,
                                    this.page,
                                    (this.slimefunItemList.size() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.PreviousButtonClickEvent(p, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        SearchGroup searchGroup = this.getByPage(Math.max(this.page - 1, 1));
                        searchGroup.open(player, playerProfile, slimefunGuideMode);
                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('N')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.NextPage.patch(
                            player,
                            ChestMenuUtils.getNextButton(
                                    player,
                                    this.page,
                                    (this.slimefunItemList.size() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.NextButtonClickEvent(p, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        SearchGroup searchGroup = this.getByPage(Math.min(
                                this.page + 1,
                                (this.slimefunItemList.size() - 1)
                                        / Formats.sub.getChars('i').size()
                                        + 1));
                        searchGroup.open(player, playerProfile, slimefunGuideMode);
                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('B')) {
            chestMenu.addItem(ss, PatchScope.Background.patch(player, ChestMenuUtils.getBackground()));
            chestMenu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        List<Integer> contentSlots = Formats.sub.getChars('i');

        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < this.slimefunItemList.size()) {
                SlimefunItem slimefunItem = slimefunItemList.get(index);
                OnDisplay.Item.display(player, slimefunItem, OnDisplay.Item.Search, implementation)
                        .at(chestMenu, contentSlots.get(i), page);
            }
        }

        GuideUtil.addRTSButton(chestMenu, player, playerProfile, Formats.sub, slimefunGuideMode, implementation);
        if (implementation instanceof JEGSlimefunGuideImplementation jeg) {
            GuideUtil.addBookMarkButton(chestMenu, player, playerProfile, Formats.sub, jeg, this);
            GuideUtil.addItemMarkButton(chestMenu, player, playerProfile, Formats.sub, jeg, this);
        }

        Formats.sub.renderCustom(chestMenu);
        return chestMenu;
    }

    @Deprecated
    public List<SlimefunItem> getAllMatchedItems(
            Player p, String searchTerm, boolean pinyin) {
        return filterItems(p, searchTerm, pinyin);
    }

    public List<SlimefunItem> filterItems(Player player, String searchTerm, boolean pinyin) {
        StringBuilder actualSearchTermBuilder = new StringBuilder();
        String[] split = searchTerm.split(" ");
        Map<FilterType, String> filters = new HashMap<>();
        for (String s : split) {
            boolean isFilter = false;
            for (FilterType filterType : FilterType.values()) {
                if (s.startsWith(filterType.getSymbol())
                        && s.length() > filterType.getSymbol().length()) {
                    isFilter = true;
                    String filterValue = s.substring(filterType.getSymbol().length());
                    filters.put(filterType, filterValue);
                    break;
                }
            }

            if (!isFilter) {
                actualSearchTermBuilder.append(s).append(" ");
            }
        }

        String actualSearchTerm = actualSearchTermBuilder.toString().trim();
        for (FilterType filterType : FilterType.values()) {
            String flag = filterType.getSymbol();
            // Quote the flag to be used as a literal replacement
            actualSearchTerm = actualSearchTerm.replaceAll(Pattern.quote(flag), Matcher.quoteReplacement(flag));
        }
        Set<SlimefunItem> merge = new HashSet<>(36 * 4);
        // The unfiltered items
        Set<SlimefunItem> items = new HashSet<>(AVAILABLE_ITEMS.stream()
                .filter(item -> item.getItemGroup().isAccessible(player))
                .toList());

        if (!actualSearchTerm.isBlank()) {
            Set<SlimefunItem> nameMatched = new HashSet<>();
            Set<SlimefunItem> allMatched = null;
            for (char c : actualSearchTerm.toCharArray()) {
                Set<SlimefunItem> cache;
                Reference<Set<SlimefunItem>> ref = CACHE.get(c);
                if (ref == null) {
                    cache = new HashSet<>();
                } else {
                    cache = ref.get();
                }
                if (cache == null) {
                    cache = new HashSet<>();
                }
                if (allMatched == null) {
                    allMatched = new HashSet<>(cache);
                } else {
                    allMatched.retainAll(new HashSet<>(cache));
                }
            }
            if (allMatched != null) {
                nameMatched.addAll(allMatched);
            }
            Set<SlimefunItem> machineMatched = new HashSet<>();
            Set<SlimefunItem> allMatched2 = null;
            for (char c : actualSearchTerm.toCharArray()) {
                Set<SlimefunItem> cache;
                Reference<Set<SlimefunItem>> ref = CACHE2.get(c);
                if (ref == null) {
                    cache = new HashSet<>();
                } else {
                    cache = ref.get();
                }
                if (cache == null) {
                    cache = new HashSet<>();
                }
                if (allMatched2 == null) {
                    allMatched2 = new HashSet<>(cache);
                } else {
                    allMatched2.retainAll(new HashSet<>(cache));
                }
            }
            if (allMatched2 != null) {
                machineMatched.addAll(allMatched2);
            }
            Debug.debug("Name matched: " + nameMatched.size());
            Debug.debug("Machine matched: " + machineMatched.size());
            merge.addAll(nameMatched);
            merge.addAll(machineMatched);
            if (this.re_search_when_cache_failed) {
                if (nameMatched.isEmpty()) {
                    Debug.debug("Re-searching item name by filters (Normal search)");
                    Set<SlimefunItem> clone = new HashSet<>(items);
                    Set<SlimefunItem> result = filterItems(FilterType.BY_ITEM_NAME, actualSearchTerm, pinyin, clone);
                    merge.addAll(result);
                }
                if (machineMatched.isEmpty()) {
                    Debug.debug("Re-searching display item name by filters (Normal search)");
                    Set<SlimefunItem> clone = new HashSet<>(items);
                    Set<SlimefunItem> result =
                            filterItems(FilterType.BY_DISPLAY_ITEM_NAME, actualSearchTerm, pinyin, clone);
                    merge.addAll(result);
                }
            }
        }

        // Filter items
        if (!filters.isEmpty()) {
            for (Map.Entry<FilterType, String> entry : filters.entrySet()) {
                items = filterItems(entry.getKey(), entry.getValue(), pinyin, items);
            }

            merge.addAll(items);
        }

        if (pinyin && onlyAscii(searchTerm)) {
            return sortByPinyinContinuity(merge, actualSearchTerm);
        } else {
            return sortByNameFit(merge, actualSearchTerm);
        }
    }

    public List<SlimefunItem> filterItems(
            FilterType filterType,
            String filterValue,
            boolean pinyin,
            List<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream()
                .filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin))
                .toList();
    }

    public Set<SlimefunItem> filterItems(
            FilterType filterType,
            String filterValue,
            boolean pinyin,
            Set<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream()
                .filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin))
                .collect(Collectors.toSet());
    }
}
