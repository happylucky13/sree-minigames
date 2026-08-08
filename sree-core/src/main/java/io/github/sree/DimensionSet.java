package io.github.sree;

import org.bukkit.World;

import java.util.List;

public record DimensionSet(World overworld, World nether, World theEnd) {
    public List<World> worlds() {
        return List.of(overworld, nether, theEnd);
    }
}
