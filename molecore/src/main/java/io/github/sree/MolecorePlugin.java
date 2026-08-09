package io.github.sree;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.sree.commands.MolecoreSettingsCommand;
import io.github.sree.commands.MolecoreStartCommand;
import io.github.sree.commands.MolecoreWorldCommand;
import io.github.sree.create_world.WorldService;
import io.github.sree.listeners.*;
import io.github.sree.pregenerate_world.PregenerateChunksService;
import io.github.sree.state.GameAnimationManager;
import io.github.sree.state.GameManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MolecorePlugin extends JavaPlugin {


    @Override
    public void onEnable() {
        final SreeCorePlugin sreeCore = (SreeCorePlugin) getServer().getPluginManager().getPlugin("sree-core");

        if (sreeCore == null) {
            throw new IllegalStateException("sree-core is not loaded!");
        }

        PrepareDimensionSet prepareDimensionSet = sreeCore.getPrepareDimensionSet();

        getLogger().info("Plugin started.");
        GameAnimationManager animationManager = new GameAnimationManager(this);
        GameManager gameManager = new GameManager(this, animationManager, prepareDimensionSet);


        List<GameListener> listeners = List.of(
                new EndermanDeathListener(gameManager.getGameState()),
                new ObjectiveListener(gameManager.getGameState(), gameManager),
                new PiglinBarterListener(gameManager.getGameState()),
                new PlayerDeathListener(gameManager.getGameState(), gameManager),
                new WitherSkeletonDeathListener(gameManager.getGameState())
        );

        listeners.forEach(gameListener ->
                getServer().getPluginManager().registerEvents(gameListener, this));

        MolecoreSettingsCommand settingsCommand = new MolecoreSettingsCommand(gameManager.getGameState());
        MolecoreStartCommand startCommand = new MolecoreStartCommand(gameManager);
        MolecoreWorldCommand worldCommand = new MolecoreWorldCommand(gameManager);

        LiteralCommandNode<CommandSourceStack> molecoreCommand = Commands.literal("molecore")
                .then(settingsCommand.createCommand())
                .then(startCommand.createCommand())
                .then(worldCommand.createCommand())
                .build();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(molecoreCommand);
        });
    }
}