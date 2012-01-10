package com.turbonips.troglodytes.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public class EnemyLayer extends Layer {
	
	public EnemyLayer(TiledMap tiledMap) {
		super(tiledMap);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		int x = (int)(off.x + container.getWidth()/2 - (playerSize.x/2))+enemyData.getX();
		int y = (int)(off.y + container.getHeight()/2 - (playerSize.y/2))+enemyData.getY();
		enemyData.setX(enemyData.getX()+1);
		enemyData.getImage().draw(x,y);
	}
}
