package com.balugaq.jeg.implementation.items;

import com.balugaq.jeg.api.groups.HiddenItemsGroup;
import com.balugaq.jeg.api.groups.NexcavateItemsGroup;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.SlimefunItemUtil;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

/**
 * This class is responsible for registering all the JEG groups.
 *
 * @author balugaq
 * @since 1.3
 */
public class GroupSetup {
    public static JEGGuideGroup guideGroup;
    public static HiddenItemsGroup hiddenItemsGroup;
    public static NexcavateItemsGroup nexcavateItemsGroup;

    public static void setup() {
        guideGroup = new JEGGuideGroup(
                new NamespacedKey(JustEnoughGuide.getInstance(), "jeg_guide_group"),
                new CustomItemStack(Material.KNOWLEDGE_BOOK, "&bJEG Usage Guide"));
        guideGroup.register(JustEnoughGuide.getInstance());
        hiddenItemsGroup = new HiddenItemsGroup(
                new NamespacedKey(JustEnoughGuide.getInstance(), "hidden_items_group"),
                new CustomItemStack(Material.BARRIER, "&cHidden Items"));
        hiddenItemsGroup.register(JustEnoughGuide.getInstance());
        if (SpecialMenuProvider.ENABLED_Nexcavate) {
            nexcavateItemsGroup = new NexcavateItemsGroup(
                    new NamespacedKey(JustEnoughGuide.getInstance(), "nexvacate_items_group"),
                    new CustomItemStack(Material.BLACKSTONE, "&6Nexvacate Items"));
            nexcavateItemsGroup.register(JustEnoughGuide.getInstance());
        }
    }

    public static void shutdown() {
        SlimefunItemUtil.unregisterItemGroup(guideGroup);
        SlimefunItemUtil.unregisterItemGroup(hiddenItemsGroup);
        if (SpecialMenuProvider.ENABLED_Nexcavate) {
            SlimefunItemUtil.unregisterItemGroup(nexcavateItemsGroup);
        }
    }
}
