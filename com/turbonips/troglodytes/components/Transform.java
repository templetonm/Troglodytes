package com.turbonips.troglodytes.components;


import org.newdawn.slick.geom.Vector2f;

import com.artemis.Component;

public class Transform extends Component {
	private Vector2f position = new Vector2f();
	private int speed;
	
	public Transform(Vector2f position, int speed) {
		setPosition(position);
		setSpeed(speed);
	}
	
	public Transform(float x, float y, int speed) {
		setPosition(x, y);
		setSpeed(speed);
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}
	
	public void setPosition(float x, float y) {
		this.position.x = x;
		this.position.y = y;
	}
	
	public void setX(float x) {
		this.position.x = x;
	}
	
	public void setY(float y) {
		this.position.y = y;
	}
	
	public float getX() {
		return this.position.x;
	}
	
	public float getY() {
		return this.position.y;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

}
