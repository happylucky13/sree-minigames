package io.github.sree.information.listeners;

import io.github.sree.information.InformationHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final InformationHandler informationHandler;

    public PlayerDeathListener(InformationHandler informationHandler) {
        this.informationHandler = informationHandler;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        informationHandler.handleDeathMessage(event);
    }
}
