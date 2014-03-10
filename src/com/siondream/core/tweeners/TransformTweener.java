package com.siondream.core.tweeners;


import com.siondream.core.entity.components.TransformComponent;

import aurelienribon.tweenengine.TweenAccessor;

public class TransformTweener implements TweenAccessor<TransformComponent> {

	public static final int Position = 1;
	public static final int Rotation = 2;
	public static final int Scale = 3;
	
	@Override
	public int getValues(TransformComponent transform, int tweenType, float[] returnValues) {
		switch (tweenType) {
		case Position:
			returnValues[0] = transform.position.x;
			returnValues[1] = transform.position.y;
			returnValues[2] = transform.position.z;
			return 3;
		case Scale:
			returnValues[0] = transform.scale;
			return 1;
		case Rotation:
			returnValues[0] = transform.angle;
			return 1;	
		default:
			return 0;
		}
	}

	@Override
	public void setValues(TransformComponent transform, int tweenType, float[] newValues) {
		switch (tweenType) {
		case Position:
			transform.position.x = newValues[0];
			transform.position.y = newValues[1];
			transform.position.z = newValues[2];
			break;
		case Scale:
			transform.scale = newValues[0];
			break;
		case Rotation:
			transform.angle = newValues[0];
			break;
		default:
			break;
		}
	}

}
