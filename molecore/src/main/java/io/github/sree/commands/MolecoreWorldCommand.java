package io.github.sree.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sree.state.GameManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class MolecoreWorldCommand {

    private final GameManager gameManager;

    public MolecoreWorldCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("world")
                .then(Commands.literal("generate")
                        .then(Commands.argument("world-name", StringArgumentType.word())
                                .executes(this::createWorld)
                        )
                );
    }

    private int createWorld(CommandContext<CommandSourceStack> ctx) {
        String worldName = ctx.getArgument("world-name", String.class);
        gameManager.prepareDimensionSet(gameManager.getWorldKey(worldName));
        return Command.SINGLE_SUCCESS;
    }
}
