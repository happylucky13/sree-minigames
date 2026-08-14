package io.github.sree.core.spectators;

import io.github.sree.core.SreeCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import java.util.*;
import java.util.stream.Collectors;

public class SpectatorService {

    private final Set<UUID> spectators = new HashSet<>();
    private SpectatorVoiceService voiceChat = new NoVoiceChat();
    private final SreeCorePlugin plugin;

    public SpectatorService(SreeCorePlugin plugin) {
        this.plugin = plugin;
    }

    public void setVoiceChat(SpectatorVoiceService voiceChat) {
        this.voiceChat = voiceChat;
    }

    private void enforceSpectatorMode(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());
        voiceChat.addSpectator(player);
        enforceSpectatorMode(player);
    }

    public Set<Player> getSpectators() {
        return spectators.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void removeAllSpectators() {
        Set<UUID> spectatorIds = new HashSet<>(spectators);
        spectators.clear();

        spectatorIds.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(voiceChat::removeSpectator);
    }

    public void handleGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (!spectators.contains(player.getUniqueId())) {
            return;
        }

        if (event.getNewGameMode() != GameMode.SPECTATOR) {
            event.setCancelled(true);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                return;
            }

            if (!spectators.contains(player.getUniqueId())) {
                return;
            }

            voiceChat.addSpectator(player);
        }, 10L);
    }
}
