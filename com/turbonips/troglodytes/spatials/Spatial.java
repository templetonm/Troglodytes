package com.turbonips.troglodytes.spatials;

import org.apache.log4j.Logger;
import org.newdawn.slick.Graphics;

import com.artemis.Entity;
import com.artemis.World;

public abstract class Spatial {
	protected World world;
	protected Entity owner;
	protected final Logger logger = Logger.getLogger(getClass());

	public Spatial(World world, Entity owner) {
		this.world = world;
		this.owner = owner;
	}

	public abstract void initalize();

	public abstract void render(Graphics g);

}
