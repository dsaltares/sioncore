package com.siondream.core;

import java.util.Locale;

import ashley.core.Engine;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.siondream.core.assets.Assets;
import com.siondream.core.entity.components.TransformComponent;
import com.siondream.core.entity.factory.ComponentReaders.ColorComponentReader;
import com.siondream.core.entity.factory.ComponentReaders.FontComponentReader;
import com.siondream.core.entity.factory.ComponentReaders.MapComponentReader;
import com.siondream.core.entity.factory.ComponentReaders.OverridePhysicsComponentReader;
import com.siondream.core.entity.factory.ComponentReaders.ParticleComponentReader;
import com.siondream.core.entity.factory.ComponentReaders.PhysicsComponentReader;
import com.siondream.core.entity.factory.ComponentReaders.ShaderComponentReader;
import com.siondream.core.entity.factory.ComponentReaders.SpineComponentReader;
import com.siondream.core.entity.factory.ComponentReaders.SpriteComponentReader;
import com.siondream.core.entity.factory.ComponentReaders.StateComponentReader;
import com.siondream.core.entity.factory.ComponentReaders.TextureComponentReader;
import com.siondream.core.entity.factory.ComponentReaders.TransformComponentReader;
import com.siondream.core.entity.factory.EntityFactory;
import com.siondream.core.physics.CategoryBitsManager;
import com.siondream.core.physics.CollisionHandler;
import com.siondream.core.tweeners.ActorTweener;
import com.siondream.core.tweeners.CameraTweener;
import com.siondream.core.tweeners.TransformTweener;
import com.siondream.core.tweeners.VirtualCameraTweener;
import com.siondream.core.virtualviewport.VirtualCamera;
import com.siondream.core.virtualviewport.VirtualViewport;
import com.siondream.core.virtualviewport.VirtualViewportBuilder;

public class SionGame extends Game implements InputProcessor {

	private final String TAG = "SionGame";
	
	private Logger logger;
	private ObjectMap<Class<? extends SionScreen>, SionScreen> screens;
	private SionScreen nextScreen;
	private SionScreen currentScreen;
	private InputMultiplexer multiplexer;
	private VirtualViewportBuilder viewportBuilder;
	private VirtualViewportBuilder uiViewportBuilder;
	private VirtualViewport viewport;
	private VirtualViewport uiViewport;
	private VirtualCamera camera;
	private VirtualCamera uiCamera;
	private Vector3 oldCameraPos;
	
	private Assets assets;
	
	private Engine engine;
	private Stage stage;
	private TweenManager tweenManager;
	private LanguageManager languageManager;
	private World world;
	private double accumulator;
	private double currentTime;
	private ParticleEffectPools particlePools;
	private ShaderManager shaderManager;
	private EntityFactory entityFactory;
	private CategoryBitsManager categoryBitsManager;
	private CollisionHandler collisionHandler;
	
	@Override
	public void create() {
		Env.init(this);
		
		logger = new Logger(TAG, Env.debugLevel);
		
		logger.info("initialising");
		
		categoryBitsManager = new CategoryBitsManager();
		
		assets = new Assets("data/config/assets.json");
		assets.loadGroup("base");
		assets.finishLoading();
		
		screens = new ObjectMap<Class<? extends SionScreen>, SionScreen>();
		nextScreen = null;
		currentScreen = null;
		
		viewportBuilder = new VirtualViewportBuilder(800 * Env.pixelsToMetres, 600 * Env.pixelsToMetres, 1280 * Env.pixelsToMetres, 720 * Env.pixelsToMetres);
		viewport = viewportBuilder.getVirtualViewport(Gdx.graphics.getWidth() * Env.pixelsToMetres, Gdx.graphics.getHeight() * Env.pixelsToMetres);
		camera = new VirtualCamera(viewport);
		camera.setPosition(0.0f, 0.0f);
		
		uiViewportBuilder = new VirtualViewportBuilder(800, 600, 1280, 720);
		uiViewport = uiViewportBuilder.getVirtualViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera = new VirtualCamera(viewport);
		uiCamera.setPosition(0.0f, 0.0f);
		
		oldCameraPos = new Vector3(camera.position);
		
		tweenManager = new TweenManager();
		Tween.registerAccessor(OrthographicCamera.class, new CameraTweener());
		Tween.registerAccessor(VirtualCamera.class, new VirtualCameraTweener());
		Tween.registerAccessor(Actor.class, new ActorTweener());
		Tween.registerAccessor(TransformComponent.class, new TransformTweener());
		Tween.setCombinedAttributesLimit(4);
		
		world = new World(Env.gravity, Env.doSleep);
		collisionHandler = new CollisionHandler();
		world.setContactListener(collisionHandler);
		
		accumulator = 0.0;
		currentTime = TimeUtils.millis() / 1000.0;
		engine = new Engine();
		stage = new Stage(Env.virtualWidth, Env.virtualHeight, true);
		
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
		Gdx.input.setCatchBackKey(Env.catchBack);
		Gdx.input.setCatchMenuKey(Env.catchMenu);
		multiplexer.addProcessor(this);
		
		languageManager = new LanguageManager("data/lang", Locale.getDefault().getLanguage());
		particlePools = new ParticleEffectPools();
		shaderManager = new ShaderManager();
		
		entityFactory = new EntityFactory();
		entityFactory.registerReader(new ColorComponentReader());
		entityFactory.registerReader(new FontComponentReader());
		entityFactory.registerReader(new MapComponentReader());
		entityFactory.registerReader(new OverridePhysicsComponentReader());
		entityFactory.registerReader(new ParticleComponentReader());
		entityFactory.registerReader(new PhysicsComponentReader());
		entityFactory.registerReader(new ShaderComponentReader());
		entityFactory.registerReader(new SpineComponentReader());
		entityFactory.registerReader(new SpriteComponentReader());
		entityFactory.registerReader(new StateComponentReader());
		entityFactory.registerReader(new TextureComponentReader());
		entityFactory.registerReader(new TransformComponentReader());
	}

