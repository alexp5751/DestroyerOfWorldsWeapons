package Hax.Bukkit.DestroyerOfWorldsWeapons;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class SingleShot extends Weapon {

	public SingleShot(JavaPlugin plugin) {
		super(plugin);
		projectileType = EntityType.SNOWBALL;
		coolDownTicks = 20;
		tickLastFired = 0;
	}
	
	@Override
	public void fireShot(PlayerInteractEvent event) {
		long currentTime = event.getPlayer().getWorld().getTime();
		if (currentTime - coolDownTicks > tickLastFired) {
			Vector lookVector = event.getPlayer().getEyeLocation().getDirection();
			Location spawnLoc = event.getPlayer().getEyeLocation().toVector().add(lookVector.multiply(2)).toLocation(event.getPlayer().getWorld());

			Entity e = event.getPlayer().getWorld().spawnEntity(spawnLoc, projectileType);
			e.setMetadata("Weapon", new FixedMetadataValue(plugin, "SingleShot"));
			e.setVelocity(lookVector);
			((Projectile)e).setShooter(event.getPlayer());
			tickLastFired = currentTime;
		} else {
			event.getPlayer().sendMessage("SingleShot not ready yet! Wait " + (((tickLastFired + coolDownTicks) - currentTime) / 20.0) + " seconds.");
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
