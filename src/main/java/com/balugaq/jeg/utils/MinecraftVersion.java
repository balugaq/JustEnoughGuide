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
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a Minecraft version. It contains a major and minor version number, and provides methods to compare
 *
 * @author balugaq
 * @since 1.2
 */
@SuppressWarnings("unused")
@Getter
public enum MinecraftVersion {
    MINECRAFT_1_16(16, 0),
    MINECRAFT_1_16_1(16, 1),
    MINECRAFT_1_16_2(16, 2),
    MINECRAFT_1_16_3(16, 3),
    MINECRAFT_1_16_4(16, 4),
    MINECRAFT_1_16_5(16, 5),
    MINECRAFT_1_17(17, 0),
    MINECRAFT_1_17_1(17, 1),
    MINECRAFT_1_17_2(17, 2),
    MINECRAFT_1_17_3(17, 3),
    MINECRAFT_1_17_4(17, 4),
    MINECRAFT_1_18(18, 0),
    MINECRAFT_1_18_1(18, 1),
    MINECRAFT_1_18_2(18, 2),
    MINECRAFT_1_18_3(18, 3),
    MINECRAFT_1_18_4(18, 4),
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
    MINECRAFT_1_22(22, 0),
    MINECRAFT_1_22_1(22, 1),
    MINECRAFT_1_22_2(22, 2),
    MINECRAFT_1_22_3(22, 3),
    MINECRAFT_1_22_4(22, 4),
    MINECRAFT_1_22_5(22, 5),
    MINECRAFT_1_22_6(22, 6),
    UNKNOWN(-1, -1);

    private final int major;
    private final int minor;

    MinecraftVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public static @NotNull MinecraftVersion of(int major, int minor) {
        for (MinecraftVersion version : values()) {
            if (version.major == major && version.minor == minor) {
                return version;
            }
        }
        return UNKNOWN;
    }

    public static @NotNull MinecraftVersion getCurrentVersion() {
        int major = PaperLib.getMinecraftVersion();
        int minor = PaperLib.getMinecraftPatchVersion();
        return of(major, minor);
    }

    public boolean isAtLeast(@NotNull MinecraftVersion version) {
        return this.major > version.major || (this.major == version.major && this.minor >= version.minor);
    }

    public boolean isBelow(@NotNull MinecraftVersion version) {
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
}
