package com.siondream.core.entity.systems;

import java.util.Iterator;

import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.siondream.core.Env;

import ashley.core.Engine;
import ashley.core.Entity;
import ashley.core.EntityListener;
import ashley.core.EntitySystem;
import ashley.core.Family;
import ashley.utils.IntMap;

public class GroupSystem extends EntitySystem implements EntityListener {
	private static final String TAG = "GroupSystem";
	
	private Logger logger;
	private IntMap<Entity> entities;
	private ObjectMap<String, IntMap<Entity>> groups;
	private Family family;
	private IntMap<Entity> emptyGroup;
	
	public GroupSystem() {
		super();
		
		logger = new Logger(TAG, Env.debugLevel);
		
		logger.info("initialising");
		
		groups = new ObjectMap<String, IntMap<Entity>>();
		family = Family.getFamilyFor();
		emptyGroup = new IntMap<Entity>();
		
		Env.game.getEngine().addEntityListener(this);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(family);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		entities = null;
	}
	
	@Override
	public void update(float deltaTime) {
		Iterator<String> groupIt = groups.keys().iterator();
		
		while(groupIt.hasNext()) {
			IntMap<Entity> groupEntities = groups.get(groupIt.next());
			Iterator<Entity> entityIt = groupEntities.values().iterator();
			
			while (entityIt.hasNext()) {
				if (!entities.containsValue(entityIt.next(), false)) {
					entityIt.remove();
				}
			}
			
			if (groupEntities.size == 0) {
				groupIt.remove();
			}
		}
	}
	
	public IntMap<Entity> getGroup(String name) {
		IntMap<Entity> group = groups.get(name, emptyGroup);
		
		if (group == emptyGroup) {
			logger.error("group " + name + " doesn't exist");
		}
		
		return group;
	}
	
	public void register(Entity entity, String name) {
		if (!entities.containsValue(entity, false)) {
			logger.error("entity " + entity + " is not registered in engine");
			return;
		}
		
		logger.info("registering entity " + entity + " in group " + name);
		IntMap<Entity> group = groups.get(name, null);
		
		if (group == null) {
			group = new IntMap<Entity>();
			groups.put(name, group);
		}
		
		group.put(entity.getIndex(), entity);
	}
	
	public void unregister(Entity entity, String name) {
		IntMap<Entity> group = getGroup(name);
		
		if (group != emptyGroup) {
			logger.info("unregistering entity " + entity + " from group " + name);
			group.remove(entity.getIndex());
			
			if (group.size == 0) {
				groups.remove(name);
			}
		}
	}
	
	public void unregister(Entity entity) {
		logger.info("unregistering entity " + entity + " from all groups");
		
		Iterator<String> groupIt = groups.keys().iterator();
		
		while(groupIt.hasNext()) {
			IntMap<Entity> groupEntities = groups.get(groupIt.next());
			Iterator<Entity> entityIt = groupEntities.values().iterator();
			
			while (entityIt.hasNext()) {
				if (entity == entityIt.next()) {
					entityIt.remove();
				}
			}
			
			if (groupEntities.size == 0) {
				groupIt.remove();
			}
		}
	}
	
	public void clear(String name) {
		groups.remove(name);
	}
	
	public void clear() {
		groups.clear();
	}

	@Override
	public void entityAdded(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entityRemoved(Entity entity) {
		unregister(entity);
	}
}
