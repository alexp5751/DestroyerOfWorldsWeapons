package Hax.Bukkit.DestroyerOfWorldsWeapons;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class TeleportShot extends Weapon {

	public TeleportShot(JavaPlugin plugin) {
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
			e.setMetadata("Weapon", new FixedMetadataValue(plugin, "TeleportShot"));
			e.setVelocity(lookVector);
			((Projectile)e).setShooter(event.getPlayer());
		} else {
			event.getPlayer().sendMessage("TeleportShot not ready yet! Wait " + (((tickLastFired + coolDownTicks) - currentTime) / 20.0) + " seconds.");
		}

	}

	@Override
	public void onProjectileHit(ProjectileHitEvent event) {
		Player teleporter = (Player)(event.getEntity()).getShooter();
		Location target = event.getEntity().getLocation();
		target.setYaw(teleporter.getLocation().getYaw());
		target.setPitch(teleporter.getLocation().getPitch());
		teleporter.teleport(target);
	}

	@Override
	public void onEntityDamagedByWeapon(EntityDamageByEntityEvent event) {
		//event.setCancelled(true);
	}

}
