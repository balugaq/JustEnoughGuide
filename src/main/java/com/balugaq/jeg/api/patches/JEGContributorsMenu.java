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

package com.balugaq.jeg.api.patches;

import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.services.github.Contributor;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author TheBusyBiscuit
 * @author balugaq
 * @see SlimefunGuide
 * @since 1.8
 */
@SuppressWarnings({"deprecation", "UnnecessaryUnicodeEscape"})
@NullMarked
public class JEGContributorsMenu {
    public static void open(Player p, int page) {
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.credits"));

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(SoundEffect.GUIDE_CONTRIBUTORS_OPEN_SOUND::playFor);

        ChestMenuUtils.drawBackground(
                menu,
                Formats.contributors.getChars('B').stream().mapToInt(i -> i).toArray());

        for (int ss : Formats.contributors.getChars('b')) {
            menu.addItem(
                    ss,
                    PatchScope.Back.patch(
                            p,
                            Converter.getItem(ChestMenuUtils.getBackButton(
                                    p, "", "&7" + Slimefun.getLocalization().getMessage(p, "guide.back.settings")))));
            menu.addMenuClickHandler(ss, (pl, slot, item, action) -> {
                JEGGuideSettings.openSettings(pl, p.getInventory().getItemInMainHand());
                return false;
            });
        }

        List<Contributor> contributors =
                new ArrayList<>(Slimefun.getGitHubService().getContributors().values());
        contributors.sort(Comparator.comparingInt(Contributor::getPosition));

        List<Integer> slots = Formats.contributors.getChars('p');
        int sizePerPage = slots.size();
        for (int i = page * sizePerPage; i < contributors.size() && i < (page + 1) * sizePerPage; i++) {
            Contributor contributor = contributors.get(i);
            ItemStack skull = getContributorHead(p, contributor);
            int ss = slots.get(i - page * sizePerPage);

            menu.addItem(ss, PatchScope.Contributor.patch(p, skull));
            menu.addMenuClickHandler(ss, (pl, slot, item, action) -> {
                if (contributor.getProfile() != null) {
                    pl.closeInventory();
                    ChatUtils.sendURL(pl, contributor.getProfile());
                }
                return false;
            });
        }

        int pages = (contributors.size() - 1) / sizePerPage + 1;

        for (int ss : Formats.contributors.getChars('P')) {
            menu.addItem(ss, PatchScope.PreviousPage.patch(p, ChestMenuUtils.getPreviousButton(p, page + 1, pages)));
            menu.addMenuClickHandler(ss, (pl, slot, item, action) -> {
                if (page > 0) {
                    open(pl, page - 1);
                }

                return false;
            });
        }

        for (int ss : Formats.contributors.getChars('N')) {
            menu.addItem(ss, PatchScope.NextPage.patch(p, ChestMenuUtils.getNextButton(p, page + 1, pages)));
            menu.addMenuClickHandler(ss, (pl, slot, item, action) -> {
                if (page + 1 < pages) {
                    open(pl, page + 1);
                }

                return false;
            });
        }

        Formats.contributors.renderCustom(menu);
        menu.open(p);
    }

    private static ItemStack getContributorHead(Player p, Contributor contributor) {
        ItemStack skull = SlimefunUtils.getCustomHead(contributor.getTexture());

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setDisplayName(contributor.getDisplayName());

        List<String> lore = new LinkedList<>();
        lore.add("");

        for (Map.Entry<String, Integer> entry : contributor.getContributions()) {
            String info = entry.getKey();

            if (!info.startsWith("&")) {
                String[] segments = CommonPatterns.COMMA.split(info);
                info = Slimefun.getLocalization().getMessage(p, "guide.credits.roles." + segments[0]);

                if (segments.length == 2) {
                    info += " &7(" + Slimefun.getLocalization().getMessage(p, "languages." + segments[1]) + ')';
                }
            }

            if (entry.getValue() > 0) {
                String commits = Slimefun.getLocalization()
                        .getMessage(p, "guide.credits." + (entry.getValue() > 1 ? "commits" : "commit"));

                info += " &7(" + entry.getValue() + ' ' + commits + ')';
            }

            lore.add(ChatColors.color(info));
        }

        if (contributor.getProfile() != null) {
            lore.add("");
            lore.add(ChatColors.color("&7\u21E8 &e")
                    + Slimefun.getLocalization().getMessage(p, "guide.credits.profile-link"));
        }

        meta.setLore(lore);
        skull.setItemMeta(meta);
        return skull;
    }
}
