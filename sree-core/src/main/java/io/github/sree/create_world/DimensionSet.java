package io.github.sree.create_world;

import org.bukkit.World;

import java.util.List;

public record DimensionSet(World overworld, World nether, World theEnd) {
    public List<World> worlds() {
        return List.of(overworld, nether, theEnd);
    }
}
