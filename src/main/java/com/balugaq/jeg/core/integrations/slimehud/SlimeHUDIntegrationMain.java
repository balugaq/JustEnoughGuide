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

package com.balugaq.jeg.core.integrations.slimehud;

import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.MinecraftVersion;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Comparator;
import org.bukkit.block.data.type.DaylightDetector;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.block.data.type.Jukebox;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.block.data.type.SculkShrieker;
import org.bukkit.block.data.type.TNT;
import org.bukkit.block.data.type.TrialSpawner;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.block.data.type.Vault;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.9
 */
@NullMarked
public class SlimeHUDIntegrationMain implements Integration {

    @Override
    public String getHookPlugin() {
        return "SlimeHUD";
    }

    @Override
    public void onEnable() {
        JEGGuideSettings.addOption(HUDMachineInfoLocationGuideOption.instance());
        JEGGuideSettings.addOption(VanillaBlockHUDDisplayGuideOption.instance());
        JEGGuideSettings.addOption(HUDReachBlockGuideOption.instance());
        for (Player player : Bukkit.getOnlinePlayers()) {
            JEGPlayerWAILA.wrap(player);
        }

        JustEnoughGuide.getListenerManager().registerListener(new PlayerWAILAUpdateListener());
    }

    @Override
    public void onDisable() {
        JEGPlayerWAILA.onDisable();
    }

    @SuppressWarnings({"unused", "UnstableApiUsage"})
    public static String getVanillaBlockName(Player player, Block block) {
        if (block.getType().isAir() || !block.getType().isItem()) {
            return "";
        }

        String name = "";
        String base = ItemStackHelper.getDisplayName(new ItemStack(block.getType()));
        BlockData data = block.getBlockData();
        if (data instanceof Openable d && !d.isOpen() && !(data instanceof Barrel)) {
            name += "关上的";
        }
        if (data instanceof Lightable d1 && d1.isLit()) {
            if (data instanceof Campfire d2 && d2.isSignalFire()) {
                name += "点燃的";
            } else {
                name += "点亮的";
            }
        }
        if (data instanceof Waterlogged d && d.isWaterlogged()) {
            name += "含水的";
        }
        if (data instanceof Snowable d && d.isSnowy()) {
            name += "覆雪的";
        }
        if (data instanceof Farmland d && d.getMoisture() == d.getMaximumMoisture()) {
            name += "湿润的";
        }
        if (data instanceof Powerable d && d.isPowered() && (data instanceof Repeater || data instanceof Comparator)) {
            name += "激活的";
        }
        if (data instanceof DaylightDetector d && d.isInverted()) {
            name += "夜间的";
        }
        if ((data instanceof Repeater d1 && d1.isLocked()) || (data instanceof Hopper d2 && !d2.isEnabled())) {
            name += "锁定的";
        }
        if (data instanceof Jukebox d && d.hasRecord()) {
            name += "正在播放音乐的";
        }
        if (data instanceof Piston d && d.isExtended()) {
            name += "伸长的";
        }
        if (data instanceof PistonHead d && d.isShort()) {
            name += "短的";
        }
        if (data instanceof SculkShrieker d && d.isCanSummon()) {
            name += "可召唤监守者的";
        }
        if (data instanceof SculkShrieker d && d.isShrieking()) {
            name += "正在尖啸的";
        }
        if (data instanceof TNT d && d.isUnstable()) {
            name += "不稳定的";
        }
        if (data instanceof Tripwire d && d.isDisarmed()) {
            name += "被触发的";
        }
        if (MinecraftVersion.current().isAtLeast(MinecraftVersion.V1_21)) {
            if ((data instanceof TrialSpawner d1 && d1.isOminous()) || (data instanceof Vault d2 && d2.isOminous())) {
                name += "不详的";
            }
        }
        BlockState state = block.getState(true);
        if (state instanceof Container container) {
            if (container.isLocked()) {
                name += "锁上的";
            }
        }
        name += base;
        if (data instanceof EndPortalFrame d && d.hasEye()) {
            name += " (有眼睛)";
        }
        if (state instanceof Container container) {
            Inventory inv = container.getSnapshotInventory();
            int space = 0;
            for (int i = 0; i < inv.getSize(); i++) {
                var item = inv.getItem(i);
                if (item == null || item.getType() == Material.AIR) {
                    space++;
                }
            }
            if (space == 0) {
                name += " (满)";
            } else if (space == inv.getSize()) {
                name += " (空)";
            } else {
                name += " (剩余 " + space + " 个空位)";
            }
        }
        return name;
    }
}
