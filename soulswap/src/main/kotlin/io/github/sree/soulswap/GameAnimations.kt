package io.github.sree.soulswap

import com.destroystokyo.paper.ParticleBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.EntityEffect
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player

internal class GameAnimations {

}

internal fun Player.reviveAnimation() {
    ParticleBuilder(Particle.TOTEM_OF_UNDYING).apply {
        location(location)
        count(50)
        offset(0.5, 1.0, 0.5)
        extra(0.1)
        spawn()
    }

    this.playEffect(EntityEffect.PROTECTED_FROM_DEATH)
    Bukkit.getServer().broadcast(Component.text(this.name + " HAS REVIVED!", NamedTextColor.GOLD))

}

internal fun Player.finalDeathAnimation() {
    Bukkit.getLogger().info("IT FREAKING RAN AT LEAST")

    ParticleBuilder(Particle.LARGE_SMOKE).apply {
        location(location)
        count(50)
        offset(0.7, 1.0, 0.7)
        extra(0.05)
        force(true)
        spawn()
    }

    ParticleBuilder(Particle.SOUL).apply {
        location(location)
        count(20)
        offset(0.5, 0.8, 0.5)
        extra(0.1)
        force(true)
        spawn()
    }

    Bukkit.getOnlinePlayers().forEach {
        it.playSound(
            this.location,
            Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE,
            1.0f,
            1.0f
        )
    }

    Bukkit.getServer().broadcast(Component.text("$name HAS BEEN ELIMINATED!", NamedTextColor.DARK_RED))
}