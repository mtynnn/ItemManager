package com.mcsv.itemmanager.listeners;

import com.mcsv.itemmanager.ItemManagerPlugin;
import com.mcsv.itemmanager.items.ItemManager;
import com.mcsv.itemmanager.utils.DebugLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemMoveListener implements Listener {
    private final ItemManagerPlugin plugin;
    private final ItemManager itemManager;
    private final DebugLogger debugLogger;

    public ItemMoveListener(ItemManagerPlugin plugin) {
        this.plugin = plugin;
        this.itemManager = plugin.getItemManager();
        this.debugLogger = plugin.getDebugLogger();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        // Verificar si el item clickeado es personalizado
        if (clickedItem != null && itemManager.isCustomItem(clickedItem)) {
            String itemId = itemManager.getItemId(clickedItem);
            if (itemId != null && !player.hasPermission("itemmanager.move." + itemId)) {
                event.setCancelled(true);
                player.sendMessage("§cNo puedes mover este item.");
                return;
            }
        }

        // Verificar si el item en el cursor es personalizado
        if (cursorItem != null && itemManager.isCustomItem(cursorItem)) {
            String itemId = itemManager.getItemId(cursorItem);
            if (itemId != null && !player.hasPermission("itemmanager.move." + itemId)) {
                event.setCancelled(true);
                player.sendMessage("§cNo puedes mover este item.");
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack draggedItem = event.getOldCursor();

        if (draggedItem != null && itemManager.isCustomItem(draggedItem)) {
            String itemId = itemManager.getItemId(draggedItem);
            if (itemId != null && !player.hasPermission("itemmanager.move." + itemId)) {
                event.setCancelled(true);
                player.sendMessage("§cNo puedes mover este item.");
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemDrop().getItemStack();

        if (itemManager.isCustomItem(droppedItem)) {
            String itemId = itemManager.getItemId(droppedItem);
            if (itemId != null && !player.hasPermission("itemmanager.drop." + itemId)) {
                event.setCancelled(true);
                player.sendMessage("§cNo puedes tirar este item.");
            }
        }
    }
}