package com.siondream.core.entity.systems;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.siondream.core.Env;
import com.siondream.core.entity.components.ColorComponent;
import com.siondream.core.entity.components.MapComponent;
import com.siondream.core.entity.components.ParticleComponent;
import com.siondream.core.entity.components.SpineComponent;
import com.siondream.core.entity.components.TextureComponent;
import com.siondream.core.entity.components.TransformComponent;

import ashley.core.Engine;
import ashley.core.Entity;
import ashley.core.EntitySystem;
import ashley.core.Family;
import ashley.utils.IntMap;
import ashley.utils.IntMap.Values;

public class RenderingSystem extends EntitySystem implements Disposable {

	protected SpriteBatch batch;
	protected OrthographicCamera camera;
	protected Viewport viewport;
	protected OrthographicCamera uiCamera;
	protected Viewport uiViewport;
	protected ShapeRenderer shapeRenderer;
	protected IntMap<Entity> mapEntities;
	
	private IntMap<Entity> worldEntities;
	private IntMap<Entity> particleEntities;
	private IntMap<Entity> spineAnimatedEntities;
	private Array<Entity> sortedEntities;
	private DepthSorter sorter;
	private Box2DDebugRenderer box2DRenderer;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private FrameBuffer particleFrameBuffer;
	private TextureRegion particleRegion;
	private SkeletonRenderer skeletonRenderer;
	
	private BitmapFont debugFont;
	
	private int previousWidth;
	private int previousHeight;
	
	public RenderingSystem() {
		super();
		
		this.sortedEntities = new Array<Entity>(100);
		this.batch = new SpriteBatch();
		this.camera = Env.game.getCamera();
		this.viewport = Env.game.getViewport();
		this.uiCamera = Env.game.getUICamera();
		this.uiViewport = Env.game.getUIViewport();
		this.sorter = new DepthSorter();
		this.shapeRenderer = new ShapeRenderer();
		this.box2DRenderer = new Box2DDebugRenderer(Env.drawBodies,
													Env.drawJoints,
													Env.drawABBs,
													Env.drawInactiveBodies,
													Env.drawVelocities,
													Env.drawContacts);
		
		this.particleFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		this.particleRegion = new TextureRegion(particleFrameBuffer.getColorBufferTexture());
		this.particleRegion.flip(false, true);
		
		this.skeletonRenderer = new SkeletonRenderer();
		
		if (Env.debug) {
			debugFont = Env.game.getAssets().get("data/ui/default.fnt", BitmapFont.class);
		}
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		worldEntities = engine.getEntitiesFor(Family.getFamilyFor(TextureComponent.class, TransformComponent.class));
		particleEntities = engine.getEntitiesFor(Family.getFamilyFor(ParticleComponent.class));
		mapEntities = engine.getEntitiesFor(Family.getFamilyFor(MapComponent.class));
		spineAnimatedEntities = engine.getEntitiesFor(Family.getFamilyFor(SpineComponent.class));
	}

	@Override
	public void update(float deltaTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		viewport.update(width, height);
		
		renderMap();
		
		batch.begin();
		renderWorldEntities();
		batch.end();
		
		renderParticles();
		
		uiViewport.update(width, height);
		uiCamera.position.set(uiViewport.getWorldWidth() * 0.5f, uiViewport.getWorldHeight() * 0.5f, 0.0f);
		Env.game.getStage().draw();
		
		viewport.update(width, height);
		debugDrawWorld();
		
		uiViewport.update(width, height);
		debugDrawUI();
		
		if (previousWidth != width || previousHeight != height) {
			Env.game.resize(width, height);
			previousWidth = width;
			previousHeight = height;
		}
	}
	
	@Override
	public void dispose() {
		if (mapRenderer != null) {
			mapRenderer.dispose();
			mapRenderer = null;
		}
		
		batch.dispose();
		shapeRenderer.dispose();
		box2DRenderer.dispose();
//		particleFrameBuffer.dispose();
	}
	
	protected void renderWorldEntities() {
		
		Values<Entity> values = worldEntities.values();
		
		while (values.hasNext()) {
			Entity entity = values.next();
			
			TextureComponent texture = entity.getComponent(TextureComponent.class);
			TransformComponent transform = entity.getComponent(TransformComponent.class);
			
			if (isInFustrum(texture, transform)) {
				sortedEntities.add(entity);
			}
		}
		
		values = spineAnimatedEntities.values();
		
		while (values.hasNext()) {
			sortedEntities.add(values.next());
		}
		
		sortedEntities.sort(sorter);
		batch.setProjectionMatrix(camera.combined);
		
		for (Entity entity : sortedEntities) {
			if (entity.hasComponent(TextureComponent.class)) {
				TextureComponent texture = entity.getComponent(TextureComponent.class);
				TransformComponent transform = entity.getComponent(TransformComponent.class);
			
				float scale = transform.scale * Env.pixelsToMetres;
				float width = texture.region.getRegionWidth();
				float height = texture.region.getRegionHeight();
				float originX = width * 0.5f;
				float originY = height * 0.5f;
				
				batch.draw(texture.region,
						   transform.position.x - originX,
						   transform.position.y - originY,
						   originX,
						   originY,
						   width,
						   height,
						   scale,
						   scale,
						   MathUtils.radiansToDegrees * transform.angle);
			}
			else {
				SpineComponent animation = entity.getComponent(SpineComponent.class);
				skeletonRenderer.draw(batch, animation.skeleton);
			}
		}
		
		sortedEntities.clear();
	}
	
