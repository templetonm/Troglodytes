package com.turbonips.troglodytes.entities;

import java.awt.Point;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

public class PositionText extends Entity {
	private final Point position;
	private final int nObject;
	private final String title;
	private static int nLines = 0;
	boolean showTilePosition = true;
	
	public PositionText(Point position, String title, boolean showTilePosition) {
		this.position = position;
		this.title = title;
		nObject = nLines;
		if (!showTilePosition)
			nLines += 3;
		else
			nLines += 4;
		this.showTilePosition = showTilePosition;
	}
	
	public PositionText(Point position, String title) {
		this.position = position;
		this.title = title;
		nObject = nLines;
		nLines += 4;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		String st;
		int w;
		int h = g.getFont().getLineHeight();
		
		st = title;
		w = g.getFont().getWidth(st);
		g.getFont().drawString(container.getWidth()-w-10, 10+(nObject)*h, st, Color.white);
		
		st = "Real pos: " + position.x + "," + position.y;
		w = g.getFont().getWidth(st);
		g.getFont().drawString(container.getWidth()-w-10, 10+(nObject+1)*h, st, Color.yellow);
		
		if (showTilePosition) {
			st = "Tile pos: " + position.x/32 + "," + position.y/32;
			w = g.getFont().getWidth(st);
			g.getFont().drawString(container.getWidth()-w-10, 10+(nObject+2)*h, st, Color.yellow);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
	}

}
