package io.github.sree.soulswap

import io.github.sree.core.SreeCorePlugin
import io.github.sree.soulswap.commands.StartCommand
import io.github.sree.soulswap.state.GameState
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class SoulSwapPlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("Plugin initialized")

        val sreeCore = server.pluginManager.getPlugin("sree-core") as SreeCorePlugin
        val gameState = GameState()
        val gameManager = GameManager(gameState, this, sreeCore)

        val listeners: List<Listener> = listOf(
            PlayerDeathListener(gameManager, gameState)
        )

        listeners.forEach { listener -> server.pluginManager.registerEvents(listener, this) }

        val soulswapCommand = Commands.literal("soulswap")
            .then(StartCommand(gameManager).createCommand())
            .build()

        this.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { commands ->
            commands.registrar().register(soulswapCommand)
        }
    }
}