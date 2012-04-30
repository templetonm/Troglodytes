package com.turbonips.troglodytes.components;

import org.newdawn.slick.geom.Vector2f;

import com.artemis.Component;

public class Position extends Component {
	private Vector2f position;
	private String map;
	
	public Position(Vector2f position, String map) {
		setPosition(position);
		setMap(map);
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

}
