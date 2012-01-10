package com.turbonips.troglodytes.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public abstract class Layer extends Entity {
	
	protected final TiledMap tiledMap;
	protected Vector2f off;
	private final int PLAYER_SPEED = 8;
	private final int WALL_LAYER = 3;
	
	protected Vector2f slidingMin;
	protected Vector2f slidingMax;
	protected Vector2f slidingPos;
	protected Vector2f playerLoc;
	protected final Vector2f playerSize;
	protected final int TILE_SIZE;
	
	public Layer(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
		playerLoc = new Vector2f(0, 0);
		playerSize = new Vector2f(32, 32);
		slidingPos = new Vector2f(0, 0);
		slidingMin = new Vector2f(-100,-100);
		slidingMax = new Vector2f(100, 100);
		off = new Vector2f(0,0);
		TILE_SIZE=32;
	}
	
	private boolean isBlocked(int tileX, int tileY) {
		boolean blocked = false;
		
		if (tileX < 0  || tileX >= tiledMap.getWidth()) {
			blocked = true;
		} else if (tileY < 0  || tileY >= tiledMap.getHeight()) {
			blocked = true;
		} else {
			int tileId = tiledMap.getTileId(tileX, tileY, WALL_LAYER);
			if (tiledMap.getTileProperty(tileId, "Type", "None").equals("Wall")) {
				blocked = true;
			}
			if (blocked) logger.info(tileX + "," + tileY + ": " + tileId);
		}
		
		
		return blocked;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		Input input = container.getInput();
		if(input.isKeyDown(Input.KEY_DOWN)) { 
			int leftBottomX, leftBottomY, rightBottomX, rightBottomY;
			leftBottomX = (int)Math.floor((playerLoc.x)/TILE_SIZE);
			leftBottomY = (int)Math.floor((playerLoc.y+playerSize.y-0.1f+PLAYER_SPEED)/TILE_SIZE);
			rightBottomX = (int)Math.floor((playerLoc.x+playerSize.x-0.1f)/TILE_SIZE);
			rightBottomY = (int)Math.floor((playerLoc.y+playerSize.y-0.1f+PLAYER_SPEED)/TILE_SIZE);
			if (!(isBlocked(leftBottomX,leftBottomY) || isBlocked(rightBottomX,rightBottomY))) {
				if (slidingPos.y >= slidingMax.y) {
					off.y -= PLAYER_SPEED;
				} else {
					slidingPos.y += PLAYER_SPEED;
				}
				
				playerLoc.y += PLAYER_SPEED;
			}
		} else if (input.isKeyDown(Input.KEY_UP)) {
			int leftTopX, leftTopY, rightTopX, rightTopY;
			leftTopX = (int)Math.floor((playerLoc.x)/TILE_SIZE);
			leftTopY = (int)Math.floor((playerLoc.y-PLAYER_SPEED)/TILE_SIZE);
			rightTopX = (int)Math.floor((playerLoc.x+playerSize.x-0.1f)/TILE_SIZE);
			rightTopY = (int)Math.floor((playerLoc.y-PLAYER_SPEED)/TILE_SIZE);
			if (!(isBlocked(leftTopX,leftTopY) || isBlocked(rightTopX,rightTopY))) {
				if (slidingPos.y <= slidingMin.y) {
					off.y += PLAYER_SPEED;
				} else {
					slidingPos.y -= PLAYER_SPEED;
				}
				
				playerLoc.y -= PLAYER_SPEED;
			}
		}
		
		if(input.isKeyDown(Input.KEY_RIGHT)) {
			int topRightX, topRightY, bottomRightX, bottomRightY;
			topRightX = (int)Math.floor((playerLoc.x+playerSize.x-0.1f+PLAYER_SPEED)/TILE_SIZE);
			topRightY = (int)Math.floor((playerLoc.y)/TILE_SIZE);
			bottomRightX = (int)Math.floor((playerLoc.x+playerSize.x-0.1f+PLAYER_SPEED)/TILE_SIZE);
			bottomRightY = (int)Math.floor((playerLoc.y+playerSize.y-0.1f)/TILE_SIZE);
			if (!(isBlocked(topRightX,topRightY) || isBlocked(bottomRightX,bottomRightY))) {
				if (slidingPos.x >= slidingMax.x) {
					off.x -= PLAYER_SPEED;
				} else {
					slidingPos.x += PLAYER_SPEED;
				}
	
				playerLoc.x += PLAYER_SPEED;
			}
		} else if (input.isKeyDown(Input.KEY_LEFT)) {
			int topLeftX, topLeftY, bottomLeftX, bottomLeftY;
			topLeftX = (int)Math.floor((playerLoc.x-PLAYER_SPEED)/TILE_SIZE);
			topLeftY = (int)Math.floor((playerLoc.y)/TILE_SIZE);
			bottomLeftX = (int)Math.floor((playerLoc.x-PLAYER_SPEED)/TILE_SIZE);
			bottomLeftY = (int)Math.floor((playerLoc.y+playerSize.y-0.1f)/TILE_SIZE);
			if (!(isBlocked(topLeftX,topLeftY) || isBlocked(bottomLeftX,bottomLeftY))) {
				if (slidingPos.x <= slidingMin.x) {
					off.x += PLAYER_SPEED;
				} else {
					slidingPos.x -= PLAYER_SPEED;
				}
				
				playerLoc.x -= PLAYER_SPEED;
			}
		}
	}
}