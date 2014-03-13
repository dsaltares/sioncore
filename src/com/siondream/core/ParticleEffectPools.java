package com.siondream.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.badlogic.gdx.utils.ObjectMap.Values;

public class ParticleEffectPools implements Disposable {
	private final static String TAG = "ParticleEffectPools";
	private final static String PARTICLES_FOLDER = "data/particles";
	private final static String PARTICLES_FILE = PARTICLES_FOLDER + "/" + "particles.json";
	
	private Logger logger;
	private ObjectMap<String, ParticleEffectPool> pools;
	
	public ParticleEffectPools() {
		logger = new Logger(TAG, Env.debugLevel);
		
		logger.info("initialising");
		
		pools = new ObjectMap<String, ParticleEffectPool>();
		
		try {
			JsonReader reader = new JsonReader();
			JsonValue root = reader.parse(Gdx.files.internal(PARTICLES_FILE));
			JsonIterator particlesIt = root.iterator();
			
			while (particlesIt.hasNext()) {
				JsonValue particleValue = particlesIt.next();
				String effectName = particleValue.asString();
				logger.info("loading " + effectName);
				ParticleEffect effect = new ParticleEffect();
				effect.load(Gdx.files.internal(effectName), Gdx.files.internal(PARTICLES_FOLDER));
				
				pools.put(effectName, new ParticleEffectPool(effect,
									    					 Env.particlePoolInitialCapacity,
												 			 Env.particlePoolMaxCapacity));
			}
		}
		catch (Exception e) {
			logger.error("failed to list directory " + PARTICLES_FILE);
		}
	}
	
	public PooledEffect obtain(String name) {
		ParticleEffectPool pool = pools.get(name);
		
		if (pool == null) {
			logger.error("pool not found " + name);
			return null;
		}
		
		return pool.obtain();
	}
	
	public void free(PooledEffect effect) {
		Values<ParticleEffectPool> values = pools.values();
		
		while (values.hasNext) {
			values.next().free(effect);
		}
	}
	
	public void clear() {
		Values<ParticleEffectPool> values = pools.values();
		
		while (values.hasNext) {
			values.next().clear();
		}
	}

	@Override
	public void dispose() {
		logger.info("shutting down");
		clear();
	}
}
