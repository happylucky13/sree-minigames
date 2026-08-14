package io.github.sree.core.create_world;

import io.github.sree.core.create_world.settings.DimensionSetSettings;
import io.github.sree.core.create_world.settings.WorldSettings;
import org.bukkit.World;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions;
import org.mvplugins.multiverse.inventories.MultiverseInventoriesApi;
import org.mvplugins.multiverse.inventories.profile.group.WorldGroup;
import org.mvplugins.multiverse.inventories.profile.group.WorldGroupManager;
import org.mvplugins.multiverse.inventories.share.Sharables;
import org.mvplugins.multiverse.netherportals.MultiverseNetherPortalsApi;
import org.mvplugins.multiverse.netherportals.links.LinksManager;
import org.mvplugins.multiverse.netherportals.links.WorldLinkType;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class WorldService {

    private final MultiverseCoreApi multiverse;
    private final WorldGroupManager groupManager = MultiverseInventoriesApi.get().getWorldGroupManager();
    private final Logger logger;

    public WorldService(MultiverseCoreApi multiverse, Logger logger) {
        this.multiverse = multiverse;
        this.logger = logger;

    }

    public CompletableFuture<World> createWorld(WorldSettings settings) {
        CompletableFuture<World> future = new CompletableFuture<>();

        multiverse.getWorldManager()
                .createWorld(
                CreateWorldOptions.worldName(settings.key().getKey())
                        .worldType(settings.type())
                        .environment(settings.environment())
                )
                .onFailure(reason -> {
                    logger.warning("World failed to create. " + reason);
                    future.completeExceptionally(new IllegalStateException("World creation failed: " + reason));
                        })
                .onSuccess(world -> {
                    world.setGameMode(settings.defaultGameMode());
                    future.complete(world.getBukkitWorld().get());
                });

        return future;
    }

    public CompletableFuture<DimensionSet> createDimensionSet(DimensionSetSettings settings) {
        CompletableFuture<World> overworld = createWorld(settings.overworld());
        CompletableFuture<World> nether = createWorld(settings.nether());
        CompletableFuture<World> end = createWorld(settings.theEnd());

        return CompletableFuture.allOf(overworld, nether, end)
                .thenApply(ignored -> new DimensionSet(
                        overworld.join(),
                        nether.join(),
                        end.join()
                ));
    }

    public void linkWorlds(DimensionSet dimensionSet, String groupName) {
        WorldGroup worldGroup = groupManager.newEmptyGroup(groupName);
        LinksManager linksManager = MultiverseNetherPortalsApi.get().getLinksManager();

        linksManager.addWorldLink(dimensionSet.overworld().getName(), dimensionSet.nether().getName(), WorldLinkType.NETHER);
        linksManager.addWorldLink(dimensionSet.nether().getName(), dimensionSet.overworld().getName(), WorldLinkType.NETHER);

        linksManager.addWorldLink(dimensionSet.overworld().getName(), dimensionSet.theEnd().getName(), WorldLinkType.END);
        linksManager.addWorldLink(dimensionSet.theEnd().getName(), dimensionSet.overworld().getName(), WorldLinkType.END);

        dimensionSet.worlds().forEach(worldGroup::addWorld);
        worldGroup.getShares().addAll(Sharables.allOf());

        groupManager.updateGroup(worldGroup);
    }
}