	protected void renderMap() {
		// If there are no map entities, dispose the renderer
		if (mapEntities.size == 0) {
			if (mapRenderer != null) {
				mapRenderer.dispose();
				mapRenderer = null;
			}
		}
		else {
			Entity mapEntity = mapEntities.values().next();
			MapComponent mapComponent = mapEntity.getComponent(MapComponent.class);
			
			if (map != mapComponent.map) {
				if (mapRenderer != null) {
					mapRenderer.dispose();
				}
				
				map = mapComponent.map;
				mapRenderer = new OrthogonalTiledMapRenderer(map, Env.pixelsToMetres);
			}
			
			// Render
			mapRenderer.setView(camera);
			mapRenderer.render();
		}
	}
	
	protected void renderUI() {
		Env.game.getStage().draw();
	}
	
	protected void renderParticles() {
		particleFrameBuffer.begin();
		batch.begin();
		
		Color initialColor = batch.getColor();
		
		Values<Entity> values = particleEntities.values();
		
		while (values.hasNext()) {
			Entity entity = values.next();
			ParticleComponent particle = entity.getComponent(ParticleComponent.class);
			ColorComponent color = entity.getComponent(ColorComponent.class);
			
			if (color != null) {
				batch.setColor(color.color);
			}
			
			particle.effect.draw(batch);
			
			batch.setColor(initialColor);
		}
		
		batch.end();
		particleFrameBuffer.end();
		
		batch.begin();
		batch.draw(particleRegion, 0.0f, 0.0f);
		batch.end();
	}
	
	protected void debugDrawWorld() {
		if (Env.debug) {
			
			shapeRenderer.setProjectionMatrix(camera.combined);
			
			if (Env.drawGrid) {
				// Debug shapes
				shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
				shapeRenderer.begin(ShapeType.Line);
				shapeRenderer.line(-Env.virtualWidth * 0.5f, 0.0f, Env.virtualWidth * 0.5f, 0.0f);
				shapeRenderer.line(0.0f, -Env.virtualHeight * 0.5f, 0.0f, Env.virtualHeight * 0.5f);
				
				shapeRenderer.setColor(0.0f, 1.0f, 0.0f, 1.0f);
				
				for (int i = -100; i <= 100; ++i) {
					if (i == 0)
						continue;
					
					shapeRenderer.line(-Env.virtualWidth * 0.5f, i, Env.virtualWidth * 0.5f, i);
				}
				
				for (int i = -100; i <= 100; ++i) {
					if (i == 0)
						continue;
					
					shapeRenderer.line(i, -Env.virtualHeight * 0.5f, i, Env.virtualHeight * 0.5f);
				}
				
				shapeRenderer.end();
			}
			
			box2DRenderer.setDrawAABBs(Env.drawABBs);
			box2DRenderer.setDrawBodies(Env.drawBodies);
			box2DRenderer.setDrawContacts(Env.drawContacts);
			box2DRenderer.setDrawInactiveBodies(Env.drawInactiveBodies);
			box2DRenderer.setDrawJoints(Env.drawJoints);
			box2DRenderer.setDrawVelocities(Env.drawVelocities);
			box2DRenderer.render(Env.game.getWorld(), camera.combined);
		}
	}
	
	protected void debugDrawUI() {
		if (Env.debug) {
			if (Env.drawFPS) {
				String fpsText = String.format("%d FPS", Gdx.graphics.getFramesPerSecond());
				TextBounds bounds = debugFont.getBounds(fpsText);
				batch.setProjectionMatrix(uiCamera.combined);
				batch.begin();
				debugFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				debugFont.draw(batch, fpsText, uiViewport.getWorldWidth() - bounds.width - 20.0f, 20.0f);
				batch.end();
			}
			
			Table.drawDebug(Env.game.getStage());
		}
	}
	
	private boolean isInFustrum(TextureComponent texture, TransformComponent transform) {
		if (camera == null) {
			return false;
		}
		
		Vector3 cameraPos = camera.position;
		Vector3 position = transform.position;
		float width = texture.region.getRegionWidth();
		float height = texture.region.getRegionHeight();
		float originX = width * 0.5f;
		float originY = height * 0.5f;
		float scale = transform.scale;
		float halfWidth = camera.viewportWidth * 0.5f;
		float halfHeight = camera.viewportHeight * 0.5f;

		if (position.x + width * scale - originX < cameraPos.x - halfWidth || position.x - originX > cameraPos.x + halfWidth) return false;
		if (position.y + height * scale - originY < cameraPos.y - halfHeight || position.y - originY > cameraPos.y + halfHeight) return false;
		
		return true;
	}
	
	private class DepthSorter implements Comparator<Entity> {

		@Override
		public int compare(Entity e1, Entity e2) {
			TransformComponent t1 = e1.getComponent(TransformComponent.class);
			TransformComponent t2 = e2.getComponent(TransformComponent.class);
			
			if (t1 == null) return -1;
			if (t2 == null) return 1;
			
			return (int)(t2.position.z - t1.position.z);
		}
	}
}
