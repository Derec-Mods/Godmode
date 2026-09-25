package io.github.derexxd.godmode;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GodManager {

    private final Plugin plugin;
    private final Path file;

    private final Set<UUID> gods = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean dirty = new AtomicBoolean(false);

    private BukkitTask saveTask;

    public GodManager(Plugin plugin) {
        this.plugin = plugin;
        this.file = plugin.getDataFolder().toPath().resolve("gods.txt");
    }

    public boolean isGod(UUID id) {
        return gods.contains(id);
    }

    public boolean toggle(UUID id) {
        boolean enabled;
        if (gods.add(id)) {
            enabled = true;
        } else {
            gods.remove(id);
            enabled = false;
        }
        dirty.set(true);
        return enabled;
    }

    public Set<UUID> all() {
        return Collections.unmodifiableSet(gods);
    }

    public void load() {
        try {
            Files.createDirectories(file.getParent());
            if (!Files.exists(file)) return;

            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    gods.add(UUID.fromString(line));
                } catch (IllegalArgumentException ex) {
                    plugin.getLogger().warning("Skipping malformed UUID: " + line);
                }
            }
            plugin.getLogger().info("Loaded " + gods.size() + " godmode entries.");
        } catch (IOException ex) {
            plugin.getLogger().severe("Failed to load gods.txt: " + ex.getMessage());
        }
    }

    public void startAutoSave() {
        saveTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                plugin,
                () -> {
                    if (dirty.compareAndSet(true, false)) writeToDisk();
                },
                20L * 30, 20L * 30
        );
    }

    public void shutdown() {
        if (saveTask != null) saveTask.cancel();
        if (dirty.get()) writeToDisk();
    }

    private void writeToDisk() {
        List<String> lines = new ArrayList<>(gods.size());
        for (UUID id : gods) lines.add(id.toString());

        Path tmp = file.resolveSibling("gods.txt.tmp");
        try {
            Files.createDirectories(file.getParent());
            Files.write(tmp, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);

            try {
                Files.move(tmp, file,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            dirty.set(true);
            plugin.getLogger().severe("Failed to save gods.txt: " + ex.getMessage());
        }
    }
}

