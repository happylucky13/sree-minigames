package io.github.sree.pregenerate_world;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.popcraft.chunky.api.ChunkyAPI;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class PregenerateChunksService {

    private final ChunkyAPI chunky;
    private final Logger logger;

    public PregenerateChunksService(ChunkyAPI chunky, Logger logger) {
        this.chunky = chunky;
        this.logger = logger;
    }

    public CompletableFuture<World> pregenerate(World world, ChunkGenerationSettings settings, Collection<Player> viewers) {
        CompletableFuture<World> future = new CompletableFuture<>();

        final BossBar progressBar = BossBar.bossBar(
                Component.text("0 chunks loaded"),
                0.0f,
                BossBar.Color.GREEN,
                BossBar.Overlay.PROGRESS
        );

        viewers.forEach(progressBar::addViewer);

        chunky.onGenerationProgress(event -> {
            progressBar.name(Component.text(event.chunks() + " chunks loaded"));
            progressBar.progress(event.progress());
        });

        chunky.onGenerationComplete(event -> {
            logger.info("Chunk generation complete.");
            future.complete(world);
        });

        chunky.startTask(
                world.getName(),
                settings.shape().getName(),
                settings.centerX(), settings.centerZ(),
                settings.radiusX(),
                settings.radiusZ(),
                settings.pattern().getName()
        );

        return future;
    }
}
