package io.github.sree;

import io.github.sree.create_world.WorldService;
import io.github.sree.pregenerate_world.PregenerateChunksService;
import io.github.sree.spectators.DimensionSwitchListener;
import io.github.sree.spectators.SpectatorService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.inventories.MultiverseInventoriesApi;
import org.popcraft.chunky.api.ChunkyAPI;

public class SreeCorePlugin extends JavaPlugin {

    private WorldService worldService;
    private PregenerateChunksService pregenerateChunksService;
    private PrepareDimensionSet prepareDimensionSet;
    private SpectatorService spectatorService;

    @Override
    public void onEnable() {
        getLogger().info("sree-core initiated");
        worldService = new WorldService(MultiverseCoreApi.get(), MultiverseInventoriesApi.get(), getLogger());
        pregenerateChunksService = new PregenerateChunksService(Bukkit.getServer().getServicesManager().load(ChunkyAPI.class), getLogger());
        prepareDimensionSet = new PrepareDimensionSet(worldService, pregenerateChunksService);
        spectatorService = new SpectatorService();

        getServer().getPluginManager().registerEvents(new DimensionSwitchListener(spectatorService), this);


    }

    public WorldService getWorldService() {
        return worldService;
    }
    public PregenerateChunksService getPregenerateChunksService() {
        return pregenerateChunksService;
    }
    public PrepareDimensionSet getPrepareDimensionSet() {
        return prepareDimensionSet;
    }
    public SpectatorService getSpectatorService() {
        return spectatorService;
    }
}
