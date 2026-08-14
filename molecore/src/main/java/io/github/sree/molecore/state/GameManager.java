package io.github.sree.molecore.state;

import io.github.sree.molecore.MolecorePlugin;
import io.github.sree.core.SreeCorePlugin;
import io.github.sree.molecore.animations.GameAnimationManager;
import io.github.sree.core.combat_tag.CombatTagSettings;
import io.github.sree.core.combat_tag.TaggingMethod;
import io.github.sree.molecore.enums.LockedSlot;
import io.github.sree.molecore.enums.Role;
import io.github.sree.molecore.enums.Winner;

import io.github.sree.core.information.InformationChannel;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class GameManager {
    private final MolecorePlugin plugin;
    private final GameAnimationManager animationManager;

    private final GameState gameState;

    private final SreeCorePlugin sreeCore;

    public GameManager(MolecorePlugin plugin, GameState gameState, GameAnimationManager animationManager, SreeCorePlugin sreeCore) {
        this.plugin = plugin;
        this.gameState = gameState;
        this.animationManager = animationManager;
        this.sreeCore = sreeCore;
    }

    public NamespacedKey getWorldKey(String worldName) {
        return new NamespacedKey(plugin, worldName);
    }

    private Executor getMainThread() {
        return task -> Bukkit.getScheduler().runTask(plugin, task);
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
                    gameState.setAlivePlayers(players.stream().map(Player::getUniqueId).collect(Collectors.toSet()));
                    sreeCore.combatTagManager().setCombatTagSettings(new CombatTagSettings(20, TaggingMethod.MOST_DMG));

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

        animationManager.startCountdown(players)
                .thenComposeAsync(ignored -> teleportPlayers(overworld), getMainThread())
                .thenComposeAsync(ignored -> {
                    plugin.getLogger().info("Grace period started!");
                    gameState.setGameStarted(true);
                    gameState.setGracePeriod(true);

                    sreeCore.informationService().set(players,
                            EnumSet.of(InformationChannel.LOCAL_CHAT, InformationChannel.LOCATOR_BAR));

                    Component playerListHeader = Component.text("Newtoncraft Molecore", NamedTextColor.RED)
                                    .append(Component.newline())
                                    .append(Component.text("---------------------", NamedTextColor.GOLD));

                    players.forEach(player -> player.sendPlayerListHeader(playerListHeader));

                    plugin.getLogger().info("Grace period starting animation!");

                    return animationManager.gracePeriodTimer(players, gameState.getSettings().gracePeriodSeconds());
                }, getMainThread())
                .thenAcceptAsync(ignored -> {
                    assignRoles();

                    Map<Player, Role> playerRoleMap = gameState.getPlayersMap().entrySet().stream()
                            .map(entry -> Map.entry(Bukkit.getPlayer(entry.getKey()), entry.getValue().getRole()))
                            .filter(entry -> entry.getKey() != null)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                    animationManager.revealRoles(playerRoleMap);
                    gameState.setGracePeriod(false);

                    plugin.getLogger().info("Game STARTED!");
                }, getMainThread());
    }

    public void endGame(Winner winner, Location endLocation) {
        switch (winner) {
            case Winner.MOLES:
                animationManager.endGameSequence(winner, gameState.getPlayersWithRole(Role.MOLE), endLocation);
                break;
            case Winner.SURVIVORS:
                animationManager.endGameSequence(winner, gameState.getPlayersWithRole(Role.SURVIVOR), endLocation);
        }

        Set<Player> onlinePlayers = new HashSet<>(Bukkit.getOnlinePlayers());
        sreeCore.informationService().reset(onlinePlayers);
        sreeCore.spectatorService().removeAllSpectators();
        gameState.resetGame();
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

        event.getDrops().removeIf(item -> item.getType() == Material.STONE_BUTTON);

        Player attacker = sreeCore.combatTagManager().getKiller(event);
        if (attacker != null) {
            gameState.incrementKills(attacker);
            gameState.lockSlots(attacker);
            dropArmor(attacker, gameState.getLockedSlots(attacker));

            if (gameState.getRole(attacker) == Role.MOLE) {
                gameState.unlockSlots(attacker);
            }
        }

        sreeCore.spectatorService().addSpectator(target);
        sreeCore.informationService().set(sreeCore.spectatorService().getSpectators(), EnumSet.of(
                InformationChannel.DEATH_MESSAGES,
                InformationChannel.LOCATOR_BAR,
                InformationChannel.TAB_LIST,
                InformationChannel.ALL_CHAT
        ));

        gameState.markDead(target.getUniqueId());
        checkWinCondition().ifPresent(winner -> endGame(winner, event.getEntity().getLocation()));
    }

    public void executeSabotage(Player player) {
        if (!gameState.isGameStarted() || gameState.isGracePeriod()) {
            player.sendMessage(Component.text("Roles aren't assigned yet."));
        }

        if (gameState.getRole(player) == Role.SURVIVOR) {
            player.sendMessage(Component.text("Only moles can use that, silly!", NamedTextColor.GREEN));
            return;
        }

        if (gameState.isSabotageOnCooldown()) {
            player.sendMessage(Component.text("Sabotage is still on cooldown.", NamedTextColor.DARK_RED));
            return;
        }

        gameState.setSabotageOnCooldown(true);
        sreeCore.informationService()
                .deny(gameState.getPlayersWithRole(Role.SURVIVOR), InformationChannel.LOCATOR_BAR);

        animationManager.sabotageOnTimer()
                .thenComposeAsync(ignored -> {
                    sreeCore.informationService()
                            .allow(gameState.getPlayersWithRole(Role.SURVIVOR), InformationChannel.LOCATOR_BAR);

                    return animationManager.sabotageCooldownTimer();
                }, getMainThread())
                .thenAccept(ignored -> {
                    gameState.setSabotageOnCooldown(false);
                });
    }

    public void dropArmor(Player player, EnumSet<LockedSlot> lockedSlots) {
        lockedSlots.forEach(slot -> {
            if (player.getInventory().getItem(slot.getSlot()).getType() == Material.STONE_BUTTON) {
                return;
            }

            PlayerInventory inv = player.getInventory();
            player.getWorld().dropItem(player.getLocation(), inv.getItem(slot.getSlot()));

            ItemStack item = new ItemStack(Material.STONE_BUTTON);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                switch (gameState.getRole(player)) {
                    case SURVIVOR:
                        Component customSurvivorName = Component.text("LOCKED SLOT");
                        meta.displayName(customSurvivorName);
                        break;
                    case MOLE:
                        Component customMoleName = Component.text("'LOCKED' SLOT");
                        meta.displayName(customMoleName);
                        meta.lore(List.of(Component.text("You may only remove this slot once to feign having armor.")));
                }
            }

            item.setItemMeta(meta);
            inv.setItem(slot.getSlot(), item);

            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1.0f, 1.0f);
        });
    }

    public void handleObjectiveCompletion(Event event) {
        if (!gameState.isGameStarted()) {
            return;
        }

        switch (gameState.getSettings().objective()) {
            case BEACON:
                if (event instanceof BeaconActivatedEvent beaconActivatedEvent) {
                    Location location = beaconActivatedEvent.getBlock().getLocation();

                    if (location.getBlockX() == 0 && location.getBlockZ() == 0) {
                        endGame(Winner.SURVIVORS, beaconActivatedEvent.getBlock().getLocation().toCenterLocation());
                    }
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
}
