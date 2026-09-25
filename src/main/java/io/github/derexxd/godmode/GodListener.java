package io.github.derexxd.godmode;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public final class GodListener implements Listener {

    private final GodManager manager;

    public GodListener(GodManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (manager.isGod(p.getUniqueId())) {
            e.setCancelled(true);
            p.setFireTicks(0);
            p.setFreezeTicks(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (manager.isGod(p.getUniqueId())) {
            e.setCancelled(true);
            p.setFoodLevel(20);
            p.setSaturation(20f);
        }
    }
}
