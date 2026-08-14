package io.github.sree.listeners;

import io.github.sree.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockedCommandListener implements Listener {
    private final Pattern blockedCommandsPattern = Pattern.compile("^/(?:me|say|minecraft:me|minecraft:say) ([A-z0-9_]*) .*");

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        Matcher matcher = blockedCommandsPattern.matcher(event.getMessage());
        if (!matcher.find()) return;

        event.setCancelled(true);
        player.sendRichMessage("<red> This command is permanently disabled.");
    }
}
