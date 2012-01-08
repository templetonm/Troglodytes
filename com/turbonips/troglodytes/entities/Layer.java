package com.turbonips.troglodytes.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public abstract class Layer extends Entity {
	
	protected final TiledMap tiledMap;
	protected Vector2f off;
	private final int PLAYER_SPEED = 10;
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
		boolean state = false;
		int tileId = tiledMap.getTileId(tileX, tileY, WALL_LAYER);
		if (tileId > 0) {
			state = true;
		}
		logger.info(tileX + "," + tileY + ": " + state);
		return state;
	}
	
	/*private boolean blocked (int x, int y) {
		boolean blocked = false;
		
		if (x < 0) {
			return true;
		}
		if (x > tiledMap.getWidth() * playerSize.x - playerSize.x) {
			return true;
		}
		
		if (y < 0) {
			return true;
		}
		if (y > tiledMap.getWidth() * playerSize.y - playerSize.y) {
			return true;
		}
		
		if (isBlocked(getX(x - (int)playerSize.x / 2), getY(y + (int)playerSize.y / 2))) {
			blocked = true;
		}
		if (isBlocked(getX(x + (int)playerSize.x / 2), getY(y + (int)playerSize.y / 2))) {
			blocked = true;
		}
		if (isBlocked(getX(x - (int)playerSize.x / 2), getY(y - (int)playerSize.y / 2))) {
			blocked = true;
		}
		if (isBlocked(getX(x + (int)playerSize.x / 2), getY(y - (int)playerSize.y / 2))) {
			blocked = true;
		}
		return blocked;
	}*/
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		Input input = container.getInput();
		
//		Vector2f curPlayerPos = new Vector2f(playerLoc.x/playerSize.x, playerLoc.y/playerSize.y);		
		
		Vector2f tempOff = off;
		Vector2f tempSlidingPos = slidingPos;
		Vector2f tempPlayerLoc = playerLoc;
						
		if(input.isKeyDown(Input.KEY_DOWN)) { 
			int left1, left2, right1, right2;
			left1 = (int)((playerLoc.x-playerSize.x/2)/32);
			left2 = (int)((playerLoc.y+playerSize.y/2+PLAYER_SPEED)/32);
			right1 = (int)((playerLoc.x+playerSize.x/2)/32);
			right2 = (int)((playerLoc.y+playerSize.y/2+PLAYER_SPEED)/32);
			if (!(isBlocked(left1,left2) || isBlocked(right1,right2))){
				if (tempSlidingPos.y >= slidingMax.y) {
					tempOff.y -= PLAYER_SPEED;
				} else {
					tempSlidingPos.y += PLAYER_SPEED;
				}
				
				tempPlayerLoc.y += PLAYER_SPEED;
			}
		} else if (input.isKeyDown(Input.KEY_UP)) {
			int left1, left2, right1, right2;
			left1 = (int)((playerLoc.x-playerSize.x/2)/32);
			left2 = (int)((playerLoc.y-playerSize.y/2-PLAYER_SPEED)/32);
			right1 = (int)((playerLoc.x+playerSize.x/2)/32);
			right2 = (int)((playerLoc.y-playerSize.y/2-PLAYER_SPEED)/32);
			if (!(isBlocked(left1,left2) || isBlocked(right1,right2))){
				if (tempSlidingPos.y <= slidingMin.y) {
					tempOff.y += PLAYER_SPEED;
				} else {
					tempSlidingPos.y -= PLAYER_SPEED;
				}
				
				tempPlayerLoc.y -= PLAYER_SPEED;
			}
		}
		
		if(input.isKeyDown(Input.KEY_RIGHT)) {
			int up1, up2, down1, down2;
			up1 = (int)((playerLoc.x+playerSize.x/2+PLAYER_SPEED)/32);
			up2 = (int)((playerLoc.y-playerSize.y/2)/32);
			down1 = (int)((playerLoc.x+playerSize.x/2+PLAYER_SPEED)/32);
			down2 = (int)((playerLoc.y+playerSize.y/2)/32);
			if (!(isBlocked(up1,up2) || isBlocked(down1,down2))){
				if (tempSlidingPos.x >= slidingMax.x) {
					tempOff.x -= PLAYER_SPEED;
				} else {
					tempSlidingPos.x += PLAYER_SPEED;
				}
	
				tempPlayerLoc.x += PLAYER_SPEED;
			}
		} else if (input.isKeyDown(Input.KEY_LEFT)) {
			int up1, up2, down1, down2;
			up1 = (int)((playerLoc.x-playerSize.x/2-PLAYER_SPEED)/32);
			up2 = (int)((playerLoc.y-playerSize.y/2)/32);
			down1 = (int)((playerLoc.x-playerSize.x/2-PLAYER_SPEED)/32);
			down2 = (int)((playerLoc.y+playerSize.y/2)/32);
			if (!(isBlocked(up1,up2) || isBlocked(down1,down2))){
				if (tempSlidingPos.x <= slidingMin.x) {
					tempOff.x += PLAYER_SPEED;
				} else {
					tempSlidingPos.x -= PLAYER_SPEED;
				}
				
				tempPlayerLoc.x -= PLAYER_SPEED;
			}
		}
		
		/*if (!blocked((int)(tempPlayerLoc.x), (int)(tempPlayerLoc.y))) {
			off = tempOff;
			slidingPos = tempSlidingPos;
			playerLoc = tempPlayerLoc;
		} else if (!blocked((int)(playerLoc.x), (int)(tempPlayerLoc.y))) {			
			playerLoc.y = tempPlayerLoc.y;
			slidingPos.y = tempSlidingPos.y;
			off.y = tempOff.y;
		} else if (!blocked((int)(tempPlayerLoc.x), (int)(playerLoc.y))) {
			playerLoc.x = tempPlayerLoc.x;
			slidingPos.x = tempSlidingPos.x;
			off.x = tempOff.x;
		}*/
	}
}