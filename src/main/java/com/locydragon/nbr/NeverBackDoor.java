package com.locydragon.nbr;

import com.locydragon.nbr.core.PluginSetup;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * @author LocyDragon
 */
public class NeverBackDoor extends JavaPlugin {
	public NeverBackDoor(){}
	public static FileConfiguration config;
	@Override
	public void onEnable() {
		saveDefaultConfig();
		config = getConfig();
		PluginSetup.setup(this);
		Bukkit.getLogger().info("NeverBackDoor正在24小时保护您的服务器!");
	}
}
