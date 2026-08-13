package io.github.sree.listeners;

import io.github.sree.state.GameManager;

import io.github.sree.state.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener extends GameListener {

    private final GameManager gameManager;

    public PlayerDeathListener(GameState gameState, GameManager gameManager) {
        super(gameState);
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getDamageSource().getCausingEntity() instanceof Player attacker && event.getEntity() instanceof Player target) {
            double damageDealt = event.getDamage();
            gameManager.markCombat(attacker, target, damageDealt);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!gameState.isGameStarted()) {
            return;
        }

        gameManager.handlePlayerDeath(event);
    }
}