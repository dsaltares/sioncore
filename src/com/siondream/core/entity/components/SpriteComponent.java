package com.siondream.core.entity.components;

import ashley.core.Component;
import ashley.utils.Pool.Poolable;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.siondream.core.animation.SpriteAnimationData;

public class SpriteComponent extends Component implements Poolable {
	public SpriteAnimationData data;
	public Animation currentAnimation;
	public float time;

	public SpriteComponent() {
		reset();
	}
	
	public SpriteComponent(SpriteComponent other) {
		data = other.data;
		currentAnimation = other.currentAnimation;
		time = other.time;
	}
	
	@Override
	public void reset() {
		data = null;
		time = 0.0f;
		currentAnimation = null;
	}
}
