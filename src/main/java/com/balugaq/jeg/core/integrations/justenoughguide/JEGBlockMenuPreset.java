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

package com.balugaq.jeg.core.integrations.justenoughguide;

import com.balugaq.jeg.api.recipe_complete.RecipeCompletableAdapter;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

/**
 * @author balugaq
 * @since 2.1
 */
@EqualsAndHashCode(callSuper = true)
@NullMarked
@Data
public class JEGBlockMenuPreset extends BlockMenuPreset {
    private BlockMenuPreset preset;

    public JEGBlockMenuPreset(BlockMenuPreset preset) {
        super(preset.getID(), preset.getTitle());
        this.preset = preset;
        this.occupiedSlots.addAll(getPresetSlots());
        ReflectionUtil.setValue(this, "items", ReflectionUtil.getValue(preset, "items"));
        this.inventory = preset.getInventory();
    }

    @Override
    public void newInstance(BlockMenu menu, Block b) {
        this.preset.newInstance(menu, b);
        if (Bukkit.getPluginManager().isPluginEnabled("JustEnoughGuide")) {
            if (Slimefun.getWorldSettingsService().isWorldEnabled(b.getWorld())) {
                RecipeCompletableAdapter.tryAutoAddJEGButton(getSlimefunItem(), menu);
            }
        }
    }

    @Override
    public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
        return this.preset.getSlotsAccessedByItemTransport(menu, flow, item);
    }

    @Override
    public String getTitle() {
        return this.preset.getTitle();
    }

    @Override
    public int getSize() {
        return this.preset.getSize();
    }

    @Override
    public Set<Integer> getPresetSlots() {
        return this.preset.getPresetSlots();
    }

    @Override
    public Set<Integer> getInventorySlots() {
        return this.preset.getInventorySlots();
    }

    @Override
    public SlimefunItem getSlimefunItem() {
        return this.preset.getSlimefunItem();
    }

    @Override
    public void init() {
        // It is called before set preset, so add a check and call it manually
        if (this.preset != null) {
            this.preset.init();
        }
    }

    @Override
    public boolean canOpen(Block b, Player p) {
        return p.isOp() || p.hasPermission("slimefun.inventory.bypass") || this.preset.canOpen(b, p);
    }

    @Override
    public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
        return this.preset.getSlotsAccessedByItemTransport(flow);
    }

    @Override
    public MenuOpeningHandler getMenuOpeningHandler() {
        return this.preset.getMenuOpeningHandler();
    }

    @Override
    public MenuCloseHandler getMenuCloseHandler() {
        return this.preset.getMenuCloseHandler();
    }

    @Override
    public MenuClickHandler getMenuClickHandler(int slot) {
        return this.preset.getMenuClickHandler(slot);
    }

    @Override
    public MenuClickHandler getPlayerInventoryClickHandler() {
        return this.preset.getPlayerInventoryClickHandler();
    }

    @Override
    public boolean isEmptySlotsClickable() {
        return this.preset.isEmptySlotsClickable();
    }

    @Override
    public boolean isPlayerInventoryClickable() {
        return this.preset.isPlayerInventoryClickable();
    }

    @Override
    public Inventory toInventory() {
        return this.preset.toInventory();
    }

    @Override
    public boolean isSizeAutomaticallyInferred() {
        return this.preset.isSizeAutomaticallyInferred();
    }
}
