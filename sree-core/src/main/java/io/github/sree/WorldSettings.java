package io.github.sree;

import org.bukkit.GameMode;
import org.bukkit.WorldType;

public record WorldSettings(String name, WorldType type, GameMode defaultGameMode, String inventoryGroup) {
}
