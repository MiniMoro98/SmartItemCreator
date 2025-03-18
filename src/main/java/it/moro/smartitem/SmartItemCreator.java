package it.moro.smartitem;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class SmartItemCreator extends JavaPlugin {

    @Getter
    private static SmartItemCreator instance;

    @Override
    public void onEnable() {
        instance = this;
        createDataFolder();
        loadFiles();
        Commands commands = new Commands();
        Objects.requireNonNull(getCommand("sic")).setExecutor(commands);
        Objects.requireNonNull(getCommand("smartitemcreator")).setExecutor(commands);
        getLogger().info("\u001B[32mAbilitato!\u001B[0m");
    }

    @Override
    public void onDisable() {
        instance = null;
        getLogger().info("Disabilitato!");
    }

    private void createDataFolder() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().severe("§cImpossibile creare la cartella dati del plugin! Controlla i permessi.");
        }
    }

    private void loadFiles() {
        File configuration = new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("SmartItemCreator")).getDataFolder(), "config.yml");
        if(!configuration.exists()){
            saveResource("config.yml", false);
            getLogger().info("File config.yml creato!");
        }
        File objects = new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("SmartItemCreator")).getDataFolder(), "items.yml");
        if(!objects.exists()){
            saveResource("items.yml", false);
            getLogger().info("File items.yml creato!");
        }
    }
}
