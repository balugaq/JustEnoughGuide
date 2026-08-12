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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.SearchGroupLoader;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.clickhandler.OnDisplay;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
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
import java.util.stream.Collectors;

/**
 * This group is used to display the search results of the search feature. Supports Pinyin search and page turning.
 *
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"deprecation", "unused", "ConstantValue", "JavaExistingMethodCanBeUsed"})
@NullMarked
public class SearchGroup extends BaseGroup<SearchGroup> {
    public static final ConcurrentHashMap<UUID, String> searchTerms = new ConcurrentHashMap<>();

    public static final Char2ObjectOpenHashMap<Set<SlimefunItem>> KEYWORD_CACHE =
        new Char2ObjectOpenHashMap<>(); // fast path for by item name
    public static final Char2ObjectOpenHashMap<Set<SlimefunItem>> DISPLAY_RECIPES_CACHE =
        new Char2ObjectOpenHashMap<>(); // fast path for by display item name
    public static final Map<String, Set<String>> SPECIAL_CACHE = new HashMap<>();

    public static final String DELIMITER = ",";

    static {
        KEYWORD_CACHE.defaultReturnValue(null);
        DISPLAY_RECIPES_CACHE.defaultReturnValue(null);
    }

    /**
     * Pre-built cache of display item names per Slimefun item ID.
     * Populated once during {@link #tryInit()} alongside CACHE2.
     * <p>
     * Key: Slimefun item ID.<br>
     * Value: unmodifiable list of lower-cased display names from
     * {@code getDisplayRecipes()}. Uses a hard reference (not SoftReference)
     * so the cache is never evicted under memory pressure — the strings are
     * cheap compared to retaining the live ItemStack objects.
     * <p>
     * Consumed by {@link com.balugaq.jeg.api.objects.enums.FilterType#BY_DISPLAY_ITEM_NAME}
     * to match display-item names without ever calling {@code getDisplayRecipes()}
     * at search time.
     */
    public static final Map<String, List<String>> DISPLAY_ITEM_NAMES_CACHE = new ConcurrentHashMap<>(5000);

    public static final int DEFAULT_MAP_SIZE = 5000;
    public static final Map<SlimefunItem, Integer> ENABLED_ITEMS = new HashMap<>(DEFAULT_MAP_SIZE);
    public static final Set<SlimefunItem> AVAILABLE_ITEMS = new HashSet<>(DEFAULT_MAP_SIZE);
    public static boolean LOADED = false;

    public final SlimefunGuideImplementation implementation;
    public final Player player;
    public final String searchTerm;
    public final boolean pinyin;
    public final List<SlimefunItem> slimefunItemList;

    public SearchGroup(
        SlimefunGuideImplementation implementation,
        final Player player,
        final String searchTerm) {
        this(implementation, player, searchTerm, JustEnoughGuide.getConfigManager().isPinyinSearch());
    }

    public SearchGroup(
        SlimefunGuideImplementation implementation,
        final Player player,
        final String searchTerm,
        boolean pinyin) {
        super();
        tryInit();
        this.page = 1;
        this.searchTerm = searchTerm;
        this.pinyin = pinyin;
        this.player = player;
        this.implementation = implementation;
        this.slimefunItemList = filterItems(player, searchTerm, pinyin);
        this.pageMap.put(1, this);
    }

    @Deprecated
    public SearchGroup(
        SlimefunGuideImplementation implementation,
        final Player player,
        final String searchTerm,
        boolean pinyin,
        boolean re_search_when_cache_failed) {
        this(implementation, player, searchTerm, pinyin);
    }

    public static boolean isFullNameApplicable(Player player, SlimefunItem slimefunItem, String searchTerm, boolean pinyin) {
        if (slimefunItem == null) return false;

        String itemName = ChatColor.stripColor(ItemStackUtil.getItemName(player, slimefunItem)).toLowerCase(Locale.ROOT);
        if (itemName.isEmpty()) return false;

        // Quick escape for common cases
        if (itemName.equalsIgnoreCase(searchTerm.toLowerCase(Locale.ROOT))) return true;

        return pinyin && getPinyin(itemName).equalsIgnoreCase(searchTerm.toLowerCase(Locale.ROOT));
    }

    public static String getPinyin(String string) {
        return getPinyin(string, PinyinStyleEnum.FIRST_LETTER);
    }

    public static String getPinyin(String string, PinyinStyleEnum style) {
        return PinyinHelper.toPinyin(string, style, "");
    }

    public static boolean isSearchFilterApplicable(Player player, SlimefunItem slimefunItem, String searchTerm, boolean pinyin) {
        if (slimefunItem == null) return false;

        String itemName = ChatColor.stripColor(ItemStackUtil.getItemName(player, slimefunItem)).toLowerCase(Locale.ROOT);
        return isSearchFilterApplicable(itemName, searchTerm.toLowerCase(Locale.ROOT), pinyin);
    }

    public static boolean isSearchFilterApplicable(String itemName, String searchTerm, boolean pinyin) {
        if (itemName.isEmpty()) return false;

        // Quick escape for common cases
        if (itemName.contains(searchTerm)) return true;

        return pinyin && getPinyin(itemName).contains(searchTerm);
    }

    @Deprecated
    public static boolean isSearchFilterApplicable(SlimefunItem slimefunItem, String searchTerm, boolean pinyin) {
        if (slimefunItem == null) return false;
        
        String itemName = ChatColor.stripColor(slimefunItem.getItemName()).toLowerCase(Locale.ROOT);
        return isSearchFilterApplicable(itemName, searchTerm.toLowerCase(Locale.ROOT), pinyin);
    }

    public static boolean isSearchFilterApplicable(ItemStack itemStack, String searchTerm, boolean pinyin) {
        if (itemStack == null) return false;
        
        String itemName = ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack)).toLowerCase(Locale.ROOT);
        return isSearchFilterApplicable(itemName, searchTerm.toLowerCase(Locale.ROOT), pinyin);
    }

    public static List<SlimefunItem> filterItems(
        Player player,
        FilterType filterType,
        String filterValue,
        boolean pinyin,
        List<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase(Locale.ROOT);
        return items.stream()
            .filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin))
            .toList();
    }

    public static List<ItemStack> filterItem(
        Player player,
        FilterType filterType,
        String filterValue,
        boolean pinyin,
        List<ItemStack> items,
        boolean passNonSlimefun) {
        String lowerFilterValue = filterValue.toLowerCase(Locale.ROOT);
        return items.stream()
            .filter(item -> {
                SlimefunItem sf = SlimefunItem.getByItem(item);
                if (sf != null) return filterType.getFilter().apply(player, sf, lowerFilterValue, pinyin);
                return passNonSlimefun;
            })
            .toList();
    }

    public static Set<SlimefunItem> filterItems(
        Player player,
        FilterType filterType,
        String filterValue,
        boolean pinyin,
        Set<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase(Locale.ROOT);
        return items.stream()
            .filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin))
            .collect(Collectors.toSet());
    }

    public static Set<ItemStack> filterItems(
        Player player,
        FilterType filterType,
        String filterValue,
        boolean pinyin,
        Set<ItemStack> items,
        boolean passNonSlimefun) {
        String lowerFilterValue = filterValue.toLowerCase(Locale.ROOT);
        return items.stream()
            .filter(item -> {
                SlimefunItem sf = SlimefunItem.getByItem(item);
                if (sf != null) return filterType.getFilter().apply(player, sf, lowerFilterValue, pinyin);
                return passNonSlimefun;
            })
            .collect(Collectors.toSet());
    }

    /**
     * Initializes the search group by populating caches and preparing data.
     */
    public static void tryInit() {
        if (LOADED) return;

        LOADED = true;
        Debug.debug("Initializing Search Group...");
        JustEnoughGuide.runLaterAsync(SearchGroupLoader::load, 3L);
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
            if (c > 127) return false;
        }
        return true;
    }

    public static int levenshteinDistance(String s1, String s2) {
        if (s1.length() < s2.length()) return levenshteinDistance(s2, s1);

        if (s2.isEmpty()) return s1.length();

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
                -nameFit(
                    getPinyin(ChatColor.stripColor(item.getItemName())),
                    searchTerm
                )))
            .toList();
    }

    @Override
    public boolean isVisible(
        final Player player,
        final PlayerProfile profile,
        final SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    @Override
    public ChestMenu generateMenu(
        final Player player,
        final PlayerProfile profile,
        final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("你正在搜索: " + ChatUtils.crop(ChatColor.WHITE, searchTerm));

        Format format = Formats.sub;
        int maxPage = (this.slimefunItemList.size() - 1) / format.getChars(Formats.Char.CONTENT).size() + 1;
        GuideUtil.commonRender(chestMenu, format, profile, player, this, this.page, maxPage);

        List<Integer> contentSlots = format.getChars(Formats.Char.CONTENT);
        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < this.slimefunItemList.size()) {
                SlimefunItem slimefunItem = slimefunItemList.get(index);
                OnDisplay.Item.display(player, slimefunItem, OnDisplay.Item.Search, implementation)
                    .at(chestMenu, contentSlots.get(i), page);
            }
        }

        return chestMenu;
    }

    @Deprecated
    public List<SlimefunItem> getAllMatchedItems(
        Player p, String searchTerm, boolean pinyin) {
        return filterItems(p, searchTerm, pinyin);
    }

    public List<SlimefunItem> filterItems(Player player, String searchTerm, boolean pinyin) {
        if (searchTerm.isEmpty()) return Collections.emptyList();
        StringBuilder sb = new StringBuilder();
        String[] split = searchTerm.split(DELIMITER); // don't use the space " ", since it is used in many item names
        EnumMap<FilterType, String> filters = new EnumMap<>(FilterType.class);
        for (String s : split) {
            boolean isFilter = false;
            for (FilterType filterType : FilterType.lengthSortedValues()) {
                for (var symbol : filterType.getSymbols()) {
                    if (s.length() <= symbol.length()) continue;
                    if (s.startsWith(symbol)) {
                        isFilter = true;
                        String filterValue = s.substring(symbol.length());
                        filters.put(filterType, filterValue);
                        break;
                    } else if (s.endsWith(symbol)) {
                        isFilter = true;
                        String filterValue = s.substring(0, s.length() - symbol.length());
                        filters.put(filterType, filterValue);
                        break;
                    }
                }
            }

            if (!isFilter) sb.append(s).append(DELIMITER); // pad the delimiter
        }

        searchTerm = sb.toString();
        // remove tail delimiter
        if (searchTerm.endsWith(DELIMITER)) {
            searchTerm = searchTerm.substring(0, searchTerm.length() - DELIMITER.length());
        }

        searchTerm = FilterType.quoteFlags(searchTerm.trim());

        Set<SlimefunItem> result = new HashSet<>(36 * 4);
        // The unfiltered items
        Set<SlimefunItem> items = new HashSet<>(AVAILABLE_ITEMS.stream()
            .filter(item -> item.getItemGroup().isAccessible(player))
            .toList());

        if (!searchTerm.isBlank()) {
            result.addAll(matchItems(searchTerm, KEYWORD_CACHE));
            result.addAll(matchItems(searchTerm, DISPLAY_RECIPES_CACHE));
        }

        // Filter items
        if (!filters.isEmpty()) {
            for (Map.Entry<FilterType, String> entry : filters.entrySet()) {
                items = filterItems(entry.getKey(), entry.getValue(), pinyin, items);
            }

            result.addAll(items);
        }

        if (pinyin && onlyAscii(searchTerm)) {
            return sortByPinyinContinuity(result, searchTerm);
        } else {
            return sortByNameFit(result, searchTerm);
        }
    }

    private Set<SlimefunItem> matchItems(String searchTerm, Map<Character, Set<SlimefunItem>> cache) {
        Set<SlimefunItem> result = null;
        for (char c : searchTerm.toCharArray()) {
            Set<SlimefunItem> items = cache.get(c);
            if (items == null || items.isEmpty()) {
                return Collections.emptySet();
            }

            if (result == null) {
                result = new HashSet<>(items);
            } else {
                result.retainAll(items);
            }

            if (result.isEmpty()) return Collections.emptySet();
        }
        return result != null ? result : Collections.emptySet();
    }

    public List<SlimefunItem> filterItems(
        FilterType filterType,
        String filterValue,
        boolean pinyin,
        List<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase(Locale.ROOT);
        return items.stream()
            .filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin))
            .toList();
    }

    public Set<SlimefunItem> filterItems(
        FilterType filterType,
        String filterValue,
        boolean pinyin,
        Set<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase(Locale.ROOT);
        return items.stream()
            .filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin))
            .collect(Collectors.toSet());
    }
}
