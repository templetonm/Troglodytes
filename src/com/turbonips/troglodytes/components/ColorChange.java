package com.turbonips.troglodytes.components;

import org.newdawn.slick.Color;

public class ColorChange extends TimeComponent {

	private Color color;
	
	public ColorChange(int time, Color color) {
		super(time);
		setColor(color);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	

}
