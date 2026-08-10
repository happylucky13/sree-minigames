package io.github.sree.state;

import io.github.sree.MolecorePlugin;
import io.github.sree.PrepareDimensionSet;
import io.github.sree.enums.Role;
import io.github.sree.enums.Winner;

import io.papermc.paper.event.block.BeaconActivatedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class GameManager {
    private final MolecorePlugin plugin;
    private final GameAnimationManager animationManager;

    private final GameState gameState = new GameState();
    private final WinConditions winConditions = new WinConditions(gameState);

    private final PrepareDimensionSet prepareDimensionSet;


    public GameManager(MolecorePlugin plugin, GameAnimationManager animationManager, PrepareDimensionSet prepareDimensionSet) {
        this.plugin = plugin;
        this.animationManager = animationManager;
        this.prepareDimensionSet = prepareDimensionSet;
    }

    public GameState getGameState() {
        return gameState;
    }

    public NamespacedKey getWorldKey(String worldName) {
        return new NamespacedKey(plugin, worldName);
    }

    public CompletableFuture<World> prepareDimensionSet(NamespacedKey worldKey) {
        return prepareDimensionSet.prepareDimensionSet(worldKey, plugin.getLogger());
    }

    private void teleportPlayers(World world) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleportAsync(world.getSpawnLocation());
        }
    }

    public void assignRoles() {
        List<Player> shuffledPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(shuffledPlayers);

        for(int i = 0; i < shuffledPlayers.size(); i++) {
            UUID id = shuffledPlayers.get(i).getUniqueId();

            if(i < gameState.getSettings().moleCount()) {
                gameState.addPlayerToRoleMap(id, Role.MOLE);
                continue;
            }

            gameState.addPlayerToRoleMap(id, Role.SURVIVOR);
        }

        gameState.setAlivePlayers();
    }

    public void startGame(NamespacedKey worldKey) {
        prepareDimensionSet(worldKey)
                .thenAccept(overworld -> {
                    plugin.getLogger().info("PREPARE WORLDS COMPLETE!");
                    List<Player> shuffledPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    Map<Player, Role> players = new HashMap<>();
                    Collections.shuffle(shuffledPlayers);

                    plugin.getLogger().info("Assigning roles...");

                    gameState.resetGame();
                    assignRoles();

                    for (UUID uuid : gameState.getRoleMap().keySet()) {
                        Player player = Bukkit.getPlayer(uuid);

                        if (player != null) {
                            players.put(player, gameState.getRoleMap().get(uuid));
                        }
                    }

                    plugin.getLogger().info("Starting game animation...");

                    gameState.setGameStarted(true);
                    animationManager.startGameSequence(players, () -> teleportPlayers(overworld));

                    plugin.getLogger().info("Game STARTED!");
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().severe(
                            "Cannot start Molecore: " + throwable.getMessage()
                    );
                    return null;
                });
    }

    public void endGame(Winner winner) {
        switch (winner) {
            case Winner.MOLES:
                animationManager.endGameSequence(winner, gameState.getPlayersWithRole(Role.MOLE));
                break;
            case Winner.SURVIVORS:
                animationManager.endGameSequence(winner, gameState.getPlayersWithRole(Role.SURVIVOR));
        }

        gameState.setGameStarted(false);
    }

    public void handlePlayerDeath(PlayerDeathEvent event) {
        Component deathComponent = event.deathMessage();
        Player player = event.getPlayer();

        if (deathComponent != null) {
            plugin.getLogger().info(PlainTextComponentSerializer.plainText().serialize(deathComponent));
        }

        player.setGameMode(GameMode.SPECTATOR);
        gameState.markDead(player.getUniqueId());
        winConditions.checkWinCondition().ifPresent(this::endGame);

        event.deathMessage(null);
    }

    public void handleObjectiveCompletion(Event event) {
        switch (gameState.getSettings().objective()) {
            case BEACON:
                if (event instanceof BeaconActivatedEvent) {
                    endGame(Winner.SURVIVORS);
                }
            case DRAGON:
                if (event instanceof EntityDeathEvent entityDeathEvent && entityDeathEvent.getEntity() instanceof EnderDragon) {
                    endGame(Winner.SURVIVORS);
                }
        }
    }
}
