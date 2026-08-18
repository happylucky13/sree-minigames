package io.github.sree.core;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import io.github.sree.core.animations.CountdownManager;
import io.github.sree.core.animations.TimerManager;
import io.github.sree.core.combat_tag.CombatTagManager;
import io.github.sree.core.combat_tag.PlayerDamageListener;
import io.github.sree.core.create_world.WorldService;
import io.github.sree.core.information.InformationEnforcer;
import io.github.sree.core.information.InformationService;
import io.github.sree.core.information.listeners.PlayerDeathListener;
import io.github.sree.core.local_chat.ChatManager;
import io.github.sree.core.local_chat.listeners.BlockedCommandListener;
import io.github.sree.core.local_chat.listeners.PlayerChatListener;
import io.github.sree.core.local_chat.listeners.PrivateMessageListener;
import io.github.sree.core.pregenerate_world.PregenerateChunksService;
import io.github.sree.core.spectators.GameModeChangeListener;
import io.github.sree.core.spectators.SpectatorService;
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
    private ChatManager chatManager;
    private PluginCoroutines coroutines;
    private TimerManager timers;
    private CountdownManager countdowns;

    @Override
    public void onEnable() {
        spectatorService = new SpectatorService(this);

        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service != null) {
            voiceChatPlugin = new SreeVoiceChatPlugin(spectatorService);
            service.registerPlugin(voiceChatPlugin);
        }

        getLogger().info("sree-core initialized");

        coroutines = new PluginCoroutines(this);
        worldService = new WorldService(MultiverseCoreApi.get(), getLogger());
        pregenerateChunksService = new PregenerateChunksService(Bukkit.getServer().getServicesManager().load(ChunkyAPI.class), getLogger());
        prepareDimensionSet = new PrepareDimensionSet(worldService, pregenerateChunksService);
        informationService = new InformationService();
        informationEnforcer = new InformationEnforcer(informationService, this);
        combatTagManager = new CombatTagManager(this);
        chatManager = new ChatManager(informationService);
        timers = new TimerManager(coroutines.getScope());
        countdowns = new CountdownManager(coroutines.getScope());

        List<Listener> listeners = List.of(
                new PlayerDeathListener(informationEnforcer),
                new GameModeChangeListener(spectatorService),
                new PlayerDamageListener(combatTagManager),
                new PlayerChatListener(chatManager),
                new PrivateMessageListener(chatManager),
                new BlockedCommandListener()
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

    public ChatManager chatManager() {
        return chatManager;
    }

    public TimerManager timers() {
        return timers;
    }

    public CountdownManager countdowns() {
        return countdowns;
    }
}
