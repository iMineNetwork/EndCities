package nl.imine.end.chests;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import nl.imine.end.CityManager;
import nl.imine.end.EndCityPlugin;

public class ChestReplacer extends BukkitRunnable {

	private static final List<ChestLocation> CHEST_LIST = new ArrayList<>();
	private ChestLocation location;

	public ChestReplacer(ChestLocation location) {
		this.location = location;
		ChestReplacer.CHEST_LIST.add(location);
	}

	@Override
	public void run() {
		boolean isPlayerInRange = false;
		for (Player player : location.getLocation().getWorld().getPlayers()) {
			if (location.getLocation().distance(player.getLocation()) <= 20) {
				isPlayerInRange = true;
			}
		}
		if (!isPlayerInRange) {
			CityManager.createLootChest(location);
			ChestReplacer.CHEST_LIST.remove(location);
		} else {
			Bukkit.getServer().getScheduler().runTaskLater(JavaPlugin.getPlugin(EndCityPlugin.class), this,
					60L * 20L);
		}
	}

	public static void replaceAllMissing() {
		for (ChestLocation loc : ChestReplacer.CHEST_LIST) {
			CityManager.createLootChest(loc);
		}
	}
}