package com.mcsv.itemmanager.listeners;

import com.mcsv.itemmanager.ItemManagerPlugin;
import com.mcsv.itemmanager.items.ItemManager;
import com.mcsv.itemmanager.utils.DebugLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {
    private final ItemManagerPlugin plugin;
    private final ItemManager itemManager;
    private final DebugLogger debugLogger;

    public ItemListener(ItemManagerPlugin plugin) {
        this.plugin = plugin;
        this.itemManager = plugin.getItemManager();
        this.debugLogger = plugin.getDebugLogger();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !itemManager.isCustomItem(item)) {
            return;
        }

        String itemId = itemManager.getItemId(item);
        if (itemId == null) {
            debugLogger.warning("Item personalizado sin ID encontrado en " + player.getName());
            return;
        }

        // Verificar permisos
        if (!player.hasPermission("itemmanager.use." + itemId)) {
            event.setCancelled(true);
            player.sendMessage("§cNo tienes permiso para usar este item.");
            return;
        }

        // Ejecutar acciones según el tipo de click
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            itemManager.executeClickActions(player, itemId, "right_click");
        } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            itemManager.executeClickActions(player, itemId, "left_click");
        }

        // Cancelar el evento si el item tiene acciones configuradas
        if (itemManager.hasClickActions(itemId)) {
            event.setCancelled(true);
        }
    }
}