package Hax.Bukkit.DestroyerOfWorldsWeapons;
 
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
 
public final class DestroyerOfWorldsWeapons extends JavaPlugin implements Listener {
	
	HashMap<Player, HashMap<String, Weapon>> playerWeapons;
	
    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(this, this);
    	playerWeapons = new HashMap<Player, HashMap<String, Weapon>>();
    }

 
    @Override
    public void onDisable() {
    	
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
    		String name = getStringName(event.getMaterial());
    		Weapon weapon = playerWeapons.get(event.getPlayer()).get(name);
    		if (weapon != null) {
    			weapon.fireShot(event);
    		}
    	}
    }
    
    @EventHandler
    public void onHit(ProjectileHitEvent event) {
    	String weaponMetadata = event.getEntity().getMetadata("Weapon").size() == 1 ? event.getEntity().getMetadata("Weapon").get(0).asString() : "";
    	Weapon weapon = playerWeapons.get((Player)(event.getEntity().getShooter())).get(weaponMetadata);
    	if (weapon != null) {
    		weapon.onProjectileHit(event);
    	}
    }
    
    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent event) {
    	if (event.getDamager() instanceof Projectile) {
	    	String weaponMetadata = event.getDamager().getMetadata("Weapon").size() == 1 
	    			? event.getDamager().getMetadata("Weapon").get(0).asString() : "";
	    	Weapon weapon = playerWeapons.get(((Projectile) (event.getDamager())).getShooter()).get(weaponMetadata);
	    	if (weapon != null) {
	    		weapon.onEntityDamagedByWeapon(event);
	    	}
    	}
    }
    
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    	Weapon weapon = getClass(event.getItem().getItemStack().getType());
    	String name = getStringName(event.getItem().getItemStack().getType());
    	if (weapon != null) {
    		playerWeapons.get(event.getPlayer()).put(name, weapon);
    	}
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
    	String name = getStringName(event.getItemDrop().getItemStack().getType());
    	if (name != null) {
    		playerWeapons.get(event.getPlayer()).remove(name);
    	}
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    	HashMap<String, Weapon> initialWeapons = new HashMap<String, Weapon>();
    	initialWeapons.put("SingleShot", new SingleShot(this));
    	initialWeapons.put("TeleportShot", new TeleportShot(this));
    	playerWeapons.put(event.getPlayer(), initialWeapons);
    	event.getPlayer().getInventory().addItem(new ItemStack(Material.SAND));
    	event.getPlayer().getInventory().addItem(new ItemStack(Material.SANDSTONE));
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	playerWeapons.get(event.getPlayer()).put("SingleShot", new SingleShot(this));
    	event.getPlayer().getInventory().addItem(new ItemStack(Material.SAND));
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
    	playerWeapons.get(event.getEntity()).clear();
    }
    
    
    private Weapon getClass(Material mat) {
    	if (mat.equals(Material.SAND)) {
    		return new SingleShot(this);
    	} else if (mat.equals(Material.SANDSTONE)) {
    		return new TeleportShot(this);
    	}
    	return null;
    }
    
    private Weapon getClass(String name) {
    	if (name.equals("SingleShot")) {
    		return new SingleShot(this);
    	} else if (name.equals("TeleportShot")) {
    		return new TeleportShot(this);
    	}
    	return null;
    }
    
    private String getStringName(Material mat) {
    	if (mat.equals(Material.SAND)) {
    		return "SingleShot";
    	} else if (mat.equals(Material.SANDSTONE)) {
    		return "TeleportShot";
    	}
    	return null;
    }
}