package com.siondream.core.entity.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;

import ashley.core.Component;

public class SpineComponent extends Component implements Poolable {
	public Skeleton skeleton;
	public AnimationState state;
	
	public SpineComponent() {
		reset();
	}
	
	public SpineComponent(SpineComponent other) {
		state = new AnimationState(other.state.getData());
		skeleton = new Skeleton(state.getData().getSkeletonData());
	}
	
	@Override
	public void reset() {
		skeleton = null;
		state = null;
	}
}
