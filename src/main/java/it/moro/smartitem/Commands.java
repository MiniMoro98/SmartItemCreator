package it.moro.smartitem;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter, Listener {
    private static SmartItemCreator plugin;

    public Commands(SmartItemCreator plugin) {
        Commands.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (player.hasPermission("smartitemcreator.cmd")) {
                if (command.getName().equalsIgnoreCase("sic") || command.getName().equalsIgnoreCase("smartitemcreator")) {
                    if (args.length == 0) {
                        player.sendMessage("§cUso corretto: /sic <nome_oggetto>");
                        return false;
                    }
                    String itemName = args[0].toLowerCase();
                    ItemStack item = Items.getItem(itemName);
                    if (item != null) {
                        player.getInventory().addItem(item);
                        player.sendMessage("§aHai ricevuto l'oggetto: " + itemName);
                    } else {
                        player.sendMessage("§cOggetto non trovato: " + itemName);
                    }
                    return true;
                }
            } else {
                player.sendMessage("Non hai il permesso per questo comando!");
            }
        } else {
            if (command.getName().equalsIgnoreCase("sic") || command.getName().equalsIgnoreCase("smartitemcreator")) {
                if (args.length == 0) {
                    sender.sendMessage("§cUso corretto: /sic <item> <player>");
                    return false;
                } else if (args.length == 1) {
                    sender.sendMessage("§cGiocatore mancante!");
                    return false;
                } else if(args.length !=  2){
                    return false;
                }
                Player player = Bukkit.getPlayerExact(args[1]);
                String itemName = args[0].toLowerCase();
                ItemStack item = Items.getItem(itemName);
                if (player != null) {
                    if (item != null) {
                        player.getInventory().addItem(item);
                        player.sendMessage("§aHai ricevuto l'oggetto: " + itemName);
                        sender.sendMessage(player.getName() + " ha ricevuto: " + itemName);
                    } else {
                        player.sendMessage("§cOggetto non trovato: " + itemName);
                    }
                } else {
                    sender.sendMessage("§cGiocatore non trovato!");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("sic") || command.getName().equalsIgnoreCase("smartitemcreator")) {
            List<String> completions = new ArrayList<>();
            if (args.length == 1) {
                for (String itemName : Items.getItemNames()) {
                    if (itemName.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(itemName);
                    }
                }
            } else if(args.length == 2){
                completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName)
                        .filter(p -> p.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList());
            }
            return completions;
        }
        return null;
    }
}
