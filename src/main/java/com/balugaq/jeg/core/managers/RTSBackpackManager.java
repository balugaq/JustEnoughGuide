package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.implementation.JustEnoughGuide;
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

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Getter
public class RTSBackpackManager extends AbstractManager {
    private static final int IDENTIFIER_SLOT = 53;
    @Deprecated
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

    public void saveInventoryBackupFor(Player player) {
        PlayerProfile profile = PlayerProfile.find(player).orElse(null);
        if (profile == null) {
            return;
        }

        PlayerBackpack b = null;
        // find an existing backpack
        Collection<PlayerBackpack> backpacks = profile.getPlayerData().getBackpacks().values();
        if (backpacks != null && !backpacks.isEmpty()) {
            for (PlayerBackpack backpack : backpacks) {
                Inventory inventory = backpack.getInventory();
                if (inventory.getContents().length < 54) {
                    continue;
                }

                // check if identifier is valid
                ItemStack identifierItem = inventory.getItem(IDENTIFIER_SLOT);
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

        // create a new backpack
        if (b == null) {
            b = profile.createBackpack(54);
        }
        Inventory inventory = b.getInventory();
        setIdentifier(player, inventory, IDENTIFIER_SLOT, true);
        ItemStack[] contents = getStorageContents(player.getInventory());
        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            inventory.setItem(i, itemStack);
        }
        profile.save();
    }

    public void clearInventoryFor(Player player) {
        ItemStack[] newContents = new ItemStack[player.getInventory().getStorageContents().length];
        for (int i = 0; i < newContents.length; i++) {
            newContents[i] = new ItemStack(Material.AIR);
        }
        player.getInventory().setStorageContents(newContents);
    }

    public void restoreInventoryFor(Player player) {
        PlayerProfile profile = PlayerProfile.find(player).orElse(null);
        if (profile == null) {
            return;
        }

        Collection<PlayerBackpack> backpacks = profile.getPlayerData().getBackpacks().values();
        if (backpacks == null || backpacks.isEmpty()) {
            return;
        }

        for (PlayerBackpack backpack : backpacks) {
            Inventory inventory = backpack.getInventory();
            ItemStack[] contents = inventory.getContents();
            if (contents.length < 54) {
                continue;
            }

            ItemStack identifierItem = inventory.getItem(IDENTIFIER_SLOT);
            if (identifierItem == null || identifierItem.getType() == Material.AIR) {
                continue;
            }

            if (!isIdentifier(identifierItem, player)) {
                continue;
            }

            if (!isOpenIdentifier(identifierItem)) {
                continue;
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
                }
            }
            setIdentifier(player, backpack.getInventory(), IDENTIFIER_SLOT, false);
            profile.save();

            break;
        }
    }

    public void setIdentifier(Player player, Inventory inventory, int slot, boolean open) {
        inventory.setItem(slot, getIdentifierItem(player, open));
    }

    public ItemStack getIdentifierItem(Player player, boolean open) {
        return new CustomItemStack(new CustomItemStack(Material.DIRT, "[RTS]", "[RTS]", "[RTS]", "[RTS]", UUID.randomUUID().toString()), meta -> {
            meta.getPersistentDataContainer().set(OWNER_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
            meta.getPersistentDataContainer().set(SERVER_KEY, PersistentDataType.STRING, JustEnoughGuide.getServerUUID().toString());
            if (open) {
                meta.getPersistentDataContainer().set(STATUS_KEY, PersistentDataType.STRING, OPEN_STATUS);
            } else {
                meta.getPersistentDataContainer().set(STATUS_KEY, PersistentDataType.STRING, CLOSE_STATUS);
            }
        });
    }

    public boolean isIdentifier(ItemStack item, Player player) {
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

    public boolean isOpenIdentifier(ItemStack item) {
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

    public static ItemStack[] getStorageContents(PlayerInventory inventory) {
        ItemStack[] contents = new ItemStack[36];
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = inventory.getItem(i);
            contents[i] = itemStack;
        }
        return contents;
    }
}
