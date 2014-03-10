package com.siondream.core.entity.components;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

import ashley.core.Component;

public class ShaderComponent extends Component implements Disposable {

	public ShaderProgram shader;
	
	public ShaderComponent() {
		shader = null;
	}
	
	public ShaderComponent(ShaderComponent other) {
		shader = other.shader;
	}
	
	@Override
	public void dispose() {
		shader = null;
	}
}
