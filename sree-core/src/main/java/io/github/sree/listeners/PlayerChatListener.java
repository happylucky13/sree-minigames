package io.github.sree.listeners;

import io.github.sree.ChatManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public record PlayerChatListener(ChatManager chatManager) implements Listener {
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player sourcePlayer = event.getPlayer();
        Set<Audience> viewers = event.viewers();

        for (Audience viewer : viewers) {
            if (viewer instanceof Player viewingPlayer) {
                if (!chatManager.playerCanSee(sourcePlayer, viewingPlayer)) {
                    event.viewers().remove(viewer);
                }
            }
        }
    }
}
