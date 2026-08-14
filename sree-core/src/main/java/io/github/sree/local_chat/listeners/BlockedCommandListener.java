package io.github.sree.local_chat.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockedCommandListener implements Listener {
    private final Pattern blockedCommandsPattern = Pattern.compile("^/(?:me|say|minecraft:me|minecraft:say) .*");

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        Matcher matcher = blockedCommandsPattern.matcher(event.getMessage());
        if (!matcher.find()) return;

        event.setCancelled(true);
        player.sendMessage(Component.text("This command is permanently disabled.", NamedTextColor.RED));
    }
}
