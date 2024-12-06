package org.hypno.spigotMail;

import org.bukkit.plugin.java.JavaPlugin;
import org.hypno.spigotMail.commands.SpigotMailCommands;
import org.hypno.spigotMail.events.SpigotMailEvents;

public final class SpigotMail extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SpigotMailEvents(), this);
        getCommand("send_package").setExecutor(new SpigotMailCommands());

        getServer().getConsoleSender().sendMessage("[SpigotMail]: Plugin is enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("[SpigotMail]: Plugin is disabled!");
    }
}
