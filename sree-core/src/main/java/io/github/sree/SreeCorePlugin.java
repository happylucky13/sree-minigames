package io.github.sree;

import io.github.sree.create_world.WorldService;
import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.inventories.MultiverseInventoriesApi;

public class SreeCorePlugin extends JavaPlugin {

    private WorldService worldService;

    @Override
    public void onEnable() {
        getLogger().info("sree-core initiated");
        worldService = new WorldService(MultiverseCoreApi.get(), MultiverseInventoriesApi.get(), getLogger());


    }

    public WorldService getWorldService() {
        return worldService;
    }

}
