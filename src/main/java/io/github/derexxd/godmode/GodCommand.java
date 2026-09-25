package io.github.derexxd.godmode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public final class GodCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final GodManager manager;

    public GodCommand(Plugin plugin, GodManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /godmode <player>");
            return true;
        }

        String name = args[0];

        Player online = Bukkit.getPlayerExact(name);
        if (online != null) {
            apply(sender, online.getUniqueId(), online.getName());
            return true;
        }

        sender.sendMessage(ChatColor.GRAY + "Looking up " + name + "...");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            OfflinePlayer off = Bukkit.getOfflinePlayer(name);

            if (!off.hasPlayedBefore() && off.getName() == null) {
                Bukkit.getScheduler().runTask(plugin,
                        () -> sender.sendMessage(ChatColor.RED + "No such player: " + name));
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> apply(sender, off.getUniqueId(),
                    off.getName() == null ? name : off.getName()));
        });
        return true;
    }

    private void apply(CommandSender sender, UUID id, String display) {
        boolean on = manager.toggle(id);
        sender.sendMessage((on ? ChatColor.GREEN : ChatColor.YELLOW)
                + "Godmode " + (on ? "ENABLED" : "DISABLED") + " for " + display);

        Player p = Bukkit.getPlayer(id);
        if (p != null && !p.equals(sender)) {
            p.sendMessage((on ? ChatColor.GREEN : ChatColor.YELLOW)
                    + "You are " + (on ? "now invincible." : "no longer invincible."));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1)
            return Collections.emptyList();
        String prefix = args[0].toLowerCase(Locale.ROOT);
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(n -> n.toLowerCase(Locale.ROOT).startsWith(prefix))
                .collect(Collectors.toList());
    }
}
