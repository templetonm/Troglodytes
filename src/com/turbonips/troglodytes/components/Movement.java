package com.turbonips.troglodytes.components;

import org.newdawn.slick.Animation;

import com.artemis.Component;
import com.turbonips.troglodytes.CreatureAnimation;


public class Movement extends Component {
	private boolean moveUp;
	private boolean moveDown;
	private boolean moveLeft;
	private boolean moveRight;
	private Animation animation;
	
	public Movement() {
		clearMovement();
	}
	public Movement(Animation animation) {
		clearMovement();
		setAnimation(animation);
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
	public Animation getAnimation() {
		return animation;
	}
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}
	public void setIdle(CreatureAnimation creatureAnimation) {
		if (this.animation == creatureAnimation.getMoveUp()) {
			setAnimation(creatureAnimation.getIdleUp());
		} else if (this.animation == creatureAnimation.getMoveDown()) {
			setAnimation(creatureAnimation.getIdleDown());
		} else if (this.animation == creatureAnimation.getMoveRight()) {
			setAnimation(creatureAnimation.getIdleRight());
		} else if (this.animation == creatureAnimation.getMoveLeft()) {
			setAnimation(creatureAnimation.getIdleLeft());
		}
	}
	public boolean isMoving() {
		return isMoveUp() || isMoveDown() || isMoveLeft() || isMoveRight();
	}
	public boolean isIdleUp(CreatureAnimation creatureAnimation) {
		return this.animation == creatureAnimation.getIdleUp();
	}
	public boolean isIdleDown(CreatureAnimation creatureAnimation) {
		return this.animation == creatureAnimation.getIdleDown();
	}
	public boolean isIdleLeft(CreatureAnimation creatureAnimation) {
		return this.animation == creatureAnimation.getIdleLeft();
	}
	public boolean isIdleRight(CreatureAnimation creatureAnimation) {
		return this.animation == creatureAnimation.getIdleRight();
	}
}
