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

package com.balugaq.jeg.api.cost.please_set_cer_patch_to_false_in_config_when_you_see_this;

import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.core.managers.IntegrationManager;
import com.balugaq.jeg.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"unused", "deprecation", "DataFlowIssue", "ConstantValue"})
@NullMarked
public class ValueTable {
    private static final int THRESHOLD = 50;
    private static final HashMap<Integer, Double> valueMap = new HashMap<>();

    public static void load() {
        IntegrationManager.scheduleRunAsync(ValueTable::loadInternal);
    }

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    @ApiStatus.Internal
    private static void loadInternal() {
        addBaseValues();
        for (SlimefunItem sf : new ArrayList<>(Slimefun.getRegistry().getEnabledSlimefunItems())) {
            getItemValueSlimefun(sf, 0);
        }

        for (Material material : Material.values()) {
            if (material.isItem() && !material.isLegacy() && !material.isAir()) {
                getItemValueVanilla(new ItemStack(material), 0);
            }
        }
    }

    public static double getValue(SlimefunItem sf) {
        return getValue(sf.getItem());
    }

    public static double getValue(@Nullable ItemStack itemStack, int amount) {
        if (itemStack == null) {
            return 0.0D;
        }

        int hash = itemStack.hashCode();
        if (valueMap.containsKey(hash)) {
            return valueMap.get(hash) * amount;
        }

        SlimefunItem sf = SlimefunItem.getByItem(itemStack);
        double v;
        if (sf != null) {
            v = getItemValueSlimefun(sf, 0);
        } else {
            v = getItemValueVanilla(itemStack, 0);
        }
        return v;
    }

    private static void setValue(ItemStack itemStack, double value) {
        if (StackUtils.itemsMatch(itemStack, new ItemStack(itemStack.getType()), true, false)) {
            valueMap.put(itemStack.getType().hashCode(), value);
        } else {
            valueMap.put(itemStack.hashCode(), value);
        }
    }

    private static void setValue(int key, double value) {
        valueMap.put(key, value);
    }

    public static double getValue(ItemStack itemStack) {
        return getValue(StackUtils.getAsQuantity(itemStack, 1), itemStack.getAmount());
    }

    public static double getValue(@Nullable ItemStack[] itemStacks) {
        double v = 0.0D;
        for (ItemStack itemStack : itemStacks) {
            v += getValue(itemStack);
        }

        return v;
    }

    public static double getValue(Material material) {
        return getValue(new ItemStack(material));
    }

    public static double getTemplateValue(ItemStack itemStack) {
        return getValue(itemStack) / 1e4;
    }

