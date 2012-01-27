package com.turbonips.troglodytes.objects;

public class WarpObject extends ObjectType {
	
	private final String mapName;
	private final int x;
	private final int y;
	

	public WarpObject(String mapName, int x, int y) {
		this.mapName = mapName;
		this.x = x;
		this.y = y;
	}
	
	
	@Override
	public int getType() {
		return WARP_OBJECT;
	}


	@Override
	public void process() {
		
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

}
