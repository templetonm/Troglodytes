package com.turbonips.troglodytes.states;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.gui.*;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.Color;

public class MenuState extends BaseGameState {
	public static final int ID = 1;
	
	private Image backgroundImage;
	private AngelCodeFont font;
	
	private MouseOverArea playGameButton;
	private Image playGameButtonImage;
	private Image playGameButtonClicked;
	
	private MouseOverArea quitGameButton;
	private Image quitGameButtonImage;
	private Image quitGameButtonClicked;

	@Override
	public void init(GameContainer container, final StateBasedGame game)
			throws SlickException {
		super.init(container, game);
		
		backgroundImage = new Image ("resources/graphics/menuBackground.png");
		font = new AngelCodeFont ("resources/fonts/c64.fnt", "resources/fonts/c64_0.png");
		
		playGameButtonImage = new Image ("resources/graphics/newGameButton.png");
		playGameButtonClicked = new Image ("resources/graphics/newGameButton-Clicked.png");
		
		quitGameButtonImage = new Image ("resources/graphics/quitButton.png");
		quitGameButtonClicked = new Image ("resources/graphics/quitButton-Clicked.png");
		
		playGameButton = new MouseOverArea (container, playGameButtonImage, 325, 400, 150, 50);
		playGameButton.setMouseOverImage(playGameButtonClicked);
		playGameButton.addListener(new ComponentListener() {
			
			public void componentActivated (AbstractComponent arg0) {
				game.enterState(LoadingState.ID);
			}
		});
		
		quitGameButton = new MouseOverArea (container, quitGameButtonImage, 325, 475, 150, 50);
		quitGameButton.setMouseOverImage(quitGameButtonClicked);
		quitGameButton.addListener(new ComponentListener() {
			
			public void componentActivated (AbstractComponent arg0) {
				System.exit(1);
			}
		});
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		// TODO Auto-generated method stub
		
		backgroundImage.draw(0,0,container.getWidth(), container.getHeight());
//		g.setFont(font);
//		font.drawString(container.getWidth()/2, container.getHeight()/2, "Troglodytes", Color.orange);
		
		playGameButton.setX(container.getWidth()/2-playGameButton.getWidth()/2);
		playGameButton.setY(container.getHeight()/2-playGameButton.getHeight()/2);
		quitGameButton.setX(container.getWidth()/2-quitGameButton.getWidth()/2);
		quitGameButton.setY(container.getHeight()/2-quitGameButton.getHeight()/2+playGameButton.getHeight()+20);
		
		playGameButton.render(container, g);
		quitGameButton.render(container, g);
		
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

}
