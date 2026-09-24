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

package com.balugaq.jeg.implementation.option;

import com.balugaq.jeg.api.objects.enums.ClickSide;
import com.balugaq.jeg.api.patches.Priorities;
import com.balugaq.jeg.api.recipe_complete.CompletionBehaviour;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Calculator;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings({"SameReturnValue"})
@NullMarked
public class RecipeCompletionGuideOption extends AbstractCustomActionGuideOption {
    public static final ItemStack DEFAULT_ICON = Converter.getItem(
        Material.CLOCK,
        "&a单击打开配方补全设置界面"
    );

    public static CompletionBehaviour get(Player player, ClickSide side) {
        String s = PersistentDataAPI.getString(player, side.key());
        if (s == null) {
            switch (side) {
                case LEFT -> {
                    set(player, side, CompletionBehaviour.SINGLE);
                    return CompletionBehaviour.SINGLE;
                }
                case RIGHT -> {
                    set(player, side, CompletionBehaviour.STACK_64);
                    return CompletionBehaviour.STACK_64;
                }
            }
        }
        var ss = s.split(";");
        return switch (ss[0]) {
            case "single" -> CompletionBehaviour.SINGLE;
            case "stack" -> CompletionBehaviour.STACK;
            case "stack_64" -> CompletionBehaviour.STACK_64;
            case "custom" -> CompletionBehaviour.CUSTOM;
            default -> CompletionBehaviour.SINGLE;
        };
    }

    public static int get(Player player, @Nullable ItemStack stack, ClickSide side) {
        String s = PersistentDataAPI.getString(player, side.key());
        if (s == null) {
            switch (side) {
                case LEFT -> {
                    set(player, side, CompletionBehaviour.SINGLE);
                    return 1;
                }
                case RIGHT -> {
                    set(player, side, CompletionBehaviour.STACK_64);
                    return 64;
                }
            }
        }
        var ss = s.split(";");
        return switch (ss[0]) {
            case "single" -> 1;
            case "stack" -> stack == null ? 0 : stack.getMaxStackSize();
            case "stack_64" -> 64;
            case "custom" -> Integer.parseInt(ss[1]);
            default -> 1;
        };
    }

    public static void set(Player player, ClickSide side, CompletionBehaviour behaviour) {
        SoundEffect.GUIDE_BUTTON_CLICK_SOUND.playFor(player);
        switch (behaviour) {
            case SINGLE -> PersistentDataAPI.setString(player, side.key(), "single");
            case STACK -> PersistentDataAPI.setString(player, side.key(), "stack");
            case STACK_64 -> PersistentDataAPI.setString(player, side.key(), "stack_64");
            case CUSTOM -> {
                player.sendMessage("请输入单次要补全的次数");
                player.closeInventory();
                ChatInput.waitForPlayer(JustEnoughGuide.getInstance(), player, s -> {
                    try {
                        int value = Calculator.calculate(s).intValue();
                        if (value < 1 || value > JustEnoughGuide.getConfigManager().getMaxRecipeCompletionTimes()) {
                            player.sendMessage("请输入 1 ~ " + JustEnoughGuide.getConfigManager().getMaxRecipeCompletionTimes() + " 之间的正整数");
                            return;
                        }

                        PersistentDataAPI.setString(player, side.key(), "custom;" + value);

                        player.sendMessage(ChatColors.color("&a已设置补全次数为 " + behaviour.timesString(player, side)));
                        GuideUtil.openRecipeCompletionGui(player);
                    } catch (NumberFormatException ignored) {
                        player.sendMessage("请输入 1 ~ " + JustEnoughGuide.getConfigManager().getMaxRecipeCompletionTimes() + " 之间的正整数");
                    }
                });
                return;
            }
        }

        player.sendMessage(ChatColors.color("&a已设置补全次数为 " + behaviour.timesString(player, side)));
        GuideUtil.openRecipeCompletionGui(player);
    }

    @Override
    public int priority() {
        return Priorities.RecipeCompletionGuideOption;
    }

    private static final RecipeCompletionGuideOption instance = new RecipeCompletionGuideOption();

    public static RecipeCompletionGuideOption instance() {
        return instance;
    }

    @Override
    public ItemStack getDisplayItem(Player p, ItemStack guide, boolean unused) {
        return DEFAULT_ICON;
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        GuideUtil.openRecipeCompletionGui(p);
    }

    @Override
    public String key0() {
        return "recipe_completion";
    }
}
