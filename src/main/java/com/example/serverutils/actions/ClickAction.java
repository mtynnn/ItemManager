package com.example.serverutils.actions;

import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.ChatColor;
import com.example.serverutils.ServerUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ClickAction {
    private final ServerUtils plugin;
    private final String type;
    private final String expression;
    private final List<String> denyCommands;
    private final List<String> allowCommands;
    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    public ClickAction(ServerUtils plugin, ConfigurationSection section) {
        this.plugin = plugin;
        this.type = section.getString("requirements.type", "EXPRESSION");
        this.expression = section.getString("requirements.expression", "true");
        this.denyCommands = section.getStringList("deny_commands");
        this.allowCommands = section.getStringList("allow_commands");
    }

    public boolean checkRequirements(Player player) {
        if (type.equals("EXPRESSION")) {
            try {
                // Crear un contexto con variables disponibles
                Map<String, Object> context = new HashMap<>();
                context.put("player", player);
                context.put("hasPermission", (java.util.function.Function<String, Boolean>) player::hasPermission);

                // Evaluar la expresión
                engine.put("player", player);
                Object result = engine.eval(expression);
                return result instanceof Boolean && (Boolean) result;
            } catch (ScriptException e) {
                plugin.getLogger().warning("Error al evaluar expresión: " + expression);
                return false;
            }
        }
        return true;
    }

    public void executeCommands(Player player, boolean allowed) {
        List<String> commands = allowed ? allowCommands : denyCommands;
        for (String command : commands) {
            if (command.startsWith("[MESSAGE]")) {
                String message = command.substring(9).trim();
                message = message.replace("%player%", player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            } else if (command.startsWith("[CONSOLE]")) {
                String cmd = command.substring(9).trim();
                cmd = cmd.replace("%player%", player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
            } else if (command.startsWith("[PLAYER]")) {
                String cmd = command.substring(8).trim();
                cmd = cmd.replace("%player%", player.getName());
                plugin.getServer().dispatchCommand(player, cmd);
            }
        }
    }

    public static class Builder {
        private final ServerUtils plugin;
        private final Map<String, ClickAction> actions = new HashMap<>();

        public Builder(ServerUtils plugin) {
            this.plugin = plugin;
        }

        public void loadFromConfig(ConfigurationSection section) {
            if (section == null)
                return;

            for (String key : section.getKeys(false)) {
                ConfigurationSection actionSection = section.getConfigurationSection(key);
                if (actionSection != null) {
                    actions.put(key, new ClickAction(plugin, actionSection));
                }
            }
        }

        public ClickAction getAction(String type) {
            return actions.get(type);
        }

        public boolean hasAction(String type) {
            return actions.containsKey(type);
        }
    }
}