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
import org.bukkit.event.EventPriority;
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
import org.bukkit.inventory.PlayerInventory;
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
    		String name = getWeaponNameFromMaterial(event.getMaterial());
    		Weapon weapon = playerWeapons.get(event.getPlayer()).get(name);
    		if (weapon != null) {
    			weapon.fireShot(event);
    			int index = event.getPlayer().getInventory().first(weapon.getGun());
    			int currentNum = event.getPlayer().getInventory().getContents()[index].getAmount();
    			event.getPlayer().getInventory().removeItem(new ItemStack(weapon.getGun(), currentNum - weapon.getAmmo()));
    			if (weapon.getAmmo() <= 0) {
    				playerWeapons.get(event.getPlayer()).remove(weapon.getName());
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onHit(ProjectileHitEvent event) {
    	Weapon weapon = (Weapon) (event.getEntity().getMetadata("Weapon").size() == 1 ? event.getEntity().getMetadata("Weapon").get(0).value() :  null);
    	if (weapon != null) {
    		weapon.onProjectileHit(event);
    	}
    }
    
    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent event) {
    	if (event.getDamager() instanceof Projectile) {
    		Weapon weapon = (Weapon) (event.getDamager().getMetadata("Weapon").size() == 1 ? event.getDamager().getMetadata("Weapon").get(0).value() :  null);
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
    	Material mat = event.getItem().getItemStack().getType();
    	String name = getWeaponNameFromMaterial(mat);
    	if (name != null) {
    		Weapon weapon = getPlayerWeapon(event.getPlayer(), name);
    		if (weapon != null) {
    			if (weapon.addAmmo()) {
    				updateInventory(event.getPlayer(), weapon, true);
    			} else {
    				event.setCancelled(true);
    			}
    		} else {
    			addPlayerWeapon(event.getPlayer(), mat);
    		}
    	} else {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    	playerWeapons.put(event.getPlayer(), new HashMap<String, Weapon>());
    	addPlayerWeapon(event.getPlayer(), new SingleShot(this));
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	addPlayerWeapon(event.getPlayer(), new SingleShot(this));
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
    	event.getEntity().getInventory().clear();
    	playerWeapons.get(event.getEntity()).clear();
    }
    
    
    private Weapon getWeaponFromMaterial(Material mat) {
    	if (mat.equals(Material.SAND)) {
    		return new SingleShot(this);
    	} else if (mat.equals(Material.SANDSTONE)) {
    		return new TeleportShot(this);
    	} else if (mat.equals(Material.BOOKSHELF)) {
    		return new TripleShot(this);
    	}
    	return null;
    }
    
    private String getWeaponNameFromMaterial(Material mat) {
    	if (mat.equals(Material.SAND)) {
    		return "SingleShot";
    	} else if (mat.equals(Material.SANDSTONE)) {
    		return "TeleportShot";
    	} else if (mat.equals(Material.BOOKSHELF)) {
    		return "TripleShot";
    	}
    	return null;
    }
    
    private void addPlayerWeapon(Player p, Material m) {
    	HashMap<String, Weapon> weapons = playerWeapons.get(p);
    	Weapon w = getWeaponFromMaterial(m);
    	weapons.put(w.getName(), w);
    	updateInventory(p, w, true);
    }
    
    private void addPlayerWeapon(Player p, Weapon w) {
    	HashMap<String, Weapon> weapons = playerWeapons.get(p);
    	weapons.put(w.getName(), w);
    	updateInventory(p, w, false);
    }
    
    private Weapon getPlayerWeapon(Player p, String name) {
    	for (Weapon w : playerWeapons.get(p).values()) {
    		if (w.getName().equals(name)) {
    			return w;
    		}
    	}
    	return null;
    }
    
	private void updateInventory(Player p, Weapon w, boolean subtractOne) {
    	PlayerInventory inventory = p.getInventory();
    	int index = inventory.first(w.getGun());
    	if (index > -1) {
    		ItemStack[] stacks = inventory.getContents();
    		int amountToAdd = w.getAmmo() - stacks[index].getAmount() - (subtractOne ? 1 : 0);
    		if (amountToAdd > 0) {
    			inventory.addItem(new ItemStack(w.getGun(), amountToAdd));
    		}
    	} else {
    		inventory.addItem(new ItemStack(w.getGun(), w.getAmmo()  - (subtractOne? 1 : 0)));
    	}
    }
}