package com.turbonips.troglodytes.systems;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.turbonips.troglodytes.components.Sliding;

public class LightingSystem extends EntityProcessingSystem {
	private Image light;
	private Graphics graphics;
	private GameContainer container;
	ComponentMapper<Sliding> slidingMapper;

	public LightingSystem(GameContainer container) {
		super(Sliding.class);
		this.container = container;
		graphics = container.getGraphics();
		try {
			light = new Image("resources/graphics/light.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void initialize() {
		slidingMapper = new ComponentMapper<Sliding>(Sliding.class, world);
	}
	
	@Override
	protected void process(Entity e) {
		Sliding sliding = slidingMapper.get(e);
		int lightSize = 5;
		float invSize = 1f / lightSize;
		graphics.scale(lightSize, lightSize);
		graphics.clearAlphaMap();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		light.drawCentered((container.getWidth()/2 - sliding.getX() + 16) * invSize, (container.getHeight()/2 - sliding.getY() + 16) * invSize);
		graphics.scale(invSize, invSize);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_DST_ALPHA);
		graphics.setColor(new Color(0,0,0,100));
		graphics.fillRect(0, 0, container.getWidth(), container.getHeight());
		graphics.setDrawMode(Graphics.MODE_NORMAL);
	}

}
