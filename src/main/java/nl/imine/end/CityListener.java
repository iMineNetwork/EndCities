package nl.imine.end;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import nl.imine.end.chests.ChestLocation;
import nl.imine.end.chests.ChestReplacer;

public class CityListener implements Listener {

	private static Logger logger = JavaPlugin.getPlugin(EndCityPlugin.class).getLogger();

	private CityListener() {

	}

	public static void init() {
		logger.finer("Init [" + CityListener.class + "]");
		Bukkit.getServer().getPluginManager().registerEvents(new CityListener(),
			JavaPlugin.getPlugin(EndCityPlugin.class));
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent evt) {
		if (evt.getCause().equals(TeleportCause.END_GATEWAY)) {
			if (evt.getFrom().getWorld().equals(CityManager.CITY_WORLD)) {
				if (evt.getTo().getBlockY() < 5) {
					evt.setCancelled(true);
					evt.getPlayer().setFallDistance(0);
					evt.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation(),
							TeleportCause.END_GATEWAY);
				}
			}
		}

		if (evt.getCause().equals(TeleportCause.END_PORTAL)) {
			evt.setCancelled(true);
			evt.getPlayer().setFallDistance(0);
			evt.getPlayer().teleport(CityManager.getRandomSpawn(), TeleportCause.END_GATEWAY);
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent evt) {
		if (evt.getEntity() instanceof Player) {
			if (evt.getCause().equals(DamageCause.WITHER)) {
				if (((Player) evt.getEntity()).getInventory().getHelmet() != null) {
					if (((Player) evt.getEntity()).getInventory().getHelmet().getItemMeta().getLore() != null
							&& ((Player) evt.getEntity()).getInventory().getHelmet().getType()
									.equals(Material.SKULL_ITEM)
							&& (((Player) evt.getEntity()).getInventory().getHelmet()
									.getDurability() == SkullType.WITHER.ordinal())) {
						evt.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent evt) {
		Block block = evt.getClickedBlock();
		Player player = evt.getPlayer();
		if (evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (block.getType().equals(Material.CHEST)) {
				if (block.getWorld().equals(CityManager.CITY_WORLD)) {
					Location bmid = new Location(block.getLocation().getWorld(), block.getLocation().getX() + 0.5,
							block.getLocation().getY(), block.getLocation().getZ() + 0.5);
					CityManager.fillChest(block);
					evt.setCancelled(true);
					Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(EndCityPlugin.class), () -> {
						for (ItemStack item : ((Chest) block.getState()).getBlockInventory().getContents()) {
							if (item != null) {
								block.getWorld().dropItem(bmid, item);
							}
						}
						block.getLocation().getWorld().spigot().playEffect(bmid, Effect.TILE_BREAK, 5, 0, 0.3F, 0.3F,
							0.3F, 0.0F, 100, 5);
						player.playSound(bmid, Sound.BLOCK_WOOD_BREAK, 1, 0);
						new ChestReplacer(new ChestLocation(block.getLocation(),
								((org.bukkit.material.Chest) block.getState().getData()).getFacing()))
										.runTaskLater(JavaPlugin.getPlugin(EndCityPlugin.class), 5L * 60L * 20L);
						block.setType(Material.AIR);
					}, 1l);
				}
			}
			if (evt.getClickedBlock().getType().equals(Material.ANVIL)) {
				if (block.getWorld().equals(CityManager.CITY_WORLD)) {
					evt.setCancelled(true);
				}
			}
			if (evt.getClickedBlock().getType().equals(Material.BED_BLOCK)) {
				if (evt.getClickedBlock().getBiome().equals(Biome.SKY)
						|| evt.getClickedBlock().getBiome().equals(Biome.HELL)) {
					evt.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onEntityHangingBreak(HangingBreakByEntityEvent evt) {
		if (evt.getEntity().getLocation().getWorld().equals(CityManager.CITY_WORLD)) {
			if (evt.getRemover() instanceof Player
					&& ((Player) evt.getRemover()).getGameMode().equals(GameMode.CREATIVE)) {
				return;
			}
			evt.setCancelled(true);
		}
	}
}
