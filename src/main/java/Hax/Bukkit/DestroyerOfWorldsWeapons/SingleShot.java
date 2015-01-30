package Hax.Bukkit.DestroyerOfWorldsWeapons;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class SingleShot extends Weapon {

	public SingleShot(JavaPlugin plugin) {
		super(plugin);
		name = "SingleShot";
		projectileType = EntityType.SNOWBALL;
		coolDownTicks = 20;
		tickLastFired = 0;
		numAmmoOnPickup = 10;
		maxAmmo = 30;
		gun = Material.SAND;
		ammo = numAmmoOnPickup;
	}
	
	@Override
	public void fireShot(PlayerInteractEvent event) {
		if (canFireShot(event)) {
			Vector lookVector = event.getPlayer().getEyeLocation().getDirection();
			Location spawnLoc = event.getPlayer().getEyeLocation().toVector().add(lookVector.multiply(2)).toLocation(event.getPlayer().getWorld());

			Entity e = event.getPlayer().getWorld().spawnEntity(spawnLoc, projectileType);
			setWeaponMetadata(e);
			e.setVelocity(lookVector);
			((Projectile)e).setShooter(event.getPlayer());
			ammo--;
		} 
	}

	@Override
	public void onProjectileHit(ProjectileHitEvent event) {
		event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 2.0f);
	}

	@Override
	public void onEntityDamagedByWeapon(EntityDamageByEntityEvent event) {
		//event.setCancelled(true);
	}
}
