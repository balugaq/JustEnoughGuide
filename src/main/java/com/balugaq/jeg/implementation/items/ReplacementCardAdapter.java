/*
 * Copyright (c) 2024-2026 balugaq
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

package com.balugaq.jeg.implementation.items;

import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.core.managers.IntegrationManager;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.experimental.UtilityClass;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author balugaq
 * @since 2.1
 */
@UtilityClass
@NullMarked
public class ReplacementCardAdapter {
    private static final Map<String, List<ItemStack>> REPLACEMENT_CARDS = new HashMap<>();
    private static @Nullable Object replaceCard;
    private static @Nullable Method methodLogiTech_getReplaceCard_va;
    private static @Nullable Method methodLogiTech_getReplaceCard_sf;

    public static void load() {
        IntegrationManager.scheduleRunPostRegistryFinalized(ReplacementCardAdapter::loadInternal);
    }

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    private static void loadInternal() {
        addCard("WATER_BUCKET", "FINALTECH_WATER_CARD");
        addCard("LAVA_BUCKET", "FINALTECH_LAVA_CARD");
        addCard("MILK_BUCKET", "FINALTECH_MILK_CARD");
        addCard("WATER_BUCKET", "_FINALTECH_WATER_CARD");
        addCard("LAVA_BUCKET", "_FINALTECH_LAVA_CARD");
        addCard("MILK_BUCKET", "_FINALTECH_MILK_CARD");
        addCard("FLINT_AND_STEEL", "_FINALTECH_FLINT_AND_STEEL_CARD");

        try {
            replaceCard = ReflectionUtil.getStaticValue(Class.forName("me.matl114.logitech.SlimefunItem.AddSlimefunItems"), "REPLACE_CARD");
        } catch (ClassNotFoundException ignored) {
            try {
                replaceCard = ReflectionUtil.getStaticValue(Class.forName("me.matl114.logitech.core.AddSlimefunItems"), "REPLACE_CARD");
            } catch (ClassNotFoundException ignored2) {
            }
        }

        if (replaceCard != null) {
            methodLogiTech_getReplaceCard_va = ReflectionUtil.getMethod(replaceCard.getClass(), "getReplaceCard", Material.class);
            if (methodLogiTech_getReplaceCard_va != null) {
                for (Material material : Material.values()) {
                    if (!material.isItem() || material.getMaxStackSize() != 1) {
                        continue;
                    }

                    addLogiTechCard(material);
                }
            }

            methodLogiTech_getReplaceCard_sf = ReflectionUtil.getMethod(replaceCard.getClass(), "getReplaceCard", SlimefunItem.class);
            if (methodLogiTech_getReplaceCard_sf != null) {
                for (SlimefunItem sf : new ArrayList<>(Slimefun.getRegistry().getEnabledSlimefunItems())) {
                    ItemStack item = sf.getItem();
                    if (item == null || !item.getType().isItem() || item.getType().getMaxStackSize() != 1) {
                        continue;
                    }

                    addLogiTechCard(sf);
                }
            }
        }

        if (JustEnoughGuide.getConfigManager().isAdaptReplacementCards()) {
            for (SlimefunItem sf : new ArrayList<>(Slimefun.getRegistry().getEnabledSlimefunItems())) {
                for (ItemStack item : sf.getRecipe()) {
                    if (item != null && item.getType() != Material.AIR && item.getMaxStackSize() == 1 && getReplacementCards(item) != null) {
                        adaptItem(sf);
                        break;
                    }
                }
            }
        }
    }

