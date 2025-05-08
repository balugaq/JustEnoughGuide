package com.balugaq.jeg.utils.clickhandler;

import com.balugaq.jeg.api.objects.ExtendedClickHandler;
import com.balugaq.jeg.implementation.guide.SurvivalGuideImplementation;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("deprecation")
public class GroupLinker {
    public static void applyGroupLinker(SlimefunGuideImplementation guide, ChestMenu menu, int slot) {
        ChestMenu.MenuClickHandler origin = menu.getMenuClickHandler(slot);
        if (origin instanceof GroupLinkClickHandler) {
            return;
        }

        menu.addMenuClickHandler(slot, (GroupLinkClickHandler) (player, clickedSlot, clickedItem, action) -> {
            if (!action.isRightClicked() && action.isShiftClicked()) {
                // Open the item's item group if exists
                final SlimefunItem sfItem = SlimefunItem.getByItem(clickedItem);
                if (sfItem != null) {
                    final ItemGroup itemGroup = sfItem.getItemGroup();
                    if (itemGroup != null) {
                        AtomicInteger page = new AtomicInteger(1);
                        if (SurvivalGuideImplementation.isTaggedGroupType(itemGroup)) {
                            page.set((itemGroup.getItems().indexOf(sfItem) / 36) + 1);
                        }
                        PlayerProfile.get(player, profile -> guide.openItemGroup(profile, itemGroup, page.get()));
                        return false;
                    }
                }
            }

            // call origin handler
            if (origin != null) {
                return origin.onClick(player, clickedSlot, clickedItem, action);
            } else {
                return false;
            }
        });
    }

    public interface GroupLinkClickHandler extends ExtendedClickHandler {
    }
}
