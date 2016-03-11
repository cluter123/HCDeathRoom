package me.cluter.hcdeath;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(new listener(this), this);
		saveDefaultConfig();
	}
	
	public void onDisable() {
		saveConfig();
	};

	ArrayList<String> dead = new ArrayList<String>();
	String prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix")) + " ";

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ((!(sender instanceof Player))) {
			sender.sendMessage("You are not a player");
			return false;
		}
		Player player = (Player) sender;
		if (label.equalsIgnoreCase("hcdr")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Hardcore Death Room made by cluter123.");
				return true;
			}
			if (args[0].equalsIgnoreCase("setspawn")) {
				if (!player.hasPermission("hcdr.setspawn")) {
					sender.sendMessage(prefix + ChatColor.RED + "You do not have permission to do this.");
					return false;
				}
				getConfig().set("spawn.world", player.getWorld().getName());
				getConfig().set("spawn.x", player.getLocation().getX());
				getConfig().set("spawn.y", player.getLocation().getY());
				getConfig().set("spawn.z", player.getLocation().getZ());
				getConfig().set("spawn.yaw", player.getLocation().getYaw());
				getConfig().set("spawn.pitch", player.getLocation().getPitch());
				saveConfig();
				player.sendMessage(prefix + ChatColor.AQUA + "Death room respawn location has been set.");
				return true;
			} else if (args[0].equalsIgnoreCase("release")) {
				if (!player.hasPermission("hcdr.release")) {
					sender.sendMessage(prefix + ChatColor.RED + "You do not have permission to do this.");
					return false;
				}
				if (args[1].equals(null)) {
					sender.sendMessage(ChatColor.RED + "Please specify a player.");
				}
				if (!dead.contains(args[1])) {
					sender.sendMessage(ChatColor.RED + "Player not found.");
					return false;
				}
				dead.remove(args[1]);
				Bukkit.getServer().getPlayer(args[1])
						.teleport(Bukkit.getServer().getPlayer(args[1]).getWorld().getSpawnLocation());
				sender.sendMessage(prefix + ChatColor.RED + args[1] + " has been released.");
				Bukkit.getServer().getPlayer(args[1]).sendMessage(
						prefix + ChatColor.translateAlternateColorCodes('&', getConfig().getString("releaseMsg")));
				return true;
			} else {
				player.sendMessage(ChatColor.RED + "Usage: </hcdr setspawn> | </hcdr release>");
				return true;
			}
		}
		return false;
	}

}
