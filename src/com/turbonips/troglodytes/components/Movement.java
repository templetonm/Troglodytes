package com.turbonips.troglodytes.components;

import com.artemis.Component;


public class Movement extends Component {
	private boolean moveUp;
	private boolean moveDown;
	private boolean moveLeft;
	private boolean moveRight;
	
	public Movement() {
		clearMovement();
	}
	public void clearMovement() {
		moveUp = false;
		moveDown = false;
		moveLeft = false;
		moveRight = false;
	}
	public boolean isMoveUp() {
		return moveUp;
	}
	public void setMoveUp(boolean moveUp) {
		this.moveUp = moveUp;
	}
	public boolean isMoveDown() {
		return moveDown;
	}
	public void setMoveDown(boolean moveDown) {
		this.moveDown = moveDown;
	}
	public boolean isMoveLeft() {
		return moveLeft;
	}
	public void setMoveLeft(boolean moveLeft) {
		this.moveLeft = moveLeft;
	}
	public boolean isMoveRight() {
		return moveRight;
	}
	public void setMoveRight(boolean moveRight) {
		this.moveRight = moveRight;
	}
}
