package com.turbonips.troglodytes.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public abstract class Creature extends Entity {
	protected final Image img;
	
	public Creature(String creaturePath) throws SlickException {
		img = new Image(creaturePath);
	}

}
