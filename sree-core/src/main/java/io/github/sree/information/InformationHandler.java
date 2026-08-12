package io.github.sree.information;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Set;
import java.util.stream.Collectors;

public class InformationHandler {

    private final InformationService informationService;

    public InformationHandler(InformationService informationService) {
        this.informationService = informationService;
    }

    public void handleDeathMessage(PlayerDeathEvent event) {
        Component deathMessage = event.deathMessage();

        if (deathMessage == null) {
            return;
        }

        event.deathMessage(null);

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> informationService.allows(player, InformationChannel.DEATH_MESSAGES))
                .forEach(player -> player.sendMessage(deathMessage));
    }
}