package org.hypno.spigotMail;

import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.hypno.spigotMail.events.SpigotMailEvents;

import java.util.List;
import java.util.UUID;

public final class SpigotMail extends JavaPlugin {

    public static final UUID PACKAGE_SKULL_UUID = UUID.fromString("0fa054c4-d9c9-4f52-9c4f-38f1f0ae72c3");
    public static final String PACKAGE_DATA_KEY = "package_data";

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.setupPackageItems();

        getServer().getPluginManager().registerEvents(new SpigotMailEvents(this), this);

        getServer().getConsoleSender().sendMessage("[SpigotMail]: Plugin is enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("[SpigotMail]: Plugin is disabled!");
    }

    private void setupPackageItems() {
        var texture = this.getConfig().getString("package-head-texture");
        if (texture == null) {
            getServer().getConsoleSender().sendMessage("[SpigotMail]: Could not load player head texture!");
            return;
        }

        var profile = Bukkit.createProfile(PACKAGE_SKULL_UUID);
        profile.setProperty(new ProfileProperty("textures", texture));

        var dataKey = new NamespacedKey(this, PACKAGE_DATA_KEY);

        var itemEmptyPackage = new ItemStack(Material.PLAYER_HEAD);
        itemEmptyPackage.editMeta(SkullMeta.class, meta -> meta.setPlayerProfile(profile));
        itemEmptyPackage.editMeta(ItemMeta.class, meta -> {
            meta.displayName(Component.text("Package").color(TextColor.fromHexString("#FFAA00")));
            meta.lore(List.of(Component.text("(Empty)").color(TextColor.fromHexString("#AAAAAA"))));
            meta.getPersistentDataContainer().set(dataKey, new PackageDataPersistentDataType(), new PackageData(PackageType.EMPTY, null));
        });

        var recipeEmptyPackage = new ShapedRecipe(new NamespacedKey(this, "package_empty"), itemEmptyPackage);
        recipeEmptyPackage.shape("AAA", "A A", "AAA");
        recipeEmptyPackage.setIngredient('A', Material.PAPER);

        getServer().addRecipe(recipeEmptyPackage);

        var itemBombPackage = new ItemStack(Material.PLAYER_HEAD);
        itemBombPackage.editMeta(SkullMeta.class, meta -> meta.setPlayerProfile(profile));
        itemBombPackage.editMeta(ItemMeta.class, meta -> {
            meta.displayName(Component.text("Package").color(TextColor.fromHexString("#FFAA00")));
            meta.getPersistentDataContainer().set(dataKey, new PackageDataPersistentDataType(), new PackageData(PackageType.BOMB, null));
        });

        var recipeBombPackage = new ShapedRecipe(new NamespacedKey(this, "package_bomb"), itemBombPackage);
        recipeBombPackage.shape("AAA", "ABA", "AAA");
        recipeBombPackage.setIngredient('A', Material.PAPER);
        recipeBombPackage.setIngredient('B', Material.TNT);

        getServer().addRecipe(recipeBombPackage);
    }

}
