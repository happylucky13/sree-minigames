package io.github.sree.molecore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sree.molecore.state.GameManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SabotageCommand {

    private final GameManager gameManager;

    public SabotageCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("sabotage")
                .executes(this::sabotage);
    }

    private int sabotage(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (sender instanceof Player player) {
            gameManager.executeSabotage(player);
            return Command.SINGLE_SUCCESS;
        }

        sender.sendPlainMessage("You may only execute this command as a player!");
        return 0;
    }
}
