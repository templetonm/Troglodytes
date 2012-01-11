package com.turbonips.troglodytes.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public class FgLayer extends Layer {
	private static final int FOREGROUND = 2;

	public FgLayer(TiledMap tiledMap) {
		super(tiledMap);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		tiledMap.render((int)(off.x + container.getWidth()/2 - (playerSize.x/2)), (int)(off.y + container.getHeight()/2 - (playerSize.y/2)), FOREGROUND);
		//tiledMap.render((int)(off.x + container.getWidth()/2 - (playerSize.x/2)), (int)(off.y + container.getHeight()/2 - (playerSize.y/2)), 3);
		String coords = (int)(playerLoc.x)/32 + "," + (int)(playerLoc.y)/32;
		int w = container.getDefaultFont().getWidth(coords);
		g.drawString(coords, container.getWidth()-w-20, 10);
	}
}
