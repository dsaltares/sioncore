package com.siondream.core.entity.components;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.utils.Disposable;
import com.siondream.core.Env;

import ashley.core.Component;

public class ParticleComponent extends Component implements Disposable {

	public PooledEffect effect;
	public String name;
	
	public ParticleComponent() {
		effect = null;
		name = null;
	}
	
	public ParticleComponent(ParticleComponent other) {
		name = other.name;
		effect = Env.game.getParticlePools().obtain(name);
	}
	
	@Override
	public void dispose() {
		if (effect != null) {
			Env.game.getParticlePools().free(effect);
		}
		
		name = null;
		effect = null;
	}
}
