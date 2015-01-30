package Hax.Bukkit.DestroyerOfWorldsWeapons;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Weapon {
	
	protected String name;
	protected JavaPlugin plugin;
	protected int coolDownTicks;
	protected long tickLastFired;
	protected int numAmmoOnPickup;
	protected int ammo;
	protected int maxAmmo;
    protected EntityType projectileType;
    protected Material gun;
	
	public Weapon(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	abstract public void fireShot(PlayerInteractEvent event);
    abstract public void onProjectileHit(ProjectileHitEvent event);
    abstract public void onEntityDamagedByWeapon(EntityDamageByEntityEvent event);
	
	public boolean canFireShot(PlayerInteractEvent event) {
		long currentTime = event.getPlayer().getWorld().getTime();
		if (currentTime - coolDownTicks >= tickLastFired) {
			tickLastFired = currentTime;
			return true;
		} else {
			event.getPlayer().sendMessage(name + " not ready yet! Wait " + (((tickLastFired + coolDownTicks) - currentTime) / 20.0) + " seconds.");
			return false;
		}
	}
    
    public boolean addAmmo() {
    	if (ammo < maxAmmo) {
    		ammo = ammo + numAmmoOnPickup < maxAmmo ? ammo + numAmmoOnPickup : maxAmmo; 
    		return true;
    	} 
    	return false;
    }
    
    protected void setWeaponMetadata(Entity e) {
    	e.setMetadata("Weapon", new FixedMetadataValue(plugin, this));
    }
    
    public int getAmmo() {
    	return ammo;
    }
    
    public String getName() {
    	return name;
    }
    
    public Material getGun() {
    	return gun;
    }
}
