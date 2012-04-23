package com.turbonips.troglodytes.components;

import org.newdawn.slick.geom.Vector2f;

import com.artemis.Component;

public class Warp extends Component {
	private Vector2f position;
	private String map;
	
	public Warp() {
	}
	
	public Warp(String map, Vector2f position) {
		setMap(map);
		setPosition(position);
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public void setPosition(Vector2f position) {
		this.position = position;
	}
	
	public String getMapName() {
		return map;
	}
	
	public void setMap(String map) {
		this.map = map;
	}

}
