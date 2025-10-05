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
import com.balugaq.jeg.api.objects.collection.cooldown.FrequencyWatcher;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.option.ShareInGuideOption;
import com.balugaq.jeg.implementation.option.ShareOutGuideOption;
import com.balugaq.jeg.utils.ClipboardUtil;
import com.balugaq.jeg.utils.platform.PlatformUtil;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author balugaq
 * @since 1.7
 */
@SuppressWarnings("deprecation")
public class NamePrinter implements Applier {

    private static final NamePrinter instance = new NamePrinter();

    private NamePrinter() {
    }

    public static void applyWith(@NotNull SlimefunGuideImplementation guide, @NotNull ChestMenu menu, int slot) {
        if (JustEnoughGuide.getConfigManager().isItemShareable()) {
            instance.apply(guide, menu, slot);
        }
    }

    @ParametersAreNonnullByDefault
    private static void shareSlimefunItem(Player player, String itemName) {
        String s = ChatColor.stripColor(itemName);
        String playerName = player.getName();

        if (PlatformUtil.isPaper()) {
            String sharedMessage = SHARED_ITEM_MESSAGE.format(new Object[]{playerName, ChatColors.color(itemName)});

            Component base = LegacyComponentSerializer.legacySection().deserialize(sharedMessage)
                    .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text(CLICK_TO_SEARCH)));
            Component clickToSearch = base.clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/sf search " + s));
            Component clickToCopy = base.clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.COPY_TO_CLIPBOARD, itemName));
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (ShareInGuideOption.isEnabled(p)) {
                    if (p.hasPermission("slimefun.command.search")) {
                        p.sendMessage(clickToSearch);
                    } else {
                        p.sendMessage(clickToCopy);
                    }
                }
            });
        } else {
            String sharedMessage = SHARED_ITEM_MESSAGE.format(new Object[]{playerName, ChatColors.color(itemName)});
            TextComponent msg = new TextComponent(sharedMessage);
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(CLICK_TO_SEARCH)));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sf search " + s));

            Bukkit.getOnlinePlayers().forEach(p -> {
                if (ShareInGuideOption.isEnabled(p)) {
                    if (p.hasPermission("slimefun.command.search")) {
                        ClipboardUtil.send(p, msg);
                    } else {
                        ClipboardUtil.send(p, ClipboardUtil.makeComponent(sharedMessage, CLICK_TO_SEARCH, itemName));
                    }
                }
            });
        }
    }



    @ParametersAreNonnullByDefault
    public void apply(SlimefunGuideImplementation guide, ChestMenu menu, int slot) {
        menu.addMenuClickHandler(
                slot, JEGClickHandler.of(guide, menu, slot).addProcessor(NamePrinterProcessor.getInstance()));
    }

    public static class NamePrinterProcessor extends Processor {
        @Getter
        private static final NamePrinterProcessor instance = new NamePrinterProcessor();

        public NamePrinterProcessor() {
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
            if (clickedItemStack != null
                    && clickedItemStack.getType() != Material.AIR
                    && (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP)) {
                if (!checkCooldown(player)) {
                    return false;
                }

                if (!ShareOutGuideOption.isEnabled(player)) {
                    return false;
                }

                String name = ItemStackHelper.getDisplayName(clickedItemStack);
                shareSlimefunItem(player, name);

                return false;
            }

            return true;
        }
    }
}
