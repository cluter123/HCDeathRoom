package me.cluter.hcdeath;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class listener implements Listener{
	Main plugin;
	 
	public listener(Main instance) {
	plugin = instance;
	}
	String perm = "hcdr.time";
	
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if (p.hasPermission("hcdr.exempt"))
			return;
		if (!(p.getKiller() instanceof Player))
			return;
		if (plugin.getConfig().getString("spawn.x") == null) {
			p.sendMessage(plugin.prefix + ChatColor.RED
					+ "HardCoreDeathRoom has not been set up properly! Please contact an admin.");
			return;
		}
		int c = 0;
		if (plugin.getConfig().getString("spawn.x") != null) {
			for (int i = 1; i < 6; i++) {
				if (!p.hasPermission(perm + i))
					continue;
				plugin.dead.add(p.getName());
				c = i;
				break;
			}
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					if (plugin.dead.contains(p.getName())) {
						plugin.dead.remove(p.getName());
						p.teleport(p.getWorld().getSpawnLocation());
						p.sendMessage(plugin.prefix
								+ ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("releaseMsg")));
					}
				}
			}, plugin.getConfig().getInt("time" + Integer.toString(c)) * 20);
		}
	}
	
	@EventHandler
	public void onSpawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (plugin.dead.contains(p.getName())) {
			World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("spawn.world"));
			Location l = new Location(w, plugin.getConfig().getDouble("spawn.x"), plugin.getConfig().getDouble("spawn.y"),
					plugin.getConfig().getDouble("spawn.z"));
			Float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
			Float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");
			l.setYaw(yaw);
			l.setPitch(pitch);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					if (plugin.dead.contains(p.getName())) {
						p.teleport(l);
						p.sendMessage(plugin.prefix
								+ ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("deathMsg")));
					}
				}
			}, 1);
		}
	}
	
	@EventHandler
	public void noCommand(PlayerCommandPreprocessEvent e) {
		if (!(plugin.dead.contains(e.getPlayer().getName())))
			return;
		e.setCancelled(true);
		e.getPlayer().sendMessage(
				plugin.prefix + ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("cmdErrorMsg")));
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (!(plugin.dead.contains(e.getPlayer().getName())))
			return;
		e.setCancelled(true);
		e.getPlayer().sendMessage(
				plugin.prefix + ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("blockErrorMsg")));

	}
}
