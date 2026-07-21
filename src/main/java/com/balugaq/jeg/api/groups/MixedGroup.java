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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.clickhandler.OnDisplay;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author balugaq
 * @since 2.1
 */
@SuppressWarnings({"deprecation", "unused"})
@Getter
@NullMarked
public abstract class MixedGroup<T extends BaseGroup<T>> extends BaseGroup<T> {
    public final List<String> actions = new ArrayList<>();
    public final List<Object> objects;

    public MixedGroup(NamespacedKey key, ItemStack icon, int tier) {
        super(key, icon, tier);
        this.page = 1;
        this.objects = new ArrayList<>();
    }

    public MixedGroup(NamespacedKey key, ItemStack icon) {
        super(key, icon);
        this.page = 1;
        this.objects = new ArrayList<>();
    }

    public void addGroup(ItemGroup itemGroup) {
        this.objects.add(itemGroup);
    }

    public void addItem(SlimefunItem item) {
        this.objects.add(item);
    }

    public void addItem(ItemStack itemStack) {
        this.objects.add(itemStack);
    }

    @Override
    public void open(
        Player player,
        PlayerProfile playerProfile,
        SlimefunGuideMode slimefunGuideMode) {
        if (actions.isEmpty()) {
            playerProfile.getGuideHistory().add(this, this.page);
            this.generateMenu(player, playerProfile, slimefunGuideMode).open(player);
            return;
        }

        String s = actions.get(ThreadLocalRandom.current().nextInt(actions.size()));
        if (s.startsWith("command /")) {
            String a = s.substring(9);
            player.closeInventory();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), a.replace("%player%", player.getName()));
        } else if (s.startsWith("commandp /")) {
            String a = s.substring(9);
            player.closeInventory();
            Bukkit.dispatchCommand(player, a.replace("%player%", player.getName()));
        } else if (s.startsWith("sayp ")) {
            player.closeInventory();
            player.chat(s.substring(5).replace("%player%", player.getName()));
        } else if (s.startsWith("lookupitem ")) {
            PlayerProfile profile = PlayerProfile.find(player).orElse(null);
            if (profile == null) return;
            SlimefunItem item = SlimefunItem.getById(s.substring(11));
            if (item == null) return;
            GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE).displayItem(profile, item, true);
        } else if (s.startsWith("lookupgroup ")) {
            PlayerProfile profile = PlayerProfile.find(player).orElse(null);
            if (profile == null) return;
            for (ItemGroup group : new ArrayList<>(Slimefun.getRegistry().getAllItemGroups())) {
                if (group.getKey().toString().equals(s.substring(12))) {
                    GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE).openItemGroup(profile, group, 1);
                    return;
                }
            }
        } else if (s.startsWith("link ")) {
            ChatUtils.sendURL(player, s.substring(5));
            player.closeInventory();
        }
    }

    public ChestMenu generateMenu(
        final Player player,
        final PlayerProfile playerProfile,
        final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu(ItemStackHelper.getDisplayName(getItem(player)));

        OnClick.preset(chestMenu);

        SlimefunGuideImplementation implementation = GuideUtil.getSlimefunGuide(slimefunGuideMode);
        Format format = Formats.sub;
        int maxPage = (this.objects.size() - 1) / format.getChars(Formats.Char.CONTENT).size() + 1;
        GuideUtil.commonRender(chestMenu, format, playerProfile, player, this, this.page, maxPage);

        List<Integer> contentSlots = Formats.sub.getChars(Formats.Char.CONTENT);
        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < this.objects.size()) {
                Object o = objects.get(index);
                switch (o) {
                    case SlimefunItem slimefunItem ->
                        OnDisplay.Item.display(player, slimefunItem.getItem(), OnDisplay.Item.Normal, implementation)
                            .at(chestMenu, contentSlots.get(i), page);
                    case ItemGroup itemGroup -> {
                        if (GuideUtil.getGuide(
                            player, GuideUtil.getLastGuideMode(player)
                        ) instanceof JEGSlimefunGuideImplementation guide) {
                            guide.showItemGroup0(chestMenu, player, playerProfile, itemGroup, contentSlots.get(i));
                        }
                    }
                    case ItemStack itemStack ->
                        OnDisplay.Item.display(player, itemStack, OnDisplay.Item.Normal, implementation)
                            .at(chestMenu, contentSlots.get(i), page);
                    default -> {
                    }
                }
            }
        }

        return chestMenu;
    }
}
