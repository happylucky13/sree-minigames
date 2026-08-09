package io.github.sree;

import io.github.sree.create_world.WorldService;
import io.github.sree.create_world.settings.DimensionKeys;
import io.github.sree.create_world.settings.DimensionSetSettings;
import io.github.sree.create_world.settings.WorldSettings;
import io.github.sree.pregenerate_world.ChunkGenerationSettings;
import io.github.sree.pregenerate_world.PregenerateChunksService;
import io.github.sree.pregenerate_world.enums.Pattern;
import io.github.sree.pregenerate_world.enums.Shape;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PrepareDimensionSet {

    private final WorldService worldService;
    private final PregenerateChunksService pregenerateChunksService;

    public PrepareDimensionSet(WorldService worldService, PregenerateChunksService pregenerateChunksService) {
        this.worldService = worldService;
        this.pregenerateChunksService = pregenerateChunksService;
    }

    public CompletableFuture<Set<World>> prepareDimensionSet(NamespacedKey worldKey, Logger logger) {

        DimensionKeys keys = DimensionKeys.from(worldKey);
        World overworld = Bukkit.getWorld(keys.overworld());
        World nether = Bukkit.getWorld(keys.nether());
        World theEnd = Bukkit.getWorld(keys.theEnd());

        if (overworld != null && nether != null && theEnd != null) {
            logger.info("Worlds already exist.");
            return CompletableFuture.completedFuture(Set.of(overworld, nether, theEnd));
        }

        if (overworld != null || nether != null || theEnd != null) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException(
                            "Dimension set is only partially loaded."
                    )
            );
        }

        return worldService.createDimensionSet(
                        new DimensionSetSettings(
                                new WorldSettings(
                                        keys.overworld(),
                                        WorldType.NORMAL,
                                        World.Environment.NORMAL,
                                        GameMode.SURVIVAL
                                ),
                                new WorldSettings(
                                        keys.nether(),
                                        WorldType.NORMAL,
                                        World.Environment.NETHER,
                                        GameMode.SURVIVAL
                                ),
                                new WorldSettings(
                                        keys.theEnd(),
                                        WorldType.NORMAL,
                                        World.Environment.THE_END,
                                        GameMode.SURVIVAL
                                )
                        )
                )
                .thenCompose(dimensionSet ->
                        pregenerateChunksService.pregenerate(
                                dimensionSet.worlds(),
                                new ChunkGenerationSettings(
                                        Shape.CIRCLE,
                                        0.0,
                                        0.0,
                                        1500,
                                        1500,
                                        Pattern.REGION
                                ),
                                Bukkit.getOnlinePlayers().stream()
                                        .filter(Player::isOp)
                                        .collect(Collectors.toSet())
                        )
                )
                .thenApply(
                        worlds -> {
                            worldService.linkWorlds(worlds, worldKey.getKey() + "_group");
                            return worlds;
                        }
                )
                .exceptionally(throwable -> {
                    logger.severe(
                            "Failed to prepare world " + worldKey + ": " + throwable.getMessage()
                    );
                    throw new CompletionException(throwable);
                });
    }
}
