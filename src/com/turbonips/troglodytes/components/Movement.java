package com.turbonips.troglodytes.components;

import org.newdawn.slick.geom.Vector2f;
import com.artemis.Component;

public class Movement extends Component {
	private Vector2f velocity;
	private float maximumSpeed;
	private Vector2f acceleration;
	private Vector2f deceleration;
	
	public Movement(float maximumSpeed, Vector2f acceleration, Vector2f deceleration) {
		setMaximumSpeed(maximumSpeed);
		setAcceleration(acceleration);
		setDeceleration(deceleration);
		setVelocity(new Vector2f(0,0));
	}

	public Movement(Vector2f velocity, float maximumSpeed, Vector2f acceleration, Vector2f deceleration) {
		setVelocity(velocity);
		setMaximumSpeed(maximumSpeed);
		setAcceleration(acceleration);
		setDeceleration(deceleration);
	}

	public Vector2f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}

	public float getCurrentSpeed() {
		return velocity.distance(new Vector2f(0,0));
	}
	
	public float getMaximumSpeed() {
		return maximumSpeed;
	}

	public void setMaximumSpeed(float maximumSpeed) {
		this.maximumSpeed = maximumSpeed;
	}

	public Vector2f getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2f acceleration) {
		this.acceleration = acceleration;
	}

	public Vector2f getDeceleration() {
		return deceleration;
	}

	public void setDeceleration(Vector2f deceleration) {
		this.deceleration = deceleration;
	}
}
