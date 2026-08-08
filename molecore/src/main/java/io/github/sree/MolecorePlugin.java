package io.github.sree;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.sree.commands.MolecoreSettingsCommand;
import io.github.sree.commands.MolecoreStartCommand;
import io.github.sree.listeners.*;
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
        getLogger().info("Plugin started.");
        GameAnimationManager animationManager = new GameAnimationManager(this);
        GameManager gameManager = new GameManager(this, animationManager);

        List<GameListener> listeners = List.of(
                new EndermanDeathListener(gameManager),
                new ObjectiveListener(gameManager),
                new PiglinBarterListener(gameManager),
                new PlayerDeathListener(gameManager),
                new WitherSkeletonDeathListener(gameManager)
        );

        listeners.forEach(gameListener ->
                getServer().getPluginManager().registerEvents(gameListener, this));


        MolecoreSettingsCommand settingsCommand = new MolecoreSettingsCommand(gameManager);
        MolecoreStartCommand startCommand = new MolecoreStartCommand(gameManager);

        LiteralCommandNode<CommandSourceStack> molecoreCommand = Commands.literal("molecore")
                .then(settingsCommand.createCommand())
                .then(startCommand.createCommand())
                .build();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(molecoreCommand);
        });
    }
}