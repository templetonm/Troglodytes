package com.turbonips.troglodytes.states;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import com.turbonips.troglodytes.ResourceFactory;
import com.turbonips.troglodytes.ResourceManager;

public abstract class BaseMenuState extends BaseGameState {
	
	private static TiledMap map;
	private static int mapX = 0, mapY = 0;
	
	private int mapVelX = (int)(Math.random()*-2) - 1; 
	private int mapVelY = (int)(Math.random()*-2) - 1;
	static int i = 5;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.init(container, game);
		ResourceFactory factory = ResourceFactory.getInstance();
		ResourceManager manager = ResourceManager.getInstance();

		if (map == null) {
			ArrayList<String> resourceIds = factory.getResourceIds("tiledmap");
			String randomMap = resourceIds.get((int)(Math.random() * resourceIds.size()));
			map = (TiledMap)manager.getResource(randomMap).getObject();
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		map.render(mapX, mapY, 0);
		map.render(mapX, mapY, 1);
		map.render(mapX, mapY, 2);
		
		if (map.getTileWidth()*map.getWidth() < Math.abs(mapX)+container.getWidth() ||
			mapX > 0) {
			mapVelX *= -1;
		}
		if (map.getTileHeight()*map.getHeight() < Math.abs(mapY)+container.getHeight() ||
			mapY > 0) {
			mapVelY *= -1;
		}
		mapX += mapVelX;
		mapY += mapVelY;		
	}

}
