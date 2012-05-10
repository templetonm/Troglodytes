package com.turbonips.troglodytes.components;

import java.util.HashMap;

import org.newdawn.slick.geom.Vector2f;
import com.artemis.Component;
import com.turbonips.troglodytes.components.Stats.StatType;

public class Movement extends Component {
	private Vector2f velocity;
	private Vector2f nonZeroVelocity;
	private float maximumSpeed;
	private Vector2f acceleration;
	private Vector2f deceleration;
	
	
	public Movement(HashMap<StatType, Integer> stats) {
		setVelocity(new Vector2f(0,0));
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
	

	public Vector2f getNonZeroVelocity() {
		return nonZeroVelocity;
	}

	public void setNonZeroVelocity(Vector2f nonZeroVelocity) {
		this.nonZeroVelocity = nonZeroVelocity;
	}
}
