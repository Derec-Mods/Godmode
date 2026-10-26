package io.github.derexxd.godmode;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.util.Vector;

public class GodListener implements Listener {

    private final Godmode plugin;
    private final GodManager manager;

    public GodListener(Godmode plugin, GodManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p))
            return;
        if (manager.isGod(p.getUniqueId())) {
            e.setCancelled(true);
            p.setFireTicks(0);

            if (plugin.getConfig().getBoolean("thorns.enabled", true)
                    && e instanceof EntityDamageByEntityEvent byEntity) {
                LivingEntity attacker = null;
                if (byEntity.getDamager() instanceof Projectile projectile
                        && projectile.getShooter() instanceof LivingEntity shooter) {
                    attacker = shooter;
                } else if (byEntity.getDamager() instanceof LivingEntity living) {
                    attacker = living;
                }

                if (attacker != null && !manager.isGod(attacker.getUniqueId())) {
                    double launchStrength = plugin.getConfig().getDouble("thorns.launch-strength", 3.0);
                    double damage = plugin.getConfig().getDouble("thorns.damage", 500.0);

                    Vector dir = attacker.getLocation().toVector().subtract(p.getLocation().toVector());
                    if (dir.lengthSquared() < 1.0E-4) {
                        dir = new Vector(0, 1, 0);
                    } else {
                        dir.normalize();
                    }
                    dir.multiply(launchStrength);
                    dir.setY(Math.max(0.8, launchStrength * 0.4));
                    attacker.setVelocity(dir);
                    attacker.damage(damage, p);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player p))
            return;
        if (manager.isGod(p.getUniqueId())) {
            e.setCancelled(true);
            p.setFoodLevel(20);
            p.setSaturation(20f);
        }
    }
}
