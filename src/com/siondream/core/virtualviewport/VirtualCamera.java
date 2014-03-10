package com.siondream.core.virtualviewport;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class VirtualCamera extends OrthographicCamera {
	Vector3 tmp = new Vector3();
	Vector2 origin = new Vector2();
	VirtualViewport virtualViewport;

	public VirtualCamera(VirtualViewport virtualViewport) {
		this(virtualViewport, 0f, 0f);
	}

	public VirtualCamera(VirtualViewport virtualViewport, float cx, float cy) {
		this.virtualViewport = virtualViewport;
		this.origin.set(cx, cy);
		updateViewport();
	}

	public void setVirtualViewport(VirtualViewport virtualViewport) {
		this.virtualViewport = virtualViewport;
		updateViewport();
	}
	
	public void setPosition(float x, float y) {
		position.set(x - viewportWidth * origin.x, y - viewportHeight * origin.y, 0f);
	}

	@Override
	public void update() {
		float left = zoom * -viewportWidth * 0.5f + virtualViewport.virtualWidth * origin.x;
		float right = zoom * viewportWidth * 0.5f + virtualViewport.virtualWidth * origin.x;
		float top = zoom * viewportHeight * 0.5f + virtualViewport.virtualHeight * origin.y;
		float bottom = zoom * -viewportHeight * 0.5f + virtualViewport.virtualHeight * origin.y;

		projection.setToOrtho(left, right, bottom, top, Math.abs(near), Math.abs(far));
		view.setToLookAt(position, tmp.set(position).add(direction), up);
		combined.set(projection);
		Matrix4.mul(combined.val, view.val);
		invProjectionView.set(combined);
		Matrix4.inv(invProjectionView.val);
		frustum.update(invProjectionView);
	}

	private void updateViewport() {
		setToOrtho(false, virtualViewport.getWidth(), virtualViewport.getHeight());
	}
}