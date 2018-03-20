package com.gmail.trentech.moblimit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;

import com.gmail.trentech.pjc.core.ConfigManager;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class Common {

	public static void init() {
		initConfig();
	}
	
	public static void initConfig() {
		ConfigManager configManager = ConfigManager.init(Main.getPlugin(), "global");
		CommentedConfigurationNode config = configManager.getConfig();

		for (EntityType entityType : Sponge.getRegistry().getAllOf(EntityType.class)) {
			if (Living.class.isAssignableFrom(entityType.getEntityClass()) && !(entityType.equals(EntityTypes.ARMOR_STAND) || entityType.equals(EntityTypes.HUMAN) || entityType.equals(EntityTypes.PLAYER))) {
				if (config.getNode("mobs", entityType.getId()).isVirtual()) {
					config.getNode("mobs", entityType.getId(), "amount").setValue(40);
				}
			}
		}

		configManager.save();
	}
}
