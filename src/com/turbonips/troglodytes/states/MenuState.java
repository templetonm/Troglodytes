package com.turbonips.troglodytes.states;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.gui.*;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.Color;

public class MenuState extends BaseGameState {
	public static final int ID = 1;

	private Image backgroundImage;
	private UnicodeFont unicodeFont;

	private String title = "Troglodytes";

	private MouseOverArea playGameButton;
	private Image playGameButtonImage;
	private Image playGameButtonClicked;

	private MouseOverArea optionsButton;
	private Image optionsButtonImage;
	private Image optionsButtonClicked;

	private MouseOverArea quitGameButton;
	private Image quitGameButtonImage;
	private Image quitGameButtonClicked;

	@Override
	public void init(GameContainer container, final StateBasedGame game)
			throws SlickException {
		super.init(container, game);

		backgroundImage = new Image ("resources/graphics/menuBackground.png");

		unicodeFont = new UnicodeFont ("resources/fonts/C64.ttf", 84, true, false);
		unicodeFont.getEffects().add(new ColorEffect(java.awt.Color.white));

		playGameButtonImage = new Image ("resources/graphics/newGameButton.png");
		playGameButtonClicked = new Image ("resources/graphics/newGameButton-Clicked.png");

		optionsButtonImage = new Image ("resources/graphics/optionsButton.png");
		optionsButtonClicked = new Image ("resources/graphics/optionsButton-Clicked.png");

		quitGameButtonImage = new Image ("resources/graphics/quitButton.png");
		quitGameButtonClicked = new Image ("resources/graphics/quitButton-Clicked.png");

		playGameButton = new MouseOverArea (container, playGameButtonImage, container.getWidth() - playGameButtonImage.getWidth(), 
				container.getHeight() - playGameButtonImage.getHeight(), playGameButtonImage.getWidth(), playGameButtonImage.getHeight());
		playGameButton.setMouseOverImage(playGameButtonClicked);
		playGameButton.addListener(new ComponentListener() {

			public void componentActivated (AbstractComponent arg0) {
				game.enterState(LoadingState.ID);
			}
		});

		optionsButton = new MouseOverArea (container, optionsButtonImage, container.getWidth() - optionsButtonImage.getWidth(), 
				container.getHeight() - optionsButtonImage.getHeight(), optionsButtonImage.getWidth(), optionsButtonImage.getHeight());
		optionsButton.setMouseOverImage(optionsButtonClicked);
		optionsButton.addListener(new ComponentListener() {

			public void componentActivated (AbstractComponent arg0) {
				game.enterState(OptionState.ID);
			}
		});

		quitGameButton = new MouseOverArea (container, quitGameButtonImage, container.getWidth() - quitGameButtonImage.getWidth(), 
				container.getHeight() - quitGameButtonImage.getHeight(), quitGameButtonImage.getWidth(), quitGameButtonImage.getHeight());
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

		unicodeFont.drawString(container.getWidth()/2-(unicodeFont.getWidth(title)/2), 
				container.getHeight()/4-unicodeFont.getYOffset(title), 
				title);

		playGameButton.setX(container.getWidth()/2-playGameButton.getWidth()/2);
		playGameButton.setY(container.getHeight()/2-playGameButton.getHeight()/2);

		optionsButton.setX(container.getWidth()/2-optionsButton.getWidth()/2);
		optionsButton.setY(container.getHeight()/2-optionsButton.getHeight()/2 + playGameButton.getHeight() + 20);

		quitGameButton.setX(container.getWidth()/2-quitGameButton.getWidth()/2);
		quitGameButton.setY(container.getHeight()/2-quitGameButton.getHeight()/2 + optionsButton.getHeight() + 60);

		playGameButton.render(container, g);
		optionsButton.render(container, g);
		quitGameButton.render(container, g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO Auto-generated method stub
		unicodeFont.loadGlyphs();
	}

	@Override
	public int getID() {
		return ID;
	}

}