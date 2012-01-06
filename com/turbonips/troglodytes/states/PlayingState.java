package com.turbonips.troglodytes.states;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import com.turbonips.troglodytes.entities.*;

public class PlayingState extends BaseGameState {
	public static final int ID = 3;
	private final ArrayList<Entity> entities = new ArrayList<Entity>();
	private final ArrayList<Creature> creatures = new ArrayList<Creature>();

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.init(container, game);
		
		entities.add(new Map("resources/demo.tmx", creatures));
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
