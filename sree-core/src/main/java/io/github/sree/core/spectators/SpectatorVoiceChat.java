package io.github.sree.core.spectators;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import org.bukkit.entity.Player;

public class SpectatorVoiceChat implements SpectatorVoiceService {
    private final VoicechatServerApi voicechat;
    private final Group deadPlayersGroup;

    public SpectatorVoiceChat(VoicechatServerApi voicechat) {
        this.voicechat = voicechat;

        deadPlayersGroup = this.voicechat.groupBuilder()
                .setName("Dead Players")
                .setHidden(true)
                .setPersistent(false)
                .build();
    }

    @Override
    public void addSpectator(Player player) {
        VoicechatConnection connection = voicechat.getConnectionOf(player.getUniqueId());

        if (connection == null) {
            return;
        }

        connection.setGroup(deadPlayersGroup);
    }

    @Override
    public void removeSpectator(Player player) {
        VoicechatConnection connection = voicechat.getConnectionOf(player.getUniqueId());

        if (connection == null) {
            return;
        }

        connection.setGroup(null);
    }
}
