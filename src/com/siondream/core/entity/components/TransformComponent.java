package com.siondream.core.entity.components;

import com.badlogic.gdx.math.Vector3;

import ashley.core.Component;
import ashley.utils.Pool.Poolable;

public class TransformComponent extends Component implements Poolable {
	public Vector3 position;
	public float scale;
	public float angle;
	
	public TransformComponent() {
		position = new Vector3();
		reset();
	}
	
	public TransformComponent(TransformComponent other) {
		position = new Vector3(other.position);
		scale = other.scale;
		angle = other.angle;
	}
	
	@Override
	public void reset() {
		position.set(0.0f, 0.0f, 0.0f);
		scale = 1.0f;
		angle = 0.0f;
	}
}
