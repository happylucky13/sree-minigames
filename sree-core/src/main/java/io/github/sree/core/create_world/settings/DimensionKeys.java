package io.github.sree.core.create_world.settings;

import org.bukkit.NamespacedKey;

public record DimensionKeys(NamespacedKey overworld, NamespacedKey nether, NamespacedKey theEnd) {
    public static DimensionKeys from(NamespacedKey base) {
        return new DimensionKeys(
                base,
                new NamespacedKey(base.getNamespace(), base.getKey() + "_nether"),
                new NamespacedKey(base.getNamespace(), base.getKey() + "_the_end")
        );
    }

    public NamespacedKey getBukkitWorldKey(NamespacedKey key) {
        return new NamespacedKey("minecraft", key.getKey());
    }
}
