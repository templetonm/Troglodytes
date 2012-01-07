package com.turbonips.troglodytes.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public abstract class Layer extends Entity {
	
	protected final TiledMap tiledMap;
	protected int offx = 0, offy = 0;
	private final int SCROLL_SPEED = 10;
	private final int SLIDE_SPEED = 10;
	
	protected Vector2f slidingMin;
	protected Vector2f slidingMax;
	protected Vector2f slidingPos;
	
	public Layer(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
		slidingPos = new Vector2f(0, 0);
		slidingMin = new Vector2f(-100,-100);
		slidingMax = new Vector2f(100, 100);
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		Input input = container.getInput();
		
		if(input.isKeyDown(Input.KEY_DOWN)) {
			if (slidingPos.y >= slidingMax.y) {
				offy -= SCROLL_SPEED;
			} else {
				slidingPos.y += SLIDE_SPEED;
			}
		} else if (input.isKeyDown(Input.KEY_UP)) {
			if (slidingPos.y <= slidingMin.y) {
				offy += SCROLL_SPEED;
			} else {
				slidingPos.y -= SLIDE_SPEED;
			}
			
		} if(input.isKeyDown(Input.KEY_RIGHT)) {
			if (slidingPos.x >= slidingMax.x) {
				offx -= SCROLL_SPEED;
			} else {
				slidingPos.x += SLIDE_SPEED;
			}
		} else if (input.isKeyDown(Input.KEY_LEFT)) {
			if (slidingPos.x <= slidingMin.x) {
				offx += SCROLL_SPEED;
			} else {
				slidingPos.x -= SLIDE_SPEED;
			}
		}
	}

}
