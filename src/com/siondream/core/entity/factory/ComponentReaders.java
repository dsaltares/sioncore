package com.siondream.core.entity.factory;

import ashley.core.Component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.siondream.core.Env;
import com.siondream.core.animation.SpriteAnimationData;
import com.siondream.core.entity.components.ColorComponent;
import com.siondream.core.entity.components.FontComponent;
import com.siondream.core.entity.components.MapComponent;
import com.siondream.core.entity.components.OverridePhysicsComponent;
import com.siondream.core.entity.components.ParticleComponent;
import com.siondream.core.entity.components.PhysicsComponent;
import com.siondream.core.entity.components.ShaderComponent;
import com.siondream.core.entity.components.SpineComponent;
import com.siondream.core.entity.components.SpriteComponent;
import com.siondream.core.entity.components.StateComponent;
import com.siondream.core.entity.components.TextureComponent;
import com.siondream.core.entity.components.TransformComponent;
import com.siondream.core.entity.factory.EntityFactory.ComponentReader;
import com.siondream.core.physics.PhysicsData;

public class ComponentReaders {
	public static class ColorComponentReader implements ComponentReader {
		private Json json = new Json();
		
		@Override
		public Class<? extends Component> getComponentClass() {
			return ColorComponent.class;
		}

		@Override
		public Component read(JsonValue value) {
			ColorComponent component = new ColorComponent();
			component.color = json.fromJson(Color.class, value.asString()); 
			return component;
		}
		
	}
	
	public static class FontComponentReader implements ComponentReader {

		@Override
		public Class<? extends Component> getComponentClass() {
			return FontComponent.class;
		}

		@Override
		public Component read(JsonValue value) {
			FontComponent component = new FontComponent();
			component.font = Env.game.getAssets().get(value.getString("name"), BitmapFont.class);
			return component;
		}
		
	}
	
	public static class MapComponentReader implements ComponentReader {

		@Override
		public Class<? extends Component> getComponentClass() {
			return MapComponent.class;
		}

		@Override
		public Component read(JsonValue value) {
			MapComponent component = new MapComponent();
			component.map = Env.game.getAssets().get(value.getString("name"), TiledMap.class);
			return component;
		}
		
	}
	
	public static class OverridePhysicsComponentReader implements ComponentReader {

		@Override
		public Class<? extends Component> getComponentClass() {
			return OverridePhysicsComponent.class;
		}

		@Override
		public Component read(JsonValue value) {
			OverridePhysicsComponent component = new OverridePhysicsComponent();
			component.enable = value.getBoolean("enable", true);
			return component;
		}
		
	}
	
	public static class ParticleComponentReader implements ComponentReader {

		@Override
		public Class<? extends Component> getComponentClass() {
			return ParticleComponent.class;
		}

		@Override
		public Component read(JsonValue value) {
			ParticleComponent component = new ParticleComponent();
			component.name = value.getString("name");
			return component;
		}
		
	}
	
	public static class PhysicsComponentReader implements ComponentReader {

		@Override
		public Class<? extends Component> getComponentClass() {
			return PhysicsComponent.class;
		}

		@Override
		public Component read(JsonValue value) {
			PhysicsComponent component = new PhysicsComponent();
			component.data = Env.game.getAssets().get(value.getString("name"), PhysicsData.class);
			return component;
		}
		
	}
	
	public static class ShaderComponentReader implements ComponentReader {

		@Override
		public Class<? extends Component> getComponentClass() {
			return ShaderComponent.class;
		}

		@Override
		public Component read(JsonValue value) {
			ShaderComponent component = new ShaderComponent();
			component.shader = Env.game.getShaderManager().get(value.getString("name"));
			return component;
		}
		
	}
	
	public static class SpineComponentReader implements ComponentReader {

		@Override
		public Class<? extends Component> getComponentClass() {
			return SpineComponent.class;
		}

		@Override
		public Component read(JsonValue value) {
			SpineComponent component = new SpineComponent();
			component.state = new AnimationState(Env.game.getAssets().get(value.getString("name"), AnimationStateData.class));
			component.skeleton = new Skeleton(component.state.getData().getSkeletonData());
			return component;
		}
		
	}
	
	public static class SpriteComponentReader implements ComponentReader {

		@Override
		public Class<? extends Component> getComponentClass() {
			return SpriteComponent.class;
		}

		@Override
		public Component read(JsonValue value) {
			SpriteComponent component = new SpriteComponent();
			component.data = Env.game.getAssets().get(value.getString("name"), SpriteAnimationData.class);
			return component;
		}
		
	}
	
	public static class StateComponentReader implements ComponentReader {

		@Override
		public Class<? extends Component> getComponentClass() {
			return StateComponent.class;
		}

		@Override
		public Component read(JsonValue value) {
			StateComponent component = new StateComponent();
			component.id = StateComponent.getID(value.getString("name", "idle"));
			return component;
		}
		
	}
	
	public static class TextureComponentReader implements ComponentReader {

		@Override
		public Class<? extends Component> getComponentClass() {
			return TextureComponent.class;
		}

		@Override
		public Component read(JsonValue value) {
			TextureComponent component = new TextureComponent();
			if (value.has("atlas") && value.has("region")) {
				TextureAtlas atlas = Env.game.getAssets().get(value.getString("atlas"), TextureAtlas.class);
				component.region = atlas.findRegion(value.getString("region"));	
			}
			else if (value.has("texture")) {
				component.region = new TextureRegion(Env.game.getAssets().get(value.getString("texture"), Texture.class));
			}
			
			return component;
		}
		
	}
	
	public static class TransformComponentReader implements ComponentReader {

		@Override
		public Class<? extends Component> getComponentClass() {
			return TransformComponent.class;
		}

		@Override
		public Component read(JsonValue value) {
			TransformComponent component = new TransformComponent();
			component.position.x = value.getFloat("x", 0.0f);
			component.position.y = value.getFloat("y", 0.0f);
			component.position.z = value.getFloat("z", 0.0f);
			component.scale = value.getFloat("scale", 1.0f);
			component.angle = value.getFloat("angle", 0.0f);
			return component;
		}
		
	}
}
