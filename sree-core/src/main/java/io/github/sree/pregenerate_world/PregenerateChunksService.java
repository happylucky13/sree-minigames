package io.github.sree.pregenerate_world;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.popcraft.chunky.api.ChunkyAPI;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class PregenerateChunksService {

    private final ChunkyAPI chunky;
    private final Logger logger;

    public PregenerateChunksService(ChunkyAPI chunky, Logger logger) {
        this.chunky = chunky;
        this.logger = logger;
    }

    public CompletableFuture<Set<World>> pregenerate(Collection<World> worlds, ChunkGenerationSettings settings, Collection<Player> viewers) {
        CompletableFuture<Set<World>> future = new CompletableFuture<>();
        Set<World> completedWorlds = new HashSet<>();

        Map<World, Float> progress = new HashMap<>();
        worlds.forEach(world -> progress.put(world, 0.0f));

        final BossBar progressBar = BossBar.bossBar(
                Component.text("Generating worlds... 0.0% complete"),
                0.0f,
                BossBar.Color.GREEN,
                BossBar.Overlay.PROGRESS
        );

        viewers.forEach(progressBar::addViewer);

        chunky.onGenerationProgress(event -> {
            World world = Bukkit.getWorld(event.world());

            if (world == null || !worlds.contains(world)) {
                return;
            }

            progress.put(world, event.progress());

            float overallProgress = (float) progress.values().stream()
                                    .mapToDouble(Float::doubleValue)
                                    .average()
                                    .orElse(0.0);

            progressBar.progress(overallProgress);
            progressBar.name(Component.text(
                    String.format("Generating worlds... %.1f%% complete", overallProgress * 100)
            ));
        });

        chunky.onGenerationComplete(event -> {
            logger.info("Chunk generation complete.");
            World world = Bukkit.getWorld(event.world());

            if (world == null || !worlds.contains(world)) {
                return;
            }

            completedWorlds.add(world);

            if (completedWorlds.size() == worlds.size()) {
                future.complete(completedWorlds);
            }
        });

        worlds.forEach(world -> chunky.startTask(
                world.getName(),
                settings.shape().getName(),
                settings.centerX(), settings.centerZ(),
                settings.radiusX(),
                settings.radiusZ(),
                settings.pattern().getName()
        ));

        return future;
    }
}
