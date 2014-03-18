package com.siondream.core.virtualviewport;

import com.badlogic.gdx.Gdx;

public class VirtualViewport {

	public final float width;
	public final float height;
	public final float x;
	public final float y;
	
	public float virtualWidth;
	public float virtualHeight;

	public VirtualViewport(float virtualWidth, float virtualHeight) {
		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		
		this.virtualWidth = virtualWidth;
		this.virtualHeight = virtualHeight;
		this.width = getWidth(screenWidth, screenHeight);
		this.height = getHeight(screenWidth, screenHeight);
		this.x = (screenWidth - width) * 0.5f;
		this.y = (screenHeight - height) * 0.5f;
	}

	private float getWidth(float screenWidth, float screenHeight) {
		float virtualAspect = virtualWidth / virtualHeight;
		float aspect = screenWidth / screenHeight;
		if (aspect > virtualAspect || (Math.abs(aspect - virtualAspect) < 0.01f)) {
			return virtualHeight * aspect;
		} else {
			return virtualWidth;
		}
	}

	private float getHeight(float screenWidth, float screenHeight) {
		float virtualAspect = virtualWidth / virtualHeight;
		float aspect = screenWidth / screenHeight;
		if (aspect > virtualAspect || (Math.abs(aspect - virtualAspect) < 0.01f)) {
			return virtualHeight;
		} else {
			return virtualWidth / aspect;
		}
	}
}
