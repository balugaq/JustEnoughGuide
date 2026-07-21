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

package com.balugaq.jeg.utils;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NullMarked;

/**
 * Minecraft versions for version judgement
 *
 * @author lijinhong11
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings("unused")
@NullMarked
public record MinecraftVersion(int major, int minor, int patch) implements Comparable<MinecraftVersion> {
    public static final MinecraftVersion UNKNOWN = new MinecraftVersion();
    public static final MinecraftVersion V1_16 = MinecraftVersion.of(1, 16);
    public static final MinecraftVersion V1_17 = MinecraftVersion.of(1, 17);
    public static final MinecraftVersion V1_17_1 = MinecraftVersion.of(1, 17, 1);
    public static final MinecraftVersion V1_19_4 = MinecraftVersion.of(1, 19, 4);
    public static final MinecraftVersion V1_20 = MinecraftVersion.of(1, 20);
    public static final MinecraftVersion V1_20_1 = MinecraftVersion.of(1, 20, 1);
    public static final MinecraftVersion V1_20_5 = MinecraftVersion.of(1, 20, 5);
    public static final MinecraftVersion V1_21 = MinecraftVersion.of(1, 21);
    public static final MinecraftVersion V1_21_10 = MinecraftVersion.of(1, 21, 10);
    public static final MinecraftVersion V26_1 = MinecraftVersion.of(26, 1);

    public MinecraftVersion() {
        this(999, 999, 999);
    }

    public static MinecraftVersion current() {
        return MinecraftVersion.of(Bukkit.getMinecraftVersion());
    }

    public static MinecraftVersion of(String version) {
        return deserialize(version);
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

    public static MinecraftVersion max(MinecraftVersion a, MinecraftVersion b) {
        if (a.isAtLeast(b)) {
            return a;
        }

        return b;
    }

    public static MinecraftVersion min(MinecraftVersion a, MinecraftVersion b) {
        if (a.isAtLeast(b)) {
            return b;
        }

        return a;
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

    @Override
    public int compareTo(MinecraftVersion o) {
        return Integer.compare(this.major * 1000000 + this.minor * 10000 + this.patch, o.major * 1000000 + o.minor * 10000 + o.patch);
    }

    public String humanize() {
        if (patch == 0) {
            return major + "." + minor;
        }

        return major + "." + minor + "." + patch;
    }
}
