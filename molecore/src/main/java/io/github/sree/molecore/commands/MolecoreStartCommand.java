package io.github.sree.molecore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sree.molecore.state.GameManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class MolecoreStartCommand {
    private final GameManager gameManager;

    public MolecoreStartCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("start")
                .then(Commands.argument("world", StringArgumentType.word())
                        .executes(this::startGame)
                );
    }

    private int startGame(CommandContext<CommandSourceStack> ctx) {
        String worldName = ctx.getArgument("world", String.class);
        gameManager.startGame(gameManager.getWorldKey(worldName));
        return Command.SINGLE_SUCCESS;
    }
}
