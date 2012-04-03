package com.turbonips.troglodytes.components;

import org.newdawn.slick.geom.Vector2f;

import com.artemis.Component;

public class Position extends Component {
	private Vector2f position;
	
	public Position(Vector2f position) {
		setPosition(position);
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

}
