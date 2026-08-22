package io.github.sree.soulswap.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.github.sree.core.SreeCorePlugin
import io.github.sree.soulswap.GameManager
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.NamespacedKey

internal class PregenerateCommand(val core: SreeCorePlugin, val gameManager: GameManager) {
    fun createCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("pregenerate")
            .then(Commands.argument("world-name", StringArgumentType.word()))
    }

    private fun pregenerateWorlds(ctx: CommandContext<CommandSourceStack>): Int {
        val worldKey: NamespacedKey = gameManager.getWorldKey(
            ctx.getArgument("world-name", String::class.java)
        )

        val prepareDimensionSet = core.prepareDimensionSet()
        prepareDimensionSet.prepareDimensionSet(worldKey)

        return Command.SINGLE_SUCCESS
    }
}