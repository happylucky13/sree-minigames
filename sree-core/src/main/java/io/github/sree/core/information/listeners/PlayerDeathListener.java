package io.github.sree.core.information.listeners;

import io.github.sree.core.information.InformationEnforcer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final InformationEnforcer informationEnforcer;

    public PlayerDeathListener(InformationEnforcer informationEnforcer) {
        this.informationEnforcer = informationEnforcer;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        informationEnforcer.handleDeathMessage(event);
    }
}
