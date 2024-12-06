package org.hypno.spigotMail.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpigotMailEvents implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

        if (itemInHand.hasItemMeta()) {
            ItemMeta meta = itemInHand.getItemMeta();

            if (meta != null && meta.hasItemName()) {
                String itemName = meta.getItemName();

                if (itemName.equals("TNT")) {
                    event.getPlayer().getWorld().createExplosion(event.getPlayer().getLocation(), 10.0f, false, false);
                } else {
                    Material material = Material.matchMaterial(itemName);
                    if (material != null) {
                        event.getPlayer().getInventory().addItem(new ItemStack(material));
                    }
                }

                event.setCancelled(true);
                event.getPlayer().getInventory().remove(itemInHand);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals(player.getDisplayName() + " - Select an Item to Send")) {
            event.setCancelled(true); // prevent item from being moved

            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                Player targetPlayer = (Player) player.getMetadata("targetPlayer").get(0).value();

                if (targetPlayer != null){
                    ItemStack item = new ItemStack(Material.CHEST);
                    ItemMeta meta = item.getItemMeta();

                    if (meta != null) {
                        meta.setLore(java.util.Collections.singletonList(ChatColor.AQUA + "Package from: " + player.getName()));
                        meta.setDisplayName("Package");

                        meta.setItemName(clickedItem.getType().toString());

                        item.setItemMeta(meta);
                    }

                    targetPlayer.getInventory().addItem(item);
                    targetPlayer.sendMessage(ChatColor.GREEN + "You received a package from " + player.getName() + "!");
                    player.getInventory().remove(clickedItem);
                }
            }else {
                player.sendMessage(ChatColor.RED + "No item selected");
            }

            player.closeInventory();
        }
    }

}