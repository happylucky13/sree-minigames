package io.github.sree.core.local_chat;

import io.github.sree.core.information.InformationChannel;
import io.github.sree.core.information.InformationService;
import org.bukkit.entity.Player;

public class ChatManager {
    private final InformationService informationService;

    public ChatManager(InformationService informationService) {
        this.informationService = informationService;
    }

    /**
     *  The maximum distance in blocks that local chat should be 'heard' from.
     *  <p>Values below 0 are considered disabled.</p>
     */
    private int chatRange = -1;

    /**
     * Get whether the <b>targetPlayer</b> should be able to see a chat message from <b>sourcePlayer</b>
     *
     * @param sourcePlayer The player the message originates from
     * @param targetPlayer The player receiving the message
     * @return Whether the <b>targetPlayer</b> can see this message
     */
    public boolean playerCanSee(Player sourcePlayer, Player targetPlayer) {
        if (chatRange < 0) {
            // local chat is disabled, all chat is global
            return true;
        }

        if (informationService.allows(targetPlayer, InformationChannel.ALL_CHAT)) {
            return true;
        }

        if (
                informationService.allows(sourcePlayer, InformationChannel.GLOBAL_CHAT)
                && informationService.allows(targetPlayer, InformationChannel.GLOBAL_CHAT)
        ) return true;

        double distanceFromSource = sourcePlayer.getLocation().distance(targetPlayer.getLocation());

        return informationService.allows(sourcePlayer, InformationChannel.LOCAL_CHAT)
                && informationService.allows(targetPlayer, InformationChannel.LOCAL_CHAT)
                && distanceFromSource <= chatRange;
    }

    public boolean canWhisperTo(Player sourcePlayer, Player targetPlayer) {
        if (chatRange < 0) {
            return true;
        }

        if (
                informationService.allows(sourcePlayer, InformationChannel.LOCAL_CHAT)
                && informationService.allows(targetPlayer, InformationChannel.LOCAL_CHAT)
        ) return true;

        if (
                informationService.allows(sourcePlayer, InformationChannel.GLOBAL_CHAT)
                && informationService.allows(targetPlayer, InformationChannel.GLOBAL_CHAT)
        ) return true;

        return informationService.allows(sourcePlayer, InformationChannel.ALL_CHAT)
                && informationService.allows(targetPlayer, InformationChannel.ALL_CHAT);
    }

    public int getChatRange() {
        return chatRange;
    }
    public void setChatRange(int value) {
        chatRange = value;
    }
}
