package io.github.sree.core;

import io.github.sree.core.create_world.WorldService;
import io.github.sree.core.create_world.settings.DimensionKeys;
import io.github.sree.core.create_world.settings.DimensionSetSettings;
import io.github.sree.core.create_world.settings.WorldSettings;
import io.github.sree.core.pregenerate_world.ChunkGenerationSettings;
import io.github.sree.core.pregenerate_world.PregenerateChunksService;
import io.github.sree.core.pregenerate_world.enums.Pattern;
import io.github.sree.core.pregenerate_world.enums.Shape;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PrepareDimensionSet {

    private final Logger logger;
    private final WorldService worldService;
    private final PregenerateChunksService pregenerateChunksService;

    public PrepareDimensionSet(Logger logger, WorldService worldService, PregenerateChunksService pregenerateChunksService) {
        this.logger = logger;
        this.worldService = worldService;
        this.pregenerateChunksService = pregenerateChunksService;
    }

    public CompletableFuture<World> prepareDimensionSet(NamespacedKey worldKey) {
        DimensionKeys keys = DimensionKeys.from(worldKey);
        World overworld = Bukkit.getWorld(keys.getBukkitWorldKey(keys.overworld()));
        World nether = Bukkit.getWorld(keys.getBukkitWorldKey(keys.nether()));
        World theEnd = Bukkit.getWorld(keys.getBukkitWorldKey(keys.theEnd()));

        Bukkit.getWorlds().forEach(world ->
                logger.info(
                        "Loaded world: name=" + world.getName()
                                + ", key=" + world.getKey()
                )
        );

        if (overworld != null && nether != null && theEnd != null) {
            logger.info("Worlds already exist.");
            return CompletableFuture.completedFuture(overworld);
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
                                dimensionSet,
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
                        dimensionSet -> {
                            worldService.linkWorlds(dimensionSet, worldKey.getKey() + "_group");
                            return dimensionSet.overworld();
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
