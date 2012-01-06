package com.turbonips.troglodytes.entities;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public class Map extends Entity {
	
	private final TiledMap tiledMap;
	private final ArrayList<Creature> creatures;
	private static final int GROUND = 0;
	private static final int BACKGROUND = 1;
	private static final int FOREGROUND = 2;
	int offx = 0, offy = 0;
	private final int SCROLL_SPEED = 10;
	
	
	public Map(String mapPath, ArrayList<Creature> creatures) throws SlickException {
		tiledMap = new TiledMap(mapPath);
		this.creatures = creatures;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		tiledMap.render(offx, offy, GROUND);
		tiledMap.render(offx, offy, BACKGROUND);
		for (Creature creature : creatures) {
			creature.render(container, game, g);
		}
		tiledMap.render(offx, offy, FOREGROUND);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		Input input = container.getInput();
		
		if(input.isKeyDown(Input.KEY_DOWN)) {
			offy -= SCROLL_SPEED;
		} else if (input.isKeyDown(Input.KEY_UP)) {
			offy += SCROLL_SPEED;
			
		} if(input.isKeyDown(Input.KEY_RIGHT)) {
			offx -= SCROLL_SPEED;
		} else if (input.isKeyDown(Input.KEY_LEFT)) {
			offx += SCROLL_SPEED;
		}
		
		for (Creature creature : creatures) {
			creature.update(container, game, delta);
		}
	}

}
