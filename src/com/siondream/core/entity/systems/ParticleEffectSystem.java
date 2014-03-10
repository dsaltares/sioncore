package com.siondream.core.entity.systems;

import com.badlogic.gdx.utils.Array;
import com.siondream.core.Env;
import com.siondream.core.ParticleEffectPools;
import com.siondream.core.entity.components.ParticleComponent;

import ashley.core.Engine;
import ashley.core.Entity;
import ashley.core.EntitySystem;
import ashley.core.Family;
import ashley.utils.IntMap;
import ashley.utils.IntMap.Values;

public class ParticleEffectSystem extends EntitySystem  {

	private IntMap<Entity> entities;
	private Array<Entity> finishedEntities;
	
	public ParticleEffectSystem() {
		super();

		finishedEntities = new Array<Entity>();
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		entities = Env.game.getEngine().getEntitiesFor(Family.getFamilyFor(ParticleComponent.class));
	}

	@Override
	public void update(float deltaTime) {
		ParticleEffectPools pools = Env.game.getParticlePools();
		Values<Entity> values = entities.values();
		
		while (values.hasNext()) {
			Entity entity = values.next();
			ParticleComponent particleComponent = entity.getComponent(ParticleComponent.class);
			particleComponent.effect.update(deltaTime);
			
			if (particleComponent.effect.isComplete()) {
				pools.free(particleComponent.effect);
				finishedEntities.add(entity);
			}
		}
		
		Engine engine = Env.game.getEngine();
		
		for (Entity entity : finishedEntities) {
			engine.removeEntity(entity);
		}
		
		finishedEntities.clear();
	}
}
