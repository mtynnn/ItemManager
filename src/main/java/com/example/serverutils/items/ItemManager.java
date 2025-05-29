package com.example.serverutils.items;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import com.example.serverutils.ServerUtils;

import java.io.File;
import java.util.*;

public class ItemManager {
    private final ServerUtils plugin;
    private final Map<String, CustomItem> items;

    public ItemManager(ServerUtils plugin) {
        this.plugin = plugin;
        this.items = new HashMap<>();
        loadItems();
    }

    private void loadItems() {
        // Guardar el archivo de configuración si no existe
        File itemsFile = new File(plugin.getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            plugin.saveResource("items.yml", false);
        }

        // Cargar la configuración
        YamlConfiguration config = YamlConfiguration.loadConfiguration(itemsFile);
        ConfigurationSection itemsSection = config.getConfigurationSection("items");

        if (itemsSection == null) {
            plugin.getLogger().warning("No se encontraron items en items.yml");
            return;
        }

        for (String itemId : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemId);
            if (itemSection == null)
                continue;

            try {
                Material material = Material.valueOf(itemSection.getString("material", "STONE"));
                String name = itemSection.getString("name");
                List<String> lore = itemSection.getStringList("lore");

                // Cargar encantamientos
                Map<Enchantment, Integer> enchantments = new HashMap<>();
                ConfigurationSection enchantsSection = itemSection.getConfigurationSection("enchantments");
                if (enchantsSection != null) {
                    for (String enchantName : enchantsSection.getKeys(false)) {
                        try {
                            Enchantment enchant = Enchantment.getByName(enchantName);
                            if (enchant != null) {
                                enchantments.put(enchant, enchantsSection.getInt(enchantName));
                            }
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("Encantamiento inválido: " + enchantName);
                        }
                    }
                }

                // Cargar flags
                List<ItemFlag> flags = new ArrayList<>();
                List<String> flagNames = itemSection.getStringList("flags");
                for (String flagName : flagNames) {
                    try {
                        flags.add(ItemFlag.valueOf(flagName));
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Flag inválido: " + flagName);
                    }
                }

                // Cargar NBT
                Map<String, Object> nbt = new HashMap<>();
                ConfigurationSection nbtSection = itemSection.getConfigurationSection("nbt");
                if (nbtSection != null) {
                    for (String nbtKey : nbtSection.getKeys(false)) {
                        nbt.put(nbtKey, nbtSection.get(nbtKey));
                    }
                }

                boolean glow = itemSection.getBoolean("glow", false);
                int cantidad = itemSection.getInt("cantidad", 1);

                CustomItem customItem = new CustomItem(plugin, itemId, material, name, lore,
                        enchantments, flags, nbt, glow, cantidad);

                items.put(itemId, customItem);
                plugin.getLogger().info("Item cargado: " + itemId);
            } catch (Exception e) {
                plugin.getLogger().severe("Error al cargar el item " + itemId + ": " + e.getMessage());
            }
        }
    }

    public CustomItem getItem(String id) {
        return items.get(id);
    }

    public Map<String, CustomItem> getAllItems() {
        return Collections.unmodifiableMap(items);
    }

    public void reload() {
        items.clear();
        loadItems();
    }
}