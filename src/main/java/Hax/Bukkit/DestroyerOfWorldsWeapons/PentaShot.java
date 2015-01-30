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

public class PentaShot extends Weapon {

	public PentaShot(JavaPlugin plugin) {
		super(plugin);
		name = "PentaShot";
		projectileType = EntityType.SNOWBALL;
		coolDownTicks = 100;
		tickLastFired = 0;
		numAmmoOnPickup = 10;
		maxAmmo = 60;
		gun = Material.FURNACE;
		ammo = numAmmoOnPickup;
	}

	@Override
	public void fireShot(PlayerInteractEvent event) {
		if (canFireShot(event)) {
			Vector lookVector = event.getPlayer().getEyeLocation().getDirection();
			Location spawnLoc = event.getPlayer().getEyeLocation().toVector().add(lookVector.multiply(2)).toLocation(event.getPlayer().getWorld());
			double x = lookVector.getX();
			double y = lookVector.getY();
			double z = lookVector.getZ();
			double angle1 = Math.PI / 12;
			double angle2 = -1 * Math.PI / 12;
			Vector splitVector = new Vector(z * Math.sin(angle1) + x * Math.cos(angle1), 
					y, 
					z * Math.cos(angle1) - x * Math.sin(angle1));
			Vector splitVector2 = new Vector(z * Math.sin(angle2) + x * Math.cos(angle2), 
					y, 
					z * Math.cos(angle2) - x * Math.sin(angle2));

			Entity e1 = event.getPlayer().getWorld().spawnEntity(spawnLoc, projectileType);
			setWeaponMetadata(e1);
			e1.setVelocity(lookVector);
			((Projectile)e1).setShooter(event.getPlayer());
			
			Entity e2 = event.getPlayer().getWorld().spawnEntity(spawnLoc,  projectileType);
			setWeaponMetadata(e2);
			e2.setVelocity(splitVector);
			((Projectile)e2).setShooter(event.getPlayer());
			
			Entity e3 = event.getPlayer().getWorld().spawnEntity(spawnLoc,  projectileType);
			setWeaponMetadata(e3);
			e3.setVelocity(splitVector2);
			((Projectile)e3).setShooter(event.getPlayer());
			
			ammo -= 3;
		}
	}

	@Override
	public void onProjectileHit(ProjectileHitEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntityDamagedByWeapon(EntityDamageByEntityEvent event) {
		// TODO Auto-generated method stub

	}

}
