package io.github.sree.core.local_chat.listeners;

import io.github.sree.core.local_chat.ChatManager;
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

        viewers.removeIf(viewer ->
                viewer instanceof Player viewingPlayer &&
                !chatManager.playerCanSee(sourcePlayer, viewingPlayer));
    }
}
