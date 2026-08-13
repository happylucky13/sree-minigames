package io.github.sree.spectators;

import io.github.sree.SreeCorePlugin;
import io.github.sree.information.InformationChannel;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

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
        Set<Player> players = spectators.stream().map(Bukkit::getPlayer).collect(Collectors.toSet());
        spectators.clear();

        players.forEach(voiceChat::removeSpectator);
    }

    public void handleSpectatorDimensionChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (spectators.contains(player.getUniqueId())) {
            Bukkit.getScheduler().runTask(plugin, () -> enforceSpectatorMode(player));
        }
    }

    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (spectators.contains(player.getUniqueId())) {
            voiceChat.addSpectator(player);
            Bukkit.getScheduler().runTask(plugin, () -> enforceSpectatorMode(player));
        }
    }
}
