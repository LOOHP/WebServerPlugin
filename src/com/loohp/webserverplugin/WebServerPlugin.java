package com.loohp.webserverplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.loohp.webserverplugin.WebServer.Web;

import net.md_5.bungee.api.ChatColor;

public class WebServerPlugin extends JavaPlugin {
	
	public static WebServerPlugin plugin;
	
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Plugin Data Folder -> ");
		File root = new File(reader.readLine());
		System.out.println("Plugin Data Folder set to " + root.getAbsolutePath());
		System.out.println();
		System.out.print("Port -> ");
		int port = Integer.parseInt(reader.readLine());
		System.out.println("Port set to " + port);
		
		Web.load(port, root, WebServerPlugin.class);
	}
	
	@Override
	public void onEnable() {
		plugin = this;
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		int port = WebServerPlugin.plugin.getConfig().getInt("WebServer.Port");
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> Web.load(port, getDataFolder(), getClass()));
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[WebServerPlugin] WebServerPlugin has been enabled!");
	}
	
	@Override
	public void onDisable() {
		try {
			Web.server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[WebServerPlugin] WebServerPlugin has been disabled!");
	}

}
