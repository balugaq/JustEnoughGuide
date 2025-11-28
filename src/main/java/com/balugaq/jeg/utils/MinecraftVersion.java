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

package com.balugaq.jeg.utils;

import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;

/**
 * This class represents a Minecraft version. It contains a major and minor version number, and provides methods to
 * compare
 *
 * @author balugaq
 * @since 1.2
 */
@SuppressWarnings("unused")
@Getter
@NullMarked
public enum MinecraftVersion {
    MINECRAFT_1_16(16, 0),
    MINECRAFT_1_16_1(16, 1),
    MINECRAFT_1_16_2(16, 2),
    MINECRAFT_1_16_3(16, 3),
    MINECRAFT_1_16_4(16, 4),
    MINECRAFT_1_16_5(16, 5),
    MINECRAFT_1_17(17, 0),
    MINECRAFT_1_17_1(17, 1),
    MINECRAFT_1_18(18, 0),
    MINECRAFT_1_18_1(18, 1),
    MINECRAFT_1_18_2(18, 2),
    MINECRAFT_1_19(19, 0),
    MINECRAFT_1_19_1(19, 1),
    MINECRAFT_1_19_2(19, 2),
    MINECRAFT_1_19_3(19, 3),
    MINECRAFT_1_19_4(19, 4),
    MINECRAFT_1_20(20, 0),
    MINECRAFT_1_20_1(20, 1),
    MINECRAFT_1_20_2(20, 2),
    MINECRAFT_1_20_3(20, 3),
    MINECRAFT_1_20_4(20, 4),
    MINECRAFT_1_20_5(20, 5),
    MINECRAFT_1_20_6(20, 6),
    MINECRAFT_1_21(21, 0),
    MINECRAFT_1_21_1(21, 1),
    MINECRAFT_1_21_2(21, 2),
    MINECRAFT_1_21_3(21, 3),
    MINECRAFT_1_21_4(21, 4),
    MINECRAFT_1_21_5(21, 5),
    MINECRAFT_1_21_6(21, 6),
    MINECRAFT_1_21_7(21, 7),
    MINECRAFT_1_21_8(21, 8),
    MINECRAFT_1_21_9(21, 9),
    MINECRAFT_1_21_10(21, 10),
    MINECRAFT_1_21_11(21, 11),
    MINECRAFT_1_21_12(21, 12),
    MINECRAFT_1_21_13(21, 13),
    MINECRAFT_1_22(22, 0),
    MINECRAFT_1_22_1(22, 1),
    MINECRAFT_1_22_2(22, 2),
    MINECRAFT_1_22_3(22, 3),
    MINECRAFT_1_22_4(22, 4),
    MINECRAFT_1_22_5(22, 5),
    MINECRAFT_1_22_6(22, 6),
    MINECRAFT_1_22_7(22, 7),
    MINECRAFT_1_22_8(22, 8),
    MINECRAFT_1_22_9(22, 9),
    MINECRAFT_1_22_10(22, 10),
    MINECRAFT_1_22_11(22, 11),
    MINECRAFT_1_22_12(22, 12),
    MINECRAFT_1_22_13(22, 13),
    MINECRAFT_1_23(23, 0),
    MINECRAFT_1_23_1(23, 1),
    MINECRAFT_1_23_2(23, 2),
    MINECRAFT_1_23_3(23, 3),
    MINECRAFT_1_23_4(23, 4),
    MINECRAFT_1_23_5(23, 5),
    MINECRAFT_1_23_6(23, 6),
    MINECRAFT_1_23_7(23, 7),
    MINECRAFT_1_23_8(23, 8),
    MINECRAFT_1_23_9(23, 9),
    MINECRAFT_1_23_10(23, 10),
    MINECRAFT_1_23_11(23, 11),
    MINECRAFT_1_23_12(23, 12),
    MINECRAFT_1_23_13(23, 13),
    MINECRAFT_1_24(24, 0),
    MINECRAFT_1_24_1(24, 1),
    MINECRAFT_1_24_2(24, 2),
    MINECRAFT_1_24_3(24, 3),
    MINECRAFT_1_24_4(24, 4),
    MINECRAFT_1_24_5(24, 5),
    MINECRAFT_1_24_6(24, 6),
    MINECRAFT_1_24_7(24, 7),
    MINECRAFT_1_24_8(24, 8),
    MINECRAFT_1_24_9(24, 9),
    MINECRAFT_1_24_10(24, 10),
    MINECRAFT_1_24_11(24, 11),
    MINECRAFT_1_24_12(24, 12),
    MINECRAFT_1_24_13(24, 13),
    MINECRAFT_1_25(25, 0),
    MINECRAFT_1_25_1(25, 1),
    MINECRAFT_1_25_2(25, 2),
    MINECRAFT_1_25_3(25, 3),
    MINECRAFT_1_25_4(25, 4),
    MINECRAFT_1_25_5(25, 5),
    MINECRAFT_1_25_6(25, 6),
    MINECRAFT_1_25_7(25, 7),
    MINECRAFT_1_25_8(25, 8),
    MINECRAFT_1_25_9(25, 9),
    MINECRAFT_1_25_10(25, 10),
    MINECRAFT_1_25_11(25, 11),
    MINECRAFT_1_25_12(25, 12),
    MINECRAFT_1_25_13(25, 13),
    MINECRAFT_1_26(26, 0),
    MINECRAFT_1_26_1(26, 1),
    MINECRAFT_1_26_2(26, 2),
    MINECRAFT_1_26_3(26, 3),
    MINECRAFT_1_26_4(26, 4),
    MINECRAFT_1_26_5(26, 5),
    MINECRAFT_1_26_6(26, 6),
    MINECRAFT_1_26_7(26, 7),
    MINECRAFT_1_26_8(26, 8),
    MINECRAFT_1_26_9(26, 9),
    MINECRAFT_1_26_10(26, 10),
    MINECRAFT_1_26_11(26, 11),
    MINECRAFT_1_26_12(26, 12),
    MINECRAFT_1_26_13(26, 13),
    MINECRAFT_1_27(27, 0),
    MINECRAFT_1_27_1(27, 1),
    MINECRAFT_1_27_2(27, 2),
    MINECRAFT_1_27_3(27, 3),
    MINECRAFT_1_27_4(27, 4),
    MINECRAFT_1_27_5(27, 5),
    MINECRAFT_1_27_6(27, 6),
    MINECRAFT_1_27_7(27, 7),
    MINECRAFT_1_27_8(27, 8),
    MINECRAFT_1_27_9(27, 9),
    MINECRAFT_1_27_10(27, 10),
    MINECRAFT_1_27_11(27, 11),
    MINECRAFT_1_27_12(27, 12),
    MINECRAFT_1_27_13(27, 13),
    MINECRAFT_1_28(28, 0),
    MINECRAFT_1_28_1(28, 1),
    MINECRAFT_1_28_2(28, 2),
    MINECRAFT_1_28_3(28, 3),
    MINECRAFT_1_28_4(28, 4),
    MINECRAFT_1_28_5(28, 5),
    MINECRAFT_1_28_6(28, 6),
    MINECRAFT_1_28_7(28, 7),
    MINECRAFT_1_28_8(28, 8),
    MINECRAFT_1_28_9(28, 9),
    MINECRAFT_1_28_10(28, 10),
    MINECRAFT_1_28_11(28, 11),
    MINECRAFT_1_28_12(28, 12),
    MINECRAFT_1_28_13(28, 13),
    MINECRAFT_1_29(29, 0),
    MINECRAFT_1_29_1(29, 1),
    MINECRAFT_1_29_2(29, 2),
    MINECRAFT_1_29_3(29, 3),
    MINECRAFT_1_29_4(29, 4),
    MINECRAFT_1_29_5(29, 5),
    MINECRAFT_1_29_6(29, 6),
    MINECRAFT_1_29_7(29, 7),
    MINECRAFT_1_29_8(29, 8),
    MINECRAFT_1_29_9(29, 9),
    MINECRAFT_1_29_10(29, 10),
    MINECRAFT_1_29_11(29, 11),
    MINECRAFT_1_29_12(29, 12),
    MINECRAFT_1_29_13(29, 13),
    MINECRAFT_1_30(30, 0),
    MINECRAFT_1_30_1(30, 1),
    MINECRAFT_1_30_2(30, 2),
    MINECRAFT_1_30_3(30, 3),
    MINECRAFT_1_30_4(30, 4),
    MINECRAFT_1_30_5(30, 5),
    MINECRAFT_1_30_6(30, 6),
    MINECRAFT_1_30_7(30, 7),
    MINECRAFT_1_30_8(30, 8),
    MINECRAFT_1_30_9(30, 9),
    MINECRAFT_1_30_10(30, 10),
    MINECRAFT_1_30_11(30, 11),
    MINECRAFT_1_30_12(30, 12),
    MINECRAFT_1_30_13(30, 13),
    UNKNOWN(999, 999);

