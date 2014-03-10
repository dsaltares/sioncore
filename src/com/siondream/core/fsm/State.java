package com.siondream.core.fsm;

public abstract class State {
	
	private StateMachine stateMachine;
	
	public State(StateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}
	
	public StateMachine getStateMachine() {
		return stateMachine;
	}
	
	public abstract void enter();
	public abstract void update(float deltaTime);
	public abstract void exit();
}
