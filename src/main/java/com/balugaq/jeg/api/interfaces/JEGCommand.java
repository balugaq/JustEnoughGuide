package com.balugaq.jeg.api.interfaces;

import com.balugaq.jeg.core.commands.JEGCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface is used to define a command that can be executed by JEG.
 * Used by {@link JEGCommands}.
 *
 * @author balugaq
 * @since 1.1
 */
public interface JEGCommand {
    /**
     * This method is used to define a tab complete for a command.
     *
     * @param sender The sender of the command.
     * @param args   The arguments of the command.
     * @return The tab complete of the command.
     */
    @NotNull
    default List<String> onTabCompleteRaw(@NotNull CommandSender sender, @NotNull String[] args) {
        return new ArrayList<>();
    }

    /**
     * This method is used to define if a command can be executed by a player.
     *
     * @param sender  The sender of the command.
     * @param command The command that is being executed.
     * @param label   The label of the command.
     * @param args    The arguments of the command.
     * @return If the command can be executed.
     */
    default boolean canCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }

    /**
     * This method is used to define what happens when a command is executed.
     *
     * @param sender  The sender of the command.
     * @param command The command that is being executed.
     * @param label   The label of the command.
     * @param args    The arguments of the command.
     */
    void onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);
}
