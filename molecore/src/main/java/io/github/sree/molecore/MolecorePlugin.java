package io.github.sree.molecore;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.sree.molecore.listeners.*;
import io.github.sree.core.SreeCorePlugin;
import io.github.sree.molecore.commands.MolecoreSettingsCommand;
import io.github.sree.molecore.commands.MolecoreStartCommand;
import io.github.sree.molecore.commands.MolecoreWorldCommand;
import io.github.sree.molecore.commands.SabotageCommand;
import io.github.sree.molecore.animations.GameAnimationManager;
import io.github.sree.molecore.listeners.*;
import io.github.sree.molecore.state.GameManager;
import io.github.sree.molecore.state.GameState;
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

        MolecoreSettingsCommand settingsCommand = new MolecoreSettingsCommand(gameState, sreeCore);
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