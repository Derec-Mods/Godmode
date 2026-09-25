package io.github.derexxd.godmode;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Godmode extends JavaPlugin {

    private GodManager manager;

    @Override
    public void onEnable() {
        manager = new GodManager(this);
        manager.load();
        manager.startAutoSave();

        getServer().getPluginManager().registerEvents(new GodListener(manager), this);
        Objects.requireNonNull(getCommand("godmode")).setExecutor(new GodCommand(this, manager));
    }

    @Override
    public void onDisable() {
        if (manager != null) manager.shutdown();
    }
}
