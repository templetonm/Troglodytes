package com.turbonips.troglodytes.states;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.turbonips.troglodytes.entities.Entity;
import com.turbonips.troglodytes.entities.Map;
import com.turbonips.troglodytes.entities.Player;
import com.turbonips.troglodytes.entities.PositionText;

public class PlayingState extends BaseGameState {
	public static final int ID = 3;
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private Map currentMap;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.init(container, game);
		
		String spritePath = "resources/player.png";
		logger.info("Loading " + spritePath);
		Point playerPosition = new Point(13*32, 39*32);
		int playerSpeed = 8;
		Player player = new Player(spritePath, playerPosition, playerSpeed);
		
		String mapPath = "resources/trog1.tmx";
		logger.info("Loading " + mapPath);
	    currentMap = new Map(mapPath, player);
	    
	    entities.add(currentMap);
	    entities.add(new PositionText(playerPosition, "Player Position"));
	    entities.add(new PositionText(player.getSlidingPosition(), "Sliding Position", false));
	    entities.add(new PositionText(currentMap.getOffset(), "Map Offset", false));
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		
		for (Entity entity : entities) {
			entity.render(container, game, g);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		
		if (container.getInput().isKeyDown(Input.KEY_ESCAPE)) {
			container.exit();
		}
		
		for (Entity entity : entities) {
			entity.update(container, game, delta);
		}
	}

	@Override
	public int getID() {
		return ID;
	}

}
