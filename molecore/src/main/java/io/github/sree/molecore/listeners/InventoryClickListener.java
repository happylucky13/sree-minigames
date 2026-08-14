package io.github.sree.molecore.listeners;

import io.github.sree.molecore.state.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener extends GameListener {
    public InventoryClickListener(GameState gameState) {
        super(gameState);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!gameState.isGameStarted()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getView().getType() != InventoryType.CRAFTING) {
            return;
        }

        EquipmentSlot lockedSlot = getEquipmentSlotFromInventorySlot(event.getSlot());

        if (lockedSlot != null &&
                gameState.getLockedSlots(player).stream()
                        .anyMatch(slot -> slot.getSlot() == lockedSlot)) {

            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!gameState.isGameStarted()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < event.getView().getTopInventory().getSize()) {
                continue;
            }

            int playerSlot = event.getView().convertSlot(rawSlot);

            EquipmentSlot equipmentSlot =
                    getEquipmentSlotFromInventorySlot(playerSlot);

            if (equipmentSlot != null &&
                    gameState.getLockedSlots(player).stream()
                            .anyMatch(slot -> slot.getSlot() == equipmentSlot)) {

                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onArmorHotswap(PlayerInteractEvent event) {
        if (!gameState.isGameStarted()) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) {
            return;
        }

        Player player = event.getPlayer();
        EquipmentSlot intendedSlot = item.getType().getEquipmentSlot();

        if (gameState.getLockedSlots(player).stream().anyMatch(slot -> slot.getSlot() == intendedSlot)) {
            event.setCancelled(true);
        }

        player.updateInventory();
    }

    private EquipmentSlot getEquipmentSlotFromInventorySlot(int rawSlot) {
        return switch (rawSlot) {
            case 39 -> EquipmentSlot.HEAD;
            case 38 -> EquipmentSlot.CHEST;
            case 37 -> EquipmentSlot.LEGS;
            case 36 -> EquipmentSlot.FEET;
            default -> null;
        };
    }
}
