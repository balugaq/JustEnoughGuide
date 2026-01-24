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

import com.balugaq.jeg.api.objects.enums.HUDLocation;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.MinecraftVersion;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.platform.PlatformUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.schntgaispock.slimehud.SlimeHUD;
import io.github.schntgaispock.slimehud.util.Util;
import io.github.schntgaispock.slimehud.waila.HudRequest;
import io.github.schntgaispock.slimehud.waila.PlayerWAILA;
import io.github.schntgaispock.slimehud.waila.WAILAManager;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBarViewer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"deprecation", "ConstantValue"})
@NullMarked
public class JEGPlayerWAILA extends PlayerWAILA {
    public static final boolean IS_1_20_1 =
            JustEnoughGuide.getMinecraftVersion().isAtLeast(MinecraftVersion.V1_20_1);
    public static final long TICK_RATE = SlimeHUD.getInstance().getConfig().getLong("waila.tick-rate");
    public final Supplier<BossBar> kyoriBossBarSupplier;
    public boolean visible;
    public @Nullable BossBar kyoriBossBar = null;

    @SuppressWarnings("DataFlowIssue")
    public JEGPlayerWAILA(Player player, @Nullable PlayerWAILA waila) {
        super(player);
        getWAILABar().removePlayer(player);
        kyoriBossBarSupplier = () -> {
            if (kyoriBossBar == null) {
                if (PlatformUtil.isPaper() && IS_1_20_1) {
                    String bossbarColor =
                            SlimeHUD.getInstance().getConfig().getString("waila.bossbar-color").trim().toLowerCase();
                    kyoriBossBar = BossBar.bossBar(
                            Component.text(""), 1.0f, toBossBarColor(bossbarColor),
                            BossBar.Overlay.PROGRESS, new HashSet<>()
                    );
                    return (BossBar) kyoriBossBar;
                } else {
                    return null;
                }
            } else {
                return (BossBar) kyoriBossBar;
            }
        };

        if (waila != null) {
            setVisible(getWAILABar().isVisible());
            setColor(getWAILABar().getColor());
            setTitle(Component.text(getWAILABar().getTitle()));
            setPaused(waila.isPaused());
        }
    }

    public static BossBar.Color toBossBarColor(String color) {
        return switch (color.trim().toLowerCase()) {
            case "red", "yellow", "green", "blue", "purple", "pink", "white" ->
                    BossBar.Color.valueOf(color.toUpperCase());
            case "default", "inherit" -> BossBar.Color.WHITE;
            default -> {
                SlimeHUD.log(
                        Level.WARNING, "[SlimeHUD] Invalid bossbar color: " + color, "[SlimeHUD] Setting color " +
                                "to white..."
                );
                yield BossBar.Color.WHITE;
            }
        };
    }

    public void setColor(BarColor color) {
        getWAILABar().setColor(color);
        if (kyoriBossBarSupplier.get() != null) {
            kyoriBossBarSupplier.get().color(BossBar.Color.valueOf(color.name()));
        }
    }

    public void setTitle(Component title) {
        if (title instanceof net.kyori.adventure.text.TextComponent tc) {
            getWAILABar().setTitle(tc.content());
        }
        if (kyoriBossBarSupplier.get() != null) {
            kyoriBossBarSupplier.get().name(title);
        }
    }

    public static void wrap(Player player) {
        synchronized (getWailaMap()) {
            getWailaMap().compute(
                    player.getUniqueId(), (k, waila) -> runTaskAsync(
                            JustEnoughGuide.getInstance(),
                            JEGPlayerWAILA.wrap(player, waila)
                    )
            );
        }
    }

    public static Map<UUID, PlayerWAILA> getWailaMap() {
        return WAILAManager.getInstance().getWailas();
    }

