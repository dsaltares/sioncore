package com.siondream.core.entity.systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Transform;
import com.siondream.core.entity.components.OverridePhysicsComponent;
import com.siondream.core.entity.components.PhysicsComponent;
import com.siondream.core.entity.components.TransformComponent;

import ashley.core.Entity;
import ashley.core.Family;
import ashley.systems.IteratingSystem;

public class PhysicsSystem extends IteratingSystem {

	public PhysicsSystem() {
		super(Family.getFamilyFor(PhysicsComponent.class,
								  TransformComponent.class));
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
		TransformComponent transform = entity.getComponent(TransformComponent.class);
		OverridePhysicsComponent override = entity.getComponent(OverridePhysicsComponent.class);
		
		Transform bodyTransform = physics.body.getTransform();
		
		if (override != null && override.enable) {
			Vector2 bodyPosition = bodyTransform.getPosition();
			bodyPosition.set(transform.position.x, transform.position.y);
			physics.body.setTransform(bodyPosition, transform.angle);
		}
		else {
			Vector2 bodyPosition = bodyTransform.getPosition();
			transform.position.x = bodyPosition.x;
			transform.position.y = bodyPosition.y;
			transform.angle = bodyTransform.getRotation();
		}
	}
}
