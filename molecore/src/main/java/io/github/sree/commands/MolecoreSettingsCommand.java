package io.github.sree.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sree.enums.Objective;
import io.github.sree.state.GameState;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class MolecoreSettingsCommand {

    final GameState gameState;

    public MolecoreSettingsCommand(GameState gameState) {
        this.gameState = gameState;
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("settings")
                .then(Commands.argument("mole_count", IntegerArgumentType.integer(0, 3))
                        .then(Commands.literal("beacon")
                                .executes(this::setBeaconSettings)
                        )
                        .then(Commands.literal("dragon_egg")
                                .executes(this::setEggSettings)
                        )
                );
    }

    private int setBeaconSettings(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        int moleCount = ctx.getArgument("mole_count", Integer.class);

        gameState.setSettings(moleCount, Objective.WITHER);

        sender.sendMessage(Component.text("Settings updated!"));

        return Command.SINGLE_SUCCESS;
    }

    private int setEggSettings(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        int moleCount = ctx.getArgument("mole_count", Integer.class);

        gameState.setSettings(moleCount, Objective.DRAGON);

        sender.sendMessage(Component.text("Settings updated!"));

        return Command.SINGLE_SUCCESS;
    }
}
