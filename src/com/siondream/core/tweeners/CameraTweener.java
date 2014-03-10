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

package com.siondream.core.tweeners;

import com.badlogic.gdx.graphics.OrthographicCamera;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * @class CameraTweener
 * @author David Saltares Márquez
 * @date 05/09/2012
 * 
 * @brief Tweener for a libgdx OrthographicCamera
 *
 */
public class CameraTweener implements TweenAccessor<OrthographicCamera>{

	/** Tween position */
	public static final int Position = 1;
	
	/** Tween zoom */
	public static final int Zoom = 2;
	
	/**
	 * @param camera camera to get values from
	 * @param tweentype type of tween (Position or Zoom)
	 * @param returnValues out parameter with the requested values
	 */
	@Override
	public int getValues(OrthographicCamera camera, int tweenType, float[] returnValues) {
		switch (tweenType) {
		case Position:
			returnValues[0] = camera.position.x;
			returnValues[1] = camera.position.y;
			returnValues[2] = camera.position.z;
			return 3;
		case Zoom:
			returnValues[0] = camera.zoom;
			return 1;
		default:
			return 0;
		}
	}

	/**
	 * @param camera camera whose some values are going to be set
	 * @param tweenType Position or Zoom
	 * @param newValues array containing the new values to configure the camera
	 */
	@Override
	public void setValues(OrthographicCamera camera, int tweenType, float[] newValues) {
		switch (tweenType) {
		case Position:
			camera.position.x = newValues[0];
			camera.position.y = newValues[1];
			camera.position.z = newValues[2];
			break;
		case Zoom:
			camera.zoom = newValues[0];
			break;
		default:
			break;
		}
	}
}
