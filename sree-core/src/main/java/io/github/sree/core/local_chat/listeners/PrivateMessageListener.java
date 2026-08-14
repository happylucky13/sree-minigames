package io.github.sree.core.local_chat.listeners;

import io.github.sree.core.local_chat.ChatManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrivateMessageListener implements Listener {
    private final ChatManager chatManager;

    public PrivateMessageListener(ChatManager chatManager) {
        this.chatManager = chatManager;
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

        if (!chatManager.playerCanSee(sourcePlayer, targetPlayer) || !chatManager.canWhisperTo(sourcePlayer, targetPlayer)) {
            event.setCancelled(true);
            sourcePlayer.sendMessage(
                    Component.text(targetPlayer.getName() + " can't hear you!", NamedTextColor.RED));
        }
    }
}
