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

package com.balugaq.jeg.api.recipe_complete.source;

import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.items.ReplacementCardAdapter;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@Getter
@NullMarked
public class RecipeCompleteProvider {
    public static final int PLAYER_INVENTORY_HANDLE_LEVEL = 5;
    public static final int SLIME_AE_PLUGIN_HANDLE_LEVEL = 10;
    public static final int NETWORKS_HANDLE_LEVEL = 15;
    public static final int PLAYER_NEARBY_CONTAINER_HANDLE_LEVEL = 20;

    @Getter
    private static final List<ItemSource> sources = new ArrayList<>();

    @Getter
    private static final List<RecipeHandler> specialRecipeHandlers = new ArrayList<>();

    public static void addSource(ItemSource source) {
        sources.addFirst(source);
        sources.sort(Comparator.comparingInt(ItemSource::handleLevel));
    }

    @CanIgnoreReturnValue
    public static RecipeHandler registerSpecialRecipeHandler(RecipeHandler handler) {
        specialRecipeHandlers.add(handler);
        return handler;
    }

    public static RecipeHandler unregisterSpecialRecipeHandler(RecipeHandler handler) {
        specialRecipeHandlers.remove(handler);
        return handler;
    }

    @Nullable
    public static ItemSource removeItemSource(VanillaRecipeCompletion source) {
        return sources.remove(source) ? source : null;
    }

    @Nullable
    public static ItemSource removeItemSource(JavaPlugin plugin) {
        for (var source : sources) {
            if (source.plugin().equals(plugin)) {
                return sources.remove(source) ? source : null;
            }
        }
        return null;
    }

    public static void shutdown() {
        sources.clear();
        specialRecipeHandlers.clear();
    }

    @Range(from = 0, to = Long.MAX_VALUE)
    public static long countAmount(RecipeCompleteSession session, ItemStack template) {
        // 这个 countAmount 扫描范围会有重叠，只能作为一个参考
        long amt = 0;
        for (var source : sources) {
            if (session.isNotHandleable(source)) {
                continue;
            }
            if (!source.handleable(session)) {
                session.setNotHandleable(source);
                continue;
            }
            List<ItemStack> replacementCards = new ArrayList<>();
            if (JustEnoughGuide.getConfigManager().isAdaptReplacementCards()) {
                List<ItemStack> cards = ReplacementCardAdapter.getReplacementCards(template);
                if (cards != null) {
                    replacementCards.addAll(cards);
                }
            }
            replacementCards.add(template);

            for (ItemStack possibleTemplate : replacementCards) {
                if (session.itemNotIn(source, possibleTemplate)) {
                    continue;
                }
                long amt0 = source.countAmount(session, possibleTemplate);
                if (amt0 == 0) {
                    session.setItemNotIn(source, possibleTemplate);
                } else {
                    amt = clampLong(amt, amt0);
                }
            }
        }
        return amt;
    }

    @Range(from = Long.MIN_VALUE, to = Long.MAX_VALUE)
    public static long clampLong(long amt, long add) {
        long result = amt + add;
        // 只有当 amt 和 add 同号，且结果与 amt 异号时才发生溢出
        if (((amt ^ result) & (add ^ result)) < 0) {
            // 溢出：add > 0 表示正溢出，add < 0 表示负溢出
            return add > 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
        }
        return result;
    }

    /**
     * @return gotten
     */
    @NonNegative
    public static long getItemStack(RecipeCompleteSession session, ItemStack template, long need) {
        long total = 0;
        for (var source : sources) {
            if (session.isNotHandleable(source)) {
                continue;
            }
            if (!source.handleable(session)) {
                session.setNotHandleable(source);
                continue;
            }
            List<ItemStack> replacementCards = new ArrayList<>();
            if (JustEnoughGuide.getConfigManager().isAdaptReplacementCards()) {
                List<ItemStack> cards = ReplacementCardAdapter.getReplacementCards(template);
                if (cards != null) {
                    replacementCards.addAll(cards);
                }
            }
            replacementCards.add(template);

            for (ItemStack possibleTemplate : replacementCards) {
                if (session.itemNotIn(source, possibleTemplate)) {
                    continue;
                }
                var gotten = source.getItemStack(session, possibleTemplate, need);
                need -= gotten;
                total += need;
                if (need <= 0) {
                    return total;
                }

                session.setItemNotIn(source, possibleTemplate);
            }
        }
        return 0;
    }

    public static void openSlimefun(RecipeCompleteSession session) {
        SlimefunRecipeCompletion.openGuide(session, null);
    }

    public static void openVanilla(RecipeCompleteSession session) {
        RecipeCompletableListener.allowSelectingItemStackToRecipeComplete(session.getPlayer().getUniqueId());
        VanillaRecipeCompletion.openGuide(session, null);
    }
}
