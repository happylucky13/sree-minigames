package io.github.sree.state;

import io.github.sree.MolecorePlugin;
import io.github.sree.SreeCorePlugin;
import io.github.sree.animations.GameAnimationManager;
import io.github.sree.enums.Role;
import io.github.sree.enums.Winner;

import io.github.sree.information.InformationChannel;
import io.papermc.paper.event.block.BeaconActivatedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class GameManager {
    private final MolecorePlugin plugin;
    private final GameAnimationManager animationManager;

    private final GameState gameState;

    private final SreeCorePlugin sreeCore;

    private final Map<UUID, BukkitTask> combatTasks = new HashMap<>();


    public GameManager(MolecorePlugin plugin, GameState gameState, GameAnimationManager animationManager, SreeCorePlugin sreeCore) {
        this.plugin = plugin;
        this.gameState = gameState;
        this.animationManager = animationManager;
        this.sreeCore = sreeCore;
    }

    public GameState getGameState() {
        return gameState;
    }

    public NamespacedKey getWorldKey(String worldName) {
        return new NamespacedKey(plugin, worldName);
    }

    public CompletableFuture<World> prepareDimensionSet(NamespacedKey worldKey) {
        return sreeCore.prepareDimensionSet().prepareDimensionSet(worldKey, plugin.getLogger());
    }

    private CompletableFuture<Void> teleportPlayers(World world) {
        return CompletableFuture.allOf(
                Bukkit.getOnlinePlayers().stream()
                        .map(player -> player.teleportAsync(world.getSpawnLocation()))
                        .toArray(CompletableFuture[]::new)
        );
    }

    private void assignRoles() {
        List<Player> shuffledPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(shuffledPlayers);

        for(int i = 0; i < shuffledPlayers.size(); i++) {
            UUID id = shuffledPlayers.get(i).getUniqueId();

            if(i < gameState.getSettings().moleCount()) {
                gameState.addPlayerToPlayersMap(id, Role.MOLE);
                continue;
            }

            gameState.addPlayerToPlayersMap(id, Role.SURVIVOR);
        }

        gameState.setAlivePlayers(gameState.getPlayersMap().keySet());
    }

    public void startGame(NamespacedKey worldKey) {
        prepareDimensionSet(worldKey)
                .thenAccept(overworld -> {
                    Set<Player> players = new HashSet<>(Bukkit.getOnlinePlayers());

                    gameState.resetGame();
                    gameState.setGameStarted(true);
                    gameState.setAlivePlayers(players.stream().map(Player::getUniqueId).collect(Collectors.toSet()));

                    startGameSequence(players, overworld);
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().severe(
                            "Cannot start Molecore: " + throwable.getMessage()
                    );
                    return null;
                });
    }

    public void startGameSequence(Set<Player> players, World overworld) {
        final Executor mainThread = task -> Bukkit.getScheduler().runTask(plugin, task);

        animationManager.startCountdown(players)
                .thenComposeAsync(ignored -> teleportPlayers(overworld), mainThread)
                .thenComposeAsync(ignored -> {
                    gameState.setGracePeriod(true);

                    sreeCore.informationService().reset(players);
                    sreeCore.informationService().deny(players, InformationChannel.DEATH_MESSAGES);
                    sreeCore.informationService().deny(players, InformationChannel.TAB_LIST);

                    Component playerListHeader = Component.text("Newtoncraft Molecore", NamedTextColor.RED)
                                    .append(Component.newline())
                                    .append(Component.text("---------------------", NamedTextColor.GOLD));

                    players.forEach(player -> player.sendPlayerListHeader(playerListHeader));

                    return animationManager.gracePeriodTimer(players, gameState.getSettings().gracePeriodSeconds());
                }, mainThread)
                .thenAcceptAsync(ignored -> {
                    assignRoles();

                    Map<Player, Role> playerRoleMap = gameState.getPlayersMap().entrySet().stream()
                            .map(entry -> Map.entry(Bukkit.getPlayer(entry.getKey()), entry.getValue().getRole()))
                            .filter(entry -> entry.getKey() != null)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                    animationManager.revealRoles(playerRoleMap);
                    gameState.setGracePeriod(false);

                    plugin.getLogger().info("Game STARTED!");
                }, mainThread);
    }

    public void endGame(Winner winner, Location endLocation) {
        switch (winner) {
            case Winner.MOLES:
                animationManager.endGameSequence(winner, gameState.getPlayersWithRole(Role.MOLE), endLocation);
                break;
            case Winner.SURVIVORS:
                animationManager.endGameSequence(winner, gameState.getPlayersWithRole(Role.SURVIVOR), endLocation);
        }

        gameState.setGameStarted(false);
    }

    public void handlePlayerDeath(PlayerDeathEvent event) {
        Component deathComponent = event.deathMessage();
        Player target = event.getPlayer();

        if (gameState.isGracePeriod()) {
            target.sendMessage(Component.text("The grace period has saved you!", NamedTextColor.GREEN));
            return;
        }

        if (deathComponent != null) {
            plugin.getLogger().info(PlainTextComponentSerializer.plainText().serialize(deathComponent));
        }

        Player attacker = gameState.getAttackerThatHurtTargetMost(target);
        gameState.incrementKills(attacker);
        gameState.lockSlots(attacker);

        sreeCore.spectatorService().addSpectator(target);
        gameState.markDead(target.getUniqueId());
        checkWinCondition().ifPresent(winner -> endGame(winner, event.getEntity().getLocation()));
    }

    public void handleObjectiveCompletion(Event event) {
        if (!gameState.isGameStarted()) {
            return;
        }

        switch (gameState.getSettings().objective()) {
            case BEACON:
                if (event instanceof BeaconActivatedEvent beaconActivatedEvent) {
                    endGame(Winner.SURVIVORS, beaconActivatedEvent.getBlock().getLocation().toCenterLocation());
                }
            case DRAGON:
                if (event instanceof EntityDeathEvent entityDeathEvent && entityDeathEvent.getEntity() instanceof EnderDragon) {
                    endGame(Winner.SURVIVORS, entityDeathEvent.getEntity().getLocation());
                }
        }
    }

    private Optional<Winner> checkWinCondition() {
        return gameState.hasAlivePlayersWithRole(Role.SURVIVOR) ? Optional.empty() : Optional.of(Winner.MOLES);
    }

    public void markCombat(Player attacker, Player target, double damageDealt) {
        gameState.setOrIncrementAttackedPlayer(attacker, target, damageDealt);

        if (combatTasks.containsKey(attacker.getUniqueId())) {
            combatTasks.get(attacker.getUniqueId()).cancel();
            attacker.sendMessage(Component.text("Cooldown reset", NamedTextColor.LIGHT_PURPLE));
        }

        BukkitTask finishCooldown = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameState.removeAttackedPlayer(attacker, target);
            attacker.sendMessage(Component.text("Out of combat.", NamedTextColor.LIGHT_PURPLE));
                }, 400L);

        combatTasks.put(attacker.getUniqueId(), finishCooldown);
    }
}
