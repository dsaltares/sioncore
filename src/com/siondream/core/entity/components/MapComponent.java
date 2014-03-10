package com.siondream.core.entity.components;

import com.badlogic.gdx.maps.tiled.TiledMap;

import ashley.core.Component;
import ashley.utils.Pool.Poolable;

public class MapComponent extends Component implements Poolable {

	public TiledMap map;
	
	public MapComponent() {
		map = null;
	}
	
	public MapComponent(MapComponent other) {
		map = other.map;
	}
	
	@Override
	public void reset() {
		map = null;
	}
}
