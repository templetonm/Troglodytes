package com.turbonips.troglodytes.states;

import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public abstract class BaseGameState extends BasicGameState {
	protected final Logger logger = Logger.getLogger(getClass());
	
	// TODO: Get rid of VSync, instead render based on delta time
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		logger.debug("Init");
		//container.setAlwaysRender(true);
		container.setVSync(true);
		container.setShowFPS(true);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.enter(container, game);
		logger.debug("Entered State");
	}
}