    private static void addBaseValues() {
        setValue(new ItemStack(Material.GRASS_BLOCK), 1);
        setValue(new ItemStack(Material.MOSS_BLOCK), 1);
        setValue(new ItemStack(Material.DIRT), 1);
        setValue(new ItemStack(Material.PODZOL), 1);
        setValue(new ItemStack(Material.CRIMSON_NYLIUM), 1);
        setValue(new ItemStack(Material.WARPED_NYLIUM), 1);
        setValue(new ItemStack(Material.COBBLESTONE), 1);
        setValue(new ItemStack(Material.DIORITE), 1);
        setValue(new ItemStack(Material.ANDESITE), 1);
        setValue(new ItemStack(Material.GRANITE), 1);
        setValue(new ItemStack(Material.OAK_SAPLING), 2);
        setValue(new ItemStack(Material.SPRUCE_SAPLING), 2);
        setValue(new ItemStack(Material.BIRCH_SAPLING), 2);
        setValue(new ItemStack(Material.JUNGLE_SAPLING), 2);
        setValue(new ItemStack(Material.ACACIA_SAPLING), 2);
        setValue(new ItemStack(Material.DARK_OAK_SAPLING), 2);
        setValue(new ItemStack(Material.MANGROVE_PROPAGULE), 2);
        setValue(new ItemStack(Material.SAND), 1);
        setValue(new ItemStack(Material.RED_SAND), 1);
        setValue(new ItemStack(Material.GRAVEL), 1);
        setValue(new ItemStack(Material.GOLD_ORE), 0);
        setValue(new ItemStack(Material.IRON_ORE), 0);
        setValue(new ItemStack(Material.COAL_ORE), 0);
        setValue(new ItemStack(Material.DEEPSLATE_GOLD_ORE), 0);
        setValue(new ItemStack(Material.DEEPSLATE_IRON_ORE), 0);
        setValue(new ItemStack(Material.DEEPSLATE_COAL_ORE), 0);
        setValue(new ItemStack(Material.NETHER_GOLD_ORE), 0);
        setValue(new ItemStack(Material.OAK_LOG), 4);
        setValue(new ItemStack(Material.SPRUCE_LOG), 4);
        setValue(new ItemStack(Material.BIRCH_LOG), 4);
        setValue(new ItemStack(Material.JUNGLE_LOG), 4);
        setValue(new ItemStack(Material.ACACIA_LOG), 4);
        setValue(new ItemStack(Material.DARK_OAK_LOG), 4);
        setValue(new ItemStack(Material.CRIMSON_STEM), 4);
        setValue(new ItemStack(Material.WARPED_STEM), 4);
        setValue(new ItemStack(Material.MANGROVE_LOG), 4);
        setValue(new ItemStack(Material.STRIPPED_OAK_LOG), 4);
        setValue(new ItemStack(Material.STRIPPED_SPRUCE_LOG), 4);
        setValue(new ItemStack(Material.STRIPPED_BIRCH_LOG), 4);
        setValue(new ItemStack(Material.STRIPPED_JUNGLE_LOG), 4);
        setValue(new ItemStack(Material.STRIPPED_ACACIA_LOG), 4);
        setValue(new ItemStack(Material.STRIPPED_DARK_OAK_LOG), 4);
        setValue(new ItemStack(Material.STRIPPED_CRIMSON_STEM), 4);
        setValue(new ItemStack(Material.STRIPPED_WARPED_STEM), 4);
        setValue(new ItemStack(Material.STRIPPED_MANGROVE_LOG), 4);
        setValue(new ItemStack(Material.STRIPPED_OAK_WOOD), 4);
        setValue(new ItemStack(Material.STRIPPED_SPRUCE_WOOD), 4);
        setValue(new ItemStack(Material.STRIPPED_BIRCH_WOOD), 4);
        setValue(new ItemStack(Material.STRIPPED_JUNGLE_WOOD), 4);
        setValue(new ItemStack(Material.STRIPPED_ACACIA_WOOD), 4);
        setValue(new ItemStack(Material.STRIPPED_DARK_OAK_WOOD), 4);
        setValue(new ItemStack(Material.STRIPPED_CRIMSON_HYPHAE), 4);
        setValue(new ItemStack(Material.STRIPPED_WARPED_HYPHAE), 4);
        setValue(new ItemStack(Material.STRIPPED_MANGROVE_WOOD), 4);
        setValue(new ItemStack(Material.OAK_WOOD), 4);
        setValue(new ItemStack(Material.SPRUCE_WOOD), 4);
        setValue(new ItemStack(Material.BIRCH_WOOD), 4);
        setValue(new ItemStack(Material.JUNGLE_WOOD), 4);
        setValue(new ItemStack(Material.ACACIA_WOOD), 4);
        setValue(new ItemStack(Material.DARK_OAK_WOOD), 4);
        setValue(new ItemStack(Material.CRIMSON_HYPHAE), 4);
        setValue(new ItemStack(Material.WARPED_HYPHAE), 4);
        setValue(new ItemStack(Material.MANGROVE_WOOD), 4);
        setValue(new ItemStack(Material.OAK_LEAVES), 1);
        setValue(new ItemStack(Material.SPRUCE_LEAVES), 1);
        setValue(new ItemStack(Material.BIRCH_LEAVES), 1);
        setValue(new ItemStack(Material.JUNGLE_LEAVES), 1);
        setValue(new ItemStack(Material.ACACIA_LEAVES), 1);
        setValue(new ItemStack(Material.DARK_OAK_LEAVES), 1);
        setValue(new ItemStack(Material.AZALEA_LEAVES), 1);
        setValue(new ItemStack(Material.FLOWERING_AZALEA_LEAVES), 1);
        setValue(new ItemStack(Material.MANGROVE_LEAVES), 1);
        setValue(new ItemStack(Material.AZALEA), 8);
        setValue(new ItemStack(Material.WET_SPONGE), 16);
        setValue(new ItemStack(Material.LAPIS_ORE), 0);
        setValue(new ItemStack(Material.DEEPSLATE_LAPIS_ORE), 0);
        setValue(new ItemStack(Material.COBWEB), 4);
        setValue(new ItemStack(Material.FERN), 1);
        setValue(new ItemStack(Material.DEAD_BUSH), 1);
        setValue(new ItemStack(Material.SEAGRASS), 1);
        setValue(new ItemStack(Material.SEA_PICKLE), 4);
        setValue(new ItemStack(Material.DANDELION), 2);
        setValue(new ItemStack(Material.POPPY), 2);
        setValue(new ItemStack(Material.BLUE_ORCHID), 2);
        setValue(new ItemStack(Material.ALLIUM), 2);
        setValue(new ItemStack(Material.AZURE_BLUET), 2);
        setValue(new ItemStack(Material.RED_TULIP), 2);
        setValue(new ItemStack(Material.ORANGE_TULIP), 2);
        setValue(new ItemStack(Material.WHITE_TULIP), 2);
        setValue(new ItemStack(Material.PINK_TULIP), 2);
        setValue(new ItemStack(Material.OXEYE_DAISY), 2);
        setValue(new ItemStack(Material.CORNFLOWER), 2);
        setValue(new ItemStack(Material.LILY_OF_THE_VALLEY), 2);
        setValue(new ItemStack(Material.WITHER_ROSE), 32);
        setValue(new ItemStack(Material.BROWN_MUSHROOM), 4);
        setValue(new ItemStack(Material.RED_MUSHROOM), 4);
        setValue(new ItemStack(Material.CRIMSON_FUNGUS), 4);
        setValue(new ItemStack(Material.WARPED_FUNGUS), 4);
        setValue(new ItemStack(Material.CRIMSON_ROOTS), 2);
        setValue(new ItemStack(Material.WARPED_ROOTS), 2);
        setValue(new ItemStack(Material.WEEPING_VINES), 2);
        setValue(new ItemStack(Material.TWISTING_VINES), 2);
        setValue(new ItemStack(Material.MANGROVE_ROOTS), 4);
        setValue(new ItemStack(Material.MUD), 8);
        setValue(new ItemStack(Material.SCULK_SHRIEKER), 1024);
        setValue(new ItemStack(Material.SCULK_VEIN), 16);
        setValue(new ItemStack(Material.SUGAR_CANE), 2);
        setValue(new ItemStack(Material.KELP), 1);
        setValue(new ItemStack(Material.BAMBOO), 1);
        setValue(new ItemStack(Material.OBSIDIAN), 16);
        setValue(new ItemStack(Material.CHORUS_FLOWER), 8);
        setValue(new ItemStack(Material.DIAMOND_ORE), 0);
        setValue(new ItemStack(Material.REDSTONE_ORE), 0);
        setValue(new ItemStack(Material.DEEPSLATE_DIAMOND_ORE), 0);
        setValue(new ItemStack(Material.DEEPSLATE_REDSTONE_ORE), 0);
        setValue(new ItemStack(Material.ICE), 1);
        setValue(new ItemStack(Material.CACTUS), 1);
        setValue(new ItemStack(Material.PUMPKIN), 4);
        setValue(new ItemStack(Material.CARVED_PUMPKIN), 4);
        setValue(new ItemStack(Material.NETHERRACK), 1);
        setValue(new ItemStack(Material.SOUL_SAND), 2);
        setValue(new ItemStack(Material.SOUL_SOIL), 2);
        setValue(new ItemStack(Material.BASALT), 1);
        setValue(new ItemStack(Material.BROWN_MUSHROOM_BLOCK), 4);
        setValue(new ItemStack(Material.RED_MUSHROOM_BLOCK), 4);
        setValue(new ItemStack(Material.MUSHROOM_STEM), 4);
        setValue(new ItemStack(Material.VINE), 2);
        setValue(new ItemStack(Material.MYCELIUM), 8);
        setValue(new ItemStack(Material.LILY_PAD), 8);
        setValue(new ItemStack(Material.END_STONE), 2);
        setValue(new ItemStack(Material.DRAGON_EGG), 32768);
        setValue(new ItemStack(Material.EMERALD_ORE), 0);
        setValue(new ItemStack(Material.DEEPSLATE_EMERALD_ORE), 0);
        setValue(new ItemStack(Material.NETHER_QUARTZ_ORE), 0);
        setValue(new ItemStack(Material.SUNFLOWER), 2);
        setValue(new ItemStack(Material.LILAC), 2);
        setValue(new ItemStack(Material.ROSE_BUSH), 2);
        setValue(new ItemStack(Material.PEONY), 2);
        setValue(new ItemStack(Material.TALL_GRASS), 1);
        setValue(new ItemStack(Material.LARGE_FERN), 1);
        setValue(new ItemStack(Material.TURTLE_EGG), 64);
        setValue(new ItemStack(Material.DEAD_TUBE_CORAL_BLOCK), 8);
        setValue(new ItemStack(Material.DEAD_BRAIN_CORAL_BLOCK), 8);
        setValue(new ItemStack(Material.DEAD_BUBBLE_CORAL_BLOCK), 8);
        setValue(new ItemStack(Material.DEAD_FIRE_CORAL_BLOCK), 8);
        setValue(new ItemStack(Material.DEAD_HORN_CORAL_BLOCK), 8);
        setValue(new ItemStack(Material.TUBE_CORAL_BLOCK), 8);
        setValue(new ItemStack(Material.BRAIN_CORAL_BLOCK), 8);
        setValue(new ItemStack(Material.BUBBLE_CORAL_BLOCK), 8);
        setValue(new ItemStack(Material.FIRE_CORAL_BLOCK), 8);
        setValue(new ItemStack(Material.HORN_CORAL_BLOCK), 8);
        setValue(new ItemStack(Material.TUBE_CORAL), 8);
        setValue(new ItemStack(Material.BRAIN_CORAL), 8);
        setValue(new ItemStack(Material.BUBBLE_CORAL), 8);
        setValue(new ItemStack(Material.FIRE_CORAL), 8);
        setValue(new ItemStack(Material.HORN_CORAL), 8);
        setValue(new ItemStack(Material.DEAD_TUBE_CORAL), 8);
        setValue(new ItemStack(Material.DEAD_BRAIN_CORAL), 8);
        setValue(new ItemStack(Material.DEAD_BUBBLE_CORAL), 8);
        setValue(new ItemStack(Material.DEAD_FIRE_CORAL), 8);
        setValue(new ItemStack(Material.DEAD_HORN_CORAL), 8);
        setValue(new ItemStack(Material.TUBE_CORAL_FAN), 8);
        setValue(new ItemStack(Material.BRAIN_CORAL_FAN), 8);
        setValue(new ItemStack(Material.BUBBLE_CORAL_FAN), 8);
        setValue(new ItemStack(Material.FIRE_CORAL_FAN), 8);
        setValue(new ItemStack(Material.HORN_CORAL_FAN), 8);
        setValue(new ItemStack(Material.DEAD_TUBE_CORAL_FAN), 8);
        setValue(new ItemStack(Material.DEAD_BRAIN_CORAL_FAN), 8);
        setValue(new ItemStack(Material.DEAD_BUBBLE_CORAL_FAN), 8);
        setValue(new ItemStack(Material.DEAD_FIRE_CORAL_FAN), 8);
        setValue(new ItemStack(Material.DEAD_HORN_CORAL_FAN), 8);
        setValue(new ItemStack(Material.APPLE), 8);
        setValue(new ItemStack(Material.DIAMOND), 1028);
        setValue(new ItemStack(Material.IRON_INGOT), 32);
        setValue(new ItemStack(Material.GOLD_INGOT), 32);
        setValue(new ItemStack(Material.NETHERITE_SCRAP), 4096);
        setValue(new ItemStack(Material.NETHERITE_INGOT), 16512);
        setValue(new ItemStack(Material.STRING), 4);
        setValue(new ItemStack(Material.FEATHER), 2);
        setValue(new ItemStack(Material.GUNPOWDER), 8);
        setValue(new ItemStack(Material.WHEAT_SEEDS), 2);
        setValue(new ItemStack(Material.WHEAT), 2);
        setValue(new ItemStack(Material.FLINT), 1);
        setValue(new ItemStack(Material.PORKCHOP), 4);
        setValue(new ItemStack(Material.WATER_BUCKET), 128);
        setValue(new ItemStack(Material.LAVA_BUCKET), 256);
        setValue(new ItemStack(Material.SADDLE), 256);
        setValue(new ItemStack(Material.REDSTONE), 16);
        setValue(new ItemStack(Material.COAL), 16);
        setValue(new ItemStack(Material.SNOWBALL), 2);
        setValue(new ItemStack(Material.MILK_BUCKET), 256);
        setValue(new ItemStack(Material.CLAY_BALL), 8);
        setValue(new ItemStack(Material.SLIME_BALL), 8);
        setValue(new ItemStack(Material.EGG), 4);
        setValue(new ItemStack(Material.GLOWSTONE_DUST), 8);
        setValue(new ItemStack(Material.COD), 64);
        setValue(new ItemStack(Material.SALMON), 64);
        setValue(new ItemStack(Material.TROPICAL_FISH), 128);
        setValue(new ItemStack(Material.PUFFERFISH), 64);
        setValue(new ItemStack(Material.INK_SAC), 16);
        setValue(new ItemStack(Material.COCOA_BEANS), 16);
        setValue(new ItemStack(Material.LAPIS_LAZULI), 16);
        setValue(new ItemStack(Material.BONE), 4);
        setValue(new ItemStack(Material.MELON_SLICE), 2);
        setValue(new ItemStack(Material.BEEF), 4);
        setValue(new ItemStack(Material.CHICKEN), 4);
        setValue(new ItemStack(Material.ROTTEN_FLESH), 4);
        setValue(new ItemStack(Material.ENDER_PEARL), 8);
        setValue(new ItemStack(Material.BLAZE_ROD), 8);
        setValue(new ItemStack(Material.GHAST_TEAR), 16);
        setValue(new ItemStack(Material.NETHER_WART), 8);
        setValue(new ItemStack(Material.SPIDER_EYE), 4);
        setValue(new ItemStack(Material.EMERALD), 128);
        setValue(new ItemStack(Material.CARROT), 2);
        setValue(new ItemStack(Material.POTATO), 2);
        setValue(new ItemStack(Material.POISONOUS_POTATO), 8);
        setValue(new ItemStack(Material.SKELETON_SKULL), 128);
        setValue(new ItemStack(Material.WITHER_SKELETON_SKULL), 1024);
        setValue(new ItemStack(Material.ZOMBIE_HEAD), 128);
        setValue(new ItemStack(Material.CREEPER_HEAD), 128);
        setValue(new ItemStack(Material.DRAGON_HEAD), 24576);
        setValue(new ItemStack(Material.NETHER_STAR), 16384);
        setValue(new ItemStack(Material.QUARTZ), 32);
        setValue(new ItemStack(Material.PRISMARINE_SHARD), 8);
        setValue(new ItemStack(Material.PRISMARINE_CRYSTALS), 16);
        setValue(new ItemStack(Material.RABBIT), 2);
        setValue(new ItemStack(Material.RABBIT_FOOT), 16);
        setValue(new ItemStack(Material.RABBIT_HIDE), 2);
        setValue(new ItemStack(Material.NAME_TAG), 256);
        setValue(new ItemStack(Material.MUTTON), 2);
        setValue(new ItemStack(Material.CHORUS_FRUIT), 4);
        setValue(new ItemStack(Material.BEETROOT), 2);
        setValue(new ItemStack(Material.BEETROOT_SEEDS), 2);
        setValue(new ItemStack(Material.DRAGON_BREATH), 512);
        setValue(new ItemStack(Material.ELYTRA), 0);
        setValue(new ItemStack(Material.SHULKER_SHELL), 32);
        setValue(new ItemStack(Material.PHANTOM_MEMBRANE), 16);
        setValue(new ItemStack(Material.NAUTILUS_SHELL), 128);
        setValue(new ItemStack(Material.HEART_OF_THE_SEA), 8192);
        setValue(new ItemStack(Material.BELL), 128);
        setValue(new ItemStack(Material.SWEET_BERRIES), 2);
        setValue(new ItemStack(Material.SHROOMLIGHT), 4);
        setValue(new ItemStack(Material.HONEYCOMB), 1);
        setValue(new ItemStack(Material.CRYING_OBSIDIAN), 64);
        setValue(new ItemStack(Material.BLACKSTONE), 2);
        setValue(new ItemStack(Material.GILDED_BLACKSTONE), 16);
        setValue(new ItemStack(Material.HONEY_BOTTLE), 1);
        setValue(new ItemStack(Material.PLAYER_HEAD), 0);
        setValue(new ItemStack(Material.RAW_IRON), 32);
        setValue(new ItemStack(Material.RAW_GOLD), 32);
        setValue(new ItemStack(Material.RAW_COPPER), 32);
        setValue(new ItemStack(Material.COPPER_INGOT), 32);
        setValue(new ItemStack(Material.DEEPSLATE), 1);
        setValue(new ItemStack(Material.AMETHYST_SHARD), 16);
        setValue(new ItemStack(Material.ECHO_SHARD), 32);
        setValue(new ItemStack(Material.COBBLED_DEEPSLATE), 1);
        setValue(new ItemStack(Material.FROGSPAWN), 64);
        setValue(SlimefunItem.getById("COPPER_DUST").getItem(), 32);
        setValue(SlimefunItem.getById("IRON_DUST").getItem(), 32);
        setValue(SlimefunItem.getById("GOLD_DUST").getItem(), 32);
        setValue(SlimefunItem.getById("TIN_DUST").getItem(), 32);
        setValue(SlimefunItem.getById("MAGNESIUM_DUST").getItem(), 32);
        setValue(SlimefunItem.getById("ZINC_DUST").getItem(), 32);
        setValue(SlimefunItem.getById("LEAD_DUST").getItem(), 32);
        setValue(SlimefunItem.getById("SILVER_DUST").getItem(), 32);
        setValue(SlimefunItem.getById("ALUMINUM_DUST").getItem(), 32);
        setValue(SlimefunItem.getById("NETHER_ICE").getItem(), 128);
        setValue(SlimefunItem.getById("BUCKET_OF_OIL").getItem(), 512);
        setValue(SlimefunItem.getById("SIFTED_ORE").getItem(), 32);
        setValue(SlimefunItem.getById("STONE_CHUNK").getItem(), 1);
        setValue(SlimefunItem.getById("BASIC_CIRCUIT_BOARD").getItem(), 1024);
    }

