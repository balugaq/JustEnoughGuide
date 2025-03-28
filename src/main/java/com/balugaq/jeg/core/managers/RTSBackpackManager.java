package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@Getter
public class RTSBackpackManager extends AbstractManager {
    private static final int IDENTIFIER_SLOT = 53;
    private static final String BACKPACK_NAME = "JEGRTSBackpack";
    private static final NamespacedKey STATUS_KEY = new NamespacedKey(JustEnoughGuide.getInstance(), "status");
    private static final String OPEN_STATUS = "open";
    private static final String CLOSE_STATUS = "close";
    private final @NotNull Plugin plugin;
    private final @NotNull NamespacedKey SERVER_KEY;
    private final @NotNull NamespacedKey OWNER_KEY;

    public RTSBackpackManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.SERVER_KEY = new NamespacedKey(plugin, "server");
        this.OWNER_KEY = new NamespacedKey(plugin, "owner");
    }

    public static ItemStack @NotNull [] getStorageContents(@NotNull PlayerInventory inventory) {
        ItemStack[] contents = new ItemStack[36];
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = inventory.getItem(i);
            contents[i] = itemStack;
        }
        return contents;
    }

    public void saveInventoryBackupFor(@NotNull Player player) {
        PlayerProfile profile = PlayerProfile.find(player).orElse(null);
        if (profile == null) {
            return;
        }

        PlayerBackpack b = null;
        // find an existing backpack
        Set<PlayerBackpack> backpacks = Slimefun.getDatabaseManager().getProfileDataController().getBackpacks(player.getUniqueId().toString());
        if (backpacks != null && !backpacks.isEmpty()) {
            for (PlayerBackpack backpack : backpacks) {
                if (backpack.getName().equals(BACKPACK_NAME)) {
                    // check if identifier is valid
                    ItemStack identifierItem = backpack.getInventory().getItem(IDENTIFIER_SLOT);
                    if (identifierItem == null || identifierItem.getType() == Material.AIR) {
                        continue;
                    }
                    if (!isIdentifier(identifierItem, player)) {
                        continue;
                    }

                    b = backpack;
                    break;
                }
            }
        }

        // create a new backpack
        if (b == null) {
            b = Slimefun.getDatabaseManager().getProfileDataController().createBackpack(player, BACKPACK_NAME, profile.nextBackpackNum(), 54);
        }
        Inventory inventory = b.getInventory();
        setIdentifier(player, inventory, IDENTIFIER_SLOT, true);
        Slimefun.getDatabaseManager().getProfileDataController().saveBackpackInventory(b, IDENTIFIER_SLOT);
        ItemStack[] contents = getStorageContents(player.getInventory());
        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            inventory.setItem(i, itemStack);
            Slimefun.getDatabaseManager().getProfileDataController().saveBackpackInventory(b, i);
        }
        b.setInventory(inventory);
    }

    public void clearInventoryFor(@NotNull Player player) {
        ItemStack[] newContents = new ItemStack[player.getInventory().getStorageContents().length];
        for (int i = 0; i < newContents.length; i++) {
            newContents[i] = new ItemStack(Material.AIR);
        }
        player.getInventory().setStorageContents(newContents);
    }

    public void restoreInventoryFor(@NotNull Player player) {
        PlayerProfile profile = PlayerProfile.find(player).orElse(null);
        if (profile == null) {
            return;
        }

        Set<PlayerBackpack> backpacks = Slimefun.getDatabaseManager().getProfileDataController().getBackpacks(profile.getUUID().toString());
        if (backpacks == null || backpacks.isEmpty()) {
            return;
        }

        for (PlayerBackpack backpack : backpacks) {
            if (backpack.getName().equals(BACKPACK_NAME)) {
                Inventory inventory = backpack.getInventory();
                ItemStack[] contents = inventory.getContents();

                ItemStack identifierItem = contents[IDENTIFIER_SLOT];
                if (identifierItem == null || identifierItem.getType() == Material.AIR) {
                    continue;
                }

                if (!isIdentifier(identifierItem, player)) {
                    continue;
                }

                if (!isOpenIdentifier(identifierItem)) {
                    return;
                }

                // found the backpack, now restore it
                for (int i = 0; i < 36; i++) {
                    ItemStack itemStack = contents[i];
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        player.getInventory().setItem(i, new ItemStack(Material.AIR));
                        continue;
                    }
                    player.getInventory().setItem(i, itemStack.clone());
                }

                // clear the backpack
                for (int i = 0; i < contents.length; i++) {
                    if (i != IDENTIFIER_SLOT) {
                        backpack.getInventory().setItem(i, new ItemStack(Material.AIR));
                        Slimefun.getDatabaseManager().getProfileDataController().saveBackpackInventory(backpack, i);
                    }
                }
                setIdentifier(player, backpack.getInventory(), IDENTIFIER_SLOT, false);
                Slimefun.getDatabaseManager().getProfileDataController().saveBackpackInventory(backpack, IDENTIFIER_SLOT);

                break;
            }
        }
    }

    public void setIdentifier(@NotNull Player player, @NotNull Inventory inventory, int slot, boolean open) {
        inventory.setItem(slot, getIdentifierItem(player, open));
    }

    public @NotNull ItemStack getIdentifierItem(@NotNull Player player, boolean open) {
        return Converter.getItem(Converter.getItem(Material.DIRT, "[RTS]", "[RTS]", "[RTS]", "[RTS]", UUID.randomUUID().toString()), meta -> {
            meta.getPersistentDataContainer().set(OWNER_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
            meta.getPersistentDataContainer().set(SERVER_KEY, PersistentDataType.STRING, JustEnoughGuide.getServerUUID().toString());
            if (open) {
                meta.getPersistentDataContainer().set(STATUS_KEY, PersistentDataType.STRING, OPEN_STATUS);
            } else {
                meta.getPersistentDataContainer().set(STATUS_KEY, PersistentDataType.STRING, CLOSE_STATUS);
            }
        });
    }

    public boolean isIdentifier(@Nullable ItemStack item, @NotNull Player player) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        String owner = item.getItemMeta().getPersistentDataContainer().get(OWNER_KEY, PersistentDataType.STRING);
        if (owner == null) {
            return false;
        }

        if (!owner.equals(player.getUniqueId().toString())) {
            return false;
        }

        String serverUUID = item.getItemMeta().getPersistentDataContainer().get(SERVER_KEY, PersistentDataType.STRING);
        if (serverUUID == null) {
            return false;
        }

        if (!serverUUID.equals(JustEnoughGuide.getServerUUID().toString())) {
            return false;
        }

        return true;
    }

    public boolean isOpenIdentifier(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        String status = item.getItemMeta().getPersistentDataContainer().get(STATUS_KEY, PersistentDataType.STRING);
        if (status == null) {
            return false;
        }

        if (status.equals(OPEN_STATUS)) {
            return true;
        }

        return false;
    }
}
