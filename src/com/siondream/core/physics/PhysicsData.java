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

package com.siondream.core.physics;

import ashley.utils.ObjectMap;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.siondream.core.Env;

public class PhysicsData {
	
	BodyDef bodyDef = new BodyDef();
	MassData massData = new MassData();
	Array<FixtureDef> fixtureDefs = new Array<FixtureDef>();
	Array<String> fixtureNames = new Array<String>();
	ObjectMap<String, Integer> fixtureIdx = new ObjectMap<String, Integer>();
	
	PhysicsData() {
	}
	
	public BodyDef getBodyDef() {
		return bodyDef;
	}
	
	public MassData getMassData() {
		return massData;
	}
	
	public Array<FixtureDef> getFixtureDefs() {
		return fixtureDefs;
	}
	
	public int getFixtureIdx(String name) {
		return fixtureIdx.get(name);
	}
	
	public String getFixtureName(int index) {
		return fixtureNames.get(index);
	}
	
	public Body createBody(Object userData) {
		World world = Env.game.getWorld();
		Body body = world.createBody(bodyDef);
		body.setMassData(massData);
		body.setUserData(userData);
		
		for (int i = 0; i < fixtureDefs.size; ++i) {
			Fixture fixture = body.createFixture(fixtureDefs.get(i));
			fixture.setUserData(i);
		}
		
		return body;
	}
}
