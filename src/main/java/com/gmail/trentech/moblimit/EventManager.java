package com.gmail.trentech.moblimit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjc.core.ConfigManager;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class EventManager {

	@Listener
	public void onSpawnEntityEvent(SpawnEntityEvent event) {
		for (Entity entity : event.getEntities()) {
			EntityType entityType = entity.getType();

			if (!Living.class.isAssignableFrom(entityType.getEntityClass())) {
				return;
			}

			if (entityType.equals(EntityTypes.ARMOR_STAND) || entityType.equals(EntityTypes.HUMAN) || entityType.equals(EntityTypes.PLAYER)) {
				return;
			}

			World world = entity.getWorld();
			
			int amount = ConfigManager.get(Main.getPlugin(), world.getName()).getConfig().getNode("mobs", entityType.getId(), "amount").getInt();

			if(amount == -1) {
				amount = ConfigManager.get(Main.getPlugin(), "global").getConfig().getNode("mobs", entityType.getId(), "amount").getInt();
			}

			if (world.getEntities(e -> entity.getType().equals(entityType)).size() >= amount) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event) {
		ConfigManager configManager = ConfigManager.init(Main.getPlugin(), event.getTargetWorld().getName());
		CommentedConfigurationNode config = configManager.getConfig();
		
		config.getNode("mobs").setComment("Set amount to -1 to use global configration");
		
		for (EntityType entityType : Sponge.getRegistry().getAllOf(EntityType.class)) {
			if (Living.class.isAssignableFrom(entityType.getEntityClass()) && !(entityType.equals(EntityTypes.ARMOR_STAND) || entityType.equals(EntityTypes.HUMAN) || entityType.equals(EntityTypes.PLAYER))) {
				if (config.getNode("mobs", entityType.getId()).isVirtual()) {
					config.getNode("mobs", entityType.getId(), "amount").setValue(-1);
				}
			}
		}
		
		configManager.save();
	}
}
