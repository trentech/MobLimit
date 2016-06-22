package com.gmail.trentech.moblimit;

import java.io.File;
import java.io.IOException;

import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private File file;
	private CommentedConfigurationNode config;
	private ConfigurationLoader<CommentedConfigurationNode> loader;

	public ConfigManager(String configName) {
		String folder = "config" + File.separator + "moblimit";
		if (!new File(folder).isDirectory()) {
			new File(folder).mkdirs();
		}
		file = new File(folder, configName + ".conf");

		create();
		load();
	}

	public ConfigManager() {
		String folder = "config" + File.separator + "moblimit";
		if (!new File(folder).isDirectory()) {
			new File(folder).mkdirs();
		}
		file = new File(folder, "config.conf");

		create();
		load();
	}

	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getConfig() {
		return config;
	}

	private void create() {
		if (!file.exists()) {
			try {
				Main.getLog().info("Creating new " + file.getName() + " file...");
				file.createNewFile();
			} catch (IOException e) {
				Main.getLog().error("Failed to create new config file");
				e.printStackTrace();
			}
		}
	}

	public void init() {
		if (file.getName().equals("global.conf")) {
			for (EntityType entityType : Main.getGame().getRegistry().getAllOf(EntityType.class)) {
				if (Living.class.isAssignableFrom(entityType.getEntityClass()) && !(entityType.equals(EntityTypes.ARMOR_STAND) || entityType.equals(EntityTypes.HUMAN) || entityType.equals(EntityTypes.PLAYER))) {
					if (config.getNode("mobs", entityType.getId()).isVirtual()) {
						config.getNode("mobs", entityType.getId(), "amount").setValue(40);
					}
				}
			}
		} else {
			ConfigurationNode config = new ConfigManager("global").getConfig();

			for (EntityType entityType : Main.getGame().getRegistry().getAllOf(EntityType.class)) {
				if (Living.class.isAssignableFrom(entityType.getEntityClass()) && !(entityType.equals(EntityTypes.ARMOR_STAND) || entityType.equals(EntityTypes.HUMAN) || entityType.equals(EntityTypes.PLAYER))) {
					if (config.getNode("mobs", entityType.getId()).isVirtual()) {
						config.getNode("mobs", entityType.getId(), "amount").setValue(config.getNode("mobs", entityType.getId(), "amount").getDouble());
					}
				}
			}
		}

		save();
	}

	private void load() {
		loader = HoconConfigurationLoader.builder().setFile(file).build();
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}
}
