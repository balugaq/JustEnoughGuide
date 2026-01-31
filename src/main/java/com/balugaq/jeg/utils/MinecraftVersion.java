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

package com.balugaq.jeg.utils;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NullMarked;

/**
 * Minecraft versions for version judgement
 *
 * @author lijinhong11
 * @author balugaq
 */
@SuppressWarnings("unused")
@NullMarked
public record MinecraftVersion(int major, int minor, int patch) implements Comparable<MinecraftVersion> {
    public static final MinecraftVersion UNKNOWN = new MinecraftVersion();
    public static final MinecraftVersion V1_16 = MinecraftVersion.of(1, 16);
    public static final MinecraftVersion V1_17_1 = MinecraftVersion.of(1, 17, 1);
    public static final MinecraftVersion V1_19_4 = MinecraftVersion.of(1, 19, 4);
    public static final MinecraftVersion V1_20 = MinecraftVersion.of(1, 20);
    public static final MinecraftVersion V1_20_1 = MinecraftVersion.of(1, 20, 1);
    public static final MinecraftVersion V1_20_5 = MinecraftVersion.of(1, 20, 5);
    public static final MinecraftVersion V1_21 = MinecraftVersion.of(1, 21);

    public MinecraftVersion() {
        this(999, 999, 999);
    }

    public static MinecraftVersion current() {
        return MinecraftVersion.of(Bukkit.getMinecraftVersion());
    }

    public static MinecraftVersion of(String version) {
        return deserialize(version);
    }

    public boolean isAtLeast(String version) {
        return isAtLeast(of(version));
    }

    public boolean isAtLeast(MinecraftVersion version) {
        return this.major > version.major ||
                this.major == version.major && this.minor > version.minor ||
                this.major == version.major && this.minor == version.minor && this.patch >= version.patch;
    }

    public boolean isBefore(String version) {
        return isBefore(of(version));
    }

    public boolean isBefore(MinecraftVersion version) {
        return !isAtLeast(version) && this != version;
    }

    public static MinecraftVersion deserialize(String s) {
        String[] split = s.split("\\.");
        if (split.length < 2) throw new IllegalArgumentException("Invalid version string: " + s);
        int major = Integer.parseUnsignedInt(split[0]);
        int minor = Integer.parseUnsignedInt(split[1]);
        int patch = Integer.parseUnsignedInt(split.length == 2 ? "0" : split[2]);
        return MinecraftVersion.of(major, minor, patch);
    }

    public static MinecraftVersion of(int major, int minor, int patch) {
        return new MinecraftVersion(major, minor, patch);
    }

    public static MinecraftVersion of(int major, int minor) {
        return of(major, minor, 0);
    }

    @Override
    public int compareTo(MinecraftVersion o) {
        return Integer.compare(this.major * 1000000 + this.minor * 10000 + this.patch, o.major * 1000000 + o.minor * 10000 + o.patch);
    }

    public String humanize() {
        return major + "." + minor + "." + patch;
    }
}
