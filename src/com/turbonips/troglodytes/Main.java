package com.turbonips.troglodytes;

import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.turbonips.troglodytes.states.*;

public class Main extends StateBasedGame {
	private static final Logger logger = Logger.getLogger(Main.class);
	
	public Main() {
		super("Troglodytes");
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		logger.debug("Setting up game states");
		addState(new MenuState());
		addState(new OptionState());
		addState(new CreditState());
		addState(new PlayingState());
		
		// It auto enters the first added state so this is just temporary
		enterState(MenuState.ID);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.debug("Initializing game engine");
		
		try {
			AppGameContainer container = new AppGameContainer(new Main());
			container.setDisplayMode(800, 600, false);
			//container.setDisplayMode(1024, 768, true);
			container.start();
		} catch (SlickException ex) {
			logger.fatal(ex);
		}
	}

}
