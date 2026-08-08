package io.github.sree;

import org.bukkit.World;

public record DimensionSet(World overworld, World nether, World theEnd) {
}
