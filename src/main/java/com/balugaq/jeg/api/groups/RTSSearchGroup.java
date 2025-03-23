package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.events.RTSEvents;
import com.balugaq.jeg.core.listeners.GuideListener;
import com.balugaq.jeg.core.listeners.RTSListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.Getter;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unused")
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
@Getter
public class RTSSearchGroup extends FlexItemGroup {
    public static final ItemStack PLACEHOLDER = Converter.getItem(Converter.getItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "&a", "&a", "&a"), meta -> {
        meta.getPersistentDataContainer().set(RTSListener.FAKE_ITEM_KEY, PersistentDataType.STRING, "____JEG_FAKE_ITEM____");
    });
    // Use RTS_SEARCH_GROUPS, RTS_PAGES, RTS_PLAYERS or RTS_SEARCH_TERMS must be by keyword "synchronized"
    public static final Map<Player, SearchGroup> RTS_SEARCH_GROUPS = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> RTS_PAGES = new ConcurrentHashMap<>();
    public static final Map<Player, AnvilInventory> RTS_PLAYERS = new ConcurrentHashMap<>();
    public static final Map<Player, String> RTS_SEARCH_TERMS = new ConcurrentHashMap<>();
    public static final ItemStack INPUT_TEXT_ICON = Lang.INPUT_TEXT_ICON;

    public static final Function<Player, ItemStack> BACK_ICON = SlimefunOfficialSupporter::getBackButton;
    public static final ItemStack AIR_ICON = new ItemStack(Material.AIR);
    private static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();

    static {
        Bukkit.getScheduler().runTaskTimer(SearchGroup.JAVA_PLUGIN, () -> {
            Map<Player, AnvilInventory> copy;
            synchronized (RTS_PLAYERS) {
                copy = new HashMap<>(RTS_PLAYERS);
            }

            Map<Player, String> searchTermCopy;
            synchronized (RTS_SEARCH_TERMS) {
                searchTermCopy = new HashMap<>(RTS_SEARCH_TERMS);
            }

            Map<Player, String> writes = new HashMap<>();
            copy.forEach((player, inventory) -> {
                if (inventory == null) {
                    return;
                }
                InventoryView view = player.getOpenInventory();
                Inventory openingInventory = view.getTopInventory();
                if (openingInventory instanceof AnvilInventory anvilInventory && openingInventory.equals(inventory)) {
                    String oldSearchTerm = searchTermCopy.get(player);
                    String newSearchTerm = anvilInventory.getRenameText();
                    if (oldSearchTerm == null || newSearchTerm == null) {
                        writes.put(player, newSearchTerm);
                        return;
                    }

                    if (!oldSearchTerm.equals(newSearchTerm)) {
                        writes.put(player, newSearchTerm);
                        RTSEvents.SearchTermChangeEvent event = new RTSEvents.SearchTermChangeEvent(player, view, anvilInventory, oldSearchTerm, newSearchTerm, GuideListener.guideModeMap.get(player));
                        Bukkit.getPluginManager().callEvent(event);
                    }
                }
            });

            writes.forEach((player, searchTerm) -> {
                if (player != null && searchTerm != null) {
                    synchronized (RTS_SEARCH_TERMS) {
                        RTS_SEARCH_TERMS.put(player, searchTerm);
                    }
                }
            });
        }, 1, 4);
    }

    private final AnvilInventory anvilInventory;
    private final String presetSearchTerm;
    private final int page;

    public RTSSearchGroup(AnvilInventory anvilInventory, String presetSearchTerm) {
        this(anvilInventory, presetSearchTerm, 1);
    }

    public RTSSearchGroup(AnvilInventory anvilInventory, String presetSearchTerm, int page) {
        super(new NamespacedKey(JAVA_PLUGIN, "jeg_rts_search_group_" + UUID.randomUUID()), new ItemStack(Material.BARRIER));
        this.anvilInventory = anvilInventory;
        this.presetSearchTerm = presetSearchTerm;
        this.page = page;
    }

    public static Inventory newRTSInventoryFor(Player player, SlimefunGuideMode guideMode) {
        return newRTSInventoryFor(player, guideMode, null);
    }

    public static Inventory newRTSInventoryFor(Player player, SlimefunGuideMode guideMode, String presetSearchTerm) {
        return newRTSInventoryFor(player, guideMode, null, null, presetSearchTerm);
    }

    public static Inventory newRTSInventoryFor(Player player, SlimefunGuideMode guideMode, @Nullable BiConsumer<Integer, AnvilGUI.StateSnapshot> clickHandler, int @Nullable [] slots, @Nullable String presetSearchTerm) {
        AnvilGUI.Builder builder = new AnvilGUI.Builder()
                .plugin(SearchGroup.JAVA_PLUGIN)
                .itemLeft(BACK_ICON.apply(player))
                .itemRight(Lang.INPUT_TEXT_ICON)
                .itemOutput(AIR_ICON)
                .text("")
                .title(Lang.getGuideMessage("rts-title"))
                .onClose((stateSnapshot) -> {
                    RTSEvents.CloseRTSEvent event = new RTSEvents.CloseRTSEvent(player, stateSnapshot, guideMode);
                    Bukkit.getPluginManager().callEvent(event);
                });
        if (clickHandler != null) {
            builder.onClickAsync((slot, stateSnapshot) -> CompletableFuture.supplyAsync(() -> {
                if (slots != null) {
                    for (int s : slots) {
                        if (s == slot) {
                            return List.of(AnvilGUI.ResponseAction.run(() -> {
                                RTSEvents.ClickAnvilItemEvent event = new RTSEvents.ClickAnvilItemEvent(player, stateSnapshot, slot, guideMode);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    clickHandler.accept(s, stateSnapshot);
                                }
                            }));
                        }
                    }
                }
                return Collections.emptyList();
            }));
        } else {
            builder.onClickAsync((slot, stateSnapshot) -> CompletableFuture.supplyAsync(Collections::emptyList));
        }

        if (presetSearchTerm != null) {
            builder.text(presetSearchTerm);
        }

        Inventory inventory = builder.open(player).getInventory();
        if (inventory instanceof AnvilInventory anvilInventory) {
            RTSEvents.OpenRTSEvent event = new RTSEvents.OpenRTSEvent(player, anvilInventory, guideMode, presetSearchTerm);
            Bukkit.getPluginManager().callEvent(event);
        }
        return inventory;
    }

    @Override
    public boolean isVisible(@NotNull Player player, @NotNull PlayerProfile playerProfile, @NotNull SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    @Override
    public void open(@NotNull Player player, @NotNull PlayerProfile playerProfile, @NotNull SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        newRTSInventoryFor(player, slimefunGuideMode, (s, stateSnapshot) -> {
            if (s == AnvilGUI.Slot.INPUT_LEFT) {
                PlayerProfile profile = PlayerProfile.find(player).orElse(null);
                if (profile == null) {
                    return;
                }
                // back button clicked
                GuideHistory history = profile.getGuideHistory();
                history.goBack(Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode));
                return;
            } else if (s == AnvilGUI.Slot.INPUT_RIGHT) {
                // previous page button clicked
                SearchGroup rts = RTS_SEARCH_GROUPS.get(player);
                if (rts != null) {
                    int oldPage = RTS_PAGES.getOrDefault(player, 1);
                    int newPage = Math.max(1, oldPage - 1);
                    RTSEvents.PageChangeEvent event = new RTSEvents.PageChangeEvent(player, RTS_PLAYERS.get(player), oldPage, newPage, slimefunGuideMode);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        synchronized (RTS_PAGES) {
                            RTS_PAGES.put(player, newPage);
                        }
                    }
                }
            } else if (s == AnvilGUI.Slot.OUTPUT) {
                // next page button clicked
                SearchGroup rts = RTS_SEARCH_GROUPS.get(player);
                if (rts != null) {
                    int oldPage = RTS_PAGES.getOrDefault(player, 1);
                    int newPage = Math.min((rts.slimefunItemList.size() - 1) / RTSListener.FILL_ORDER.length + 1, oldPage + 1);
                    RTSEvents.PageChangeEvent event = new RTSEvents.PageChangeEvent(player, RTS_PLAYERS.get(player), oldPage, newPage, slimefunGuideMode);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        synchronized (RTS_PAGES) {
                            RTS_PAGES.put(player, newPage);
                        }
                    }
                }
            }
        }, new int[]{AnvilGUI.Slot.INPUT_LEFT, AnvilGUI.Slot.INPUT_RIGHT, AnvilGUI.Slot.OUTPUT}, presetSearchTerm);
        synchronized (RTS_PAGES) {
            RTS_PAGES.put(player, this.page);
        }
        RTSEvents.PageChangeEvent event = new RTSEvents.PageChangeEvent(player, RTS_PLAYERS.get(player), page, page, slimefunGuideMode);
        Bukkit.getPluginManager().callEvent(event);
    }
}
