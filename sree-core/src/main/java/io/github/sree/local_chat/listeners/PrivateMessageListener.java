package io.github.sree.local_chat.listeners;

import io.github.sree.local_chat.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrivateMessageListener implements Listener {
    private final ChatManager ChatManager;

    public PrivateMessageListener(ChatManager chatManager) {
        ChatManager = chatManager;
    }

    private final Pattern privateMessagePattern = Pattern.compile("^/(?:tell|w|msg|minecraft:tell|minecraft:w|minecraft:msg) ([A-z0-9_]*) .*");

    @EventHandler
    public void onAttemptedPrivateMessage(PlayerCommandPreprocessEvent event) {
        Player sourcePlayer = event.getPlayer();

        // lord forgive me for the code I (peeblyweeb) am about to write
        Matcher matcher = privateMessagePattern.matcher(event.getMessage());
        if (!matcher.find()) return;

        String recipientUsername = matcher.group(1);
        Player targetPlayer = Bukkit.getServer().getPlayerExact(recipientUsername);
        if (targetPlayer == null) {
            return;
        }

        if (!ChatManager.playerCanSee(sourcePlayer, targetPlayer)) {
            event.setCancelled(true);
            sourcePlayer.sendRichMessage("<red>" + targetPlayer.getName() + " is too far away, they can't hear you!");
        }
    }
}
