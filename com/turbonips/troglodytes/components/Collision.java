package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class Collision extends Component {
	private boolean collidingUp = false,
					collidingDown = false,
					collidingRight = false,
					collidingLeft = false;
	
	public Collision() {
	}
	
	public void setColliding(boolean collidingUp, boolean collidingDown, boolean collidingLeft, boolean collidingRight) {
		setCollidingUp(collidingUp);
		setCollidingDown(collidingDown);
		setCollidingLeft(collidingLeft);
		setCollidingRight(collidingRight);
	}

	public boolean isCollidingUp() {
		return collidingUp;
	}

	public void setCollidingUp(boolean collidingUp) {
		this.collidingUp = collidingUp;
	}

	public boolean isCollidingDown() {
		return collidingDown;
	}

	public void setCollidingDown(boolean collidingDown) {
		this.collidingDown = collidingDown;
	}

	public boolean isCollidingRight() {
		return collidingRight;
	}

	public void setCollidingRight(boolean collidingRight) {
		this.collidingRight = collidingRight;
	}

	public boolean isCollidingLeft() {
		return collidingLeft;
	}

	public void setCollidingLeft(boolean collidingLeft) {
		this.collidingLeft = collidingLeft;
	}
			
			

}
