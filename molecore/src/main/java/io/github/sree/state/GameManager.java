package io.github.sree.state;

import io.github.sree.MolecorePlugin;
import io.github.sree.create_world.WorldService;
import io.github.sree.create_world.settings.DimensionSetSettings;
import io.github.sree.create_world.settings.WorldSettings;
import io.github.sree.enums.Role;
import io.github.sree.enums.Objective;
import io.github.sree.enums.Winner;
import io.github.sree.pregenerate_world.ChunkGenerationSettings;
import io.github.sree.pregenerate_world.PregenerateChunksService;
import io.github.sree.pregenerate_world.enums.Pattern;
import io.github.sree.pregenerate_world.enums.Shape;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class GameManager {
    private final MolecorePlugin plugin;
    private final GameAnimationManager animationManager;

    private final GameState gameState = new GameState();
    private final WinConditions winConditions = new WinConditions(gameState);

    private final WorldService worldService;
    private final PregenerateChunksService pregenerateChunksService;


    public GameManager(MolecorePlugin plugin, GameAnimationManager animationManager, WorldService worldService, PregenerateChunksService pregenerateChunksService) {
        this.plugin = plugin;
        this.animationManager = animationManager;
        this.worldService = worldService;
        this.pregenerateChunksService = pregenerateChunksService;
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
        prepareWorld(worldKey)
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

    public CompletableFuture<Set<World>> prepareWorld(NamespacedKey worldKey) {

        DimensionKeys keys = DimensionKeys.from(worldKey);
        World overworld = Bukkit.getWorld(keys.overworld());
        World nether = Bukkit.getWorld(keys.nether());
        World theEnd = Bukkit.getWorld(keys.theEnd());

        if (overworld != null && nether != null && theEnd != null) {
            plugin.getLogger().info("Worlds already exist.");
            return CompletableFuture.completedFuture(Set.of(overworld, nether, theEnd));
        }

        if (overworld != null || nether != null || theEnd != null) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException(
                            "Dimension set is only partially loaded."
                    )
            );
        }

        return worldService.createDimensionSet(
                new DimensionSetSettings(
                        new WorldSettings(
                                keys.overworld(),
                                WorldType.NORMAL,
                                World.Environment.NORMAL,
                                GameMode.SURVIVAL
                        ),
                        new WorldSettings(
                                keys.nether(),
                                WorldType.NORMAL,
                                World.Environment.NETHER,
                                GameMode.SURVIVAL
                        ),
                        new WorldSettings(
                                keys.theEnd(),
                                WorldType.NORMAL,
                                World.Environment.THE_END,
                                GameMode.SURVIVAL
                        )
                )
        )
                .thenCompose(dimensionSet ->
                        pregenerateChunksService.pregenerate(
                                dimensionSet.worlds(),
                                new ChunkGenerationSettings(
                                        Shape.CIRCLE,
                                        0.0,
                                        0.0,
                                        1500,
                                        1500,
                                        Pattern.REGION
                                ),
                                Bukkit.getOnlinePlayers().stream()
                                        .filter(Player::isOp)
                                        .collect(Collectors.toSet())
                        )
                )
                .thenApply(
                        worlds -> {
                            worldService.linkWorlds(worlds, worldKey.getKey() + "_group");
                            return worlds;
                        }
                )
                .exceptionally(throwable -> {
                    plugin.getLogger().severe(
                            "Failed to prepare world " + worldKey + ": " + throwable.getMessage()
                    );
                    throw new CompletionException(throwable);
                });
    }
}
