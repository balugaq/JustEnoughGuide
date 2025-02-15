package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.Timer;
import com.balugaq.jeg.api.objects.annotaions.Warn;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.core.services.localization.LanguagePreset;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.RandomizedSet;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.SlimefunTranslationAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This group is used to display the search results of the search feature.
 *
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"deprecation", "unused"})
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
public class SearchGroup extends FlexItemGroup {
    @Deprecated
    public static final Integer ACONTAINER_OFFSET = 50000;
    public static final Integer EN_THRESHOLD = 2;
    public static final Integer MAX_FIX_TIMES = 3;
    public static final String SPLIT = " ";
    @Warn(reason = "No longer using it in EN version")
    public static final Map<Character, Reference<Set<SlimefunItem>>> CACHE = new HashMap<>(); // fast way for by item name
    @Warn(reason = "No longer using it in EN version")
    public static final Map<Character, Reference<Set<SlimefunItem>>> CACHE2 = new HashMap<>(); // fast way for by display item name
    public static final List<String> EN_WORDS = new ArrayList<>();
    public static final Map<String, List<String>> EN_CACHE_ROLLBACK = new HashMap<>();
    public static final Map<String, Reference<Set<String>>> SPECIAL_CACHE = new HashMap<>();
    public static final Map<String, Reference<Set<SlimefunItem>>> EN_CACHE = new HashMap<>();
    public static final Map<String, Reference<Set<SlimefunItem>>> EN_CACHE2 = new HashMap<>();
    @Warn(reason = "No longer using it in EN version")
    public static final Set<String> SHARED_CHARS = new HashSet<>();
    public static final Set<String[]> SHARED_WORDS = new HashSet<>();
    public static final Boolean SHOW_HIDDEN_ITEM_GROUPS = SlimefunOfficialSupporter.isShowHiddenItemGroups();
    public static final Integer DEFAULT_HASH_SIZE = 5000;
    public static final Map<SlimefunItem, Integer> ENABLED_ITEMS = new HashMap<>(DEFAULT_HASH_SIZE);
    public static final Set<SlimefunItem> AVAILABLE_ITEMS = new HashSet<>(DEFAULT_HASH_SIZE);
    public static final Integer BACK_SLOT = 1;
    public static final Integer SEARCH_SLOT = 7;
    public static final Integer PREVIOUS_SLOT = 46;
    public static final Integer NEXT_SLOT = 52;
    public static final Integer[] BORDER = new Integer[]{0, 2, 3, 4, 5, 6, 8, 45, 47, 48, 49, 50, 51, 53};
    public static final Integer[] MAIN_CONTENT = new Integer[]{
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44
    };
    public static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();
    public static @NotNull Boolean LOADED = false;
    public final SlimefunGuideImplementation implementation;
    public final Player player;
    public final String searchTerm;
    public final Boolean pinyin;
    public final @NotNull Integer page;
    public final List<SlimefunItem> slimefunItemList;
    public final boolean re_search_when_cache_failed;
    public Map<Integer, SearchGroup> pageMap = new LinkedHashMap<>();

    /**
     * Constructor for the SearchGroup.
     *
     * @param implementation The Slimefun guide implementation.
     * @param player         The player who opened the guide.
     * @param searchTerm     The search term.
     * @param pinyin         Whether the search term is in Pinyin.
     */
    public SearchGroup(
            SlimefunGuideImplementation implementation,
            @NotNull Player player,
            @NotNull String searchTerm,
            boolean pinyin) {
        this(implementation, player, searchTerm, pinyin, true);
    }

