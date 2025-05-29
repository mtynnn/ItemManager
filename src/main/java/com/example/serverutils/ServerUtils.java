package com.example.serverutils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import com.example.serverutils.items.ItemManager;
import com.example.serverutils.items.CustomItem;
import com.example.serverutils.actions.ClickAction;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ServerUtils extends JavaPlugin implements Listener {
    private ItemManager itemManager;
    private Map<String, ClickAction.Builder> itemActions;

    @Override
    public void onEnable() {
        // Guardar configuración por defecto
        saveDefaultConfig();

        // Inicializar el gestor de items
        itemManager = new ItemManager(this);

        // Cargar acciones de items
        loadItemActions();

        // Registrar eventos
        getServer().getPluginManager().registerEvents(this, this);

        // Registrar comandos
        getCommand("serverutils").setExecutor(this);
        getCommand("giveitem").setExecutor(this);

        getLogger().info("ServerUtils ha sido activado!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ServerUtils ha sido desactivado!");
    }

    private void loadItemActions() {
        itemActions = new HashMap<>();
        ConfigurationSection itemsSection = getConfig().getConfigurationSection("items");

        if (itemsSection != null) {
            for (String itemId : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemId);
                if (itemSection != null) {
                    ConfigurationSection actionsSection = itemSection.getConfigurationSection("click_actions");
                    if (actionsSection != null) {
                        ClickAction.Builder builder = new ClickAction.Builder(this);
                        builder.loadFromConfig(actionsSection);
                        itemActions.put(itemId, builder);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Verificar si el kit de bienvenida está habilitado
        if (!getConfig().getBoolean("welcome_kit.enabled", true)) {
            return;
        }

        // Verificar si el jugador es nuevo
        if (!player.hasPlayedBefore()) {
            // Verificar permiso para el kit de bienvenida
            if (player.hasPermission(getConfig().getString("welcome_kit.permission", "serverutils.welcome.kit"))) {
                giveWelcomeKit(player);
                sendMessage(player, getConfig().getString("messages.welcome_first_join"));
            }
        } else {
            sendMessage(player, getConfig().getString("messages.welcome_back"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getItemMeta() == null)
            return;

        // Obtener el ID del item
        String itemId = item.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(this, "custom_item_id"), PersistentDataType.STRING);

        if (itemId != null && itemActions.containsKey(itemId)) {
            ClickAction.Builder builder = itemActions.get(itemId);

            // Determinar el tipo de click
            String clickType = "";
            if (event.getPlayer().isSneaking()) {
                clickType = "shift_";
            }

            if (event.getAction().name().contains("RIGHT_CLICK")) {
                clickType += "right_click";
            } else if (event.getAction().name().contains("LEFT_CLICK")) {
                clickType += "left_click";
            } else if (event.getAction().name().contains("MIDDLE_CLICK")) {
                clickType += "middle_click";
            }

            if (builder.hasAction(clickType)) {
                event.setCancelled(true);
                ClickAction action = builder.getAction(clickType);

                if (action.checkRequirements(event.getPlayer())) {
                    action.executeCommands(event.getPlayer(), true);
                } else {
                    action.executeCommands(event.getPlayer(), false);
                }
            }
        }
    }

    private void giveWelcomeKit(Player player) {
        List<String> kitItems = getConfig().getStringList("welcome_kit.items");
        for (String itemId : kitItems) {
            giveItem(player, itemId);
        }
        sendMessage(player, getConfig().getString("welcome_kit.message"));
    }

    private void giveItem(Player player, String itemId) {
        CustomItem customItem = itemManager.getItem(itemId);
        if (customItem != null) {
            player.getInventory().addItem(customItem.create());
        }
    }

    private void sendMessage(Player player, String message) {
        if (message != null) {
            String prefix = getConfig().getString("messages.prefix", "&8[&bServerUtils&8] &r");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("serverutils")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("serverutils.reload")) {
                        sendMessage(sender, getConfig().getString("messages.no_permission"));
                        return true;
                    }
                    reloadConfig();
                    itemManager.reload();
                    loadItemActions();
                    sendMessage(sender, "&a¡Configuración recargada!");
                    return true;
                }
            }
            sender.sendMessage("§a¡Bienvenido a ServerUtils!");
            sender.sendMessage("§7Comandos disponibles:");
            sender.sendMessage("§e/serverutils reload §7- Recarga la configuración");
            return true;
        }

        if (command.getName().equalsIgnoreCase("giveitem")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cEste comando solo puede ser usado por jugadores.");
                return true;
            }

            if (!sender.hasPermission("serverutils.giveitem")) {
                sendMessage(sender, getConfig().getString("messages.no_permission"));
                return true;
            }

            if (args.length < 1) {
                sender.sendMessage("§cUso: /giveitem <id_del_item>");
                return true;
            }

            String itemId = args[0];
            CustomItem customItem = itemManager.getItem(itemId);

            if (customItem == null) {
                String notFoundMsg = getConfig().getString("messages.item_not_found", "&cItem no encontrado: %item%")
                        .replace("%item%", itemId);
                sendMessage(sender, notFoundMsg);

                String availableMsg = getConfig().getString("messages.available_items", "&7Items disponibles: %items%")
                        .replace("%items%", String.join(", ", itemManager.getAllItems().keySet()));
                sendMessage(sender, availableMsg);
                return true;
            }

            giveItem((Player) sender, itemId);
            sendMessage(sender, "&aHas recibido el item: " + itemId);
            return true;
        }

        return false;
    }

    private void sendMessage(CommandSender sender, String message) {
        if (message != null) {
            String prefix = getConfig().getString("messages.prefix", "&8[&bServerUtils&8] &r");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
        }
    }
}