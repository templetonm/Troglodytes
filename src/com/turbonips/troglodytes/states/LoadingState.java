package com.turbonips.troglodytes.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.turbonips.troglodytes.ResourceManager;

public class LoadingState extends BaseGameState implements Runnable {
	public static final int ID = 4;
	boolean finished = false;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.init(container, game);
		new Thread(this).start();

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		if (!finished) {
			g.setColor(Color.white);
			String text = "Loading...";
			g.drawString(text, container.getWidth()/2-container.getDefaultFont().getWidth(text)/2, container.getHeight()/2-container.getDefaultFont().getHeight(text)/2);
		} else {
			game.enterState(PlayingState.ID);
		}
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void run() {
		ResourceManager resourceManager = ResourceManager.getInstance();
		//resourceManager.loadMusicResources();
		finished = true;
	}

}