    private static void adaptItem(SlimefunItem sf) {
        ItemStack[] originArray = sf.getRecipe();
        List<ItemStack> origin = Arrays.asList(originArray);

        List<Map<ItemStack, ItemStack>> replacementMaps = new ArrayList<>();
        replacementMaps.add(new HashMap<>());

        for (ItemStack targetItem : origin) {
            List<ItemStack> replacements = getReplacementCards(targetItem);
            if (replacements == null) continue;
            List<Map<ItemStack, ItemStack>> newMaps = new ArrayList<>();

            for (Map<ItemStack, ItemStack> map : replacementMaps) {
                for (ItemStack replacement : replacements) {
                    Map<ItemStack, ItemStack> newMap = new HashMap<>(map);
                    newMap.put(targetItem, replacement);
                    newMaps.add(newMap);
                }
            }
            replacementMaps.addAll(newMaps);
            if (replacementMaps.size() > 128) {
                break;
            }
        }

        for (Map<ItemStack, ItemStack> replaceMap : replacementMaps) {
            if (replaceMap.isEmpty()) continue;
            List<ItemStack> newRecipe = origin.stream()
                    .map(item -> replaceMap.getOrDefault(item, item))
                    .collect(Collectors.toList());
            adaptItem(sf, newRecipe);
        }
        Debug.debug("Added " + (replacementMaps.size() - 1) + " adaptional recipes for " + ItemStackHelper.getDisplayName(sf.getItem()));
    }

    private static void adaptItem(SlimefunItem sf, List<ItemStack> resultList) {
        for (int retry = 0; retry < 128; retry++) {
            String newId = "JEG_" + sf.getId() + "_" + retry;
            if (SlimefunItem.getById(newId) != null) continue;

            var newSf = new SlimefunItem(GroupSetup.jegItemsGroup, new SlimefunItemStack(newId, sf.getItem()), sf.getRecipeType(), resultList.toArray(new ItemStack[0]), sf.getRecipeOutput());
            boolean before = JustEnoughGuide.disableAutomaticallyLoadItems();
            newSf.register(JustEnoughGuide.getInstance());
            try {
                newSf.load();
            } catch (IllegalStateException ex) {
                if (ex.getMessage().equals("Asynchronous Recipe Add!")) {
                    JustEnoughGuide.runNextTick(newSf::load);
                }
            } catch (Exception ignored) {
            }
            newSf.setHidden(true);
            JustEnoughGuide.setAutomaticallyLoadItems(before);
            return;
        }
    }
    
    @Nullable
    public static ItemStack getLogiTechReplacementCard(Material material) {
        if (methodLogiTech_getReplaceCard_va == null) return null;
        return (ItemStack) ReflectionUtil.invokeMethod(methodLogiTech_getReplaceCard_va, replaceCard, material);
    }

    @Nullable
    public static ItemStack getLogiTechReplacementCard(SlimefunItem sf) {
        if (methodLogiTech_getReplaceCard_sf == null) return null;
        return (ItemStack) ReflectionUtil.invokeMethod(methodLogiTech_getReplaceCard_sf, replaceCard, sf);
    }

    private static void addLogiTechCard(Material material) {
        ItemStack item = getLogiTechReplacementCard(material);
        if (item != null) {
            addCard(material.name(), item);
        }
    }

    private static void addLogiTechCard(SlimefunItem sf) {
        ItemStack item = ReplacementCardAdapter.getLogiTechReplacementCard(sf);
        if (item != null) {
            addCard(sf.getId(), item);
        }
    }

    public static void addCard(String itemId, String cardId) {
        SlimefunItem sf = SlimefunItem.getById(cardId);
        if (sf != null) {
            addCard(itemId, sf.getItem());
        }
    }

    public static void addCard(String itemId, ItemStack item) {
        REPLACEMENT_CARDS.computeIfAbsent(itemId, k -> new ArrayList<>());
        REPLACEMENT_CARDS.get(itemId).add(item);
    }

    @Nullable
    public static List<ItemStack> getReplacementCards(Material material) {
        return getReplacementCards(material.name());
    }

    @Nullable
    public static List<ItemStack> getReplacementCards(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        SlimefunItem sf = SlimefunItem.getByItem(itemStack);
        if (sf != null) {
            return getReplacementCards(sf.getId());
        }

        return getReplacementCards(itemStack.getType().name());
    }

    @Nullable
    public static List<ItemStack> getReplacementCards(SlimefunItem sf) {
        return getReplacementCards(sf.getId());
    }

    @Nullable
    public static List<ItemStack> getReplacementCards(String itemId) {
        return REPLACEMENT_CARDS.get(itemId);
    }

    public static Map<String, List<ItemStack>> getReplacementCards() {
        return REPLACEMENT_CARDS;
    }
}
