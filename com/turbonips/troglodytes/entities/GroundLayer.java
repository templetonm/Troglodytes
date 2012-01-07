package com.turbonips.troglodytes.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public class GroundLayer extends Layer {
	
	private static final int GROUND = 0;

	public GroundLayer(TiledMap tiledMap) {
		super(tiledMap);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		tiledMap.render(offx, offy, GROUND);
	}

}
