package com.turbonips.troglodytes.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Player extends Creature {

	public Player(String creaturePath) throws SlickException {
		super(creaturePath);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		img.draw(container.getWidth()/2-img.getWidth()/2, container.getHeight()/2-img.getHeight()/2);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		
	}

}
