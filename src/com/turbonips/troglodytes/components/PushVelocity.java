package com.turbonips.troglodytes.components;

import org.newdawn.slick.geom.Vector2f;

import com.artemis.Component;

public class PushVelocity extends Component {
	private Vector2f velocity;
	
	public PushVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}

	public Vector2f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}
}
