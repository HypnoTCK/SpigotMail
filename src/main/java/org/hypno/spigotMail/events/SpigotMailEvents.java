package org.hypno.spigotMail.events;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hypno.spigotMail.*;

import java.util.Collections;

public class SpigotMailEvents implements Listener {

    private final SpigotMail plugin;

    public SpigotMailEvents(SpigotMail plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        var itemInHand = event.getItem();

        if (!isItemPackage(itemInHand)) {
            return;
        }

        event.setCancelled(true);

        var dataKey = new NamespacedKey(this.plugin, SpigotMail.PACKAGE_DATA_KEY);
        var data = itemInHand.getItemMeta().getPersistentDataContainer().get(dataKey, new PackageDataPersistentDataType());
        if (data == null) {
            plugin.getServer().getConsoleSender().sendMessage("[SpigotMail]: Error: Package data missing unexpectedly!");
            return;
        }

        switch (data.type()) {
            case EMPTY -> {
                var inv = new EmptyPackageInventory(plugin);
                event.getPlayer().openInventory(inv.getInventory());
            }
            case ITEM -> {
                event.getPlayer().getInventory().remove(event.getItem());
                event.getPlayer().getInventory().addItem(data.item());
                event.getPlayer().playSound(
                        Sound.sound()
                                .source(Sound.Source.MASTER)
                                .type(new NamespacedKey(NamespacedKey.MINECRAFT, "item.armor.equip_leather"))
                                .build()
                );
            }
            case BOMB -> {
                event.getPlayer().getInventory().remove(event.getItem());
                event.getPlayer().getWorld().createExplosion(event.getPlayer().getLocation(), 10.0f, false, false);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        var inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof EmptyPackageInventory)) {
            return;
        }

        if (event.getClickedInventory() != player.getInventory() && event.getSlot() != 4) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        var inventory = event.getInventory();
        var player = event.getPlayer();

        if (!(inventory.getHolder(false) instanceof EmptyPackageInventory)) {
            return;
        }

        var itemContent = inventory.getItem(4);
        if (itemContent == null) {
            return;
        }

        var isMainHand = true;
        ItemStack packageItem;

        if (isItemPackage(player.getInventory().getItemInMainHand())) {
            packageItem = player.getInventory().getItemInMainHand();
        } else if (isItemPackage(player.getInventory().getItemInOffHand())) {
            packageItem = player.getInventory().getItemInOffHand();
            isMainHand = false;
        } else {
            player.getInventory().addItem(itemContent);
            return;
        }

        if (packageItem.getAmount() == 1) {
            packageItem.editMeta(ItemMeta.class, meta -> {
                meta.lore(Collections.emptyList());

                var dataKey = new NamespacedKey(this.plugin, SpigotMail.PACKAGE_DATA_KEY);
                meta.getPersistentDataContainer().set(dataKey, new PackageDataPersistentDataType(), new PackageData(PackageType.ITEM, itemContent));
            });

            if (isMainHand) {
                player.getInventory().setItemInMainHand(packageItem);
            } else {
                player.getInventory().setItemInOffHand(packageItem);
            }
        } else {
            packageItem.setAmount(packageItem.getAmount() - 1);
            if (isMainHand) {
                player.getInventory().setItemInMainHand(packageItem);
            } else {
                player.getInventory().setItemInOffHand(packageItem);
            }

            var newPackageItem = packageItem.clone();
            newPackageItem.setAmount(1);
            newPackageItem.editMeta(ItemMeta.class, meta -> {
                meta.lore(Collections.emptyList());

                var dataKey = new NamespacedKey(this.plugin, SpigotMail.PACKAGE_DATA_KEY);
                meta.getPersistentDataContainer().set(dataKey, new PackageDataPersistentDataType(), new PackageData(PackageType.ITEM, itemContent));
            });
            player.getInventory().addItem(newPackageItem);
        }

        player.playSound(
                Sound.sound()
                        .source(Sound.Source.MASTER)
                        .type(new NamespacedKey(NamespacedKey.MINECRAFT, "item.armor.equip_leather"))
                        .build()
        );
    }

    private boolean isItemPackage(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.PLAYER_HEAD) {
            return false;
        }

        var persistentData = itemStack.getItemMeta().getPersistentDataContainer();
        var dataKey = new NamespacedKey(this.plugin, SpigotMail.PACKAGE_DATA_KEY);
        return persistentData.has(dataKey);
    }

}