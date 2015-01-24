package Hax.Bukkit.PaintballPlugin;
 
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
 
public final class PaintballPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(this, this);
    }
 
    @Override
    public void onDisable() {
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	System.out.println(event.getItem().toString());
    	if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
    		if (event.getItem().getType().equals(Material.SAND)) {
	    		Vector lookVector = event.getPlayer().getEyeLocation().getDirection();
	    		Location spawnLoc = event.getPlayer().getEyeLocation().toVector().add(lookVector.multiply(2)).toLocation(event.getPlayer().getWorld());
	    		
	        	Entity e = event.getPlayer().getWorld().spawnEntity(spawnLoc, EntityType.SNOWBALL);
	        	((Snowball)e).setVelocity(lookVector);
	        	((Snowball)e).setShooter(event.getPlayer());
    		} else if (event.getItem().getType().equals(Material.SANDSTONE)) {
    			System.out.println("LOL");
    			Vector baseVector = event.getPlayer().getEyeLocation().getDirection();
    			Location spawnLoc = event.getPlayer().getEyeLocation().toVector().add(baseVector.multiply(2)).toLocation(event.getPlayer().getWorld());
    			for (int i = 0; i < 20; i++) {
    				Vector shotVector = new Vector(baseVector.getX() + (Math.random() * 1 - 0.5),
    						baseVector.getY() + (Math.random() * 1 - 0.5),
    								baseVector.getZ() + (Math.random() * 1 - 0.5));
    				Entity e = event.getPlayer().getWorld().spawnEntity(spawnLoc, EntityType.SNOWBALL);
    				((Snowball)e).setVelocity(shotVector);
    	        	((Snowball)e).setShooter(event.getPlayer());
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onHit(ProjectileHitEvent event) {
    	if (event.getEntity() instanceof Snowball) {
    		//changeNearbyBlocks(Material.SNOW_BLOCK, event.getEntity().getLocation());
    		event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
    		//event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 10f, true);
    		for (int i = 0; i < 100; i++) {
    	    	Entity e = event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.ARROW);
    			((Arrow)e).setVelocity(new Vector(Math.random() * 1 - 0.5, Math.random() * 1 + 1, Math.random() * 1 - 0.5));
        	}
    	}
    }
    
    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent event) {
    	if (event.getDamager() instanceof Snowball) {
    		Snowball snowball = (Snowball) event.getDamager();
    		if (snowball.getShooter() == event.getEntity()) {
    			event.setCancelled(true);
    		}
    	}
    }
    
    private void changeNearbyBlocks(Material material, Location loc) {
    	for (int x = loc.getBlockX() - 1; x < loc.getBlockX() + 2; x++) {
    		for (int y = loc.getBlockY() - 1; y < loc.getBlockY() + 2; y++) {
    			for (int z = loc.getBlockZ() - 1; z < loc.getBlockZ() + 2; z++) {
    				if (loc.getWorld().getBlockAt(x, y, z).getType().isSolid()) {
    					loc.getWorld().getBlockAt(x, y, z).setType(material);
    				}
    			}
    		}
    	}
    }
}