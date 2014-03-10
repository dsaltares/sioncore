package com.siondream.core.entity.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Disposable;

import ashley.core.Component;

public class ColorComponent extends Component implements Disposable {

	public Color color;
	
	public ColorComponent() {
		color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public ColorComponent(ColorComponent other) {
		color = new Color(other.color);
	}
	
	@Override
	public void dispose() {
		color.r = color.b = color.b = color.a = 1.0f;
	}

}
