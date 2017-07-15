package nl.imine.end;

import org.bukkit.plugin.java.JavaPlugin;

import nl.imine.end.chests.ChestReplacer;

public class EndCityPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		CityManager.init();
	}

	@Override
	public void onDisable() {
		ChestReplacer.replaceAllMissing();
	}
}
