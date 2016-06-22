package com.gmail.trentech.moblimit;

import java.util.function.Predicate;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.world.World;

public class EventManager {

	@Listener
	public void onSpawnEntityEvent(SpawnEntityEvent event) {
		World world = event.getTargetWorld();

		for (Entity entity : event.getEntities()) {
			EntityType entityType = entity.getType();

			if (!Living.class.isAssignableFrom(entityType.getEntityClass())) {
				return;
			}

			if (entityType.equals(EntityTypes.ARMOR_STAND) || entityType.equals(EntityTypes.HUMAN) || entityType.equals(EntityTypes.PLAYER)) {
				return;
			}

			int amount = new ConfigManager(world.getName()).getConfig().getNode("mobs", entityType.getId(), "amount").getInt();

			Predicate<Entity> filter = new Predicate<Entity>() {

				@Override
				public boolean test(Entity entity) {
					return entity.getType().equals(entityType);
				}
			};

			if (world.getEntities(filter).size() >= amount) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event) {
		String name = event.getTargetWorld().getName();

		new ConfigManager(name).init();
	}
}
