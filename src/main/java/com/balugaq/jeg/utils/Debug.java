package com.balugaq.jeg.utils;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused"})
public class Debug {
    private static final String debugPrefix = Lang.getDebug("debug-prefix");
    private static JavaPlugin plugin;

    public static void debug(Object @NotNull ... objects) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : objects) {
            if (obj == null) {
                sb.append("null");
            } else {
                sb.append(obj);
            }
        }
        debug(sb.toString());
    }

    public static void debug(@NotNull Throwable e) {
        debug(e.getMessage());
        e.printStackTrace();
    }

    public static void debug(@NotNull Object object) {
        debug(object.toString());
    }

    public static void debug(String @NotNull ... messages) {
        for (String message : messages) {
            debug(message);
        }
    }

    public static void debug(String message) {
        if (JustEnoughGuide.getConfigManager().isDebug()) {
            log(debugPrefix + message);
        }
    }

    public static void sendMessage(@NotNull Player player, Object @NotNull ... objects) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : objects) {
            if (obj == null) {
                sb.append("null");
            } else {
                sb.append(obj);
            }
        }
        sendMessage(player, sb.toString());
    }

    public static void sendMessage(@NotNull Player player, @Nullable Object object) {
        if (object == null) {
            sendMessage(player, "null");
            return;
        }
        sendMessage(player, object.toString());
    }

    public static void sendMessages(@NotNull Player player, String @NotNull ... messages) {
        for (String message : messages) {
            sendMessage(player, message);
        }
    }

    public static void sendMessage(@NotNull Player player, String message) {
        init();
        player.sendMessage("[" + plugin.getLogger().getName() + "]" + message);
    }

    public static void stackTraceManually() {
        try {
            throw new Error();
        } catch (Throwable e) {
            Debug.log(e);
        }
    }

    public static void log(Object @NotNull ... object) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : object) {
            if (obj == null) {
                sb.append("null");
            } else {
                sb.append(obj);
            }
        }

        log(sb.toString());
    }

    public static void log(@NotNull Object object) {
        log(object.toString());
    }

    public static void log(String @NotNull ... messages) {
        for (String message : messages) {
            log(message);
        }
    }

    public static void log(@NotNull String message) {
        init();
        plugin.getServer().getConsoleSender().sendMessage("[" + JustEnoughGuide.getInstance().getName() + "] " + ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void log(@NotNull Throwable e) {
        e.printStackTrace();
    }

    public static void log() {
        log("");
    }

    public static void init() {
        if (plugin == null) {
            plugin = JustEnoughGuide.getInstance();
        }
    }
}
