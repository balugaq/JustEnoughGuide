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

package com.balugaq.jeg.utils;

import com.balugaq.jeg.api.objects.annotations.Author;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class provides utility methods for working with ItemStacks.
 *
 * @author balugaq
 * @since 1.1
 */
@UtilityClass
public final class ItemStackUtil {
    @Author("lijinhong11")
    private static final Map<String, String> materialMappings = Map.of(
            "GRASS", "SHORT_GRASS",
            "SHORT_GRASS", "GRASS",
            "SCUTE", "TURTLE_SCUTE",
            "TURTLE_SCUTE", "SCUTE");

    @Author("lijinhong11")
    public static @NotNull ItemStack doGlow(@NotNull ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
        item.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        return item;
    }

    /**
     * This method is used to convert an {@code MyItemStack extends ItemStack} to a pure {@code ItemStack}.
     *
     * @param item The MyItemStack to be converted.
     * @return A pure ItemStack.
     */
    @NotNull public static ItemStack getCleanItem(@Nullable ItemStack item) {
        if (item == null) {
            return new ItemStack(Material.AIR);
        }

        ItemStack cleanItem = new ItemStack(item.getType());
        cleanItem.setAmount(item.getAmount());
        if (item.hasItemMeta()) {
            cleanItem.setItemMeta(item.getItemMeta());
        }

        return cleanItem;
    }

    public static @NotNull ItemStack getAsQuantity(@Nullable ItemStack itemStack, int amount) {
        if (itemStack == null) {
            return new ItemStack(Material.AIR);
        } else {
            ItemStack clone = itemStack.clone();
            clone.setAmount(amount);
            return clone;
        }
    }

    @SneakyThrows
    @Nullable @Author("lijinhong11 & balugaq")
    public static ItemStack readItem(char c, @Nullable ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        String type = section.getString("material_type", "mc");
        if (!type.equalsIgnoreCase("none") && !section.contains("material")) {
            Debug.severe("Icon 定义 " + c + " 缺少 material 字段");
            return null;
        }

        String material = section.getString("material", "");
        List<String> lore;
        List<String> rawLore = section.getStringList("lore");
        if (JustEnoughGuide.getIntegrationManager().isEnabledCMILib()) {
            lore = CMIChatColor.translate(rawLore);
        } else {
            lore = new ArrayList<>();
            for (String loreLine : rawLore) {
                lore.add(ChatColors.color(loreLine));
            }
        }
        String name;
        String rawName = section.getString("name", "");
        if (JustEnoughGuide.getIntegrationManager().isEnabledCMILib()) {
            name = CMIChatColor.translate(rawName);
        } else {
            name = ChatColors.color(rawName);
        }
        boolean glow = section.getBoolean("glow", false);
        boolean hasEnchantment = section.contains("enchantments") && section.isList("enchantments");
        int modelId = section.getInt("modelId");
        int amount = section.getInt("amount", 1);
        if (material.contains("|")) {
            String[] split = material.split("\\|");
            for (String mat : split) {
                ItemStack item =
                        readItem(c, section, type, mat.trim(), name, lore, glow, hasEnchantment, modelId, amount, true);
                if (item != null) {
                    return item;
                }
            }

            Debug.severe("Icon 定义 " + c + " 无法读取，已转为石头");
            return null;
        } else {
            return readItem(
                    c, section, type, material.trim(), name, lore, glow, hasEnchantment, modelId, amount, false);
        }
    }

