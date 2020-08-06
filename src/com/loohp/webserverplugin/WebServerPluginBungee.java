package com.loohp.webserverplugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import com.loohp.webserverplugin.WebServer.Web;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class WebServerPluginBungee extends Plugin {

	public WebServerPluginBungee plugin;

	public static net.md_5.bungee.config.Configuration configuration;
	public static ConfigurationProvider config;

	@Override
	public void onEnable() {
		plugin = this;

		if (!getDataFolder().exists())
			getDataFolder().mkdirs();

		File file = new File(getDataFolder(), "config.yml");

		if (!file.exists()) {
			try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		config = ConfigurationProvider.getProvider(YamlConfiguration.class);
		
		try {
			configuration = config.load(new File(getDataFolder(), "config.yml"));
			config.save(configuration, new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int port = configuration.getInt("WebServer.Port");

		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				Web.load(port, getDataFolder(), getClass());
			}
		});
		t1.start();

		getLogger().info(ChatColor.GREEN + "[WebServerPlugin] WebServerPlugin has been enabled!");
	}

	@Override
	public void onDisable() {
		getLogger().info(ChatColor.RED + "[WebServerPlugin] WebServerPlugin has been disabled!");
	}
}
