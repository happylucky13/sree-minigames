package io.github.sree;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import io.github.sree.create_world.WorldService;
import io.github.sree.pregenerate_world.PregenerateChunksService;
import io.github.sree.spectators.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.popcraft.chunky.api.ChunkyAPI;

public class SreeCorePlugin extends JavaPlugin {

    private WorldService worldService;
    private PregenerateChunksService pregenerateChunksService;
    private PrepareDimensionSet prepareDimensionSet;
    private SpectatorService spectatorService;
    private SreeVoiceChatPlugin voiceChatPlugin;

    @Override
    public void onEnable() {
        spectatorService = new SpectatorService();

        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service != null) {
            voiceChatPlugin = new SreeVoiceChatPlugin(spectatorService);
            service.registerPlugin(voiceChatPlugin);
        }

        getLogger().info("sree-core initiated");
        worldService = new WorldService(MultiverseCoreApi.get(), getLogger());
        pregenerateChunksService = new PregenerateChunksService(Bukkit.getServer().getServicesManager().load(ChunkyAPI.class), getLogger());
        prepareDimensionSet = new PrepareDimensionSet(worldService, pregenerateChunksService);

        getServer().getPluginManager().registerEvents(new DimensionSwitchListener(spectatorService), this);
    }

    public WorldService getWorldService() {
        return worldService;
    }
    public PregenerateChunksService getPregenerateChunksService() {
        return pregenerateChunksService;
    }
    public PrepareDimensionSet prepareDimensionSet() {
        return prepareDimensionSet;
    }
    public SpectatorService spectatorService() {
        return spectatorService;
    }
}
