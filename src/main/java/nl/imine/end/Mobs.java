package nl.imine.end;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Mobs implements Listener {

	private static Logger logger = JavaPlugin.getPlugin(EndCityPlugin.class).getLogger();

	private static final Random random = new Random();

	private Mobs() {

	}

	public static void init() {
		logger.finer("Mobs [" + CityListener.class + "]");
		Bukkit.getServer().getPluginManager().registerEvents(new Mobs(),
				JavaPlugin.getPlugin(EndCityPlugin.class));
		Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(EndCityPlugin.class), () -> {
			if (CityManager.CITY_WORLD != null) {
				CityManager.CITY_WORLD.getEntitiesByClass(Skeleton.class)
						.forEach(s -> s.getWorld().spawnParticle(Particle.SMOKE_NORMAL,
							s.getLocation().clone().add(0, 0.8, 0), 10, 0.09, 0.09, 0.09, 0.005));
			}
		}, 0l, 10l);
	}

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent evt) {
		if (evt.getLocation().getWorld().equals(CityManager.CITY_WORLD)) {
			if (evt.getSpawnReason().equals(SpawnReason.NATURAL)) {
				if (evt.getEntity().getNearbyEntities(10, 10, 10).size() > 4) {
					evt.setCancelled(true);
				} else {
					int pct = random.nextInt(100);
					if (pct < 45) {
						if (evt.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getType()
								.equals(Material.PURPUR_BLOCK)) {
							if (random.nextInt(20) == 0) {
								evt.getLocation().getWorld().spawnEntity(evt.getLocation(), EntityType.SHULKER);
								evt.setCancelled(true);
							}
						}
					} else if (pct < 70) {
						evt.setCancelled(true);
						Endermite endermite = (Endermite) evt.getLocation().getWorld().spawnEntity(evt.getLocation(),
							EntityType.ENDERMITE);
					} else {
						evt.setCancelled(true);
						Skeleton skeleton = (Skeleton) evt.getLocation().getWorld().spawnEntity(evt.getLocation(),
							EntityType.WITHER_SKELETON);
						skeleton.setCustomName("Wight");
						skeleton.addPotionEffect(
							new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false), true);
						skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(
							skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2);
						skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(
							skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() + 5);
						skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
						skeleton.setHealth(40);
						skeleton.setSilent(true);
						Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(EndCityPlugin.class), () -> {
							skeleton.getEquipment().setHelmet(
								new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.WITHER.ordinal()));
							ItemStack chestPlate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
							LeatherArmorMeta chestPlateMeta = (LeatherArmorMeta) chestPlate.getItemMeta();
							chestPlateMeta.setColor(Color.BLACK);
							chestPlate.setItemMeta(chestPlateMeta);
							skeleton.getEquipment().setChestplate(chestPlate);
							skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
							skeleton.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
							skeleton.getEquipment().setBoots(new ItemStack(Material.AIR));
							skeleton.getEquipment().setLeggings(new ItemStack(Material.AIR));
							skeleton.getEquipment().setHelmetDropChance(0F);
						}, 5l);
					}
				}
			}
		}
	}

	@EventHandler
	public void onMobTarget(EntityTargetEvent evt) {
		if (evt.getEntity().getName().equals("Wight")) {
			if (evt.getEntity().getType().equals(EntityType.WITHER_SKELETON) && evt.getTarget() != null
					&& evt.getTarget().getType().equals(EntityType.PLAYER)) {
				evt.getTarget().getWorld().playSound(evt.getEntity().getLocation(), Sound.ENTITY_GHAST_DEATH, 1f, 0.1f);
				evt.getTarget().getWorld().spawnParticle(Particle.SMOKE_LARGE,
					evt.getEntity().getLocation().clone().add(0, 1.3, 0), 10);
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent evt) {
		if (evt.getEntity().getType().equals(EntityType.WITHER_SKELETON)
				&& evt.getEntity().getWorld().equals(CityManager.CITY_WORLD)
				&& evt.getDamager() instanceof Projectile) {
			evt.setCancelled(true);
		}
		if (evt.getDamager().getType().equals(EntityType.WITHER_SKELETON)
				&& evt.getDamager().getWorld().equals(CityManager.CITY_WORLD)) {
			evt.setDamage(EntityDamageEvent.DamageModifier.ARMOR,
				evt.getDamage(EntityDamageEvent.DamageModifier.ARMOR) * 0.75);
		}
		if (evt.getEntityType().equals(EntityType.ARMOR_STAND)) {
			if (evt.getDamager().getType().equals(EntityType.PLAYER)
					&& ((Player) evt.getDamager()).getGameMode().equals(GameMode.CREATIVE)) {
				evt.setCancelled(false);
				return;
			}
			evt.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityInteract(PlayerInteractAtEntityEvent evt) {
		if (evt.getRightClicked().getWorld().equals(CityManager.CITY_WORLD)
				&& !evt.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			evt.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent evt) {
		if (evt.getEntity().getType().equals(EntityType.WITHER_SKELETON) && evt.getEntity().getName().equals("Wight")) {
			evt.getDrops().removeIf(item -> item.getType().equals(Material.BONE) || item.getType().equals(Material.COAL)
					|| item.getType().equals(Material.SKULL_ITEM));
			int lootinglvl = 0;
			if (evt.getEntity().getKiller() != null) {
				if (((Player) evt.getEntity().getKiller()).getInventory().getItemInMainHand() != null) {
					lootinglvl = ((Player) evt.getEntity().getKiller()).getInventory().getItemInMainHand()
							.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
				}
			}
			if (((double) random.nextInt(1000) / 10.0) <= 2.5 + lootinglvl) {
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1);
				item.setDurability((short) 1);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(String.format("%s%sWight Skull", ChatColor.RESET, ChatColor.AQUA));
				meta.setLore(Arrays.asList(String.format("&r&7Prevents withering damage", ChatColor.RESET, ChatColor.GRAY)));
				item.setItemMeta(meta);
				evt.getDrops().add(item);
			}
			int nPurper = random.nextInt(4 + lootinglvl);
			if (nPurper > 0) {
				evt.getDrops().add(new ItemStack(Material.CHORUS_FRUIT, nPurper));
			}
		}
	}
}
