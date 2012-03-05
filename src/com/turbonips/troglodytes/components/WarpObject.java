package com.turbonips.troglodytes.components;

import java.awt.Point;

import com.artemis.Component;

public class WarpObject extends ObjectType {
	
	private final String mapName;
	private final int x;
	private final int y;
	private final Point position;
	

	public WarpObject(String mapName, int x, int y) {
		this.mapName = mapName;
		this.x = x;
		this.y = y;
		this.position = new Point(x,y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}


	public String getMapName() {
		return mapName;
	}


	public Point getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		return "WarpObject: " + mapName + "," + x + "," + y;
		
	}

	@Override
	public int getType() {
		return WARP_OBJECT;
	}

}
