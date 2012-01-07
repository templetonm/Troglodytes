package com.turbonips.troglodytes.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public class PlayerLayer extends Layer {
	private final Image player;

	public PlayerLayer(TiledMap tiledMap, Image player) {
		super(tiledMap);
		this.player = player;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		player.draw(slidingPos.x+container.getWidth()/2-player.getWidth()/2, slidingPos.y+container.getHeight()/2-player.getHeight()/2);
	}

}
