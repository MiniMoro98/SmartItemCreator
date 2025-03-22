package it.moro.smartitem;

import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

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
            String materialName = "STONE";
            if (config.contains("items." + itemName + ".material")) {
                materialName = Objects.requireNonNull(config.getString("items." + itemName + ".material")).replaceAll("&", "§");
            }
            //--------------------------------------HEAD---------------------------------
            if (materialName.equalsIgnoreCase("PLAYER_HEAD")) {
                String url = "http://textures.minecraft.net/texture/da99b05b9a1db4d29b5e673d77ae54a77eab66818586035c8a2005aeb810602a";
                if (config.contains("items." + itemName + ".head_url")) {
                    url = config.getString("items." + itemName + ".head_url");
                }
                String name = "";
                int amount = 1;
                if (config.contains("items." + itemName + ".display_name")) {
                    name = Objects.requireNonNull(config.getString("items." + itemName + ".display_name")).replaceAll("&", "§");
                }
                if (config.contains("items." + itemName + ".amount")) {
                    amount = config.getInt("items." + itemName + ".amount");
                }
                List<Component> lore = new ArrayList<>();
                if (config.contains("items." + itemName + ".lore")) {
                    List<String> loreList = config.getStringList("items." + itemName + ".lore");
                    for (String loreLine : loreList) {
                        lore.add(Component.text(loreLine.replaceAll("&", "§")));
                    }
                }
                return new ItemStack(generateHead(url, name, lore, amount));
                //------------------------------------POTION--------------------------------------
            } else if (materialName.equalsIgnoreCase("POTION") || materialName.equalsIgnoreCase("SPLASH_POTION") || materialName.equalsIgnoreCase("LINGERING_POTION")) {
                String name = "";
                int amount = 1;
                if (config.contains("items." + itemName + ".display_name")) {
                    name = Objects.requireNonNull(config.getString("items." + itemName + ".display_name")).replaceAll("&", "§");
                }
                if (config.contains("items." + itemName + ".amount")) {
                    amount = config.getInt("items." + itemName + ".amount");
                }
                String color = "";
                if (config.contains("items." + itemName + ".color")) {
                    color = config.getString("items." + itemName + ".color");
                }
                List<Component> lore = new ArrayList<>();
                if (config.contains("items." + itemName + ".lore")) {
                    List<String> loreList = config.getStringList("items." + itemName + ".lore");
                    for (String loreLine : loreList) {
                        lore.add(Component.text(loreLine.replaceAll("&", "§")));
                    }
                }
                Map<PotionEffectType, Integer> effects = getPotionEffects(itemName);
                Map<PotionEffectType, Integer> durations = getPotionDurations(itemName);
                Material type = Material.getMaterial(materialName);
                return createPotion(name, lore, effects, durations, type, amount, color);

                //-----------------------------------OTHER----------------------------------------
            } else {
                int amount = 1;
                if (config.contains("items." + itemName + ".amount")) {
                    amount = config.getInt("items." + itemName + ".amount", 1);
                }
                Material material = Material.getMaterial(materialName.toUpperCase());
                if (material != null) {
                    ItemStack item = new ItemStack(material, amount);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        if (config.contains("items." + itemName + ".display_name")) {
                            String displayName = Objects.requireNonNull(config.getString("items." + itemName + ".display_name")).replaceAll("&", "§");
                            meta.displayName(Component.text(displayName));
                        }
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
                        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        if(config.contains("items." + itemName + ".hide-enchants")){
                            if (config.getBoolean("items." + itemName + ".hide-enchants")) {
                                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            }
                        }
                        if (Bukkit.getBukkitVersion().startsWith("1_21") || Bukkit.getBukkitVersion().startsWith("1.21")) {
                            NamespacedKey spaceKey = new NamespacedKey(SmartItemCreator.getInstance(), "attack_speed");
                            AttributeModifier attackSpeedModifier = new AttributeModifier(spaceKey, 1.0, AttributeModifier.Operation.ADD_NUMBER);
                            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeedModifier);
                        }
                        item.setItemMeta(meta);
                    }
                    return item;
                }
            }
        }
        return null;
    }

    static ItemStack generateHead(String url, String name, List<Component> lore, int amount) {
        ItemStack head = new ItemStack(head(url));
        ItemMeta meta = head.getItemMeta();
        if (!name.equalsIgnoreCase("")) {
            meta.displayName(Component.text(name));
        }
        if (!lore.isEmpty()) {
            meta.lore(lore);
        }
        head.setItemMeta(meta);
        head.setAmount(amount);
        return head;
    }

    public static ItemStack head(String base64) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (base64.length() > 40) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + base64 + "\"}}}";
            base64 = Base64.getEncoder().encodeToString(json.getBytes());
            profile.setProperty(new ProfileProperty("textures", base64));
            meta.setPlayerProfile(profile);
        } else {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(base64));
        }
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack createPotion(String displayName, List<Component> lore, Map<PotionEffectType, Integer> effects, Map<PotionEffectType, Integer> durations, Material type, int amount, String color) {
            ItemStack potion = new ItemStack(type, amount);
            PotionMeta meta = (PotionMeta) potion.getItemMeta();
            if (meta != null) {
                if (color != null && !color.isEmpty()) {
                    meta.setColor(getColorFromString(color));
                }
                if (displayName != null) {
                    meta.displayName(Component.text(displayName));
                }
                if (lore != null && !lore.isEmpty()) {
                    meta.lore(lore);
                }
                if (effects != null && !effects.isEmpty()) {
                    for (Map.Entry<PotionEffectType, Integer> entry : effects.entrySet()) {
                        PotionEffectType effectType = entry.getKey();
                        int amplifier = entry.getValue();
                        int duration = durations.getOrDefault(effectType, 200);
                        meta.addCustomEffect(new PotionEffect(effectType, duration, amplifier), true);
                    }
                }
                potion.setItemMeta(meta);
            }
            return potion;
    }

    public static Color getColorFromString(String colorStr) {
        try {
            if (colorStr.contains(",")) {
                String[] rgb = colorStr.split(",");
                int r = Integer.parseInt(rgb[0].trim());
                int g = Integer.parseInt(rgb[1].trim());
                int b = Integer.parseInt(rgb[2].trim());
                return Color.fromRGB(r, g, b);
            } else {
                return switch (colorStr.toUpperCase()) {
                    case "RED" -> Color.RED;
                    case "BLUE" -> Color.BLUE;
                    case "GREEN" -> Color.GREEN;
                    case "YELLOW" -> Color.YELLOW;
                    case "BLACK" -> Color.BLACK;
                    case "GRAY" -> Color.GRAY;
                    default -> Color.WHITE;
                };
            }
        } catch (Exception e) {
            return Color.WHITE;
        }
    }

    public static Map<PotionEffectType, Integer> getPotionEffects(String itemName) {
        Map<PotionEffectType, Integer> effects = new HashMap<>();
        if (config.contains("items." + itemName + ".potion_effects")) {
            for (String effectKey : Objects.requireNonNull(config.getConfigurationSection("items." + itemName + ".potion_effects")).getKeys(false)) {
                PotionEffectType effectType = PotionEffectType.getByName(effectKey.toUpperCase());
                if (effectType != null) {
                    int amplifier = config.getInt("items." + itemName + ".potion_effects." + effectKey + ".amplifier", 1);
                    effects.put(effectType, amplifier);
                }
            }
        }
        return effects;
    }

    public static Map<PotionEffectType, Integer> getPotionDurations(String itemName) {
        Map<PotionEffectType, Integer> durations = new HashMap<>();
        if (config.contains("items." + itemName + ".potion_effects")) {
            for (String effectKey : Objects.requireNonNull(config.getConfigurationSection("items." + itemName + ".potion_effects")).getKeys(false)) {
                PotionEffectType effectType = PotionEffectType.getByName(effectKey.toUpperCase());
                if (effectType != null) {
                    int duration = config.getInt("items." + itemName + ".potion_effects." + effectKey + ".duration", 200);
                    durations.put(effectType, duration);
                }
            }
        }
        return durations;
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
