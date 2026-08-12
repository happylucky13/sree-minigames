package io.github.sree.spectators;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpectatorService {

    private final Set<UUID> spectators = new HashSet<>();
    private final VoicechatServerApi voicechat;
    private final Group deadPlayersGroup;

    public SpectatorService(VoicechatServerApi voicechat) {
        this.voicechat = voicechat;

        deadPlayersGroup = this.voicechat.groupBuilder()
                .setName("Dead players")
                .setHidden(true)
                .setPersistent(false)
                .build();
    }

    private void enforceSpectatorMode(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());
        addSpectatorToVoiceChatGroup(player);
        enforceSpectatorMode(player);
    }

    public void handleSpectatorDimensionChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (spectators.contains(player.getUniqueId())) {
            enforceSpectatorMode(player);
        }
    }

    private void addSpectatorToVoiceChatGroup(Player player) {
        VoicechatConnection connection = voicechat.getConnectionOf(player.getUniqueId());

        if (connection == null) {
            return;
        }

        connection.setGroup(deadPlayersGroup);
    }
}
