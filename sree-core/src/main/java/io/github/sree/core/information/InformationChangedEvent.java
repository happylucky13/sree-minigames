package io.github.sree.core.information;

import org.bukkit.entity.Player;

import java.util.EnumSet;

public record InformationChangedEvent(Player player, EnumSet<InformationChannel> channels) {
}