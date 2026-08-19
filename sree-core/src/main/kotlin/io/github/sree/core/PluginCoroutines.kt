package io.github.sree.core

import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.github.shynixn.mccoroutine.bukkit.scope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class PluginCoroutines(plugin: SreeCorePlugin) {
    val scope: CoroutineScope = plugin.scope
    val mainDispatcher: CoroutineContext = plugin.minecraftDispatcher
}