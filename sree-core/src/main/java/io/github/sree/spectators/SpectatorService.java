package io.github.sree.spectators;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpectatorService {

    private final Set<UUID> spectators = new HashSet<>();
    private SpectatorVoiceService voiceChat = new NoVoiceChat();

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

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());
        voiceChat.removeSpectator(player);
    }

    public void handleSpectatorDimensionChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (spectators.contains(player.getUniqueId())) {
            enforceSpectatorMode(player);
        }
    }
}
