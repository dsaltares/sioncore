package com.siondream.core.entity.systems;

import com.siondream.core.entity.components.SpineComponent;
import com.siondream.core.entity.components.TransformComponent;

import ashley.core.Entity;
import ashley.core.Family;
import ashley.systems.IteratingSystem;

public class SpineAnimationSystem extends IteratingSystem {
	public SpineAnimationSystem() {
		super(Family.getFamilyFor(SpineComponent.class));
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		SpineComponent animation = entity.getComponent(SpineComponent.class);
		TransformComponent transform = entity.getComponent(TransformComponent.class);
		
		if (transform != null) {
			animation.skeleton.setX(transform.position.x);
			animation.skeleton.setY(transform.position.y);
		}
		
		animation.state.update(deltaTime);
		animation.state.apply(animation.skeleton);
		animation.skeleton.updateWorldTransform();
	}
}
