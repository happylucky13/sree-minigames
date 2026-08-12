package io.github.sree.listeners;

import io.github.sree.enums.LockedSlot;
import io.github.sree.state.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
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

        if (event.getView().getType() != InventoryType.CRAFTING && event.getView().getType() != InventoryType.PLAYER) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            EquipmentSlot targetedSlot = getEquipmentSlotFromRawId(event.getSlot());
            if (gameState.getLockedSlots(player).stream().anyMatch(slot -> slot.getSlot() == targetedSlot)) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.isShiftClick() && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType().isAir()) {
                return;
            }

            EquipmentSlot intendedSlot = clickedItem.getType().getEquipmentSlot();

            if (gameState.getLockedSlots(player).stream().anyMatch(slot -> slot.getSlot() == intendedSlot)) {
                event.setCancelled(true);
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

    private EquipmentSlot getEquipmentSlotFromRawId(int rawSlot) {
        return switch (rawSlot) {
            case 39 -> EquipmentSlot.HEAD;
            case 38 -> EquipmentSlot.CHEST;
            case 37 -> EquipmentSlot.LEGS;
            case 36 -> EquipmentSlot.FEET;
            default -> null;
        };
    }
}
