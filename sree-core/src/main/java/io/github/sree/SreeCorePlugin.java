package io.github.sree;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;

import io.github.sree.combat_tag.CombatTagManager;
import io.github.sree.create_world.WorldService;
import io.github.sree.information.InformationEnforcer;
import io.github.sree.information.InformationService;
import io.github.sree.information.listeners.PlayerDeathListener;
import io.github.sree.pregenerate_world.PregenerateChunksService;
import io.github.sree.spectators.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.popcraft.chunky.api.ChunkyAPI;

import java.util.List;

public class SreeCorePlugin extends JavaPlugin {

    private WorldService worldService;
    private PregenerateChunksService pregenerateChunksService;
    private PrepareDimensionSet prepareDimensionSet;
    private SpectatorService spectatorService;
    private SreeVoiceChatPlugin voiceChatPlugin;
    private InformationService informationService;
    private InformationEnforcer informationEnforcer;
    private CombatTagManager combatTagManager;

    @Override
    public void onEnable() {
        spectatorService = new SpectatorService(this);

        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service != null) {
            voiceChatPlugin = new SreeVoiceChatPlugin(spectatorService);
            service.registerPlugin(voiceChatPlugin);
        }

        getLogger().info("sree-core initialized");
      
        worldService = new WorldService(MultiverseCoreApi.get(), getLogger());
        pregenerateChunksService = new PregenerateChunksService(Bukkit.getServer().getServicesManager().load(ChunkyAPI.class), getLogger());
        prepareDimensionSet = new PrepareDimensionSet(worldService, pregenerateChunksService);
        informationService = new InformationService();
        informationEnforcer = new InformationEnforcer(informationService, this);
        combatTagManager = new CombatTagManager(this);

        List<Listener> listeners = List.of(
                new PlayerDeathListener(informationEnforcer),
                new DimensionSwitchListener(spectatorService)
        );

        listeners.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    public WorldService worldService() {
        return worldService;
    }

    public PregenerateChunksService pregenerateChunksService() {
        return pregenerateChunksService;
    }

    public PrepareDimensionSet prepareDimensionSet() {
        return prepareDimensionSet;
    }

    public SpectatorService spectatorService() {
        return spectatorService;
    }

    public InformationService informationService() {
        return informationService;
    }

    public CombatTagManager combatTagManager() {
        return combatTagManager;
    }
}
