package io.github.sree;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.sree.commands.MolecoreSettingsCommand;
import io.github.sree.commands.MolecoreStartCommand;
import io.github.sree.commands.MolecoreWorldCommand;
import io.github.sree.commands.SabotageCommand;
import io.github.sree.listeners.*;
import io.github.sree.animations.GameAnimationManager;
import io.github.sree.spectators.SpectatorService;
import io.github.sree.state.GameManager;
import io.github.sree.state.GameState;
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

        getLogger().info("Plugin started.");
        GameState gameState = new GameState();
        GameAnimationManager animationManager = new GameAnimationManager(this, gameState);
        GameManager gameManager = new GameManager(this, gameState, animationManager, sreeCore);


        List<GameListener> listeners = List.of(
                new EndermanDeathListener(gameState),
                new ObjectiveListener(gameState, gameManager),
                new PiglinBarterListener(gameState),
                new PlayerDeathListener(gameState, gameManager),
                new WitherSkeletonDeathListener(gameState),
                new InventoryClickListener(gameState)
        );

        listeners.forEach(gameListener ->
                getServer().getPluginManager().registerEvents(gameListener, this));

        MolecoreSettingsCommand settingsCommand = new MolecoreSettingsCommand(gameState);
        MolecoreStartCommand startCommand = new MolecoreStartCommand(gameManager);
        MolecoreWorldCommand worldCommand = new MolecoreWorldCommand(gameManager);

        LiteralCommandNode<CommandSourceStack> molecoreCommand = Commands.literal("molecore")
                .then(settingsCommand.createCommand())
                .then(startCommand.createCommand())
                .then(worldCommand.createCommand())
                .requires(ctx -> ctx.getSender().isOp())
                .build();

        LiteralCommandNode<CommandSourceStack> sabotageCommand = new SabotageCommand(gameManager).createCommand().build();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(molecoreCommand);
            commands.registrar().register(sabotageCommand);
        });
    }
}