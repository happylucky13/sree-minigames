package io.github.sree.core.pregenerate_world;

import io.github.sree.core.create_world.DimensionSet;
import org.bukkit.entity.Player;
import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent;
import org.popcraft.chunky.api.event.task.GenerationProgressEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class PregenerateChunksService {

    private final ChunkyAPI chunky;
    private final Logger logger;

    private volatile GenerationSession currentSession;

    public PregenerateChunksService(ChunkyAPI chunky, Logger logger) {
        this.chunky = chunky;
        this.logger = logger;
        chunky.onGenerationProgress(this::handleProgress);
        chunky.onGenerationComplete(this::handleComplete);
    }

    public CompletableFuture<DimensionSet> pregenerate(DimensionSet dimensionSet, ChunkGenerationSettings settings, Collection<Player> viewers) {
        if (currentSession != null) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException("Chunk generation already in progress.")
            );
        }

        GenerationSession session = new GenerationSession(dimensionSet, viewers, logger);
        currentSession = session;

        try {
            session.start(chunky, settings);
        } catch (Exception e) {
            currentSession = null;
            return CompletableFuture.failedFuture(e);
        }

        return session.getFuture()
                .whenComplete((result, throwable) -> {
                    if (currentSession == session) {
                        currentSession = null;
                    }
                });
    }

    private void handleProgress(GenerationProgressEvent event) {
        if (currentSession != null) {
            currentSession.handleProgress(event);
        }
    }

    private void handleComplete(GenerationCompleteEvent event) {
        if (currentSession != null) {
            currentSession.handleComplete(event);
        }
    }
}
