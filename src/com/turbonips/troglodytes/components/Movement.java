package com.turbonips.troglodytes.components;


import org.newdawn.slick.geom.Vector2f;
import com.artemis.Component;

public class Movement extends Component {
	private Vector2f velocity;
	private Vector2f nonZeroVelocity;
	
	public Movement(Vector2f startVelocity) {
		setVelocity(new Vector2f(startVelocity));
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
