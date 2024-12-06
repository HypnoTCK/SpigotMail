package org.hypno.spigotMail;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class EmptyPackageInventory implements InventoryHolder {

    private final Inventory inventory;

    public EmptyPackageInventory(SpigotMail plugin) {
        this.inventory = plugin.getServer().createInventory(
                this,
                9,
                Component.text("Package content")
        );
        var redGlassItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        redGlassItem.editMeta(ItemMeta.class, meta -> meta.displayName(Component.text("Only one item per package!")));
        for (var i = 0; i < inventory.getSize(); i++) {
            if (i != 4) {
                this.inventory.setItem(i, redGlassItem);
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
