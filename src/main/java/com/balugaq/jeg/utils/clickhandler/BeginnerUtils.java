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

package com.balugaq.jeg.utils.clickhandler;

import com.balugaq.jeg.api.clickhandler.JEGClickHandler;
import com.balugaq.jeg.api.clickhandler.Processor;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.option.BeginnersGuideOption;
import com.balugaq.jeg.utils.EventUtil;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author balugaq
 * @since 1.5
 */
@SuppressWarnings("deprecation")
public class BeginnerUtils implements Applier {
    private static final BeginnerUtils instance = new BeginnerUtils();

    private BeginnerUtils() {
    }

    public static void applyWith(@NotNull SlimefunGuideImplementation guide, @NotNull ChestMenu menu, int slot) {
        instance.apply(guide, menu, slot);
    }

    public static boolean isNew(Player player) {
        return BeginnersGuideOption.isEnabled(player);
    }

    @ParametersAreNonnullByDefault
    public void apply(SlimefunGuideImplementation guide, ChestMenu menu, int slot) {
        if (!JustEnoughGuide.getConfigManager().isBeginnerOption()) {
            return;
        }

        menu.addMenuClickHandler(
                slot, JEGClickHandler.of(guide, menu, slot).addProcessor(BeginnerProcessor.getInstance()));
    }

    public static class BeginnerProcessor extends Processor {
        @Getter
        private static final BeginnerProcessor instance = new BeginnerProcessor();

        public BeginnerProcessor() {
            super(Strategy.HEAD);
        }

        /**
         * A simple Mixin processor
         * Handles the events to happen when player clicked.
         *
         * @param guide            the guide
         * @param menu             the menu
         * @param event            the event
         * @param player           the player
         * @param clickedSlot      the clicked slot
         * @param clickedItemStack the clicked item stack
         * @param clickAction      the click action
         * @param processedResult  the processed result, null if the {@link Processor#getStrategy()} is {@link Strategy#HEAD}.
         * @return false if the process is handled successfully, true and handle other {@link Processor}s otherwise.
         */
        @Override
        public boolean process(
                final @NotNull SlimefunGuideImplementation guide,
                final @NotNull ChestMenu menu,
                final @NotNull InventoryClickEvent event,
                final @NotNull Player player,
                @Range(from = 0, to = 53) int clickedSlot,
                final @Nullable ItemStack clickedItemStack,
                final @NotNull ClickAction clickAction,
                final @Nullable Boolean processedResult) {
            if (isNew(player)
                    && clickAction.isShiftClicked()
                    && clickAction.isRightClicked()
                    && clickedItemStack != null
                    && clickedItemStack.getType() != Material.AIR) {
                return EventUtil.callEvent(new GuideEvents.BeginnerButtonClickEvent(
                                player, clickedItemStack, clickedSlot, clickAction, menu, guide))
                        .ifSuccess(() -> {
                            PlayerProfile.get(
                                    player,
                                    profile -> guide.openSearch(
                                            profile,
                                            ChatColor.stripColor(ItemStackHelper.getDisplayName(clickedItemStack)),
                                            true));
                            return false;
                        });
            }

            return true;
        }
    }
}
