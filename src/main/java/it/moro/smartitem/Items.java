package it.moro.smartitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Items {

    private static FileConfiguration config;

    public static void loadItems() {
        File file = new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("SmartItemCreator")).getDataFolder(), "items.yml");
        if (!file.exists()) {
            Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("SmartItemCreator")).saveResource("items.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static ItemStack getItem(String itemName) {
        if (config == null) {
            loadItems();
        }
        if (config.contains("items." + itemName)) {
            String materialName = Objects.requireNonNull(config.getString("items." + itemName + ".material")).replaceAll("&", "§");
            int amount = config.getInt("items." + itemName + ".amount", 1);
            Material material = Material.getMaterial(materialName.toUpperCase());
            if (material != null) {
                ItemStack item = new ItemStack(material, amount);
                if (config.contains("items." + itemName + ".display_name")) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        String displayName = Objects.requireNonNull(config.getString("items." + itemName + ".display_name")).replaceAll("&", "§");
                        meta.displayName(Component.text(displayName));
                        if (config.contains("items." + itemName + ".lore")) {
                            List<String> loreList = config.getStringList("items." + itemName + ".lore");
                            List<Component> lore = new ArrayList<>();
                            for (String loreLine : loreList) {
                                lore.add(Component.text(loreLine.replaceAll("&", "§")));
                            }
                            meta.lore(lore);
                        }
                        if (config.contains("items." + itemName + ".enchantments")) {
                            for (String enchantmentKey : Objects.requireNonNull(config.getConfigurationSection("items." + itemName + ".enchantments")).getKeys(false)) {
                                Enchantment enchantment = Enchantment.getByName(enchantmentKey);
                                if (enchantment != null) {
                                    int level = config.getInt("items." + itemName + ".enchantments." + enchantmentKey);
                                    meta.addEnchant(enchantment, level, true);
                                }
                            }
                        }
                    }
                    if (Bukkit.getBukkitVersion().startsWith("1_21") || Bukkit.getBukkitVersion().startsWith("1.21")) {
                        NamespacedKey spaceKey = new NamespacedKey(SmartItemCreator.getInstance(), "attack_speed");
                        AttributeModifier attackSpeedModifier = new AttributeModifier(spaceKey, 1.0, AttributeModifier.Operation.ADD_NUMBER);
                        if(meta != null) meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeedModifier);
                    }
                    if(meta != null) meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    if(config.getBoolean("items." + itemName + ".hide-enchants")) {
                        if(meta != null) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    item.setItemMeta(meta);
                }
                return item;
            }
        }
        return null;
    }

    public static List<String> getItemNames() {
        if (config == null) loadItems();
        List<String> itemNames = new ArrayList<>();
        if (config.contains("items")) {
            itemNames.addAll(Objects.requireNonNull(config.getConfigurationSection("items")).getKeys(false));
        }
        return itemNames;
    }
}
