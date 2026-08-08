package io.github.sree;

import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.inventories.profile.group.WorldGroupManager;

public class SreeCorePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("sree-core initiated");
        WorldService worldService = new WorldService(MultiverseCoreApi.get(), getLogger());

    }

}
