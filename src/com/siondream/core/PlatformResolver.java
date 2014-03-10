package com.siondream.core;

import com.badlogic.gdx.files.FileHandle;

public interface PlatformResolver {
	public void openURL(String url);
	public void rateApp();
	public void sendFeedback();
	public FileHandle[] listFolder(String path);
}
