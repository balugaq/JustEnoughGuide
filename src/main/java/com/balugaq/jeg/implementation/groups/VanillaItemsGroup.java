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

package com.balugaq.jeg.implementation.groups;

import com.balugaq.jeg.api.groups.BaseGroup;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.VanillaItemShade;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.clickhandler.OnDisplay;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * This class used to create groups to display all the vanilla items in the guide. Display for JEG recipe complete in
 * NetworksExpansion / SlimeAEPlugin
 *
 * @author balugaq
 * @since 1.7
 */
@SuppressWarnings({"deprecation", "unused", "ConstantValue"})
@NotDisplayInCheatMode
@NullMarked
public class VanillaItemsGroup extends BaseGroup<VanillaItemsGroup> {
    public static final List<SlimefunItem> slimefunItems = new ArrayList<>();

    private static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();

    static {
        JustEnoughGuide.runLater(
            () -> {
                boolean before = JustEnoughGuide.disableAutomaticallyLoadItems();
                try {
                    for (Material material : Material.values()) {
                        if (!material.isAir() && material.isItem() && !material.isLegacy()) {
                            slimefunItems.add(createSlimefunItem(material));
                        }
                    }
                } catch (Exception e) {
                    Debug.trace(e);
                } finally {
                    JustEnoughGuide.setAutomaticallyLoadItems(before);
                }
            }, 1L
        );
    }

    public VanillaItemsGroup(NamespacedKey key, ItemStack icon) {
        super(key, icon, Integer.MAX_VALUE);
        this.page = 1;
        this.pageMap.put(1, this);
    }

    private static VanillaItem createSlimefunItem(Material material) {
        Preconditions.checkArgument(material != null, "The material cannot be null.");
        Preconditions.checkArgument(!material.isAir(), "The material cannot be air.");
        Preconditions.checkArgument(material.isItem(), "The material must be an item.");
        Preconditions.checkArgument(!material.isLegacy(), "The material cannot be legacy.");

        VanillaItem vi = VanillaItem.create(material);
        vi.register(JustEnoughGuide.getInstance());
        return vi;
    }

    /**
     * Always returns false.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     * @return false.
     */
    @Override
    public boolean isVisible(
        final Player player,
        final PlayerProfile playerProfile,
        final SlimefunGuideMode slimefunGuideMode) {
        return true;
    }

    /**
     * Opens the group for the player.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     */
    @Override
    public void open(
        final Player player,
        final PlayerProfile playerProfile,
        final SlimefunGuideMode slimefunGuideMode) {
        playerProfile.getGuideHistory().add(this, this.page);
        this.generateMenu(player, playerProfile, slimefunGuideMode).open(player);
    }

    @Override
    public ChestMenu generateMenu(
        final Player player,
        final PlayerProfile playerProfile,
        final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("原版物品");

        Format format = Formats.sub;
        int maxPage = (slimefunItems.size() - 1) / format.getChars('i').size() + 1;
        GuideUtil.commonRender(chestMenu, format, playerProfile, player, this, this.page, maxPage);
        SlimefunGuideImplementation implementation = GuideUtil.getSlimefunGuide(slimefunGuideMode);

        List<Integer> contentSlots = Formats.sub.getChars('i');
        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < slimefunItems.size()) {
                SlimefunItem slimefunItem = slimefunItems.get(index);
                OnDisplay.Item.display(player, slimefunItem, OnDisplay.Item.Normal, implementation)
                    .at(chestMenu, contentSlots.get(i), page);
            }
        }

        return chestMenu;
    }

    /**
     * Reopens the menu for the player.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     */
    public void refresh(
        final Player player,
        final PlayerProfile playerProfile,
        final SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        this.open(player, playerProfile, slimefunGuideMode);
    }

    @Override
    public int getTier() {
        return Integer.MAX_VALUE;
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class VanillaItem extends SlimefunItem implements VanillaItemShade {
        private final ItemStack customIcon;

        public VanillaItem(SlimefunItemStack item, ItemStack customIcon) {
            super(GroupSetup.vanillaItemsGroup, item, RecipeType.NULL, new ItemStack[0], customIcon);
            this.customIcon = customIcon.clone();
        }

        public static VanillaItem create(Material material) {
            ItemStack icon = new ItemStack(material);
            try {
                // against ID machine
                return new VanillaItem(new SlimefunItemStack("αJEG_VANILLA_" + material.name(), icon.clone()), icon);
            } catch (Exception ignored) {
                return new VanillaItem(new SlimefunItemStack("JEG_VANILLA_" + material.name(), icon.clone()), icon);
            }
        }
    }
}
