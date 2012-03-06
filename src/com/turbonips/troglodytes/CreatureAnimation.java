package com.turbonips.troglodytes;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import com.artemis.Component;

public class CreatureAnimation {
	private SpriteSheet sheet;
	private Animation moveUp;
	private Animation moveRight;
	private Animation moveDown;
	private Animation moveLeft;
	private Animation idleUp;
	private Animation idleRight;
	private Animation idleDown;
	private Animation idleLeft;
	// private Animation attackUp;
	// private Animation attackRight;
	// private Animation attackDown;
	// private Animation attackLeft;

	private Animation current;

	public CreatureAnimation(SpriteSheet sheet) {
		this.sheet = sheet;
		// All info should be grab-able from the spritesheet;
		// Scheme like so:
		// Row 0 = moveDown
		// Row 1 = moveLeft
		// Row 2 = moveUp
		// Row 3 = moveRight
		// Row 4 = attackUp
		// Row 5 = attackRight
		// Row 6 = attackDown
		// Row 7 = attackLeft
		// Idles should be the first frame of a move animation (or maybe should
		// be first frame and animation is frame 2+)

		// demoChar is a 4 row sheet, movement and idle only.
		int animationSpeed = 50;
		Image[] moveUpFrames = { this.sheet.getSubImage(1, 2),
				this.sheet.getSubImage(2, 2), this.sheet.getSubImage(3, 2),
				this.sheet.getSubImage(4, 2), this.sheet.getSubImage(5, 2),
				this.sheet.getSubImage(6, 2) };
		Image[] moveRightFrames = { this.sheet.getSubImage(1, 3),
				this.sheet.getSubImage(2, 3), this.sheet.getSubImage(3, 3),
				this.sheet.getSubImage(4, 3), this.sheet.getSubImage(5, 3),
				this.sheet.getSubImage(6, 3) };
		Image[] moveDownFrames = { this.sheet.getSubImage(1, 0),
				this.sheet.getSubImage(2, 0), this.sheet.getSubImage(3, 0),
				this.sheet.getSubImage(4, 0), this.sheet.getSubImage(5, 0),
				this.sheet.getSubImage(6, 0) };
		Image[] moveLeftFrames = { this.sheet.getSubImage(1, 1),
				this.sheet.getSubImage(2, 1), this.sheet.getSubImage(3, 1),
				this.sheet.getSubImage(4, 1), this.sheet.getSubImage(5, 1),
				this.sheet.getSubImage(6, 1) };
		this.moveUp = new Animation(moveUpFrames, animationSpeed);
		this.moveRight = new Animation(moveRightFrames, animationSpeed);
		this.moveDown = new Animation(moveDownFrames, animationSpeed);
		this.moveLeft = new Animation(moveLeftFrames, animationSpeed);
		Image[] idleUpFrames = { this.sheet.getSubImage(0, 2) };
		Image[] idleRightFrames = { this.sheet.getSubImage(0, 3) };
		Image[] idleDownFrames = { this.sheet.getSubImage(0, 0) };
		Image[] idleLeftFrames = { this.sheet.getSubImage(0, 1) };
		this.setIdleUp(new Animation(idleUpFrames, animationSpeed));
		this.idleRight = new Animation(idleRightFrames, animationSpeed);
		this.idleDown = new Animation(idleDownFrames, animationSpeed);
		this.idleLeft = new Animation(idleLeftFrames, animationSpeed);
		this.current = this.idleDown;
	}

	public CreatureAnimation(String path, int width, int height) throws SlickException {
		this(new SpriteSheet(path, width, height));
	}

	public Animation getMoveUp() {
		return this.moveUp;
	}

	public Animation getMoveDown() {
		return this.moveDown;
	}

	public Animation getMoveLeft() {
		return this.moveLeft;
	}

	public Animation getMoveRight() {
		return this.moveRight;
	}

	public Animation getCurrent() {
		return this.current;
	}

	public void setCurrent(Animation current) {
		//this.current = current;
	}

	public void setIdle() {
		/*if (this.current == this.moveUp) {
			this.current = this.idleUp;
		} else if (this.current == this.moveDown) {
			this.current = this.idleDown;
		} else if (this.current == this.moveRight) {
			this.current = this.idleRight;
		} else if (this.current == this.moveLeft) {
			this.current = this.idleLeft;
		}*/
	}

	public Animation getIdleUp() {
		return idleUp;
	}

	public void setIdleUp(Animation idleUp) {
		this.idleUp = idleUp;
	}

	public Animation getIdleRight() {
		return idleRight;
	}
	
	public Animation getIdleLeft() {
		return idleLeft;
	}

	public Animation getIdleDown() {
		return idleDown;
	}

}
