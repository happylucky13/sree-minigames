package io.github.sree.combat_tag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

    private final CombatTagManager combatTagManager;

    public PlayerDamageListener(CombatTagManager combatTagManager) {
        this.combatTagManager = combatTagManager;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        combatTagManager.handlePlayerDamage(event);
    }
}
