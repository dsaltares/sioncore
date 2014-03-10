package com.siondream.core.entity.systems;

import com.badlogic.gdx.utils.Array;
import com.siondream.core.Env;
import com.siondream.core.entity.components.StateComponent;

import ashley.core.Engine;
import ashley.core.Entity;
import ashley.core.Family;
import ashley.systems.IteratingSystem;

public class DisposingSystem extends IteratingSystem {

	Array<Entity> toRemove;
	int eraseID;
	
	
	public DisposingSystem() {
		super(Family.getFamilyFor(StateComponent.class));
		
		toRemove = new Array<Entity>();
		eraseID = StateComponent.getID("erase");
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		Engine engine = Env.game.getEngine();
		
		for (Entity entity : toRemove) {
			engine.removeEntity(entity);
		}
		
		toRemove.clear();
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		StateComponent state = entity.getComponent(StateComponent.class);
		
		if (state.id == eraseID) {
			toRemove.add(entity);
		}
	}

}