	@Override
	public void dispose() {
		logger.info("shutting down");
		
		assets.dispose();
		world.dispose();
		stage.dispose();
		
		if (currentScreen != null) {
			currentScreen.dispose();
		}
	}
	
	@Override
	public void render() {
		
		double newTime = TimeUtils.millis() / 1000.0;
		double frameTime = Math.min(newTime - currentTime, 0.25);
		float deltaTime = (float)frameTime;
		
		currentTime = newTime;
		
		if (currentScreen != null) {
			currentScreen.render(deltaTime);
		}
		
		tweenManager.update(deltaTime);
		
		accumulator += frameTime;
		
		while (accumulator >= Env.physicsDeltaTime) {
			world.step(Env.physicsDeltaTime, Env.velocityIterations, Env.positionIterations);
			accumulator -= Env.physicsDeltaTime;
		}
		
		Color background = Env.backgroundColor;
		Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_BLEND);
		
		camera.update();
		uiCamera.update();
		engine.update(deltaTime);
		stage.act(deltaTime);
		
		performScreenChange();
	}

	@Override
	public void resize(int width, int height) {
		oldCameraPos.set(camera.position);
		viewport = viewportBuilder.getVirtualViewport(width * Env.pixelsToMetres, height * Env.pixelsToMetres);
		camera.setVirtualViewport(viewport);
		camera.setPosition(oldCameraPos.x, oldCameraPos.y);
		
		oldCameraPos.set(uiCamera.position);
		uiViewport = uiViewportBuilder.getVirtualViewport(width, height);
		uiCamera.setVirtualViewport(uiViewport);
		uiCamera.setPosition(oldCameraPos.x, oldCameraPos.y);
		
		if (currentScreen != null) {
			currentScreen.resize(width, height);
		}
	}

	@Override
	public void pause() {
		if (currentScreen != null) {
			currentScreen.pause();
		}
	}

	@Override
	public void resume() {
		if (currentScreen != null) {
			currentScreen.resume();
		}
	}
	
	protected void addScreen(SionScreen screen) {
		screens.put(screen.getClass(), screen);
	}
	
	public <T extends SionScreen> T getScreen(Class<T> type) {
		return type.cast(screens.get(type));
	}
	
	@Override
	public void setScreen(Screen screen) {
		logger.error("method not supported");
	}
	
	public void setScreen(Class<? extends SionScreen> type) {
		SionScreen screen = screens.get(type);
		
		if (screen != null) {
			nextScreen = screen;
		}
		else {
			logger.error("invalid screen " + type.getName());
		}
	}
	
	public Assets getAssets() {
		return assets;
	}
	
	public VirtualCamera getCamera() {
		return camera;
	}
	
	public VirtualViewport getViewport() {
		return viewport;
	}
	
	public VirtualViewportBuilder getViewportBuilder() {
		return viewportBuilder;
	}
	
	public VirtualCamera getUICamera() {
		return uiCamera;
	}
	
	public VirtualViewport getUIViewport() {
		return uiViewport;
	}
	
	public VirtualViewportBuilder getUIViewportBuilder() {
		return uiViewportBuilder;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public TweenManager getTweenManager() {
		return tweenManager;
	}
	
	public Engine getEngine() {
		return engine;
	}
	
	public LanguageManager getLang() {
		return languageManager;
	}
	
	public InputMultiplexer getInputMultiplexer() {
		return multiplexer;
	}
	
	public ParticleEffectPools getParticlePools() {
		return particlePools;
	}
	
	public ShaderManager getShaderManager() {
		return shaderManager;
	}
	
	public EntityFactory getEntityFactory() {
		return entityFactory;
	}
	
	public CategoryBitsManager getCategoryBitsManager() {
		return categoryBitsManager;
	}
	
	public CollisionHandler getCollisionHandler() {
		return collisionHandler;
	}
	
	private void performScreenChange() {
		if (nextScreen != null) {
			logger.info("switching to screen " + screens.findKey(nextScreen, false));
			multiplexer.removeProcessor(currentScreen);
			setScreenInternal(nextScreen);
			multiplexer.addProcessor(currentScreen);
			nextScreen = null;
		}
	}
	
	private void setScreenInternal(SionScreen screen) {
		super.setScreen(screen);
		currentScreen = screen;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (Env.debug) {
			if (keycode == Keys.F1) {
				Env.init(this);
			}
		}
		
		return stage.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		return stage.keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		return stage.keyTyped(character);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return stage.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return stage.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return stage.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return stage.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean scrolled(int amount) {
		return stage.scrolled(amount);
	}
}
