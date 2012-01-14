package com.turbonips.troglodytes.entities;

import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

public abstract class Entity {
	protected final Logger logger = Logger.getLogger(getClass());
	public abstract void render(GameContainer container, StateBasedGame game, Graphics g);
	public abstract void update(GameContainer container, StateBasedGame game, int delta);
}
