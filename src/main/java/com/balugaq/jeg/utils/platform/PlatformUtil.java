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

package com.balugaq.jeg.utils.platform;

import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
@UtilityClass
@NullMarked
public class PlatformUtil {
    @Getter
    private static final boolean bukkit;
    @Getter
    private static final boolean spigot;
    @Getter
    private static final boolean paper;
    @Getter
    private static final boolean pufferfish;
    @Getter
    private static final boolean purpur;
    @Getter
    private static final boolean folia;
    @Getter
    private static final boolean leaf;
    @Getter
    private static final boolean leaves;

    static {
        bukkit = true;
        spigot = PaperLib.isSpigot(); // > bukkit
        paper = PaperLib.isPaper(); // > spigot
        pufferfish = classExist("gg.pufferfish.pufferfish.simd.SIMDChecker"); // > paper
        purpur = classExist("org.purpurmc.purpur.util.permissions.PurpurPermissions"); // > pufferfish
        folia = classExist("io.papermc.paper.threadedregions.RegionizedServer"); // > paper
        leaf = classExist("org.leavesmc.leaves.entity.photographer.PhotographerManager"); // > paper
        leaves = classExist("org.leavesmc.leaves.config.LeavesConfigValue"); // > paper
    }

    public static boolean classExist(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("EmptyMethod")
    public static void initialize() {
    }
}
