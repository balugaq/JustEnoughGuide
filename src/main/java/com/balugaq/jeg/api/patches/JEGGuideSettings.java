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

package com.balugaq.jeg.api.patches;

import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.implementation.option.delegate.FireworksOption;
import com.balugaq.jeg.implementation.option.delegate.GuideModeOption;
import com.balugaq.jeg.implementation.option.delegate.LearningAnimationOption;
import com.balugaq.jeg.implementation.option.delegate.PlayerLanguageOption;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import io.github.thebusybiscuit.slimefun4.core.services.LocalizationService;
import io.github.thebusybiscuit.slimefun4.core.services.github.GitHubService;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author TheBusyBiscuit
 * @author balugaq
 * @see SlimefunGuide
 * @since 1.8
 */
@SuppressWarnings({"deprecation", "UnnecessaryUnicodeEscape", "DataFlowIssue"})
@Getter
@NullMarked
public class JEGGuideSettings {
    @Getter
    private static final List<SlimefunGuideOption<?>> patched = new ArrayList<>();
    private static final Map<UUID, Integer> pages = new HashMap<>();

    public static int getLastPage(Player p) {
        return pages.getOrDefault(p.getUniqueId(), 1);
    }

    public static void setLastPage(Player p, int page) {
        pages.put(p.getUniqueId(), page);
    }

    public static void openSettings(final Player p, final ItemStack guide) {
        openSettings(p, guide, getLastPage(p));
    }

    public static void openSettings(
            final Player p,
            final ItemStack guide,
            @Range(from = 1, to = Integer.MAX_VALUE) int page) {
        setLastPage(p, page);
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.settings"));

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(SoundEffect.GUIDE_OPEN_SETTING_SOUND::playFor);

        ChestMenuUtils.drawBackground(
                menu, Formats.settings.getChars('B').stream().mapToInt(i -> i).toArray());

        addHeader(p, menu);
        addConfigurableOptions(p, menu, guide, page);

        Formats.settings.renderCustom(menu);
        menu.open(p);
    }

