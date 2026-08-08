package io.github.sree.create_world.settings;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldType;

public record WorldSettings(NamespacedKey key, WorldType type, World.Environment environment, GameMode defaultGameMode) {
}
