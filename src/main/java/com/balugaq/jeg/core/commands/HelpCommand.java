package com.balugaq.jeg.core.commands;

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
 * This is the implementation of the "/jeg help" command.
 * It shows the list of available commands and their usage.
 * <p>
 * This command is also the default command when no other command is specified.
 *
 * @author balugaq
 * @since 1.1
 */
@Getter
public class HelpCommand implements JEGCommand {
    private final Plugin plugin;

    public HelpCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<String> onTabCompleteRaw(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("help");
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
                if ("help".equalsIgnoreCase(args[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        onHelp(sender);
    }

    private void onHelp(@NotNull CommandSender sender) {
        sender.sendMessage(Lang.getCommandSuccess("help"));
    }
}