    private final int major;
    private final int minor;

    MinecraftVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public static MinecraftVersion of(int major, int minor) {
        for (MinecraftVersion version : values()) {
            if (version.major == major && version.minor == minor) {
                return version;
            }
        }
        return UNKNOWN;
    }

    public static MinecraftVersion getCurrentVersion() {
        int major = PaperLib.getMinecraftVersion();
        int minor = PaperLib.getMinecraftPatchVersion();
        return of(major, minor);
    }

    public boolean isAtLeast(MinecraftVersion version) {
        return this.major > version.major || (this.major == version.major && this.minor >= version.minor);
    }

    public boolean isBelow(MinecraftVersion version) {
        return this.major < version.major || (this.major == version.major && this.minor < version.minor);
    }

    public boolean isAtLeast(int major, int minor) {
        return this.major > major || (this.major == major && this.minor >= minor);
    }

    public boolean isBelow(int major, int minor) {
        return this.major < major || (this.major == major && this.minor < minor);
    }

    public boolean equals(int major, int minor) {
        return this.major == major && this.minor == minor;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    public boolean isNotUnknown() {
        return this != UNKNOWN;
    }

    /**
     * Humanize the version
     *
     * @return 1.x.x
     */
    public String humanize() {
        return "1." + this.major + "." + this.minor;
    }
}
