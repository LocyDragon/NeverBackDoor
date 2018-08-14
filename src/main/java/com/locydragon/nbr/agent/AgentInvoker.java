package com.locydragon.nbr.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;

public class AgentInvoker {
	public static void premain(String args, Instrumentation i) {
		try {
			System.out.println("[NeverBackDoor]我们正在动态注入.");
			ClassPool pool = ClassPool.getDefault();
			Path path = Paths.get(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6));
			String pathS = URLDecoder.decode(path.toFile().getPath(), "utf-8");
			System.out.print("如有报错，请确认核心地址: "+pathS);
			pool.insertClassPath(pathS);
			pool.importPackage("org.bukkit.plugin.java.PluginClassLoader");
			pool.importPackage("org.bukkit.Bukkit");
			pool.importPackage("org.bukkit.plugin.java.JavaPluginLoader");
			pool.importPackage("java.io.File");
			pool.importPackage("java.io.FileReader");
			pool.importPackage("java.io.InputStream");
			pool.importPackage("org.bukkit.plugin.PluginDescriptionFile");
			pool.importPackage("java.util.jar.JarEntry");
			pool.importPackage("java.util.jar.JarFile");
			{ //JavaPlugin 注入
				CtClass pluginClass = pool.getCtClass("org.bukkit.plugin.java.JavaPlugin");
				CtConstructor con = pluginClass.getConstructors()[0];
				pluginClass.removeConstructor(con);
				StringBuilder body = new StringBuilder();
				body.append("{\n");
				body.append("if (getClass().getName().equals(\"com.locydragon.nbr.NeverBackDoor\")) { \n");
				body.append("ClassLoader clr = getClass().getClassLoader();\n");
				body.append("File plugin = new File(\".//plugins//NeverBackDoor.jar\");\n");
				body.append("File folder = new File(\".//plugins//\");\n");
				body.append("JarFile jar = null;\n");
				body.append("JarEntry entry = null;\n");
				body.append("InputStream stream = null;\n");
				body.append("try{ \n");
				body.append("jar = new JarFile(plugin);\n");
				body.append("entry = jar.getJarEntry(\"plugin.yml\");\n");
				body.append("stream = jar.getInputStream(entry);\n");
				body.append("} catch (Exception e){}\n");
				body.append("PluginDescriptionFile desc = new PluginDescriptionFile(stream);\n");
				body.append("PluginClassLoader cl = new PluginClassLoader(new JavaPluginLoader(Bukkit.getServer()), clr, desc, plugin, folder);\n");
				body.append("cl.initialize(this);\n");
				body.append("return;\n");
				body.append("}\n");
				body.append("ClassLoader classLoader = getClass().getClassLoader();\n" +
						"    if (!(classLoader instanceof PluginClassLoader)) {\n" +
						"      throw new IllegalStateException(\"JavaPlugin requires \" + PluginClassLoader.class.getName());\n" +
						"    }\n" +
						"    ((PluginClassLoader)classLoader).initialize(this);");
				body.append("}\n");
				con.setBody(body.toString());
				pluginClass.addConstructor(con);
				i.addTransformer(new ClassFileTransformer() {
					@Override
					public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
						className = className.replace("/", ".");
						if (className.equals("org.bukkit.plugin.java.JavaPlugin")) {
							try {
								return pluginClass.toBytecode();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						return null;
					}
				});
			}
			{
				Thread asyncThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(20 * 1000);
							PluginLoader loader = new JavaPluginLoader(Bukkit.getServer());
							loader.loadPlugin(new File(".//plugins//NeverBackDoor.jar"));
						} catch (Exception exc) {
							exc.printStackTrace();
						}
					}
				});
				asyncThread.start();
			}
			//############################################
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
