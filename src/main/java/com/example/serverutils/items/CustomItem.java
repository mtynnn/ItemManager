package com.example.serverutils.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.ChatColor;
import com.example.serverutils.ServerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomItem {
    private final ServerUtils plugin;
    private final String id;
    private final Material material;
    private final String name;
    private final List<String> lore;
    private final Map<Enchantment, Integer> enchantments;
    private final List<ItemFlag> flags;
    private final Map<String, Object> nbt;
    private final boolean glow;
    private final int cantidad;
    private String clickCommand;

    public CustomItem(ServerUtils plugin, String id, Material material, String name, List<String> lore,
            Map<Enchantment, Integer> enchantments, List<ItemFlag> flags, Map<String, Object> nbt,
            boolean glow, int cantidad) {
        this.plugin = plugin;
        this.id = id;
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.enchantments = enchantments;
        this.flags = flags;
        this.nbt = nbt;
        this.glow = glow;
        this.cantidad = cantidad;

        // Extraer el comando de click si existe
        if (nbt != null && nbt.containsKey("click_command")) {
            this.clickCommand = (String) nbt.get("click_command");
        }
    }

    public ItemStack create() {
        ItemStack item = new ItemStack(material, cantidad);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Establecer nombre
            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            // Establecer lore
            if (lore != null && !lore.isEmpty()) {
                List<String> translatedLore = new ArrayList<>();
                for (String line : lore) {
                    translatedLore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(translatedLore);
            }

            // Establecer encantamientos
            if (enchantments != null) {
                for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
                    meta.addEnchant(enchant.getKey(), enchant.getValue(), true);
                }
            }

            // Establecer flags
            if (flags != null) {
                for (ItemFlag flag : flags) {
                    meta.addItemFlags(flag);
                }
            }

            // Establecer NBT
            if (nbt != null) {
                for (Map.Entry<String, Object> entry : nbt.entrySet()) {
                    if (entry.getKey().equals("custom_model_data") && entry.getValue() instanceof Integer) {
                        meta.setCustomModelData((Integer) entry.getValue());
                    }
                }
            }

            // Establecer glow
            if (glow) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            // Guardar el ID del item en NBT
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "custom_item_id"),
                    PersistentDataType.STRING,
                    id);

            // Guardar el comando de click si existe
            if (clickCommand != null) {
                meta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "click_command"),
                        PersistentDataType.STRING,
                        clickCommand);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public String getId() {
        return id;
    }

    public String getClickCommand() {
        return clickCommand;
    }
}