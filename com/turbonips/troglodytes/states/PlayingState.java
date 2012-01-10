package com.turbonips.troglodytes.states;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import com.turbonips.troglodytes.entities.*;

public class PlayingState extends BaseGameState {
	public static final int ID = 3;
	private final ArrayList<Entity> entities = new ArrayList<Entity>();

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.init(container, game);
		
		
		TiledMap tiledMap = new TiledMap("resources/demo.tmx");
		entities.add(new GroundLayer(tiledMap));
		entities.add(new BgLayer(tiledMap));
		entities.add(new PlayerLayer(tiledMap, new Image("resources/player.png")));
		entities.add(new EnemyLayer(tiledMap));
		entities.add(new FgLayer(tiledMap));
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
		
		for (Entity entity : entities) {
			entity.update(container, game, delta);
		}
		
	}

	@Override
	public int getID() {
		return ID;
	}

}
