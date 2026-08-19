package io.github.sree.molecore.animations;

import io.github.sree.molecore.MolecorePlugin;
import io.github.sree.molecore.enums.Objective;
import io.github.sree.molecore.enums.Role;
import io.github.sree.molecore.enums.Winner;
import io.github.sree.molecore.state.GameState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public class GameAnimationManager {
    private final MolecorePlugin plugin;
    private final GameState gameState;

    public GameAnimationManager(MolecorePlugin plugin, GameState gameState) {
        this.plugin = plugin;
        this.gameState = gameState;
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
