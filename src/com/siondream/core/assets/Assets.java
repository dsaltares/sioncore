package com.siondream.core.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.siondream.core.Env;
import com.siondream.core.animation.SpriteAnimationData;
import com.siondream.core.animation.SpriteAnimationLoader;
import com.siondream.core.physics.PhysicsData;
import com.siondream.core.physics.PhysicsLoader;
import com.siondream.core.spine.AnimationStateDataLoader;
import com.siondream.core.spine.SkeletonDataLoader;

public class Assets implements Disposable, AssetErrorListener {

	private static final String TAG = "Assets";
	private Logger logger;
	private AssetManager manager;
	private ObjectMap<String, Array<Asset>> groups;
	
	public Assets(String assetFile) {
		logger = new Logger(TAG, Env.debugLevel);
		
		logger.info("initialising");
		
		manager = new AssetManager();
//		manager.setErrorListener(this);
		manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		manager.setLoader(PhysicsData.class, new PhysicsLoader(new InternalFileHandleResolver()));
		manager.setLoader(SpriteAnimationData.class, new SpriteAnimationLoader(new InternalFileHandleResolver()));
		manager.setLoader(SkeletonData.class, new SkeletonDataLoader(new InternalFileHandleResolver()));
		manager.setLoader(AnimationStateData.class, new AnimationStateDataLoader(new InternalFileHandleResolver()));
		
		loadGroups(assetFile);
	}
		
	public void loadGroup(String groupName) {
		logger.info("loading group " + groupName);
		
		Array<Asset> assets = groups.get(groupName, null);
		
		if (assets != null) {
			for (Asset asset : assets) {
				manager.load(asset.path, asset.type, asset.parameters);
			}
		}
		else {
			logger.error("error loading group " + groupName + ", not found");
		}
	}
	
	public void unloadGroup(String groupName) {
		logger.info("unloading group " + groupName);
		
		Array<Asset> assets = groups.get(groupName, null);
		
		if (assets != null) {
			for (Asset asset : assets) {
				if (manager.isLoaded(asset.path, asset.type)) {
					manager.unload(asset.path);
				}
			}
		}
		else {
			logger.error("error unloading group " + groupName + ", not found");
		}
	}
	
	public synchronized <T> T get(String fileName) {
		return manager.get(fileName);
	}
	
	public synchronized <T> T get(String fileName, Class<T> type) {
		return manager.get(fileName, type);
	}
	
	public <T> boolean isLoaded(String fileName, Class<T> type) {
		return manager.isLoaded(fileName, type);
	}
	
	public boolean update() {
		return manager.update();
	}
	
	public void finishLoading() {
		manager.finishLoading();
	}
	
	public float getProgress() {
		return manager.getProgress();
	}

	@Override
	public void dispose() {
		logger.info("shutting down");
		manager.dispose();
	}
	
	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		logger.error("error loading " + asset.fileName + " message: " + throwable.getMessage());
	}
	
	private void loadGroups(String assetFile) {
		groups = new ObjectMap<String, Array<Asset>>();
		
		logger.info("loading file " + assetFile);
		
//		try {
			Json json = new Json();
			JsonReader reader = new JsonReader();
			JsonValue root = reader.parse(Gdx.files.internal(assetFile));
			
			JsonIterator groupIt = root.iterator();
			
			while (groupIt.hasNext()) {
				JsonValue groupValue = groupIt.next();
				
				if (groups.containsKey(groupValue.name)) {
					logger.error("group " + groupValue.name + " already exists, skipping");
					continue;
				}
				
				logger.info("registering group " + groupValue.name);
				
				Array<Asset> assets = new Array<Asset>();
				
				JsonIterator assetIt = groupValue.iterator();
				
				while (assetIt.hasNext()) {
					JsonValue assetValue = assetIt.next();
					
					Asset asset = json.fromJson(Asset.class, assetValue.toString());
					assets.add(asset);
				}
				
				groups.put(groupValue.name, assets);
			}
//		}
//		catch (Exception e) {
//			logger.error("error loading file " + assetFile + " " + e.getMessage());
//		}
	}
}