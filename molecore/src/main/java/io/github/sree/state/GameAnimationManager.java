package io.github.sree.state;

import io.github.sree.MolecorePlugin;
import io.github.sree.enums.Role;
import io.github.sree.enums.Winner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public class GameAnimationManager {
    private final MolecorePlugin plugin;

    public GameAnimationManager(MolecorePlugin plugin) {
        this.plugin = plugin;
    }

    public void startGameSequence(Map<Player, Role> players, Runnable onCountdownFinished) {
        startCountdown(players);

        Bukkit.getScheduler().runTaskLater(plugin, onCountdownFinished, 60L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> revealRoles(players), 70L);

    }

    private void revealRoles(Map<Player, Role> players) {
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

    private void startCountdown(Map<Player, Role> players) {
        for (int i = 0; i < 4; i++) {
            int timerCount = i;

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Player player : players.keySet()) {
                    if (timerCount > 2) {
                        player.playSound(
                                player.getLocation(),
                                Sound.BLOCK_NOTE_BLOCK_PLING,
                                1.0f,
                                2.0f
                        );
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
    }

    public void endGameSequence(Winner winner, Set<Player> winners, Location endLocation) {
        NamedTextColor color = winner == Winner.SURVIVORS ? NamedTextColor.GREEN : NamedTextColor.RED;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(
                    Title.title(
                            Component.text("Game Over!", NamedTextColor.GOLD),
                            Component.text("The ", NamedTextColor.WHITE)
                                    .append(Component.text(winner.name(), color))
                                    .append(Component.text(" have won!", NamedTextColor.WHITE)),
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
    }
}
