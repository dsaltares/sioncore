package com.siondream.core.virtualviewport;

public class VirtualViewportBuilder {
	public final float minWidth;
	public final float minHeight;
	public final float maxWidth;
	public final float maxHeight;

	public VirtualViewportBuilder(float minWidth, float minHeight, float maxWidth, float maxHeight) {
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	public VirtualViewport getVirtualViewport(float width, float height) {
		if (insideBounds(width, height))
			return new VirtualViewport(width, height);

		float aspect = width / height;

		float scaleForMinSize = minWidth / width;
		float scaleForMaxSize = maxWidth / width;

		float virtualViewportWidth = width * scaleForMaxSize;
		float virtualViewportHeight = virtualViewportWidth / aspect;

		if (insideBounds(virtualViewportWidth, virtualViewportHeight))
			return new VirtualViewport(virtualViewportWidth, virtualViewportHeight);

		virtualViewportWidth = width * scaleForMinSize;
		virtualViewportHeight = virtualViewportWidth / aspect;

		if (insideBounds(virtualViewportWidth, virtualViewportHeight))
			return new VirtualViewport(virtualViewportWidth, virtualViewportHeight);
		
		return new VirtualViewport(minWidth, minHeight);
	}
	
	private boolean insideBounds(float width, float height) {
		if (width < minWidth || width > maxWidth)
			return false;
		if (height < minHeight || height > maxHeight)
			return false;
		return true;
	}
}