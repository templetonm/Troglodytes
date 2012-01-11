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
		if (enemyData != null) {
			int x = (int)(off.x + container.getWidth()/2 - (playerSize.x/2))+enemyData.getX();
			int y = (int)(off.y + container.getHeight()/2 - (playerSize.y/2))+enemyData.getY();

			if (enemyData.getX() < playerLoc.x)
			{
				enemyData.setX(enemyData.getX()+enemyData.getSpeed());
				mobAnimation = mobRightAnim;
			}
			if (enemyData.getY() < playerLoc.y) enemyData.setY(enemyData.getY()+enemyData.getSpeed());
			if (enemyData.getX() > playerLoc.x)
			{
				enemyData.setX(enemyData.getX()-enemyData.getSpeed());
				mobAnimation = mobLeftAnim;
			}
			if (enemyData.getY() > playerLoc.y) enemyData.setY(enemyData.getY()-enemyData.getSpeed());

			
			mobAnimation.draw(x, y);
		}
	}
}
