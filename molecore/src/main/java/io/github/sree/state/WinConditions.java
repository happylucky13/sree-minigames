package io.github.sree.state;

import io.github.sree.enums.Objective;
import io.github.sree.enums.Role;
import io.github.sree.enums.Winner;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Optional;

public class WinConditions {

    private final GameState gameState;

    public WinConditions(GameState gameState) {
        this.gameState = gameState;
    }

    public Optional<Winner> checkWinCondition() {
        return gameState.hasAlivePlayersWithRole(Role.SURVIVOR) ? Optional.empty() : Optional.of(Winner.MOLES);
    }

    public Optional<Winner> checkObjectiveCompletion(Player player) {
        Material objective = gameState.getSettings().objective() == Objective.WITHER ? Material.BEACON : Material.DRAGON_EGG;
        boolean survivorsWin = player.getInventory().contains(objective) && gameState.getRole(player) == Role.SURVIVOR;

        return survivorsWin ? Optional.of(Winner.SURVIVORS) : Optional.empty();
    }
}
