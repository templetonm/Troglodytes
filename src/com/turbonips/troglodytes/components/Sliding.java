package com.turbonips.troglodytes.components;


import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Sliding extends Position {
	
	private Rectangle box;
	
	public Sliding(Vector2f position, int speed, Rectangle box) {
		super(position, speed);
		setBox(box);
	}

	public Sliding(float x, float y, int speed, Rectangle box) {
		super(x, y, speed);
		setBox(box);
	}

	public Rectangle getBox() {
		return box;
	}

	public void setBox(Rectangle box) {
		this.box = box;
	}

}
