package io.github.derexxd.godmode;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Godmode extends JavaPlugin {

    private GodManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        manager = new GodManager(this);
        manager.load();
        manager.startAutoSave();

        getServer().getPluginManager().registerEvents(new GodListener(this, manager), this);
        Objects.requireNonNull(getCommand("godmode")).setExecutor(new GodCommand(this, manager));
    }

    @Override
    public void onDisable() {
        if (manager != null) manager.shutdown();
    }
}
