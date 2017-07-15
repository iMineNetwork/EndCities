package nl.imine.end.util;

import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import net.drgnome.nbtlib.NBT;
import net.drgnome.nbtlib.NBTLib;
import net.drgnome.nbtlib.Tag;
import nl.imine.end.EndCityPlugin;

public class NBTUtil {

	private static Logger logger = JavaPlugin.getPlugin(EndCityPlugin.class).getLogger();

	public static Map<String, Tag> getEntityNBT(Entity entity, String entityType) {
		try {
			Object mcEntity = NBTLib.invokeCraftbukkit("entity.CraftEntity", entity, "getHandle", new Class[0]);
			Object nbt = NBTLib.instantiateMinecraft("NBTTagCompound", new Class[0]);
			NBTLib.invokeMinecraft(entityType, mcEntity, "b", new Class[]{NBTLib.getMinecraftClass("NBTTagCompound")},
					nbt);
			return NBT.NBTToMap(nbt);
		} catch (Exception e) {
				logger.severe("Exception loading Entity NBT: " + e.getMessage());
		}
		return null;
	}

	public static void setEntityNBT(Entity entity, String entityType, Map<String, Tag> nbt) {
		try {
			Object mcEntity = NBTLib.invokeCraftbukkit("entity.CraftEntity", entity, "getHandle", new Class[0]);
			Object nbtObject = NBT.mapToNBT(nbt);
			NBTLib.invokeMinecraft(entityType, mcEntity, "a", new Class[]{NBTLib.getMinecraftClass("NBTTagCompound")},
					nbtObject);
		} catch (Exception e) {
			logger.severe("Exception saving Entity NBT: " + e.getMessage());
		}
	}

	public static Map<String, Tag> getTileEntityNBT(Block block, String blockType) {
		try {
			Object mcBlock = NBTLib.invokeCraftbukkit("block.CraftChest", block.getState(), "getTileEntity",
					new Class[0]);
			Object nbt = NBTLib.instantiateMinecraft("NBTTagCompound", new Class[0]);
			NBTLib.invokeMinecraft(blockType, mcBlock, "a", new Class[]{NBTLib.getMinecraftClass("NBTTagCompound")},
					nbt);
			return NBT.NBTToMap(nbt);
		} catch (Exception e) {
			logger.severe("Exception loading Block NBT: " + e.getMessage());
		}
		return null;
	}

	public static void setTileEntityNBT(Block block, String blockType, Map<String, Tag> nbt) {
		try {
			Object tileEntity = NBTLib.invokeCraftbukkit("block.CraftChest", block.getState(), "getTileEntity",
					new Class[0]);
			Object nbtObject = NBT.mapToNBT(nbt);
			NBTLib.invokeMinecraft(blockType, tileEntity, "a", new Class[]{NBTLib.getMinecraftClass("NBTTagCompound")},
					nbtObject);
		} catch (Exception e) {
			logger.severe("Exception saving Block NBT: " + e.getMessage());
		}
	}
}
