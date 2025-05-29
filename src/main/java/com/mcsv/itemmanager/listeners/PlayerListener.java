package com.mcsv.itemmanager.listeners;

import com.mcsv.itemmanager.ItemManagerPlugin;
import com.mcsv.itemmanager.items.ItemManager;
import com.mcsv.itemmanager.utils.DebugLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final ItemManagerPlugin plugin;
    private final ItemManager itemManager;
    private final DebugLogger debugLogger;

    public PlayerListener(ItemManagerPlugin plugin) {
        this.plugin = plugin;
        this.itemManager = plugin.getItemManager();
        this.debugLogger = plugin.getDebugLogger();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Dar items automáticos al unirse
        itemManager.getItemsWithAutoGive().forEach(itemId -> {
            if (player.hasPermission("itemmanager.kit." + itemId)) {
                if (itemManager.giveItem(player, itemId)) {
                    debugLogger.info("Item automático " + itemId + " dado a " + player.getName());
                }
            }
        });
    }
}