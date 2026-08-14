package io.github.sree.core.combat_tag;

import io.github.sree.core.SreeCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class CombatTagManager {

    private final Map<UUID, List<DamageInstance>>  damageHistories = new HashMap<>();
    private final Map<UUID, BukkitTask> expirationTasks = new HashMap<>();

    private CombatTagSettings combatTagSettings = new CombatTagSettings(15, TaggingMethod.LAST_HIT);

    private final SreeCorePlugin plugin;

    public CombatTagManager(SreeCorePlugin plugin) {
        this.plugin = plugin;
    }

    public void setCombatTagSettings(CombatTagSettings settings) {
        combatTagSettings = settings;
    }

    protected void handlePlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player victim) || !(event.getDamageSource().getCausingEntity() instanceof Player attacker)) {
            return;
        }

        UUID victimId = victim.getUniqueId();
        UUID attackerId = attacker.getUniqueId();

        List<DamageInstance> history = damageHistories.computeIfAbsent(victimId, ignored -> new ArrayList<>());
        history.add(new DamageInstance(attackerId, event.getFinalDamage()));

        BukkitTask existingTask = expirationTasks.remove(victimId);
        if (existingTask != null) {
            existingTask.cancel();
        }

        long ticks = (long) combatTagSettings.expirationTime() * 20;

        BukkitTask newTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            damageHistories.remove(victimId);
            expirationTasks.remove(victimId);
        }, ticks);

        expirationTasks.put(victimId, newTask);
    }

    @Nullable
    public Player getKiller(PlayerDeathEvent event) {
        Player victim = event.getPlayer();
        UUID victimId = victim.getUniqueId();

        BukkitTask existingTask = expirationTasks.remove(victimId);
        if (existingTask != null) {
            existingTask.cancel();
        }

        List<DamageInstance> history = damageHistories.remove(victimId);
        if (history == null || history.isEmpty()) {
            return null;
        }

        UUID killerId = null;

        switch (combatTagSettings.taggingMethod()) {
            case MOST_DMG:
                Map<UUID, Double> damageTotals = new HashMap<>();
                for (DamageInstance instance : history) {
                    damageTotals.put(instance.attackerId(),
                            damageTotals.getOrDefault(instance.attackerId(), 0.0) + instance.damageDealt());
                }

                killerId = Collections.max(damageTotals.entrySet(), Map.Entry.comparingByValue()).getKey();
                break;

            case MOST_HITS:
                Map<UUID, Integer> hitTotals = new HashMap<>();
                for (DamageInstance instance : history) {
                    hitTotals.put(instance.attackerId(),
                            hitTotals.getOrDefault(instance.attackerId(), 0) + 1);
                }

                killerId = Collections.max(hitTotals.entrySet(), Map.Entry.comparingByValue()).getKey();
                break;

            case LAST_HIT:
                killerId = history.getLast().attackerId();
        }

        return Bukkit.getPlayer(killerId);
    }
}