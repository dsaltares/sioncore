package com.siondream.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;

public class ShaderManager implements Disposable {
	private static final String TAG = "ShaderManager";
	private static final String file = "data/shaders/shaders.json";
	
	private Logger logger;
	private ObjectMap<String, ShaderProgram> shaders;
	
	public ShaderManager() {
		logger = new Logger(TAG, Env.debugLevel);
		
		logger.info("initialising");
		
		shaders = new ObjectMap<String, ShaderProgram>();
		
		loadJson();
	}
	
	public ShaderProgram get(String name) {
		ShaderProgram shader = shaders.get(name, null);
		
		if (shader == null) {
			logger.error("shader " + name + " not found");
		}
		
		return shader;
	}

	@Override
	public void dispose() {
		logger.info("shutting down");
		
		Values<ShaderProgram> values = shaders.values();
		
		while (values.hasNext) {
			values.next().dispose();
		}
	}
	
	private void loadJson() {
		logger.info("loading file " + file);
		
		try {
			JsonReader reader = new JsonReader();
			JsonValue root = reader.parse(Gdx.files.internal(file));
			JsonIterator rootIt = root.iterator();
			
			while (rootIt.hasNext()) {
				JsonValue shaderValue = rootIt.next();
				
				String name = shaderValue.get("name").asString();
				String vertex = shaderValue.get("vertex").asString();
				String fragment = shaderValue.get("fragment").asString();
				
				logger.info("loading shader " + name + " vertex: " + vertex + " fragment: " + fragment);
				
				try {
					ShaderProgram shader = new ShaderProgram(Gdx.files.internal(vertex), Gdx.files.internal(fragment));
					
					if (!shader.isCompiled()) {
						logger.error("error loading shader " + name);
						logger.equals(shader.getLog());
					}
					else {
						shaders.put(name, shader);
					}
				}
				catch (Exception e) {
					logger.error("error loading shader " + name + " " + e.getMessage());
				}
			}
		}
		catch (Exception e) {
			logger.error("error loading file " + file + " " + e.getMessage());
		}
	}
}
