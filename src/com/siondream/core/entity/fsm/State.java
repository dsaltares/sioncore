package com.siondream.core.entity.fsm;

public abstract class State {
	
	protected StateMachine fsm;
	
	public State(StateMachine stateMachine) {
		this.fsm = stateMachine;
	}
	
	public StateMachine getFSM() {
		return fsm;
	}
	
	public abstract void enter();
	public abstract void update(float deltaTime);
	public abstract void exit();
}
