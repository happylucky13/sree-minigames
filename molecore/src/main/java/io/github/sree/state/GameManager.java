package io.github.sree.state;

import io.github.sree.MolecorePlugin;
import io.github.sree.PrepareDimensionSet;
import io.github.sree.create_world.WorldService;
import io.github.sree.enums.Role;
import io.github.sree.enums.Winner;
import io.github.sree.pregenerate_world.PregenerateChunksService;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;

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

    public void checkObjective(Player player) {
        Optional<Winner> winner = winConditions.checkObjectiveCompletion(player);

        winner.ifPresent(this::endGame);
    }

    private void teleportPlayers(NamespacedKey worldKey) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleportAsync(Bukkit.getWorld(worldKey).getSpawnLocation());
        }
    }

    public void startGame(NamespacedKey worldKey) {
        prepareDimensionSet.prepareDimensionSet(worldKey, plugin.getLogger())
                .thenAccept(worlds -> {
                    List<Player> shuffledPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    Map<Player, Role> players = new HashMap<>();
                    Collections.shuffle(shuffledPlayers);

                    gameState.assignRoles();

                    for (UUID uuid : gameState.getRoleMap().keySet()) {
                        Player player = Bukkit.getPlayer(uuid);

                        if (player != null) {
                            players.put(player, gameState.getRoleMap().get(uuid));
                        }
                    }

                    gameState.setGameStarted(true);
                    animationManager.startGameSequence(players, () -> teleportPlayers(worldKey));
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().severe(
                            "Cannot start Molecore: " + throwable.getMessage()
                    );
                    return null;
                });
    }

    private void endGame(Winner winner) {
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
        gameState.getAlivePlayers().remove(player.getUniqueId());
        winConditions.checkWinCondition().ifPresent(this::endGame);

        event.deathMessage(null);
    }
}
