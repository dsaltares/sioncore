package com.siondream.core.virtualviewport;

import com.badlogic.gdx.Gdx;

public class VirtualViewport {

	public final float virtualWidth;
	public final float virtualHeight;

	public VirtualViewport(float virtualWidth, float virtualHeight) {
		this.virtualWidth = virtualWidth;
		this.virtualHeight = virtualHeight;
	}
	
	public float getWidth() {
		float virtualAspect = virtualWidth / virtualHeight;
		float aspect = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		if (aspect > virtualAspect || (Math.abs(aspect - virtualAspect) < 0.01f)) {
			return virtualHeight * aspect;
		} else {
			return virtualWidth;
		}
	}

	public float getHeight() {
		float virtualAspect = virtualWidth / virtualHeight;
		float aspect = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		if (aspect > virtualAspect || (Math.abs(aspect - virtualAspect) < 0.01f)) {
			return virtualHeight;
		} else {
			return virtualWidth / aspect;
		}
	}
	
	public float getX() {
		return (Gdx.graphics.getWidth() - getWidth()) * 0.5f;
	}
	
	public float getY() {
		return (Gdx.graphics.getHeight() - getHeight()) * 0.5f;
	}
}