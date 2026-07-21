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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.balugaq.jeg.implementation.option.delegate;

import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.api.patches.Priorities;
import com.balugaq.jeg.api.patches.PrioritySlimefunGuideOption;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerLanguageChangeEvent;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author TheBusyBiscuit
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"DataFlowIssue", "deprecation", "ConstantValue"})
@NullMarked
public class PlayerLanguageOption implements PrioritySlimefunGuideOption<String> {
    @Override
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        if (Slimefun.getLocalization().isEnabled()) {
            Language language = Slimefun.getLocalization().getLanguage(p);
            String languageName = language.isDefault() ? Slimefun.getLocalization().getMessage(
                p,
                "languages.default"
            ) + ChatColor.DARK_GRAY + " (" + language.getName(p) + ")" : Slimefun.getLocalization().getMessage(p, "languages." + language.getId());
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&e&o" + Slimefun.getLocalization().getMessage(p, "guide.work-in-progress"));
            lore.add("");
            lore.addAll(Slimefun.getLocalization().getMessages(
                p, "guide.languages.description",
                (msg) -> msg.replace(
                    "%contributors%",
                    String.valueOf(Slimefun.getGitHubService().getContributors().size())
                )
            ));
            lore.add("");
            lore.add("&7⇨ &e" + Slimefun.getLocalization().getMessage(p, "guide.languages.change"));
            ItemStack item = new CustomItemStack(
                language.getItem(), "&7" + Slimefun.getLocalization().getMessage(
                p,
                "guide.languages.selected-language"
            ) + " &a" + languageName, lore.toArray(new String[0])
            );
            return Optional.of(item);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        this.openLanguageSelection(p, guide);
    }

    @Override
    public Optional<String> getSelectedOption(Player p, ItemStack guide) {
        return Optional.of(Slimefun.getLocalization().getLanguage(p).getId());
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, @Nullable String value) {
        if (value == null) {
            PersistentDataAPI.remove(p, this.getKey());
        } else {
            PersistentDataAPI.setString(p, this.getKey(), value);
        }

    }

    private void openLanguageSelection(Player p, ItemStack guide) {
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.languages"));
        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(SoundEffect.GUIDE_LANGUAGE_OPEN_SOUND::playFor);

        for (int i = 0; i < 9; ++i) {
            if (i == 1) {
                menu.addItem(
                    1, ChestMenuUtils.getBackButton(
                        p, "", "&7" + Slimefun.getLocalization().getMessage(
                            p, "guide" +
                                ".back.settings"
                        )
                    ), (pl, slotx, item, action) -> {
                        JEGGuideSettings.openSettings(pl, guide);
                        return false;
                    }
                );
            } else if (i == 7) {
                menu.addItem(
                    7, new CustomItemStack(
                        SlimefunUtils.getCustomHead(HeadTexture.ADD_NEW_LANGUAGE.getTexture())
                        , Slimefun.getLocalization().getMessage(p, "guide.languages.translations.name"), "",
                        "&7⇨ &e" + Slimefun.getLocalization().getMessage(
                            p, "guide.languages" +
                                ".translations.lore"
                        )
                    ), (pl, slotx, item, action) -> {
                        ChatUtils.sendURL(pl, "https://slimefun-wiki.guizhanss.cn/Translating-Slimefun");
                        pl.closeInventory();
                        return false;
                    }
                );
            } else {
                menu.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }
        }

        Language defaultLanguage = Slimefun.getLocalization().getDefaultLanguage();
        String defaultLanguageString = Slimefun.getLocalization().getMessage(p, "languages.default");
        menu.addItem(
            9, new CustomItemStack(
                defaultLanguage.getItem(),
                ChatColor.GRAY + defaultLanguageString + ChatColor.DARK_GRAY + " (" + defaultLanguage.getName(p) + ")", "", "&7⇨ &e" + Slimefun.getLocalization().getMessage(p, "guide.languages.select-default")
            ), (pl, ix, item, action) -> {
                Slimefun.instance().getServer().getPluginManager().callEvent(new PlayerLanguageChangeEvent(
                    pl,
                    Slimefun.getLocalization().getLanguage(pl), defaultLanguage
                ));
                this.setSelectedOption(pl, guide, null);
                Slimefun.getLocalization().sendMessage(pl, "guide.languages.updated", (msg) -> msg.replace("%lang%", defaultLanguageString));
                JEGGuideSettings.openSettings(pl, guide);
                return false;
            }
        );
        int slot = 10;

        for (Language language : Slimefun.getLocalization().getLanguages()) {
            menu.addItem(
                slot, new CustomItemStack(
                    language.getItem(), ChatColor.GREEN + language.getName(p),
                    "&b" + language.getTranslationProgress() + "%", "",
                    "&7⇨ &e" + Slimefun.getLocalization().getMessage(
                        p, "guide.languages" +
                            ".select"
                    )
                ), (pl, ix, item, action) -> {
                    Slimefun.instance().getServer().getPluginManager().callEvent(new PlayerLanguageChangeEvent(
                        pl
                        , Slimefun.getLocalization().getLanguage(pl), language
                    ));
                    this.setSelectedOption(pl, guide, language.getId());
                    String name = language.getName(pl);
                    Slimefun.getLocalization().sendMessage(
                        pl, "guide.languages.updated", (msg) -> msg.replace(
                            "%lang%", name)
                    );
                    JEGGuideSettings.openSettings(pl, guide);
                    return false;
                }
            );
            ++slot;
        }

        menu.open(p);
    }

    @Override
    public NamespacedKey getKey() {
        return Slimefun.getLocalization().getKey();
    }

    @Override
    public int priority() {
        return Priorities.PlayerLanguageOption;
    }
}
