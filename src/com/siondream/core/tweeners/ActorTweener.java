package com.siondream.core.tweeners;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;

import aurelienribon.tweenengine.TweenAccessor;


public class ActorTweener implements TweenAccessor<Actor>{

	public static final int Position = 1;
	public static final int Scale = 2;
	public static final int Rotation = 3;
	public static final int Color = 4;
	
	@Override
	public int getValues(Actor actor, int tweenType, float[] returnValues) {
		switch(tweenType) {
		case Position:
			returnValues[0] = actor.getX();
			returnValues[1] = actor.getY();
			return 2;
		case Scale:
			returnValues[0] = actor.getScaleX();
			returnValues[1] = actor.getScaleY();
			return 2;
		case Rotation:
			returnValues[0] = actor.getRotation();
			return 1;
		case Color:
			Color color = actor.getColor();
			returnValues[0] = color.r;
			returnValues[1] = color.g;
			returnValues[2] = color.b;
			returnValues[3] = color.a;
			return 4;
		default:
			return 0;
		}
	}

	@Override
	public void setValues(Actor actor, int tweenType, float[] newValues) {
		switch(tweenType) {
		case Position:
			actor.setX(newValues[0]);
			actor.setY(newValues[1]);
			break;
		case Scale:
			actor.setScaleX(newValues[0]);
			actor.setScaleY(newValues[1]);
			break;
		case Rotation:
			actor.setRotation(newValues[0]);
			break;
		case Color:
			Color color = actor.getColor();
			color.r = newValues[0];
			color.g = newValues[1];
			color.b = newValues[2];
			color.a = newValues[3];
			break;
		default:
			break;
		}
	}

}
