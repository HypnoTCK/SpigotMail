package org.hypno.spigotMail.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.hypno.spigotMail.SpigotMail;

public class SpigotMailCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;
        String targetName = args[0];
        Player targetPlayer = player.getServer().getPlayer(targetName);

        if (targetPlayer == null){
            player.sendMessage("Target player not found");
            return true;
        }

        if(command.getName().equalsIgnoreCase("send_package")){ // send_package command
            Inventory inventory = player.getInventory();
            Inventory gui = org.bukkit.Bukkit.createInventory(null, 27, player.getDisplayName() + " - Select an Item to Send");

            for (int i = 0; i < 27; i++) {
                if (i < inventory.getSize()) {
                    ItemStack item = inventory.getItem(i);
                    if (item != null) {
                        gui.setItem(i, item.clone()); // add item to gui
                    }
                }
            }

            Plugin plugin = JavaPlugin.getPlugin(SpigotMail.class);
            player.setMetadata("targetPlayer", new FixedMetadataValue(plugin, targetPlayer));
            player.openInventory(gui);
        }

        return true;
    }
}