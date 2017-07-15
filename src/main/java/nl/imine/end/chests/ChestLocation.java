package nl.imine.end.chests;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class ChestLocation {

	private final Location location;
	private final BlockFace facing;

	public ChestLocation(Location location, BlockFace facing) {
		this.location = location;
		this.facing = facing;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the facing
	 */
	public BlockFace getFacing() {
		return facing;
	}
}
