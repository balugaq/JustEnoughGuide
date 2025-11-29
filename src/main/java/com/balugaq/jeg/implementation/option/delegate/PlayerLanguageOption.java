/*
 * Copyright (c) 2024-2025 balugaq
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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.balugaq.jeg.implementation.option.delegate;

import com.balugaq.jeg.api.patches.JEGGuideSettings;
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
public class PlayerLanguageOption implements SlimefunGuideOption<String> {
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

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

    public void onClick(Player p, ItemStack guide) {
        this.openLanguageSelection(p, guide);
    }

    public Optional<String> getSelectedOption(Player p, ItemStack guide) {
        return Optional.of(Slimefun.getLocalization().getLanguage(p).getId());
    }

    public void setSelectedOption(Player p, ItemStack guide, String value) {
        if (value == null) {
            PersistentDataAPI.remove(p, this.getKey());
        } else {
            PersistentDataAPI.setString(p, this.getKey(), value);
        }

    }

    private void openLanguageSelection(Player p, ItemStack guide) {
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.languages"));
        menu.setEmptySlotsClickable(false);
        SoundEffect var10001 = SoundEffect.GUIDE_LANGUAGE_OPEN_SOUND;
        Objects.requireNonNull(var10001);
        menu.addMenuOpeningHandler(var10001::playFor);

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

    public NamespacedKey getKey() {
        return Slimefun.getLocalization().getKey();
    }
}