    private static void addHeader(
            final Player p, final ChestMenu menu) {
        LocalizationService locale = Slimefun.getLocalization();

        // @formatter:off
        ItemStack b = PatchScope.Background.patch(
                p,
                Converter.getItem(
                        SlimefunGuide.getItem(SlimefunGuideMode.SURVIVAL_MODE),
                        "&e\u21E6 " + locale.getMessage(p, "guide.back.title"),
                        "",
                        "&7" + locale.getMessage(p, "guide.back.guide")));

        for (int ss : Formats.settings.getChars('b')) {
            menu.addItem(ss, b, (pl, slot, item, action) -> {
                GuideUtil.openMainMenuAsync(pl);
                return false;
            });
        }
        // @formatter:on

        GitHubService github = Slimefun.getGitHubService();

        List<String> contributorsLore = new ArrayList<>();
        contributorsLore.add("");
        contributorsLore.addAll(locale.getMessages(
                p,
                "guide.credits.description",
                msg -> msg.replace(
                        "%contributors%",
                        String.valueOf(github.getContributors().size())
                )
        ));
        contributorsLore.add("");
        contributorsLore.add("&7\u21E8 &e" + locale.getMessage(p, "guide.credits.open"));

        // @formatter:off
        ItemStack s = PatchScope.SettingsContributors.patch(
                p,
                Converter.getItem(
                        SlimefunUtils.getCustomHead("e952d2b3f351a6b0487cc59db31bf5f2641133e5ba0006b18576e996a0293e52"),
                        "&c" + locale.getMessage(p, "guide.title.credits"),
                        contributorsLore.toArray(new String[0])));
        for (int ss : Formats.settings.getChars('s')) {
            menu.addItem(ss, s, (pl, slot, action, item) -> {
                JEGContributorsMenu.open(pl, 0);
                return false;
            });
        }
        // @formatter:on

        // @formatter:off
        ItemStack v = PatchScope.SlimefunVersion.patch(
                p,
                Converter.getItem(
                        Material.WRITABLE_BOOK,
                        ChatColor.GREEN + locale.getMessage(p, "guide.title.versions"),
                        "&7&o" + locale.getMessage(p, "guide.tooltips.versions-notice"),
                        "",
                        "&f汉化 By StarWishsama",
                        "&c请不要将此版本信息截图到 Discord/GitHub 反馈 Bug",
                        "&c而是优先到汉化页面反馈",
                        "",
                        "&cTHIS BUILD IS UNOFFICIAL BUILD, DO NOT REPORT TO SLIMEFUN DEV",
                        "",
                        "&fMinecraft: &a" + Bukkit.getBukkitVersion(),
                        "&fSlimefun: &a" + Slimefun.getVersion()));
        for (int ss : Formats.settings.getChars('v')) {
            menu.addItem(ss, v, ChestMenuUtils.getEmptyClickHandler());
        }
        // @formatter:on

        // @formatter:off
        ItemStack u = PatchScope.SlimefunSourceCode.patch(
                p,
                Converter.getItem(
                        Material.COMPARATOR,
                        "&e" + locale.getMessage(p, "guide.title.source"),
                        "",
                        "&7最近活动于: &a" + NumberUtils.getElapsedTime(github.getLastUpdate()) + " 前",
                        "&7Forks: &e" + github.getForks(),
                        "&7Stars: &e" + github.getStars(),
                        "",
                        "&7&oSlimefun 4 是一个由社区参与的项目,",
                        "&7&o源代码可以在 GitHub 上找到",
                        "&7&o如果你想让这个项目持续下去",
                        "&7&o你可以考虑对项目做出贡献",
                        "",
                        "&7\u21E8 &e点击前往汉化版 GitHub 仓库"));
        for (int ss : Formats.settings.getChars('u')) {
            menu.addItem(ss, u, (pl, slot, item, action) -> {
                pl.closeInventory();
                ChatUtils.sendURL(pl, "https://github.com/SlimefunGuguProject/Slimefun4");
                return false;
            });
        }
        // @formatter:on

        // @formatter:off
        ItemStack W = PatchScope.SlimefunWiki.patch(
                p,
                Converter.getItem(
                        Material.KNOWLEDGE_BOOK,
                        "&3" + locale.getMessage(p, "guide.title.wiki"),
                        "",
                        "&7你需要对物品或机器方面的帮助吗?",
                        "&7你不知道要干什么?",
                        "&7查看我们的由社区维护的维基",
                        "&7并考虑成为一名编辑者!",
                        "",
                        "&7\u21E8 &e点击前往非官方中文 Wiki"));
        for (int ss : Formats.settings.getChars('W')) {
            menu.addItem(ss, W, (pl, slot, item, action) -> {
                pl.closeInventory();
                ChatUtils.sendURL(pl, "https://slimefun-wiki.guizhanss.cn/");
                return false;
            });
        }
        // @formatter:on

        // @formatter:off
        ItemStack l = PatchScope.AddonCount.patch(
                p,
                Converter.getItem(
                        Material.BOOKSHELF,
                        "&3" + locale.getMessage(p, "guide.title.addons"),
                        "",
                        "&7Slimefun 是一个大型项目，但附属插件的存在",
                        "&7能让 Slimefun 真正的发光发亮",
                        "&7看一看它们，也许你要寻找的附属插件就在那里!",
                        "",
                        "&7该服务器已安装附属插件: &b" + Slimefun.getInstalledAddons().size(),
                        "",
                        "&7\u21E8 &e点击查看 Slimefun4 可用的附属插件"));
        for (int ss : Formats.settings.getChars('l')) {
            menu.addItem(ss, l, (pl, slot, item, action) -> {
                pl.closeInventory();
                ChatUtils.sendURL(pl, "https://slimefun-wiki.guizhanss.cn/Addons");
                return false;
            });
        }
        // @formatter:on

        for (int ss : Formats.settings.getChars('z')) {
            if (Slimefun.getUpdater().getBranch().isOfficial()) {
                // @formatter:off
                menu.addItem(
                        ss,
                        PatchScope.UnofficialTips.patch(
                                p,
                                Converter.getItem(
                                        Material.REDSTONE_TORCH,
                                        "&4" + locale.getMessage(p, "guide.title.bugs"),
                                        "",
                                        "&7&oBug reports have to be made in English!",
                                        "",
                                        "&7Open Issues: &a" + github.getOpenIssues(),
                                        "&7Pending Pull Requests: &a" + github.getPendingPullRequests(),
                                        "",
                                        "&7\u21E8 &eClick to go to the Slimefun4 Bug Tracker")));
                // @formatter:on

                menu.addMenuClickHandler(
                        ss, (pl, slot, item, action) -> {
                            pl.closeInventory();
                            ChatUtils.sendURL(pl, "https://github.com/SlimefunGuguProject/Slimefun4/issues");
                            return false;
                        }
                );
            } else {
                menu.addItem(ss, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }
        }

        for (int ss : Formats.settings.getChars('U')) {
            menu.addItem(
                    ss,
                    PatchScope.UnknownFeature.patch(
                            p,
                            Converter.getItem(
                                    Material.TOTEM_OF_UNDYING,
                                    ChatColor.RED + locale.getMessage(p, "guide.work-in-progress")
                            )
                    ),
                    (pl, slot, item, action) -> {
                        // Add something here
                        return false;
                    }
            );
        }
    }

    private static void addConfigurableOptions(
            final Player p,
            final ChestMenu menu,
            final ItemStack guide,
            @Range(from = 1, to = Integer.MAX_VALUE) int page) {
        List<Integer> slots = Formats.settings.getChars('o');
        List<SlimefunGuideOption<?>> options = new ArrayList<>(getOptions());
        int maxPage = (int) Math.ceil(options.size() / (double) slots.size());
        List<SlimefunGuideOption<?>> split = options.stream()
                .skip((long) (page - 1) * slots.size())
                .limit(slots.size())
                .toList();
        int fail = 0;
        for (int i = 0; i < split.size(); i++) {
            SlimefunGuideOption<?> option = split.get(i);

            if (fail > i) {
                // Shouldn't happen
                fail = i;
            }

            int slot = slots.get(i - fail);
            Optional<ItemStack> item = option.getDisplayItem(p, guide);

            if (item.isPresent()) {
                menu.addItem(slot, PatchScope.GuideOption.patch(p, item.get()));
                menu.addMenuClickHandler(
                        slot, (pl, s, stack, action) -> {
                            option.onClick(p, guide);
                            return false;
                        }
                );
            } else {
                fail++;
            }
        }

        for (int ss : Formats.settings.getChars('P')) {
            menu.addItem(ss, ChestMenuUtils.getPreviousButton(p, page, maxPage));
            menu.addMenuClickHandler(
                    ss, (pl, slot, item, action) -> {
                        if (page > 1) {
                            openSettings(pl, guide, page - 1);
                        }

                        return false;
                    }
            );
        }

        for (int ss : Formats.settings.getChars('N')) {
            menu.addItem(ss, ChestMenuUtils.getNextButton(p, page, maxPage));
            menu.addMenuClickHandler(
                    ss, (pl, slot, item, action) -> {
                        if (page + 1 <= maxPage) {
                            openSettings(pl, guide, page + 1);
                        }

                        return false;
                    }
            );
        }
    }

    public static void patchSlimefun() {
        if (!patched.isEmpty()) return;
        for (var option : getOptions()) {
            if (option.getAddon() instanceof Slimefun) {
                patched.add(option);
            }
        }

        for (var po : patched) {
            getOptions().remove(po);
        }

        var ss = patched.stream().map(s -> s.getClass().getSimpleName()).toList();

        if (ss.contains("GuideModeOption")) addOption(new GuideModeOption());
        if (ss.contains("FireworksOption")) addOption(new FireworksOption());
        if (ss.contains("LearningAnimationOption")) addOption(new LearningAnimationOption());
        if (ss.contains("PlayerLanguageOption")) addOption(new PlayerLanguageOption());
    }

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    public static List<SlimefunGuideOption<?>> getOptions() {
        return (List<SlimefunGuideOption<?>>)
                ReflectionUtil.getStaticValue(SlimefunGuideSettings.class, "options", List.class);
    }

    public static void addOption(SlimefunGuideOption<?> option) {
        SlimefunGuideSettings.addOption(option);
    }

    public static void unpatchSlimefun() {
        for (var po : patched) {
            getOptions().add(po);
        }
        patched.clear();
    }

    @SuppressWarnings("unused")
    public static boolean hasFireworksEnabled(Player p) {
        return getOptionValue(p, FireworksOption.class, true);
    }

    public static <T extends SlimefunGuideOption<V>, V> V getOptionValue(Player p, Class<T> optionsClass,
                                                                         V defaultValue) {
        for (SlimefunGuideOption<?> option : getOptions()) {
            if (optionsClass.isInstance(option)) {
                T o = optionsClass.cast(option);
                ItemStack guide = SlimefunGuide.getItem(SlimefunGuideMode.SURVIVAL_MODE);
                return o.getSelectedOption(p, guide).orElse(defaultValue);
            }
        }

        return defaultValue;
    }

    @SuppressWarnings("unused")
    public static boolean hasLearningAnimationEnabled(Player p) {
        return getOptionValue(p, LearningAnimationOption.class, true);
    }
}
