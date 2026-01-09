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

import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"unused", "deprecation", "CallToPrintStackTrace", "ResultOfMethodCallIgnored"})
@NullMarked
public class Debug {
    public static final File errorsFolder =
            new File(JustEnoughGuide.getInstance().getDataFolder(), "error-reports");
    private static final String debugPrefix = "[Debug] ";
    @Setter
    private static @Nullable JavaPlugin plugin = null;

    static {
        if (!errorsFolder.exists()) {
            errorsFolder.mkdirs();
        }
    }

    public static void severe(Object... objects) {
        severe(Arrays.toString(objects));
    }

    public static void severe(String message) {
        log("&e[ERROR] " + message);
    }

    public static void log(String message) {
        Bukkit.getServer()
                .getConsoleSender()
                .sendMessage("[" + JustEnoughGuide.getInstance().getName() + "] " + ChatColors.color(message));
    }

    public static void severe(Throwable e) {
        severe(e.getMessage());
        trace(e);
    }

    public static void trace(Throwable e) {
        trace(e, null);
    }

    public static void trace(Throwable e, @Nullable String doing) {
        trace(e, doing, null);
    }

    public static void trace(Throwable e, @Nullable String doing, @Nullable Integer code) {
        try {
            getPlugin()
                    .getLogger()
                    .severe(
                            "DO NOT REPORT THIS ERROR TO JustEnoughGuide DEVELOPERS!!! THIS IS NOT A JustEnoughGuide " +
                                    "BUG!");
            if (code != null) {
                getPlugin().getLogger().severe("Error code: " + code);
            }
            getPlugin()
                    .getLogger()
                    .severe("If you are sure that this is a JustEnoughGuide bug, please report to "
                                    + JustEnoughGuide.getInstance().getBugTrackerURL());
            if (doing != null) {
                getPlugin().getLogger().severe("An unexpected error occurred while " + doing);
            } else {
                getPlugin().getLogger().severe("An unexpected error occurred.");
            }

            e.printStackTrace();

            dumpToFile(e, code);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    public static JavaPlugin getPlugin() {
        if (plugin == null) {
            plugin = JustEnoughGuide.getInstance();
        }
        return plugin;
    }

    public static void dumpToFile(Throwable e, @Nullable Integer code) {
        // Format as: yyyy-MM-dd-HH-mm-ss-e.getClass().getSimpleName()-uuid
        String fileName = "error-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
                + "-" + e.getClass().getSimpleName() + "-" + UUID.randomUUID() + ".txt";

        File file = new File(errorsFolder, fileName);
        try {
            file.createNewFile();
            try (PrintStream stream = new PrintStream(file, StandardCharsets.UTF_8)) {
                stream.println("====================AN FATAL OCCURRED====================");
                stream.println(
                        "DO NOT REPORT THIS ERROR TO JustEnoughGuide DEVELOPERS!!! THIS IS NOT A JustEnoughGuide BUG!");
                stream.println("If you are sure that this is a JustEnoughGuide bug, please report to "
                                       + JustEnoughGuide.getInstance().getBugTrackerURL());
                stream.println("An unexpected error occurred.");
                stream.println("JustEnoughGuide version: "
                                       + JustEnoughGuide.getInstance().getDescription().getVersion());
                stream.println("Java version: " + System.getProperty("java.version"));
                stream.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " "
                                       + System.getProperty("os.arch"));
                stream.println("Minecraft version: " + JustEnoughGuide.getMinecraftVersion());
                stream.println("Slimefun version: " + Slimefun.getVersion());
                if (code != null) {
                    stream.println("Error code: " + code);
                }
                stream.println("Error: " + e);
                stream.println("Stack trace:");
                e.printStackTrace(stream);

                warn("");
                warn("An Error occurred! It has been saved as: ");
                warn("/plugins/JustEnoughGuide/error-reports/" + file.getName());
                warn("Please put this file on https://pastebin.com/ and report this to the developer(s).");

                warn("Please DO NOT send screenshots of these logs to the developer(s).");
                warn("");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void warn(String message) {
        log("&e[WARN] " + message);
    }

    public static void severe(@Nullable Object object) {
        severe(object == null ? "null" : object.toString());
    }

    public static void severe(String... messages) {
        for (String message : messages) {
            severe(message);
        }
    }

    public static void warn(Object... objects) {
        warn(Arrays.toString(objects));
    }

    public static void warn(Throwable e) {
        warn(e.getMessage());
        trace(e);
    }

    public static void warn(@Nullable Object object) {
        warn(object == null ? "null" : object.toString());
    }

    public static void warn(String... messages) {
        for (String message : messages) {
            warn(message);
        }
    }

    public static void debug(Object... objects) {
        debug(Arrays.toString(objects));
    }

    public static void debug(String message) {
        if (JustEnoughGuide.getConfigManager().isDebug()) {
            log(debugPrefix + message);
        }
    }

    public static void debug(Throwable e) {
        debug(e.getMessage());
        trace(e);
    }

    public static void debug(@Nullable Object object) {
        debug(object == null ? "null" : object.toString());
    }

    public static void debug(String... messages) {
        for (String message : messages) {
            debug(message);
        }
    }

    public static void sendMessage(Player player, Object... objects) {
        sendMessage(player, Arrays.toString(objects));
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage("[" + getPlugin().getName() + "]" + message);
    }

    public static void sendMessage(Player player, @Nullable Object object) {
        if (object == null) {
            sendMessage(player, "null");
            return;
        }
        sendMessage(player, object.toString());
    }

    public static void sendMessages(Player player, String... messages) {
        for (String message : messages) {
            sendMessage(player, message);
        }
    }

    public static void dumpStack() {
        Thread.dumpStack();
    }

    public static void log(Object... object) {
        log(Arrays.toString(object));
    }

    public static void log(@Nullable Object object) {
        log(object == null ? "null" : object.toString());
    }

    public static void log(String... messages) {
        for (String message : messages) {
            log(message);
        }
    }

    public static void log(Throwable e) {
        Debug.trace(e);
    }

    public static void log() {
        log("");
    }

    public static void traceExactly(Throwable e, @Nullable String doing, @Nullable Integer code) {
        try {
            getPlugin()
                    .getLogger()
                    .severe("====================AN FATAL OCCURRED"
                                    + (doing != null ? (" WHEN " + doing.toUpperCase()) : "") + "====================");
            getPlugin()
                    .getLogger()
                    .severe(
                            "DO NOT REPORT THIS ERROR TO JustEnoughGuide DEVELOPERS!!! THIS IS NOT A JustEnoughGuide " +
                                    "BUG!");
            if (code != null) {
                getPlugin().getLogger().severe("Error code: " + code);
            }
            getPlugin()
                    .getLogger()
                    .severe("If you are sure that this is a JustEnoughGuide bug, please report to "
                                    + JustEnoughGuide.getInstance().getBugTrackerURL());
            if (doing != null) {
                getPlugin().getLogger().severe("An unexpected error occurred while " + doing);
            } else {
                getPlugin().getLogger().severe("An unexpected error occurred.");
            }

            e.printStackTrace();

            getPlugin().getLogger().severe("ALL EXCEPTION INFORMATION IS BELOW:");
            getPlugin().getLogger().severe("message: " + e.getMessage());
            getPlugin().getLogger().severe("localizedMessage: " + e.getLocalizedMessage());
            getPlugin().getLogger().severe("cause: " + e.getCause());
            getPlugin().getLogger().severe("stackTrace: " + Arrays.toString(e.getStackTrace()));
            getPlugin().getLogger().severe("suppressed: " + Arrays.toString(e.getSuppressed()));

            dumpToFile(e, code);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }
}
