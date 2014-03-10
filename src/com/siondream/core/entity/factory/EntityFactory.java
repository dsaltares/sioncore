package com.siondream.core.entity.factory;

import java.lang.reflect.Constructor;

import ashley.core.Component;
import ashley.core.Entity;
import ashley.utils.ImmutableArray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.siondream.core.Env;

public class EntityFactory {
	
	private static final String TAG = "EntityFactory";
	private static final String ARCHETYPES_FILE = "data/entities/entities.json"; 
	
	private Logger logger;
	private ObjectMap<String, Entity> archetypes;
	private ObjectMap<Class<? extends Component>, ComponentReader> readers;
	private ObjectMap<Class<? extends Component>, Constructor<? extends Component>> constructors;
	
	public EntityFactory () {
		logger = new Logger(TAG, Env.debugLevel);
		logger.info("initialising");
		
		archetypes = new ObjectMap<String, Entity>();
		readers = new ObjectMap<Class<? extends Component>, ComponentReader>();
		constructors = new ObjectMap<Class<? extends Component>, Constructor<? extends Component>>();
	}
	
	public void registerReader(ComponentReader reader) {
		Class<? extends Component> c = reader.getComponentClass();
		
		logger.info("registering reader for component " + c.getSimpleName());
		
		if (readers.containsKey(c)) {
			logger.error("already has a reader for component " + c.getSimpleName());
			return;
		}
		
		// Check whether or not the component class is copy-constructable
		try {	
			Constructor<? extends Component> constructor = c.getConstructor(c);
			constructors.put(c, constructor);
		}
		catch (NoSuchMethodException e) {
			logger.error("component does not have a copy constructor " + c.getSimpleName());
			return;
		}
		
		readers.put(c, reader);
	}
	
	public void loadArchetypes() {
		try {
			JsonReader reader = new JsonReader();
			JsonValue root = reader.parse(Gdx.files.internal(ARCHETYPES_FILE));
			JsonIterator entitiesIt = root.iterator();
			
			while (entitiesIt.hasNext()) {
				JsonValue entity = entitiesIt.next();
				loadEntityArchetype(Gdx.files.internal(entity.asString()));
			}
		}
		catch (Exception e) {
			logger.error("failed to process entity list " + ARCHETYPES_FILE);
		}
	}
	
	public Entity createEntity(String name) {
		logger.info("creating a new entity from archetype " + name);
		
		Entity archetype = archetypes.get(name);
		
		if (archetype == null) {
			logger.error("the archetype name does not exist " + name);
		}
		
		Entity newEntity = new Entity();
		ImmutableArray<Component> components = archetype.getComponents();
		int numComponents = components.getSize();
		
		for (int i = 0; i < numComponents; ++i) {
			Component component = components.get(i);
			Class<? extends Component> componentClass = component.getClass();
			ComponentReader reader = readers.get(componentClass);
			Constructor<? extends Component> constructor = constructors.get(componentClass);
			
			if (constructor == null || reader == null) {
				logger.error("component doesn't have either a valid reader or a copy constructor " + componentClass.getSimpleName());
				continue;
			}
			
			try {
				logger.info("adding new component " + componentClass.getSimpleName());
				newEntity.add(constructor.newInstance(archetype.getComponent(componentClass)));
			}catch (Exception e) {
				logger.error("error trying to instantiate component " + componentClass.getSimpleName());				
			}	
		}
		
		logger.info("adding entity to engine " + name);
		Env.game.getEngine().addEntity(newEntity);
		
		return newEntity;
	}
	
	private void loadEntityArchetype(FileHandle file) {
		logger.info("loading entity archetype " + file.name());
		
		try {
			JsonReader reader = new JsonReader();
			JsonValue root = reader.parse(file);
			
			Entity entity = new Entity();
			
			JsonIterator componentIt = root.iterator();
			
			while (componentIt.hasNext()) {
				JsonValue componentValue = componentIt.next();
				
				String name = componentValue.get("class").asString();
				Class<? extends Component> componentClass = (Class<? extends Component>)Class.forName(name);
				
				if (componentClass == null) {
					logger.error("invalid component class " + name);
					continue;
				}
				
				ComponentReader componentReader = readers.get(componentClass);
				
				if (componentReader == null) {
					logger.error("component reader not found for class " + name);
					continue;
				}
				
				Component component = componentReader.read(componentValue);
				
				if (component != null) {
					logger.info("adding component to entity " + name);
					entity.add(component);
				}
			}
			
			archetypes.put(file.nameWithoutExtension(), entity);
		}
		catch (Exception e) {
			logger.error("error reading " + file.name());
		}
	}
	
	public static interface ComponentReader {
		public Class<? extends Component> getComponentClass();
		public Component read(JsonValue value);
	}
}
