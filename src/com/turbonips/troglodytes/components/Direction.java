package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class Direction extends Component {
	public enum Dir { 
		UP, 
		DOWN,
		LEFT, 
		RIGHT,
		UP_RIGHT,
		UP_LEFT,
		DOWN_RIGHT,
		DOWN_LEFT
	}
	private Dir direction;
	
	public Direction (Dir direction) {
		this.direction = direction;
	}
	
	public Dir getDirection() {
		return direction;
	}
	
	public void setDirection(Dir direction) {
		this.direction = direction;
	}
}