    @SneakyThrows
    @Nullable @SuppressWarnings("deprecation")
    @Author("lijinhong11 & balugaq")
    public static ItemStack readItem(
            char c,
            final @NotNull ConfigurationSection section,
            String type,
            final @NotNull String material,
            final @NotNull String name,
            final @NotNull List<String> lore,
            boolean glow,
            boolean hasEnchantment,
            int modelId,
            int amount,
            boolean isBranch) {

        if (material.startsWith("ey") || material.startsWith("ew")) {
            type = "skull";
        } else if (material.startsWith("http") || material.startsWith("https")) {
            type = "skull_url";
        } else if (material.matches("^[0-9A-Fa-f]{64}+$")) {
            type = "skull_hash";
        }

        ItemStack itemStack;

        switch (type.toLowerCase()) {
            case "none" -> {
                return new ItemStack(Material.AIR, 1);
            }
            case "skull_hash" -> {
                PlayerSkin playerSkin = PlayerSkin.fromHashCode(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);

                itemStack = Converter.getItem(head, name, lore);
            }
            case "skull_base64", "skull" -> {
                PlayerSkin playerSkin = PlayerSkin.fromBase64(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);

                itemStack = Converter.getItem(head, name, lore);
            }
            case "skull_url" -> {
                PlayerSkin playerSkin = PlayerSkin.fromURL(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);

                itemStack = Converter.getItem(head, name, lore);
            }
            case "slimefun" -> {
                SlimefunItem sfItem = SlimefunItem.getById(material.toUpperCase());
                if (sfItem != null) {
                    itemStack = Converter.getItem(sfItem.getItem().clone());
                    itemStack.editMeta(m -> {
                        if (!name.isBlank()) {
                            m.setDisplayName(name);
                        }

                        if (!lore.isEmpty()) {
                            m.setLore(lore);
                        }
                    });
                } else {
                    if (isBranch) {
                        return null;
                    }
                    Debug.severe("Icon 定义 " + c + " 无法读取，已转为石头");
                    itemStack = Converter.getItem(Material.STONE, name, lore);
                }
            }
                // mc
            default -> {
                Optional<Material> materialOptional = Optional.ofNullable(Material.matchMaterial(material));
                Material mat = Material.STONE;

                if (materialOptional.isPresent()) {
                    mat = materialOptional.get();
                } else if (SlimefunItem.getById(material) == null) {
                    if (materialMappings.containsKey(material)) {
                        materialOptional = Optional.ofNullable(Material.matchMaterial(materialMappings.get(material)));
                        if (materialOptional.isPresent()) {
                            mat = materialOptional.get();
                            Debug.warn("Icon 定义 " + c + " 的 material 字段 " + material + " 已自动修复为 " + mat);
                        } else {
                            if (isBranch) {
                                return null;
                            }
                            Debug.severe("Icon 定义 " + c + " 无法读取，已转为石头");
                        }
                    } else {
                        if (isBranch) {
                            return null;
                        }
                        Debug.severe("Icon 定义 " + c + " 无法读取，已转为石头");
                    }
                }

                if (!mat.isItem() || mat.isLegacy()) {
                    Debug.warn("Icon 定义存在无效的 material: " + mat + ", 已转为石头");
                    mat = Material.STONE;
                }

                itemStack = Converter.getItem(mat, name, lore);
            }
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (modelId > 0) {
            meta.setCustomModelData(modelId);
        }

        if (amount > 100 || amount < -1) {
            Debug.severe("Icon 定义 " + c + " 无法读取，字段 amount 的值不在范围内: -1 < amount <= 100");
            return null;
        }
        itemStack.setAmount(amount);

        itemStack.setItemMeta(meta);

        if (hasEnchantment) {
            List<String> enchants = section.getStringList("enchantments");
            for (String enchant : enchants) {
                String[] s2 = enchant.split(" ");
                if (s2.length != 2) {
                    Debug.severe("Icon 定义 " + c + " 无法读取附属 " + enchant + ", 跳过添加此附魔");
                    continue;
                }

                String enchantName = s2[0];
                int lvl = Integer.parseInt(s2[1]);

                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantName.toLowerCase()));
                if (enchantment == null) {
                    Debug.severe("Icon 定义 " + c + " 无法读取附属 " + enchant + ", 跳过添加此附魔");
                    continue;
                }

                itemStack.addUnsafeEnchantment(enchantment, lvl);
            }
        }

        return glow ? doGlow(itemStack) : itemStack;
    }
}
