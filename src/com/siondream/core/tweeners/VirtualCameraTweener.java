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

import com.siondream.core.virtualviewport.VirtualCamera;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * @class CameraTweener
 * @author David Saltares Márquez
 * @date 26/02/2014
 * 
 * @brief Tweener for a OrthographicCameraWithVirtualViewport
 *
 */
public class VirtualCameraTweener implements TweenAccessor<VirtualCamera>{

	/** Tween position */
	public static final int Position = 1;
	
	/** Tween zoom */
	public static final int Zoom = 2;
	
	@Override
	public int getValues(VirtualCamera camera, int tweenType, float[] returnValues) {
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

	@Override
	public void setValues(VirtualCamera camera, int tweenType, float[] newValues) {
		switch (tweenType) {
		case Position:
			camera.setPosition(newValues[0], newValues[1]);
			break;
		case Zoom:
			camera.zoom = newValues[0];
			break;
		default:
			break;
		}
	}
}
