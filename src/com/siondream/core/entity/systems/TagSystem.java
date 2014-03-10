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

public class TagSystem extends EntitySystem implements EntityListener {
	private static final String TAG = "TagSystem";
	
	private Logger logger;
	private IntMap<Entity> entities;
	private ObjectMap<String, Entity> tags;
	private Family family;
	
	public TagSystem() {
		super();
		
		logger = new Logger(TAG, Env.debugLevel);
		
		logger.info("initialising");
		
		tags = new ObjectMap<String, Entity>();
		family = Family.getFamilyFor();
		
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
		Iterator<String> it = tags.keys().iterator();
		
		while(it.hasNext()) {
			Entity entity = tags.get(it.next());
			
			if (!entities.containsValue(entity, false)) {
				it.remove();
			}
		}
	}
	
	public Entity getEntity(String tag) {
		Entity entity = tags.get(tag, null);
		
		if (entity != null) {
			if (entities.containsValue(entity, false)) {
				return entity;
			}
		}
		
		return null;
	}
	
	public void setTag(Entity entity, String tag) {
		tags.put(tag, entity);
	}

	@Override
	public void entityAdded(Entity entity) {
				
	}

	@Override
	public void entityRemoved(Entity entity) {
		Iterator<Entity> it = tags.values().iterator();
		
		while (it.hasNext()) {
			Entity e = it.next();
			if (e == entity) {
				it.remove();
				break;
			}
		}
	}
}
