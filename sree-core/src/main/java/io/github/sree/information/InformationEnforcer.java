package io.github.sree.information;

import io.github.sree.SreeCorePlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class InformationEnforcer {

    private final InformationService informationService;
    private final SreeCorePlugin plugin;

    public InformationEnforcer(InformationService informationService, SreeCorePlugin plugin) {
        this.informationService = informationService;
        this.plugin = plugin;

        informationService.addListener(this::handleInformationChange);
    }

    public void handleInformationChange(InformationChangedEvent event) {
        Player player = event.player();

        plugin.getLogger().info("Information change received for " + player.getName());

        if (event.channels().contains(InformationChannel.LOCATOR_BAR)) {
            updateLocatorBar(player, informationService.allows(player, InformationChannel.LOCATOR_BAR));
        }

        if (event.channels().contains(InformationChannel.TAB_LIST)) {
            Bukkit.getScheduler().runTask(plugin, () ->
                    updateTabList(player, informationService.allows(player, InformationChannel.TAB_LIST)));
        }
    }

    public void handleDeathMessage(PlayerDeathEvent event) {
        Component deathMessage = event.deathMessage();

        if (deathMessage == null) {
            return;
        }

        event.deathMessage(null);

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> informationService.allows(player, InformationChannel.DEATH_MESSAGES))
                .forEach(player -> player.sendMessage(deathMessage));
    }

    public void updateLocatorBar(Player player, boolean allowed) {
        AttributeInstance attr = player.getAttribute(Attribute.WAYPOINT_RECEIVE_RANGE);
        if (attr != null) {
            attr.setBaseValue(allowed ? 60000000.0 : 0.0);
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.equals(player)) continue;

            player.hidePlayer(plugin, onlinePlayer);

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (player.isOnline() && onlinePlayer.isOnline()) {
                    player.showPlayer(plugin, onlinePlayer);
                }
            });
        }
    }

    public void updateTabList(Player player, boolean allowed) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.equals(player)) {
                continue;
            }

            if (allowed) {
                if (!player.isListed(onlinePlayer)) {
                    player.listPlayer(onlinePlayer);
                }
            }
            else {
                if (player.isListed(onlinePlayer)) {
                    player.unlistPlayer(onlinePlayer);
                }
            }
            plugin.getLogger().info("Tab list hidden!");
        }
    }
}