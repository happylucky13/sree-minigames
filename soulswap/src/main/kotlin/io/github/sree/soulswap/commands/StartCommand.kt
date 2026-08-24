package io.github.sree.soulswap.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.github.sree.soulswap.GameManager
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import org.bukkit.World

internal class StartCommand(val gameManager: GameManager) {

    fun createCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("start")
            .then(Commands.argument("world", ArgumentTypes.world())
                .executes { ctx -> startGame(ctx) }
            )
    }

    private fun startGame(ctx: CommandContext<CommandSourceStack>): Int {
        val world: World = ctx.getArgument("world", World::class.java)
        gameManager.startGame(world.name)

        return Command.SINGLE_SUCCESS
    }
}