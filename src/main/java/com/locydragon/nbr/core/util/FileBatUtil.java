package com.locydragon.nbr.core.util;

import com.locydragon.nbr.core.PluginSetup;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBatUtil {
	public static File getBatInServer() {
		for (File obj : new File(".//").listFiles()) {
			if (obj.getName().endsWith(".bat")) {
				return obj;
			}
		}
		return null;
	}
	public static void setUpJavaAgent(File targetBat) {
		PluginSetup.instance.getLogger().info(targetBat.getAbsolutePath());
		try {
			BufferedReader br = new BufferedReader(new FileReader(targetBat));
			List<String> lines = new ArrayList<>();
			String obj;
			while ((obj = br.readLine()) != null) {
				lines.add(obj);
			}
			Bukkit.getLogger().info(lines.toString());
			br.close();
			targetBat.delete();
			targetBat.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(targetBat));
			for (String each : lines) {
				if (each.startsWith("java")) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0;i < each.toCharArray().length;i++) {
						if (i == 4) {
							sb.append(" ");
							sb.append("-javaagent:.\\plugins\\NeverBackDoor.jar=NeverBackDoor");
							sb.append(" ");
						}
						sb.append(each.toCharArray()[i]);
					}
					bw.write(sb.toString());
				} else {
					bw.write(each);
				}
				bw.newLine();
				continue;
			}
			bw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
