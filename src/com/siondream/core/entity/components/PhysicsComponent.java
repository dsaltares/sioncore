package com.siondream.core.entity.components;

import com.badlogic.gdx.physics.box2d.Body;
import com.siondream.core.Env;
import com.siondream.core.physics.PhysicsData;

import ashley.core.Component;
import ashley.utils.Pool.Poolable;

public class PhysicsComponent extends Component implements Poolable {
	public Body body;
	public PhysicsData data;
	
	public PhysicsComponent() {
		body = null;
		data = null;
	}
	
	public PhysicsComponent(PhysicsComponent other) {
		data = other.data;
		body = data.createBody(null);
	}
	
	@Override
	public void reset() {
		if (body != null) {
			Env.game.getWorld().destroyBody(body);	
		}
		
		body = null;
		data = null;
	}
}
