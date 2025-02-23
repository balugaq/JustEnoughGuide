package com.balugaq.jeg.core.commands;

import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.interfaces.JEGCommand;
import com.balugaq.jeg.utils.Lang;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull List<String> onTabCompleteRaw(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("reload");
            }

            default -> {
                return List.of();
            }
        }
    }

    @Override
    public boolean canCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args) {
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
    public void onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        onReload(sender);
    }

    private void onReload(@NotNull CommandSender sender) {
        sender.sendMessage(Lang.getCommandMessage("reload", "start"));
        try {
            if (plugin == null) {
                sender.sendMessage(Lang.getCommandMessage("reload", "precheck-failed"));
                return;
            }

            plugin.onDisable();
            plugin.onEnable();
            plugin.reloadConfig();
            SearchGroup.LOADED = false;
            SearchGroup.init();
            sender.sendMessage(Lang.getCommandMessage("reload", "success"));
        } catch (Throwable e) {
            sender.sendMessage(Lang.getCommandMessage("reload", "failed"));
            e.printStackTrace();
            return;
        }
    }
}