    /**
     * Constructor for the SearchGroup.
     *
     * @param implementation              The Slimefun guide implementation.
     * @param player                      The player who opened the guide.
     * @param searchTerm                  The search term.
     * @param pinyin                      Whether the search term is in Pinyin.
     * @param re_search_when_cache_failed Whether to re-search when cache failed.
     */
    public SearchGroup(
            SlimefunGuideImplementation implementation,
            @NotNull Player player,
            @NotNull String searchTerm,
            boolean pinyin,
            boolean re_search_when_cache_failed) {
        super(new NamespacedKey(JAVA_PLUGIN, "jeg_search_group_" + UUID.randomUUID()), new ItemStack(Material.BARRIER));
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

    /**
     * Constructor for the SearchGroup.
     *
     * @param searchGroup The SearchGroup to copy.
     * @param page        The page to display.
     */
    protected SearchGroup(@NotNull SearchGroup searchGroup, int page) {
        super(searchGroup.key, new ItemStack(Material.BARRIER));
        this.page = page;
        this.searchTerm = searchGroup.searchTerm;
        this.pinyin = searchGroup.pinyin;
        this.player = searchGroup.player;
        this.re_search_when_cache_failed = searchGroup.re_search_when_cache_failed;
        this.implementation = searchGroup.implementation;
        this.slimefunItemList = searchGroup.slimefunItemList;
        this.pageMap.put(page, this);
    }

    /**
     * Checks if the search filter is applicable.
     *
     * @param player       The player.
     * @param slimefunItem The Slimefun item.
     * @param searchTerm   The search term.
     * @param pinyin       Whether the search term is in Pinyin.
     * @return True if the search filter is applicable.
     */
    @ParametersAreNonnullByDefault
    public static boolean isSearchFilterApplicable(Player player, SlimefunItem slimefunItem, String searchTerm, boolean pinyin) {
        if (slimefunItem == null) {
            return false;
        }
        String itemName = ChatColor.stripColor(SlimefunOfficialSupporter.getTranslatedItemName(player, slimefunItem)).toLowerCase(Locale.ROOT);
        return isSearchFilterApplicable(itemName, searchTerm.toLowerCase(), pinyin);
    }

    /**
     * Checks if the search filter is applicable.
     *
     * @param player     The player.
     * @param itemStack  The item stack.
     * @param searchTerm The search term.
     * @param pinyin     Whether the search term is in Pinyin.
     * @return True if the search filter is applicable.
     */
    @ParametersAreNonnullByDefault
    public static boolean isSearchFilterApplicable(Player player, ItemStack itemStack, String searchTerm, boolean pinyin) {
        if (itemStack == null) {
            return false;
        }
        String itemName =
                ChatColor.stripColor(ItemUtils.getItemName(SlimefunOfficialSupporter.translateItem(player, itemStack))).toLowerCase(Locale.ROOT);
        return isSearchFilterApplicable(itemName, searchTerm.toLowerCase(), pinyin);
    }

    /**
     * Checks if the search filter is applicable.
     *
     * @param itemName   The item name.
     * @param searchTerm The search term.
     * @param pinyin     Whether the search term is in Pinyin.
     * @return True if the search filter is applicable.
     */
    @ParametersAreNonnullByDefault
    public static boolean isSearchFilterApplicable(String itemName, String searchTerm, boolean pinyin) {
        if (itemName.isEmpty()) {
            return false;
        }

        // Quick escape for common cases
        boolean result = itemName.contains(searchTerm);
        if (result) {
            return true;
        }

        /* Not using Pinyin in EN version
        if (pinyin) {
            final String pinyinFirstLetter = PinyinHelper.toPinyin(itemName, PinyinStyleEnum.FIRST_LETTER, "");
            return pinyinFirstLetter.contains(searchTerm);
        }
         */

        return false;
    }

    public static @NotNull List<SlimefunItem> filterItems(Player player, @NotNull FilterType filterType, @NotNull String filterValue, boolean pinyin, @NotNull List<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream().filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin)).toList();
    }

    public static @NotNull Set<SlimefunItem> filterItems(Player player, @NotNull FilterType filterType, @NotNull String filterValue, boolean pinyin, @NotNull Set<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream().filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin)).collect(Collectors.toSet());
    }

    public static void init() {
        if (!LOADED) {
            LOADED = true;
            Debug.debug("Initializing Search Group...");
            Timer.start();
            Bukkit.getScheduler().runTaskAsynchronously(JAVA_PLUGIN, () -> {
                synchronized (EN_CACHE_ROLLBACK) {
                    EN_CACHE_ROLLBACK.clear();
                }
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
                                                    continue;
                                                }
                                                // InfinityExpansion StrainerBase
                                                if (OOUTPUTS instanceof ItemStack[] outputs) {
                                                    if (!isInstance(item, "StrainerBase")) {
                                                        continue;
                                                    }
                                                    for (ItemStack output : outputs) {
                                                        cache.add(ItemUtils.getItemName(output));
                                                    }
                                                }
                                            }
                                            // InfinityExpansion Quarry
                                            else if (Ooutputs instanceof Material[] outputs) {
                                                if (!isInstance(item, "Quarry")) {
                                                    continue;
                                                }
                                                for (Material material : outputs) {
                                                    cache.add(ItemUtils.getItemName(new ItemStack(material)));
                                                }
                                            }
                                        }
                                        // InfinityExpansion SingularityConstructor
                                        else if (ORECIPE_LIST instanceof List<?> recipes) {
                                            if (!isInstance(item, "SingularityConstructor")) {
                                                continue;
                                            }
                                            for (Object recipe : recipes) {
                                                ItemStack input = (ItemStack) ReflectionUtil.getValue(recipe, "input");
                                                if (input != null) {
                                                    cache.add(ItemUtils.getItemName(input));
                                                }
                                                SlimefunItemStack output = (SlimefunItemStack) ReflectionUtil.getValue(recipe, "output");
                                                if (output != null) {
                                                    SlimefunItem slimefunItem = output.getItem();
                                                    if (slimefunItem != null) {
                                                        cache.add(slimefunItem.getItemName());
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        // InfinityExpansion MaterialGenerator
                                        if (!isInstance(item, "MaterialGenerator")) {
                                            continue;
                                        }
                                        cache.add(ItemUtils.getItemName(new ItemStack((Material) Omaterial)));
                                    }
                                }
                                // InfinityExpansion ResourceSynthesizer
                                if (Orecipes instanceof SlimefunItemStack[] recipes) {
                                    if (!isInstance(item, "ResourceSynthesizer")) {
                                        continue;
                                    }
                                    for (SlimefunItemStack slimefunItemStack : recipes) {
                                        SlimefunItem slimefunItem = slimefunItemStack.getItem();
                                        if (slimefunItem != null) {
                                            cache.add(slimefunItem.getItemName());
                                        }
                                    }
                                }
                                // InfinityExpansion GrowingMachine
                                else if (Orecipes instanceof EnumMap<?, ?> recipes) {
                                    if (!isInstance(item, "GrowingMachine")) {
                                        continue;
                                    }
                                    recipes.values().forEach(obj -> {
                                        ItemStack[] items = (ItemStack[]) obj;
                                        for (ItemStack itemStack : items) {
                                            cache.add(ItemUtils.getItemName(itemStack));
                                        }
                                    });
                                }
                                // InfinityExpansion MachineBlock
                                else if (Orecipes instanceof List<?> recipes) {
                                    if (!isInstance(item, "MachineBlock")) {
                                        continue;
                                    }
                                    for (Object recipe : recipes) {
                                        String[] strings = (String[]) ReflectionUtil.getValue(recipe, "strings");
                                        if (strings == null) {
                                            continue;
                                        }
                                        for (String string : strings) {
                                            SlimefunItem slimefunItem = SlimefunItem.getById(string);
                                            if (slimefunItem != null) {
                                                cache.add(slimefunItem.getItemName());
                                            } else {
                                                Material material = Material.getMaterial(string);
                                                if (material != null) {
                                                    cache.add(ItemUtils.getItemName(new ItemStack(material)));
                                                }
                                            }
                                        }

                                        ItemStack output = (ItemStack) ReflectionUtil.getValue(recipe, "output");
                                        if (output != null) {
                                            cache.add(ItemUtils.getItemName(output));
                                        }
                                    }
                                }

                                if (!cache.isEmpty()) {
                                    SPECIAL_CACHE.put(id, new SoftReference<>(cache));
                                }
                            }
                        } catch (Throwable ignored) {
                        }
                    } catch (Throwable ignored) {
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
                    cache.add(ItemUtils.getItemName(new ItemStack(material)));
                }
                SPECIAL_CACHE.put("STONEWORKS_FACTORY", new SoftReference<>(cache));

                // InfinityExpansion VoidHarvester
                SlimefunItem item2 = SlimefunItem.getById("VOID_BIT");
                if (item2 != null) {
                    Set<String> cache2 = new HashSet<>();
                    cache2.add(item2.getItemName());
                    SPECIAL_CACHE.put("VOID_HARVESTER", new SoftReference<>(cache2));
                }

                // InfinityExpansion MobDataCard
                label2:
                {
                    try {
                        Class<?> MobDataCardClass = Class.forName("io.github.mooy1.infinityexpansion.items.mobdata.MobDataCard");
                        @SuppressWarnings("unchecked") Map<String, Object> cards = (Map<String, Object>) ReflectionUtil.getStaticValue(MobDataCardClass, "CARDS");
                        if (cards == null) {
                            break label2;
                        }
                        cards.values().forEach(card -> {
                            @SuppressWarnings("unchecked") RandomizedSet<ItemStack> drops = (RandomizedSet<ItemStack>) ReflectionUtil.getValue(card, "drops");
                            if (drops == null) {
                                return;
                            }
                            Set<String> cache2 = new HashSet<>();
                            for (ItemStack itemStack : drops.toMap().keySet()) {
                                cache2.add(ItemUtils.getItemName(itemStack));
                            }
                            SPECIAL_CACHE.put(((SlimefunItem) card).getId(), new SoftReference<>(cache2));
                        });
                    } catch (Throwable ignored) {
                    }
                }

                for (SlimefunItem slimefunItem : AVAILABLE_ITEMS) {
                    try {
                        if (slimefunItem == null) {
                            continue;
                        }
                        String name = ChatColor.stripColor(slimefunItem.getItemName());
                        for (String s : name.split(SPLIT)) {
                            String d = s.toLowerCase(Locale.ROOT);
                            if (!EN_WORDS.contains(d)) {
                                EN_WORDS.add(d);
                            }
                            EN_CACHE.putIfAbsent(s, new SoftReference<>(new HashSet<>()));
                            Reference<Set<SlimefunItem>> ref = EN_CACHE.get(d);
                            if (ref != null) {
                                Set<SlimefunItem> set = ref.get();
                                if (set != null) {
                                    set.add(slimefunItem);
                                }
                            }
                        }

                        /* Not using Pinyin in EN version
                        if (JustEnoughGuide.getConfigManager().isPinyinSearch()) {
                            final String pinyinFirstLetter = PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "");
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
                                    set.add(slimefunItem);
                                }
                            }
                        }

                         */

                        List<ItemStack> displayRecipes = null;
                        if (slimefunItem instanceof AContainer ac) {
                            displayRecipes = ac.getDisplayRecipes();
                        } else if (slimefunItem instanceof MultiBlockMachine mb) {
                            displayRecipes = mb.getDisplayRecipes();
                        }
                        if (displayRecipes != null) {
                            for (ItemStack itemStack : displayRecipes) {
                                if (itemStack != null) {
                                    String name2 = ChatColor.stripColor(ItemUtils.getItemName(itemStack));
                                    for (String s : name2.split(SPLIT)) {
                                        String d = s.toLowerCase(Locale.ROOT);
                                        EN_CACHE2.putIfAbsent(d, new SoftReference<>(new HashSet<>()));
                                        Reference<Set<SlimefunItem>> ref = EN_CACHE2.get(d);
                                        if (ref != null) {
                                            Set<SlimefunItem> set = ref.get();
                                            if (set == null) {
                                                set = new HashSet<>();
                                                EN_CACHE2.put(d, new SoftReference<>(set));
                                            }
                                            set.add(slimefunItem);
                                        }
                                        if (!EN_WORDS.contains(s)) {
                                            EN_WORDS.add(s);
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
                                        String d = s.toLowerCase(Locale.ROOT);

                                        EN_CACHE2.putIfAbsent(d, new SoftReference<>(new HashSet<>()));
                                        Reference<Set<SlimefunItem>> ref = EN_CACHE2.get(d);
                                        if (ref != null) {
                                            Set<SlimefunItem> set = ref.get();
                                            if (set != null) {
                                                set.add(slimefunItem);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                }

                // FluffyMachines SmartFactory
                Set<SlimefunItemStack> ACCEPTED_ITEMS = new HashSet<>(Arrays.asList(
                        SlimefunItems.BILLON_INGOT, SlimefunItems.SOLDER_INGOT, SlimefunItems.NICKEL_INGOT,
                        SlimefunItems.COBALT_INGOT, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.BRONZE_INGOT,
                        SlimefunItems.BRASS_INGOT, SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.STEEL_INGOT,
                        SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.ALUMINUM_BRONZE_INGOT,
                        SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.GILDED_IRON, SlimefunItems.REDSTONE_ALLOY,
                        SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.FERROSILICON,
                        SlimefunItems.ELECTRO_MAGNET, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.HEATING_COIL,
                        SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.GOLD_4K, SlimefunItems.GOLD_6K, SlimefunItems.GOLD_8K,
                        SlimefunItems.GOLD_10K, SlimefunItems.GOLD_12K, SlimefunItems.GOLD_14K, SlimefunItems.GOLD_16K,
                        SlimefunItems.GOLD_18K, SlimefunItems.GOLD_20K, SlimefunItems.GOLD_22K, SlimefunItems.GOLD_24K
                ));
                Set<String> items = new HashSet<>();
                for (SlimefunItemStack slimefunItemStack : ACCEPTED_ITEMS) {
                    SlimefunItem slimefunItem = slimefunItemStack.getItem();
                    if (slimefunItem != null) {
                        items.add(slimefunItem.getItemName());
                    }
                }
                SPECIAL_CACHE.put("SMART_FACTORY", new SoftReference<>(items));

                // shared cache
                /*
                SHARED_CHARS.add("粘黏");
                SHARED_CHARS.add("荧萤");
                SHARED_CHARS.add("机器级");
                SHARED_CHARS.add("灵零");
                SHARED_CHARS.add("动力");

                 */
                /*
                for (String s : SHARED_CHARS) {
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
                                    Debug.debug("Shared cache added to CACHE char \"" + c + "\" (" + sharedItems.size() + " items)");
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
                                    Debug.debug("Shared cache added to CACHE2 char \"" + c + "\" (" + sharedItems2.size() + " items)");
                                }
                            }
                        }
                    }
                }
                 */
                SHARED_WORDS.add(new String[]{"storage", "barrel"});

                Debug.debug("Cache initialized.");

                Timer.log();
                Debug.debug("Search Group initialized.");
                Debug.debug("Enabled items: " + ENABLED_ITEMS.size());
                Debug.debug("Available items: " + AVAILABLE_ITEMS.size());
                Debug.debug("Machine blocks cache: " + SPECIAL_CACHE.size());
                Debug.debug("Shared cache: " + SHARED_WORDS.size());
                Debug.debug("EN Words: " + EN_WORDS.size());
                Debug.debug("EN Cache 1 (Keywords): " + EN_CACHE.size());
                Debug.debug("EN Cache 2 (Display Recipes): " + EN_CACHE2.size());
            });
        }
    }

    public static boolean isInstance(@NotNull SlimefunItem item, String classSimpleName) {
        Class<?> clazz = item.getClass();
        while (clazz != SlimefunItem.class) {
            if (clazz.getSimpleName().equals(classSimpleName)) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    /**
     * Always returns false.
     *
     * @param player            The player to print the error message to.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     * @return false.
     */
    @Override
    public boolean isVisible(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    /**
     * Opens the search group.
     *
     * @param player            The player who opened the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     */
    @Override
    public void open(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        playerProfile.getGuideHistory().add(this, this.page);
        this.generateMenu(player, playerProfile, slimefunGuideMode).open(player);
    }

    /**
     * Refreshes the search group.
     *
     * @param player            The player who opened the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     */
    public void refresh(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        this.open(player, playerProfile, slimefunGuideMode);
    }

    /**
     * Generates the menu for the search group.
     *
     * @param player            The player who opened the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     * @return The generated menu.
     */
    @NotNull
    private ChestMenu generateMenu(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu =
                new ChestMenu(Lang.getGuideMessage("searching", "item_name", ChatUtils.crop(ChatColor.WHITE, searchTerm)));

        chestMenu.setEmptySlotsClickable(false);
        chestMenu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1));

        chestMenu.addItem(BACK_SLOT, SlimefunOfficialSupporter.getBackButton(player));
        chestMenu.addMenuClickHandler(BACK_SLOT, (pl, s, is, action) -> {
            GuideHistory guideHistory = playerProfile.getGuideHistory();
            if (action.isShiftClicked()) {
                SlimefunGuide.openMainMenu(playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
            } else {
                guideHistory.goBack(Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode));
            }
            return false;
        });

        // Search feature!
        chestMenu.addItem(SEARCH_SLOT, ItemStackUtil.getCleanItem(ChestMenuUtils.getSearchButton(player)));
        chestMenu.addMenuClickHandler(SEARCH_SLOT, (pl, slot, item, action) -> {
            pl.closeInventory();

            Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
            ChatInput.waitForPlayer(
                    JAVA_PLUGIN,
                    pl,
                    msg -> implementation.openSearch(
                            playerProfile, msg, implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE));

            return false;
        });

        chestMenu.addItem(
                PREVIOUS_SLOT,
                ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(
                        player, this.page, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1)));
        chestMenu.addMenuClickHandler(PREVIOUS_SLOT, (p, slot, item, action) -> {
            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
            SearchGroup searchGroup = this.getByPage(Math.max(this.page - 1, 1));
            searchGroup.open(player, playerProfile, slimefunGuideMode);
            return false;
        });

        chestMenu.addItem(
                NEXT_SLOT,
                ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(
                        player, this.page, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1)));
        chestMenu.addMenuClickHandler(NEXT_SLOT, (p, slot, item, action) -> {
            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
            SearchGroup searchGroup = this.getByPage(
                    Math.min(this.page + 1, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1));
            searchGroup.open(player, playerProfile, slimefunGuideMode);
            return false;
        });

        for (int slot : BORDER) {
            chestMenu.addItem(slot, ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground()));
            chestMenu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i = 0; i < MAIN_CONTENT.length; i++) {
            int index = i + this.page * MAIN_CONTENT.length - MAIN_CONTENT.length;
            if (index < this.slimefunItemList.size()) {
                SlimefunItem slimefunItem = slimefunItemList.get(index);
                ItemStack itemstack = ItemStackUtil.getCleanItem(new CustomItemStack(SlimefunOfficialSupporter.translateItem(player, slimefunItem.getItem()), meta -> {
                    ItemGroup itemGroup = slimefunItem.getItemGroup();
                    List<String> additionLore = List.of(
                            "",
                            ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE
                                    + (LocalHelper.getAddonName(itemGroup, slimefunItem.getId())) + ChatColor.WHITE + " - "
                                    + LocalHelper.getDisplayName(itemGroup, player));
                    if (meta.hasLore() && meta.getLore() != null) {
                        List<String> lore = meta.getLore();
                        lore.addAll(additionLore);
                        meta.setLore(lore);
                    } else {
                        meta.setLore(additionLore);
                    }

                    meta.addItemFlags(
                            ItemFlag.HIDE_ATTRIBUTES,
                            ItemFlag.HIDE_ENCHANTS,
                            JEGVersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                }));
                chestMenu.addItem(MAIN_CONTENT[i], ItemStackUtil.getCleanItem(SlimefunOfficialSupporter.translateItem(player, itemstack)), (pl, slot, itm, action) -> {
                    try {
                        if (implementation.getMode() != SlimefunGuideMode.SURVIVAL_MODE
                                && (pl.isOp() || pl.hasPermission("slimefun.cheat.items"))) {
                            pl.getInventory().addItem(slimefunItem.getItem().clone());
                        } else {
                            implementation.displayItem(playerProfile, slimefunItem, true);
                        }
                    } catch (Exception | LinkageError x) {
                        printErrorMessage(pl, slimefunItem, x);
                    }

                    return false;
                });
            }
        }

        return chestMenu;
    }

    /**
     * Gets the search group by page.
     *
     * @param page The page to get.
     * @return The search group by page.
     */
    @NotNull
    public SearchGroup getByPage(int page) {
        if (this.pageMap.containsKey(page)) {
            return this.pageMap.get(page);
        } else {
            synchronized (this.pageMap.get(1)) {
                if (this.pageMap.containsKey(page)) {
                    return this.pageMap.get(page);
                }

                SearchGroup searchGroup = new SearchGroup(this, page);
                searchGroup.pageMap = this.pageMap;
                this.pageMap.put(page, searchGroup);
                return searchGroup;
            }
        }
    }

    /**
     * Gets all matched items.
     *
     * @param p          The player.
     * @param searchTerm The search term.
     * @param pinyin     Whether the search term is in Pinyin.
     * @return The matched items.
     */
    @Deprecated
    public @NotNull List<SlimefunItem> getAllMatchedItems(@NotNull Player p, @NotNull String searchTerm, boolean pinyin) {
        return filterItems(p, searchTerm, pinyin);
    }

    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, Throwable x) {
        p.sendMessage(Lang.getError("internal-error"));
        JustEnoughGuide.getInstance().getLogger().log(Level.SEVERE, Lang.getError("error-occurred"), x);
        JustEnoughGuide.getInstance().getLogger().warning(Lang.getError("trying-fix-guide", "player_name", p.getName()));
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }

    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, SlimefunItem item, Throwable x) {
        p.sendMessage(Lang.getError("internal-error"));
        item.error(Lang.getError("item-error"), x);
        JustEnoughGuide.getInstance()
                .getLogger()
                .warning(Lang.getError("trying-fix-guide", "player_name", p.getName()));
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }

    public @NotNull List<SlimefunItem> filterItems(@NotNull Player player, @NotNull String searchTerm, boolean pinyin) {
        StringBuilder actualSearchTermBuilder = new StringBuilder();
        String[] split = searchTerm.split(SPLIT);
        Map<FilterType, String> filters = new HashMap<>();
        for (String s : split) {
            boolean isFilter = false;
            for (FilterType filterType : FilterType.values()) {
                if (s.startsWith(filterType.getFlag()) && s.length() > filterType.getFlag().length()) {
                    isFilter = true;
                    String filterValue = s.substring(filterType.getFlag().length()).replace(".", " ");
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
            String flag = filterType.getFlag();
            // Quote the flag to be used as a literal replacement
            actualSearchTerm = actualSearchTerm.replaceAll(Pattern.quote(flag), Matcher.quoteReplacement(flag));
        }
        Set<SlimefunItem> merge = new HashSet<>(36 * 4);
        // The unfiltered items
        Set<SlimefunItem> items = new HashSet<>(AVAILABLE_ITEMS
                .stream()
                .filter(item -> item.getItemGroup().isAccessible(player)).toList());

        if (!actualSearchTerm.isBlank()) {
            int beforeSize = merge.size();
            Debug.debug("Search term: " + actualSearchTerm);
            String[] words = actualSearchTerm.split(SPLIT);
            boolean first = true;
            for (String word : words) {
                Debug.debug("Word: " + word);
                List<String> fixedWords = List.of();
                if (words.length == 1) {
                    // maybe a language that not split by space, should change the fixedWords
                    String language = Slimefun.getLocalization().getLanguage(player).getId();
                    if (isContinuousScriptLanguage(language)) {
                        // Find continuous script language, should change the fixedWords
                        fixedWords = List.of(word);
                    } else {
                        fixedWords = findMostSimilar(word, EN_THRESHOLD);
                    }
                } else {
                    fixedWords = findMostSimilar(word, EN_THRESHOLD);
                }
                if (fixedWords.isEmpty()) {
                    Debug.debug("No fixed words found.");
                    // fallback
                    if (re_search_when_cache_failed) {
                        merge.addAll(filterItems(FilterType.BY_ITEM_NAME, word, false, new HashSet<>(items)));
                        merge.addAll(filterItems(FilterType.BY_DISPLAY_ITEM_NAME, word, false, new HashSet<>(items)));
                    }
                } else {
                    Debug.debug("Fixed words: " + fixedWords);
                    for (String candidate : fixedWords) {
                        merge.addAll(filterItems(FilterType.BY_ITEM_NAME, candidate, false, new HashSet<>(items)));
                        merge.addAll(filterItems(FilterType.BY_DISPLAY_ITEM_NAME, candidate, false, new HashSet<>(items)));
                    }
                }
            }

            int afterSize = merge.size();
            // fallback
            if (beforeSize == afterSize) {
                Debug.debug("Same size, fallback to search by name.");
                merge.addAll(filterItems(FilterType.BY_ITEM_NAME, actualSearchTerm, false, new HashSet<>(items)));
                merge.addAll(filterItems(FilterType.BY_DISPLAY_ITEM_NAME, actualSearchTerm, false, new HashSet<>(items)));
            }
            Debug.debug("Filtered items: " + merge.size());
        }

        // Filter items
        if (!filters.isEmpty()) {
            for (Map.Entry<FilterType, String> entry : filters.entrySet()) {
                items = filterItems(entry.getKey(), entry.getValue(), pinyin, items);
            }

            merge.addAll(items);
        }

        return merge.stream().sorted((a, b) -> ENABLED_ITEMS.get(a) < ENABLED_ITEMS.get(b) ? -1 : 1).toList();
    }

    public @NotNull List<SlimefunItem> filterItems(@NotNull FilterType filterType, @NotNull String filterValue, boolean pinyin, @NotNull List<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream().filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin)).toList();
    }

    public @NotNull Set<SlimefunItem> filterItems(@NotNull FilterType filterType, @NotNull String filterValue, boolean pinyin, @NotNull Set<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream().filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin)).collect(Collectors.toSet());
    }

    public static int levenshteinDistance(String s1, String s2) {
        if (s1.length() > s2.length()) {
            String temp = s1;
            s1 = s2;
            s2 = temp;
        }

        int[] distances = new int[s1.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            distances[i] = i;
        }

        for (int i = 1; i <= s2.length(); i++) {
            int[] prevDistances = distances.clone();
            distances[0] = i;
            for (int j = 1; j <= s1.length(); j++) {
                int cost = (s1.charAt(j - 1) == s2.charAt(i - 1)) ? 0 : 1;
                distances[j] = Math.min(Math.min(distances[j - 1] + 1, prevDistances[j] + 1), prevDistances[j - 1] + cost);
            }
        }

        return distances[s1.length()];
    }

    public static List<String> findMostSimilar(String target, int threshold) {
        if (EN_CACHE_ROLLBACK.containsKey(target)) {
            return EN_CACHE_ROLLBACK.get(target);
        }

        PriorityQueue<Map.Entry<String, Integer>> minHeap = new PriorityQueue<>(5, (a, b) -> b.getValue() - a.getValue());

        for (String s : EN_WORDS) {
            int distance = levenshteinDistance(s, target);
            if (distance == 0) {
                return List.of(s);
            }

            if (distance <= threshold) {
                Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<>(s, distance);
                if (minHeap.size() < MAX_FIX_TIMES) {
                    minHeap.offer(entry);
                } else if (distance < minHeap.peek().getValue()) {
                    minHeap.poll();
                    minHeap.offer(entry);
                }
            }
        }

        List<String> mostSimilar = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            mostSimilar.add(0, minHeap.poll().getKey());
        }

        synchronized (EN_CACHE_ROLLBACK) {
            EN_CACHE_ROLLBACK.put(target, mostSimilar);
        }
        return mostSimilar;
    }

    public static boolean isContinuousScriptLanguage(String language) {
        return language.startsWith("zh") ||
                language.startsWith("ja") ||
                language.startsWith("ko") ||
                language.startsWith("th") ||
                language.startsWith("vi") ||
                language.startsWith("he") ||
                language.startsWith("fa");
    }
}
