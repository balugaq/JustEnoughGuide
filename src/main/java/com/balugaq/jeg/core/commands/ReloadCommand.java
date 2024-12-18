package com.balugaq.jeg.core.commands;

import com.balugaq.jeg.api.interfaces.JEGCommand;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This is the implementation of the "/jeg reload" command.
 * It reloads the JEG plugin configuration.
 *
 * @author balugaq
 * @since 1.1
 */
@Getter
public class ReloadCommand implements JEGCommand {
    private final Plugin plugin;

    public ReloadCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nonnull List<String> onTabCompleteRaw(@Nonnull CommandSender sender, @Nonnull String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of(
                        "reload"
                );
            }

            default -> {
                return List.of();
            }
        }
    }

    @Override
    public boolean canCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (sender.isOp()) {
            if (args.length == 1) {
                if ("reload".equalsIgnoreCase(args[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        onReload(sender);
    }

    private void onReload(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Reloading plugin...");
        try {
            if (plugin == null) {
                sender.sendMessage(ChatColor.RED + "Failed to reload plugin.");
                return;
            }

            plugin.onDisable();
            plugin.onEnable();
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "plugin has been reloaded.");
        } catch (Throwable e) {
            sender.sendMessage(ChatColor.RED + "Failed to reload plugin.");
            e.printStackTrace();
            return;
        }
    }
}