    @UnknownNullability
    @Contract("_, null -> null; _, !null -> !null")
    public static <T extends BukkitRunnable> T runTaskAsync(Plugin plugin, @Nullable T runnable) {
        if (runnable == null) {
            return null;
        }
        runnable.runTaskTimerAsynchronously(plugin, 0L, TICK_RATE);
        return runnable;
    }

    public static JEGPlayerWAILA wrap(Player player, @Nullable PlayerWAILA waila) {
        boolean f = waila != null && !waila.getClass().getSimpleName().equals("JEGPlayerWAILA");

        if (waila instanceof JEGPlayerWAILA jpw) {
            return jpw;
        }
        JEGPlayerWAILA v2 = new JEGPlayerWAILA(player, waila);
        if (f) {
            waila.setVisible(false);
            waila.setPaused(true);
        }
        return v2;
    }

    public static void onDisable() {
        synchronized (getWailaMap()) {
            for (Map.Entry<UUID, PlayerWAILA> entry : getWailaMap().entrySet()) {
                getWailaMap().compute(
                        entry.getKey(), (k, waila) -> runTaskAsync(
                                SlimeHUD.getInstance(),
                                JEGPlayerWAILA.unwrap(waila)
                        )
                );
            }
        }
    }

    @SuppressWarnings({"DataFlowIssue", "unchecked"})
    public static PlayerWAILA unwrap(@Nullable PlayerWAILA waila) {
        if (waila == null) {
            return null;
        }

        if (waila.getClass().getSimpleName().equals("JEGPlayerWAILA")) {
            Supplier<BossBar> kyoriBossBarSupplier = ReflectionUtil.getValue(
                    waila, "kyoriBossBarSupplier",
                    Supplier.class
            );
            for (BossBarViewer bbv : kyoriBossBarSupplier.get().viewers()) {
                if (bbv instanceof Audience adn) {
                    kyoriBossBarSupplier.get().removeViewer(adn);
                }
            }
            PlayerWAILA v2 = (PlayerWAILA) ReflectionUtil.invokeMethod(waila, "toOrigin");
            waila.cancel();
            return v2;
        }

        return waila;
    }

    @SuppressWarnings({"RedundantLabeledSwitchRuleCodeBlock", "DataFlowIssue"})
    public void run() {
        updateFacing0();

        if (isPaused()) {
            return;
        }

        String facing = getFacing();

        ReflectionUtil.setValue(this, "previousFacing", facing);
        // JEG Patch start - Player custom HUD location
        HUDLocation hudLocation = HUDMachineInfoLocationGuideOption.getSelectedOption(getPlayer());
        if (hudLocation == HUDLocation.DEFAULT) {
            switch (this.WAILALocation0()) {
                case "bossbar" -> {
                    bossbar(facing);
                }
                case "hotbar", "actionbar" -> {
                    actionbar(facing);
                }
            }
        } else {
            if (hudLocation == HUDLocation.BOSSBAR) {
                bossbar(facing);
            } else if (hudLocation == HUDLocation.ACTION_BAR) {
                actionbar(facing);
            }
        }
        // JEG Patch end - Player custom HUD location
    }

    public void updateFacing0() {
        Block targetBlock = getPlayer().getTargetBlockExact(HUDReachBlockGuideOption.getReachBlock(getPlayer()));
        if (targetBlock == null || targetBlock.getType().isAir()) {
            clearFacing0();
            return;
        }

        SlimefunItem item = StorageCacheUtils.getSfItem(targetBlock.getLocation());
        if (item == null) {
            if (VanillaBlockHUDDisplayGuideOption.isEnabled(getPlayer())) {
                ReflectionUtil.setValue(this, "facingBlock", SlimeHUDIntegrationMain.getVanillaBlockName(getPlayer(), targetBlock));
                ReflectionUtil.setValue(this, "facingBlockInfo", "");
                ReflectionUtil.setValue(
                        this, "facing", ChatColor.translateAlternateColorCodes(
                                '&',
                                getFacingBlock() + (getFacingBlockInfo().isEmpty() ? "" : " &7| " + getFacingBlockInfo())
                        )
                );
                return;
            } else {
                clearFacing0();
                return;
            }
        }

        Location target = targetBlock.getLocation();
        HudRequest request = new HudRequest(item, target, getPlayer());
        ReflectionUtil.setValue(this, "facingBlock", SlimeHUD.getTranslationManager().getItemName(getPlayer(), item));
        ReflectionUtil.setValue(this, "facingBlockInfo", SlimeHUD.getHudController().processRequest(request));
        ReflectionUtil.setValue(
                this, "facing", ChatColor.translateAlternateColorCodes(
                        '&',
                        getFacingBlock() + (getFacingBlockInfo().isEmpty() ? "" : " &7| " + getFacingBlockInfo())
                )
        );
    }

