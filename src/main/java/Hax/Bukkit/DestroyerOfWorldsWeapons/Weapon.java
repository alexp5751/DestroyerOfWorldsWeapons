package Hax.Bukkit.DestroyerOfWorldsWeapons;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Weapon {
	
	protected JavaPlugin plugin;
	protected int coolDownTicks;
	protected long tickLastFired;
	
	public Weapon(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
    protected EntityType projectileType;
	
	abstract public void fireShot(PlayerInteractEvent event);
    abstract public void onProjectileHit(ProjectileHitEvent event);
    abstract public void onEntityDamagedByWeapon(EntityDamageByEntityEvent event);
}
