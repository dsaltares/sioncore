/*  Copyright 2012 SionEngine
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.siondream.core.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.siondream.core.Env;

/**
 * @class AnimationData
 * @author David Saltares Márquez
 * @date 09/09/2012
 * 
 * @brief Holds animation frame sequences and reads them from JSON files and a spritesheet-like texture
 * 
 * It will look for a name.json and name.png files for loading.
 *
 */
public class SpriteAnimationData {
	private Logger logger; 
	
	// Package private members for the AnimationLoader ease of access
	Texture texture = null;
	int rows = 0;
	int columns = 0;
	float frameDuration = 0.0f;
	ObjectMap<String, Animation> animations = new ObjectMap<String, Animation>();
	Animation defaultAnimation = null;
	
	/**
	 * @param loggingLevel verbosity of the animation data logging
	 */
	public SpriteAnimationData() {
		logger = new Logger("Animation", Env.debugLevel);
	}
	
	/**
	 * @param animationName name of the desired animation
	 * @return animation object containing the sequence of frames, null if not found
	 */
	public Animation getAnimation(String animationName) {
		Animation animation = animations.get(animationName);
		
		if (animation == null) {
			logger.info("Animation: " + animationName + " not found returning default");
			
			return defaultAnimation;
		}
		
		return animation;
	}
	
	public Texture getTexture() {
		return texture;
	}
}