    public @Nullable String WAILALocation0() {
        return ReflectionUtil.getValue(this, "WAILALocation", String.class);
    }

    public void bossbar(String facing) {
        if (facing.isEmpty()) {
            setVisible(false);
        } else {
            setVisible(true);
            setTitle(keepTextColors0() ? LegacyComponentSerializer.legacySection().deserialize(facing) :
                             Component.text(ChatColor.stripColor(facing)));
            if (useAutoBossBarColor0()) {
                setColor(Util.pickBarColorFromName(facing));
            }
        }
    }

    public void actionbar(String facing) {
        setVisible(false); // Hide the BossBar
        if (facing.isEmpty()) return;
        if (PlatformUtil.isPaper()) {
            getPlayer().sendActionBar(LegacyComponentSerializer.legacySection().deserialize(facing));
        } else {
            getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(facing));
        }
    }

    public void clearFacing0() {
        ReflectionUtil.setValue(this, "facingBlock", "");
        ReflectionUtil.setValue(this, "facingBlockInfo", null);
        ReflectionUtil.setValue(this, "facing", "");
    }

    @SuppressWarnings("DataFlowIssue")
    public boolean keepTextColors0() {
        return ReflectionUtil.getValue(this, "keepTextColors", boolean.class);
    }

    @SuppressWarnings("DataFlowIssue")
    public boolean useAutoBossBarColor0() {
        return ReflectionUtil.getValue(this, "useAutoBossBarColor", boolean.class);
    }

    public PlayerWAILA setVisible(boolean visible) {
        this.visible = visible;
        if (kyoriBossBarSupplier.get() != null) {
            getWAILABar().setVisible(false);
            if (visible) {
                for (BossBar bar : getPlayer().activeBossBars()) {
                    if (bar == kyoriBossBarSupplier.get()) {
                        // skip
                        return this;
                    }
                }

                kyoriBossBarSupplier.get().addViewer(getPlayer());
            } else {
                kyoriBossBarSupplier.get().removeViewer(getPlayer());
            }
        } else {
            getWAILABar().setVisible(visible);
        }
        return this;
    }

    @SuppressWarnings("unused")
    public void setColor(BossBar.Color color) {
        getWAILABar().setColor(toBarColor(color));
        if (kyoriBossBarSupplier.get() != null) {
            kyoriBossBarSupplier.get().color(BossBar.Color.valueOf(color.name()));
        }
    }

    public static BarColor toBarColor(BossBar.Color color) {
        return BarColor.valueOf(color.name());
    }

    @SuppressWarnings("unused")
    public PlayerWAILA toOrigin() {
        PlayerWAILA waila = new PlayerWAILA(getPlayer());
        waila.setPaused(isPaused());
        org.bukkit.boss.BossBar bukkit = getWAILABar();
        BossBar kyori = kyoriBossBarSupplier.get();
        if (kyori != null) {
            bukkit.setTitle(((net.kyori.adventure.text.TextComponent) kyori.name()).content());
            bukkit.setColor(toBarColor(kyori.color()));
        }
        bukkit.setVisible(visible);
        return waila;
    }

    @Override
    public void cancel() {
        setVisible(false);
    }
}
