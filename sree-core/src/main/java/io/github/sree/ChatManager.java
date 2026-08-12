package io.github.sree;

import io.github.sree.information.InformationChannel;
import io.github.sree.information.InformationService;
import org.bukkit.entity.Player;

public class ChatManager {
    private final InformationService InformationService;

    public ChatManager(InformationService informationService) {
        InformationService = informationService;
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

        if (InformationService.allows(targetPlayer, InformationChannel.ALL_CHAT)) {
            return true;
        }
        if (
                InformationService.allows(sourcePlayer, InformationChannel.GLOBAL_CHAT)
                && InformationService.allows(targetPlayer, InformationChannel.GLOBAL_CHAT)
        ) {
            return true;
        }

        double distanceFromSource = sourcePlayer.getLocation().distance(targetPlayer.getLocation());

        return InformationService.allows(sourcePlayer, InformationChannel.LOCAL_CHAT)
                && InformationService.allows(targetPlayer, InformationChannel.LOCAL_CHAT)
                && distanceFromSource <= chatRange;
    }

    public int getChatRange() {
        return chatRange;
    }
    public void setChatRange(int value) {
        chatRange = value;
    }
}
