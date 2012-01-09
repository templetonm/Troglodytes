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
	
	public Layer(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
		playerLoc = new Vector2f(0, 0);
		playerSize = new Vector2f(32, 32);
		slidingPos = new Vector2f(0, 0);
		slidingMin = new Vector2f(-100,-100);
		slidingMax = new Vector2f(100, 100);
		off = new Vector2f(0,0);
	}
	
	private boolean isBlocked(int tileX, int tileY) {
		boolean blocked = false;
		
		if (tileX < 0  || tileX >= tiledMap.getWidth()) {
			blocked = true;
		} else if (tileY < 0  || tileY >= tiledMap.getHeight()) {
			blocked = true;
		} else {
			int tileId = tiledMap.getTileId(tileX, tileY, WALL_LAYER);
			if (tileId > 0) {
				blocked = true;
			}
		}
		
		if (blocked) logger.info(tileX + "," + tileY + ": " + blocked);
		return blocked;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		Input input = container.getInput();
		
		
		
		if(input.isKeyDown(Input.KEY_DOWN)) { 
			int leftBottomX, leftBottomY, rightBottomX, rightBottomY;
			leftBottomX = (int)Math.floor((playerLoc.x)/32);
			leftBottomY = (int)Math.floor((playerLoc.y+playerSize.y-0.1f+PLAYER_SPEED)/32f);
			rightBottomX = (int)Math.floor((playerLoc.x+playerSize.x-0.1f)/32);
			rightBottomY = (int)Math.floor((playerLoc.y+playerSize.y-0.1f+PLAYER_SPEED)/32f);

			if (!(isBlocked(leftBottomX,leftBottomY) || isBlocked(rightBottomX,rightBottomY))){
				if (slidingPos.y >= slidingMax.y) {
					off.y -= PLAYER_SPEED;
				} else {
					slidingPos.y += PLAYER_SPEED;
				}
				
				playerLoc.y += PLAYER_SPEED;
			}
		} else if (input.isKeyDown(Input.KEY_UP)) {
			int leftTopX, leftTopY, rightTopX, rightTopY;
			leftTopX = (int)Math.floor((playerLoc.x)/32);
			leftTopY = (int)Math.floor((playerLoc.y-PLAYER_SPEED)/32);
			rightTopX = (int)Math.floor((playerLoc.x+playerSize.x-0.1f)/32);
			rightTopY = (int)Math.floor((playerLoc.y-PLAYER_SPEED)/32);
			if (!(isBlocked(leftTopX,leftTopY) || isBlocked(rightTopX,rightTopY))){
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
			topRightX = (int)Math.floor((playerLoc.x+playerSize.x-0.1f+PLAYER_SPEED)/32);
			topRightY = (int)Math.floor((playerLoc.y)/32);
			bottomRightX = (int)Math.floor((playerLoc.x+playerSize.x-0.1f+PLAYER_SPEED)/32);
			bottomRightY = (int)Math.floor((playerLoc.y+playerSize.y-0.1f)/32);
			if (!(isBlocked(topRightX,topRightY) || isBlocked(bottomRightX,bottomRightY))){
				if (slidingPos.x >= slidingMax.x) {
					off.x -= PLAYER_SPEED;
				} else {
					slidingPos.x += PLAYER_SPEED;
				}
	
				playerLoc.x += PLAYER_SPEED;
			}
		} else if (input.isKeyDown(Input.KEY_LEFT)) {
			int topLeftX, topLeftY, bottomLeftX, bottomLeftY;
			topLeftX = (int)Math.floor((playerLoc.x-PLAYER_SPEED)/32);
			topLeftY = (int)Math.floor((playerLoc.y)/32);
			bottomLeftX = (int)Math.floor((playerLoc.x-PLAYER_SPEED)/32);
			bottomLeftY = (int)Math.floor((playerLoc.y+playerSize.y-0.1f)/32);
			//logger.info((playerLoc.x-PLAYER_SPEED)/32 + "," + topLeftY);
			if (!(isBlocked(topLeftX,topLeftY) || isBlocked(bottomLeftX,bottomLeftY))){
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