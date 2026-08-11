package io.github.sree.animations;

import io.github.sree.MolecorePlugin;
import io.github.sree.enums.Objective;
import io.github.sree.enums.Role;
import io.github.sree.enums.Winner;
import io.github.sree.state.GameState;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class GameAnimationManager {
    private final MolecorePlugin plugin;
    private final GameState gameState;

    public GameAnimationManager(MolecorePlugin plugin, GameState gameState) {
        this.plugin = plugin;
        this.gameState = gameState;
    }

    public CompletableFuture<Void> gracePeriodTimer(Set<Player> players, int gracePeriodSeconds) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        String formattedTime = String.format("%02d:%02d", gracePeriodSeconds / 60, gracePeriodSeconds % 60);

        BossBar gracePeriodTimerBar = BossBar.bossBar(
                Component.text("Grace Period: " + formattedTime),
                1.0f,
                BossBar.Color.GREEN,
                BossBar.Overlay.PROGRESS
        );

        players.forEach(player -> player.showBossBar(gracePeriodTimerBar));

        new BukkitRunnable() {
            int remainingTime = gracePeriodSeconds;

            public void run() {
                if (remainingTime <= 0) {
                    players.forEach(player -> player.hideBossBar(gracePeriodTimerBar));
                    future.complete(null);
                    this.cancel();
                }

                float progress = (float) remainingTime / gracePeriodSeconds;
                String updatedTime = String.format("%02d:%02d", remainingTime / 60, remainingTime % 60);

                gracePeriodTimerBar.progress(progress);
                gracePeriodTimerBar.name(Component.text("Grace period: " + updatedTime));

                remainingTime --;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        return future;
    }

    public void revealRoles(Map<Player, Role> players) {
        int[] delays = {0, 2, 4, 6, 8, 10, 14, 18, 25, 40, 60};

        for (int i = 0; i < delays.length; i++) {
            int animationStep = i;
            int delay = delays[i];

            // Fake role cycling
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Role fakeRole = animationStep % 2 == 0 ? Role.SURVIVOR : Role.MOLE;
                NamedTextColor color = fakeRole == Role.SURVIVOR ? NamedTextColor.GREEN : NamedTextColor.RED;
                float pitch = 0.8f + (animationStep * 0.05f);

                for (Player player : players.keySet()) {
                    player.showTitle(
                            Title.title(
                                    Component.text("You are a ")
                                            .color(NamedTextColor.WHITE)
                                            .append(Component.text(fakeRole.name(), color)),
                                    Component.empty(),
                                    Title.Times.times(
                                            Duration.ZERO,
                                            Duration.ofSeconds(3),
                                            Duration.ZERO
                                    )
                            )
                    );

                    player.playSound(
                            player.getLocation(),
                            Sound.UI_BUTTON_CLICK,
                            1.0f,
                            pitch
                    );
                }
            }, delay);
        }

        // Reveal real role
        for (Map.Entry<Player, Role> entry : players.entrySet()) {
            Player player = entry.getKey();
            Role role = entry.getValue();

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                NamedTextColor color = role == Role.SURVIVOR ? NamedTextColor.GREEN : NamedTextColor.RED;
                Sound sound = role == Role.SURVIVOR ? Sound.ENTITY_PLAYER_LEVELUP : Sound.ENTITY_WITHER_SPAWN;
                player.showTitle(
                        Title.title(
                                Component.text("You are a ")
                                        .append(Component.text(role.name(), color)),
                                Component.empty(),
                                Title.Times.times(
                                        Duration.ofMillis(250),
                                        Duration.ofSeconds(4),
                                        Duration.ofMillis(750)
                                )
                        )
                );

                switch (role) {
                    case Role.SURVIVOR:
                        player.sendMessage(Component.text("You are a SURVIVOR! Complete the objective for your team to win.", color));
                        break;
                    case Role.MOLE:
                        player.sendMessage(Component.text("You are a MOLE! Kill all survivors for your team to win.", color));
                }

                player.playSound(
                        player.getLocation(),
                        sound,
                        1.5f,
                        1.0f
                );
            }, 80L);
        }
    }

    public CompletableFuture<Void> startCountdown(Set<Player> players) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        for (int i = 0; i < 4; i++) {
            int timerCount = i;

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Player player : players) {
                    if (timerCount > 2) {
                        player.playSound(
                                player.getLocation(),
                                Sound.BLOCK_NOTE_BLOCK_PLING,
                                1.0f,
                                2.0f
                        );
                        future.complete(null);
                        continue;
                    }

                    player.showTitle(
                            Title.title(
                                    Component.text(3 - timerCount, NamedTextColor.GOLD),
                                    Component.empty()
                            )
                    );

                    player.playSound(
                            player.getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_PLING,
                            1.0f,
                            1.0f
                    );
                }
            }, 20 * i);
        }

        return future;
    }

    public void endGameSequence(Winner winner, Set<Player> winners, Location endLocation) {
        NamedTextColor color = winner == Winner.SURVIVORS ? NamedTextColor.GREEN : NamedTextColor.RED;
        Color fireworkColor = winner == Winner.SURVIVORS ? Color.GREEN : Color.RED;

        World world = endLocation.getWorld();
        int[] detonateDelays = {0, 10, 20};

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(
                    Title.title(
                            Component.text("Game Over!", NamedTextColor.GOLD),
                            Component.text("The ", NamedTextColor.WHITE)
                                    .append(Component.text(winner.name(), color))
                                    .append(Component.text(" have won the event!", NamedTextColor.WHITE)),
                            Title.Times.times(
                                    Duration.ofMillis(500),
                                    Duration.ofSeconds(6),
                                    Duration.ofMillis(500)
                            )
                    )
            );

            player.playSound(
                    player.getLocation(),
                    Sound.ENTITY_ENDER_DRAGON_GROWL,
                    1.0f,
                    1.0f
            );

            player.sendMessage(Component.text("-- WINNERS --", color));
            winners.forEach(name -> player.sendMessage(Component.text(name.getName())));
        }

        switch (new AnimationRegistry(winner, gameState.getSettings().objective())) {
            case AnimationRegistry(Winner w, Objective o) when w == Winner.SURVIVORS && o == Objective.BEACON:

                for (int delay : detonateDelays) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Firework firework = world.spawn(endLocation, Firework.class);
                        FireworkMeta meta = firework.getFireworkMeta();
                        meta.addEffect(
                                FireworkEffect.builder()
                                        .withColor(fireworkColor)
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .build()
                        );
                        meta.setPower(1);

                        firework.setFireworkMeta(meta);
                    }, delay);
                }

                break;

            case AnimationRegistry(Winner w, Objective o) when w == Winner.MOLES && o == Objective.BEACON:

                for (int delay : detonateDelays) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Firework firework = world.spawn(endLocation, Firework.class);
                        FireworkMeta meta = firework.getFireworkMeta();
                        meta.addEffect(
                                FireworkEffect.builder()
                                        .withColor(fireworkColor)
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .build()
                        );
                        meta.setPower(1);

                        firework.setFireworkMeta(meta);
                    }, delay);
                }

                winners.forEach(player -> player.setGlowing(true));
                Bukkit.getScheduler().runTaskLater(plugin, () ->
                        winners.forEach(player -> player.setGlowing(false)), 200L);

                break;

            default:
                break;
        }
    }
}
