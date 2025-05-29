package com.mcsv.itemmanager.utils;

import com.mcsv.itemmanager.ItemManagerPlugin;
import org.bukkit.ChatColor;

public class DebugLogger {
    private final ItemManagerPlugin plugin;
    private boolean debugEnabled;

    public DebugLogger(ItemManagerPlugin plugin) {
        this.plugin = plugin;
        this.debugEnabled = plugin.getConfig().getBoolean("debug", false);
    }

    public void info(String message) {
        plugin.getLogger().info(ChatColor.stripColor(message));
    }

    public void warning(String message) {
        plugin.getLogger().warning(ChatColor.stripColor(message));
    }

    public void error(String message) {
        plugin.getLogger().severe(ChatColor.stripColor(message));
    }

    public void debug(String message) {
        if (debugEnabled) {
            plugin.getLogger().info("[DEBUG] " + ChatColor.stripColor(message));
        }
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
        plugin.getConfig().set("debug", debugEnabled);
        plugin.saveConfig();
    }
}