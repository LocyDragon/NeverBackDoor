package com.locydragon.nbr.core;

import com.locydragon.nbr.NeverBackDoor;
import com.locydragon.nbr.core.util.FileBatUtil;
import org.bukkit.Bukkit;

public class PluginSetup {
	public static NeverBackDoor instance;
	public static void setup(NeverBackDoor target) {
		instance = target;
		if (instance.getConfig().getBoolean("SetUpYet")) {
			instance.getLogger().info("NeverBackDoor已经安装完成了.无需安装.");
			return;
		} else {
			if (FileBatUtil.getBatInServer() == null) {
				try {
					throw new UnsupportedOperationException("不兼容的服务端: 未找到启动Bat文件,可能是面板服的原因.");
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
				} finally {
					Bukkit.getLogger().info("不兼容的服务端: 未找到启动Bat文件,可能是面板服的原因.");
					Bukkit.getPluginManager().disablePlugin(instance);
				}
				return;
			}
			Bukkit.getLogger().info("============[NeverBackDoor安装程序]============");
			Bukkit.getLogger().info("NeverBackDoor正在安装!请耐心等待");
			Bukkit.getLogger().info("步骤1: 修改启动文件中...");
			FileBatUtil.setUpJavaAgent(FileBatUtil.getBatInServer());
			instance.getConfig().set("SetUpYet", true);
			instance.saveConfig();
			instance.reloadConfig();
		}
	}
}