    private static double getItemValueVanilla(ItemStack itemStack, int depth) {
        if (depth > THRESHOLD) {
            return 128.0D;
        }

        Double storedValue = valueMap.get(itemStack.getType().hashCode());
        if (storedValue != null) {
            return storedValue;
        } else {
            double value = 0.0D;
            List<Recipe> recipeList = Bukkit.getRecipesFor(itemStack);
            if (recipeList.isEmpty()) {
                return 0.0D;
            } else {
                for (Recipe recipe : recipeList) {
                    double recipeValue = handleRecipeVanilla(recipe, depth + 1);
                    if (recipeValue > 0.0D) {
                        recipeValue = (double) Math.round(recipeValue * 100.0D) / 100.0D;
                        if (value == 0.0D) {
                            value = recipeValue;
                        } else if (recipeValue < value) {
                            value = recipeValue;
                        }
                    }
                }

                setValue(itemStack, value);
                return value;
            }
        }
    }

    private static double handleRecipeVanilla(Recipe recipe, int depth) {
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            return handleShapedRecipe(shapedRecipe, depth);
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            return handleShapelessRecipe(shapelessRecipe, depth);
        } else if (recipe instanceof CookingRecipe<?> cookingRecipe) {
            return handleCookingRecipe(cookingRecipe, depth);
        } else if (recipe instanceof SmithingRecipe smithingRecipe) {
            return handleSmithingRecipe(smithingRecipe, depth);
        } else if (recipe instanceof StonecuttingRecipe stonecuttingRecipe) {
            return handleStonecuttingRecipe(stonecuttingRecipe, depth);
        } else {
            return 0.0D;
        }
    }

    private static double handleShapedRecipe(ShapedRecipe shapedRecipe, int depth) {
        double value = 0.0D;
        Map<Character, Double> valueMap = new HashMap<>();

        for (Map.Entry<Character, ItemStack> entry : shapedRecipe.getIngredientMap().entrySet()) {
            if (entry.getValue() != null) {
                double entryValue = getItemValueVanilla(entry.getValue(), depth);
                if (entryValue == 0.0D) {
                    return 0.0D;
                }

                valueMap.put(entry.getKey(), entryValue);
            }
        }

        for (String string : shapedRecipe.getShape()) {
            for (char character : string.toCharArray()) {
                Double charValue = valueMap.get(character);
                if (charValue != null) {
                    value += valueMap.get(character);
                }
            }
        }

        value /= shapedRecipe.getResult().getAmount();
        return value;
    }

    private static double handleShapelessRecipe(ShapelessRecipe shapelessRecipe, int depth) {
        double value = 0.0D;

        for (ItemStack itemStack : shapelessRecipe.getIngredientList()) {
            double itemValue = getItemValueVanilla(itemStack, depth);
            if (itemValue == 0.0D) {
                return 0.0D;
            }

            value += itemValue;
        }

        value /= shapelessRecipe.getResult().getAmount();
        return value;
    }

    private static double handleCookingRecipe(CookingRecipe<?> cookingRecipe, int depth) {
        double value = getItemValueVanilla(cookingRecipe.getInput(), depth);

        value /= cookingRecipe.getResult().getAmount();
        return value;
    }

    private static double handleSmithingRecipe(SmithingRecipe smithingRecipe, int depth) {
        double baseValue = getItemValueVanilla(smithingRecipe.getBase().getItemStack(), depth);
        double additionValue = getItemValueVanilla(smithingRecipe.getAddition().getItemStack(), depth);

        double value = baseValue + additionValue;
        value /= smithingRecipe.getResult().getAmount();
        return value;
    }

    private static double handleStonecuttingRecipe(StonecuttingRecipe stonecuttingRecipe, int depth) {
        double value = getItemValueVanilla(stonecuttingRecipe.getInput(), depth);

        value /= stonecuttingRecipe.getResult().getAmount();
        return value;
    }

    private static double getItemValueSlimefun(SlimefunItem slimefunItem, int depth) {
        if (depth > THRESHOLD) {
            return 128.0D;
        }

        Double storedValue = valueMap.get(slimefunItem.getId().hashCode());
        if (storedValue != null) {
            return storedValue;
        } else if (slimefunItem.isDisabled()) {
            return 0.0D;
        } else {
            double value = 0.0D;
            double recipeValue = handleRecipeSlimefun(
                    slimefunItem.getRecipe(),
                    slimefunItem.getRecipeOutput().getAmount() + 1, depth + 1
            );
            if (recipeValue > 0.0D) {
                recipeValue = (double) Math.round(recipeValue * 100.0D) / 100.0D;
                value = recipeValue;
            }

            setValue(slimefunItem.getId().hashCode(), value);
            return value;
        }
    }

    @SuppressWarnings("ConstantValue")
    private static double handleRecipeSlimefun(ItemStack[] recipe, int outputAmount, int depth) {
        double value = 0.0D;

        for (ItemStack itemStack : recipe) {
            if (itemStack != null) {
                SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
                double itemValue;
                if (slimefunItem != null) {
                    itemValue = getItemValueSlimefun(slimefunItem, depth) * itemStack.getAmount();
                } else {
                    itemValue = getItemValueVanilla(itemStack, depth) * itemStack.getAmount();
                }

                value += itemValue;
            }
        }

        return value / (double) outputAmount;
    }
}
