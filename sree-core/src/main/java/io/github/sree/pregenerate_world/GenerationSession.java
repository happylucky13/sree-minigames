package io.github.sree.pregenerate_world;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent;
import org.popcraft.chunky.api.event.task.GenerationProgressEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class GenerationSession {

    private final Set<World> worlds;
    private final Map<World, Float> progress = new HashMap<>();
    private final Set<World> completedWorlds = new HashSet<>();
    private final Collection<Player> viewers;
    private final CompletableFuture<Set<World>> future = new CompletableFuture<>();
    private final BossBar progressBar;
    private final Logger logger;

    public GenerationSession(Collection<World> worlds, Collection<Player> viewers, Logger logger) {
        this.worlds = Set.copyOf(worlds);
        this.logger = logger;
        this.viewers = viewers;

        worlds.forEach(world -> progress.put(world, 0.0f));

         progressBar = BossBar.bossBar(
                Component.text("Generating worlds... 0.0% complete"),
                0.0f,
                BossBar.Color.GREEN,
                BossBar.Overlay.PROGRESS
        );

        viewers.forEach(progressBar::addViewer);
    }

    public CompletableFuture<Set<World>> getFuture() {
        return future;
    }

    public void start(ChunkyAPI chunkyAPI, ChunkGenerationSettings settings) {
        worlds.forEach(world ->
                chunkyAPI.startTask(
                        world.getName(),
                        settings.shape().getName(),
                        settings.centerX(),
                        settings.centerZ(),
                        settings.radiusX(),
                        settings.radiusZ(),
                        settings.pattern().getName()
                )
        );
    }

    public void handleProgress(GenerationProgressEvent event) {
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
    }

    public void handleComplete(GenerationCompleteEvent event) {
        World world = Bukkit.getWorld(event.world());

        if (world == null) {
            logger.warning("Could not find Bukkit world for: " + event.world());
            return;
        }

        if (!worlds.contains(world)) {
            logger.warning("Completed world wasn't in requested set: " + world.getName());
            return;
        }

        completedWorlds.add(world);
        progress.put(world, 1.0f);

        float overallProgress = (float) progress.values().stream()
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);

        progressBar.progress(overallProgress);

        if (completedWorlds.size() == worlds.size()) {
            future.complete(completedWorlds);
            viewers.forEach(progressBar::removeViewer);
        }
    }


}
