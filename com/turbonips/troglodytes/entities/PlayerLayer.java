package com.turbonips.troglodytes.entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public class PlayerLayer extends Layer {
	
	public PlayerLayer(TiledMap tiledMap, Image player) {
		super(tiledMap);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		playerAnimation.draw(slidingPos.x+container.getWidth()/2-playerSize.x/2, slidingPos.y+container.getHeight()/2-playerSize.y/2);
	}
	
}
