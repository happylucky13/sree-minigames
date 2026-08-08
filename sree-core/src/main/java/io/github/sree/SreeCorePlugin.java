package io.github.sree;

import io.github.sree.create_world.WorldService;
import io.github.sree.pregenerate_world.PregenerateChunksService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.inventories.MultiverseInventoriesApi;
import org.popcraft.chunky.api.ChunkyAPI;

public class SreeCorePlugin extends JavaPlugin {

    private WorldService worldService;
    private PregenerateChunksService pregenerateChunksService;

    @Override
    public void onEnable() {
        getLogger().info("sree-core initiated");
        worldService = new WorldService(MultiverseCoreApi.get(), MultiverseInventoriesApi.get(), getLogger());
        pregenerateChunksService = new PregenerateChunksService(Bukkit.getServer().getServicesManager().load(ChunkyAPI.class), getLogger());


    }

    public WorldService getWorldService() {
        return worldService;
    }

}
