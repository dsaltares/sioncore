package com.siondream.core.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ObjectMap;

public class CollisionHandler implements ContactListener {

	private ObjectMap<Short, ObjectMap<Short, ContactListener>> listeners;
	
	public CollisionHandler() {
		listeners = new ObjectMap<Short, ObjectMap<Short, ContactListener>>();
	}
	
	public void addListener(short categoryA, short categoryB, ContactListener listener) {
		addListenerInternal(categoryA, categoryB, listener);
		addListenerInternal(categoryB, categoryA, listener);
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		ContactListener listener = getListener(fixtureA.getFilterData().categoryBits,
											   fixtureB.getFilterData().categoryBits);
		
		if (listener != null) {
			listener.beginContact(contact);
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		ContactListener listener = getListener(fixtureA.getFilterData().categoryBits,
											   fixtureB.getFilterData().categoryBits);
		
		if (listener != null) {
			listener.endContact(contact);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		ContactListener listener = getListener(fixtureA.getFilterData().categoryBits,
											   fixtureB.getFilterData().categoryBits);
		
		if (listener != null) {
			listener.preSolve(contact, oldManifold);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		ContactListener listener = getListener(fixtureA.getFilterData().categoryBits,
											   fixtureB.getFilterData().categoryBits);
		
		if (listener != null) {
			listener.postSolve(contact, impulse);
		}
	}
	
	private void addListenerInternal(short categoryA, short categoryB, ContactListener listener) {
		ObjectMap<Short, ContactListener> listenerCollection = listeners.get(categoryA);
		
		if (listenerCollection == null) {
			listenerCollection = new ObjectMap<Short, ContactListener>();
			listeners.put(categoryA, listenerCollection);
		}
		
		listenerCollection.put(categoryB, listener);
	}
	
	private ContactListener getListener(short categoryA, short categoryB) {
		ObjectMap<Short, ContactListener> listenerCollection = listeners.get(categoryA);
		
		if (listenerCollection == null) {
			return null;
		}
		
		return listenerCollection.get(categoryB);
	}
}
