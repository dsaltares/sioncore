package com.siondream.core.spine;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.badlogic.gdx.utils.Logger;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.siondream.core.Env;
import com.siondream.core.spine.SkeletonDataLoader.SkeletonDataLoaderParameter;

public class AnimationStateDataLoader
	extends AsynchronousAssetLoader<AnimationStateData,
									AnimationStateDataLoader.AnimationStateDataLoaderParameter> {

	private Logger logger;
	private AnimationStateData data;
	
	public AnimationStateDataLoader(FileHandleResolver resolver) {
		super(resolver);
		
		logger = new Logger("AnimationStateDataLoader", Env.debugLevel);
		data = null;
		
		logger.info("initialising");
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, AnimationStateDataLoaderParameter parameter) {
		logger.info("loading " + fileName);
		
		data = new AnimationStateData(manager.get(parameter.skeletonName, SkeletonData.class));
		
		try {
			JsonReader reader = new JsonReader();
			JsonValue root = reader.parse(file);
			
			float defaultBlend = root.get("defaultBlend").asFloat();
			
			logger.info("default blend: " + defaultBlend + "s");
			
			data.setDefaultMix(defaultBlend);
			
			JsonIterator blendIt = root.get("blends").iterator();
			
			while (blendIt.hasNext()) {
				JsonValue blendValue = blendIt.next();
				String from = blendValue.get("from").asString(); 
				String to = blendValue.get("to").asString();
				float duration = blendValue.get("duration").asFloat();
				
				data.setMix(from, to , duration);
				
				logger.info("blend: " + from + " -> " + to + " [" + duration + "s]");
			}
		}
		catch (Exception e) {
			logger.error("error loading " + fileName + " " + e.getMessage());
		}
	}

	@Override
	public AnimationStateData loadSync(AssetManager manager, String fileName, FileHandle file, AnimationStateDataLoaderParameter parameter) {
		return data;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AnimationStateDataLoaderParameter parameter) {
		SkeletonDataLoaderParameter skeletonParam = new SkeletonDataLoaderParameter();
		skeletonParam.atlasName = parameter.atlasName;
		skeletonParam.scale = Env.pixelsToMetres * parameter.scale;
		
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
		deps.add(new AssetDescriptor(parameter.skeletonName, SkeletonData.class, skeletonParam));
		
		return deps;
	}
	
	static public class AnimationStateDataLoaderParameter extends AssetLoaderParameters<AnimationStateData> {
		private String skeletonName;
		private String atlasName;
		private float scale;
		
		public AnimationStateDataLoaderParameter() {
			skeletonName = "";
			atlasName = "";
			scale = 1.0f;
		}
	}
}
