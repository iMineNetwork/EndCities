package nl.imine.end;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.material.Chest;
import org.bukkit.material.Skull;
import org.bukkit.plugin.java.JavaPlugin;

import net.drgnome.nbtlib.Tag;
import nl.imine.end.chests.ChestLocation;
import nl.imine.end.util.NBTUtil;
import sun.rmi.runtime.Log;

public class CityManager {

	private static Logger logger = JavaPlugin.getPlugin(EndCityPlugin.class).getLogger();

	private static final List<Location> SPAWNS = new ArrayList<>();

	public static World CITY_WORLD;

	public static void init() {
		logger.finer("Init [" + CityManager.class + "]");
		CityListener.init();
		Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(EndCityPlugin.class), () -> {
			CITY_WORLD = Bukkit
					.createWorld(new WorldCreator("EndCities").environment(World.Environment.THE_END));
			SPAWNS.add(new Location(CITY_WORLD, -160.5, 78.5, 263.5, 0, 0));
			SPAWNS.add(new Location(CITY_WORLD, -132.5, 92.5, 331.5, 180, 0));
			SPAWNS.add(new Location(CITY_WORLD, -243.5, 104.5, 339.5, 0, 0));
			SPAWNS.add(new Location(CITY_WORLD, -264.5, 95.5, 260.5, 90, 0));
			SPAWNS.add(new Location(CITY_WORLD, -224.5, 70.5, 280.5, -90, 0));
		}, 1L);
		Mobs.init();
	}

	public static Location getRandomSpawn() {
		return SPAWNS.get(new Random().nextInt(SPAWNS.size()));
	}

	public static void createLootChest(ChestLocation location) {
		System.out.println(location.getLocation() + " || " + location.getFacing());
		Block block = location.getLocation().getBlock();
		Chest md = new Chest(location.getFacing());
		block.setTypeIdAndData(md.getItemTypeId(), md.getData(), false);
	}

	public static void fillChest(Block block) {
		try {
			Map<String, Tag> nbt = NBTUtil.getTileEntityNBT(block, "TileEntityChest");
			nbt.put("LootTable", Tag.newString("chests/end_city_treasure"));
			NBTUtil.setTileEntityNBT(block, "TileEntityChest", nbt);
		} catch (Exception e) {
			logger.severe("Exception handeling lootchest data: " + e.getMessage());
		}
	}
}
