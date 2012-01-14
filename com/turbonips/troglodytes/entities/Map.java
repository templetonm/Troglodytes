package com.turbonips.troglodytes.entities;

import java.awt.Point;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public class Map extends Entity {
	private TiledMap tiledMap;
	private Point offset;
	private final Player player;
	
	public Map(String mapPath, Player player) throws SlickException {
		this.player = player;
		loadMap(mapPath);
		setOffset(new Point(player.getPosition().x*-1, player.getPosition().y*-1));
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		
		Point mapOffset = new Point(offset.x + container.getWidth()/2, offset.y + container.getHeight()/2);
		g.setDrawMode(Graphics.MODE_NORMAL);
		tiledMap.render(mapOffset.x, mapOffset.y, 0);
		tiledMap.render(mapOffset.x, mapOffset.y, 1);
		player.render(container, game, g);
		tiledMap.render(mapOffset.x, mapOffset.y, 2);
		
		g.setDrawMode(Graphics.MODE_ALPHA_MAP);
		g.setColor(new Color(0,0,0,225));
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		player.drawLight(container, g, 75, 150);
		
		g.setDrawMode(Graphics.MODE_ALPHA_BLEND);
		g.setColor(new Color(0,0,0,255));
		g.fillRect(0, 0, container.getWidth(), container.getHeight());


		g.setDrawMode(Graphics.MODE_NORMAL);
		//tiledMap.render(mapOffset.x, mapOffset.y, 3);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		Input input = container.getInput();
		if (input.isKeyDown(Input.KEY_UP)) {
			if (player.getSlidingPosition().y <= player.getSlidingBox().y) {
				offset.y += player.getSpeed();
			}
		} else if (input.isKeyDown(Input.KEY_DOWN)) {
			if (player.getSlidingPosition().y >= player.getSlidingBox().height) {
				offset.y -= player.getSpeed();
			}
		}
		
		if (input.isKeyDown(Input.KEY_LEFT)) {
			if (player.getSlidingPosition().x <= player.getSlidingBox().x) {
				offset.x += player.getSpeed();
			}
		} else if (input.isKeyDown(Input.KEY_RIGHT)) {
			if (player.getSlidingPosition().x >= player.getSlidingBox().width) {
				offset.x -= player.getSpeed();
			}
		}
		player.update(container, game, delta);
	}

	public Point getOffset() {
		return offset;
	}

	public void setOffset(Point offset) {
		this.offset = offset;
	}
	
	public void loadMap(String mapPath) throws SlickException {
		this.tiledMap = new TiledMap(mapPath);
	}

}
