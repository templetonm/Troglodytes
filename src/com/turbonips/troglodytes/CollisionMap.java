package com.turbonips.troglodytes;

import org.apache.log4j.Logger;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

public class CollisionMap implements TileBasedMap
{
	protected final Logger logger = Logger.getLogger(getClass());
	
	private TiledMap tiledMap;
	private static final int collisionLayerID = 3;
	private static final float cost = 1;
	
	public CollisionMap (TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}
	
	@Override
	public boolean blocked(PathFindingContext pfc, int x, int y) {
		return tiledMap.getTileId(x, y, collisionLayerID) > 0;
	}

	@Override
	public float getCost(PathFindingContext pfc, int x, int y) {
		return cost;
	}

	@Override
	public int getHeightInTiles() {
		return tiledMap.getHeight();
	}

	@Override
	public int getWidthInTiles() {
		return tiledMap.getWidth();
	}

	@Override
	public void pathFinderVisited(int x, int y) {
		// Leave blank!
	}

}
