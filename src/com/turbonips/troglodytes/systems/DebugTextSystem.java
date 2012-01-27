package com.turbonips.troglodytes.systems;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.AnimationCreature;
import com.turbonips.troglodytes.components.Transform;

public class DebugTextSystem extends EntitySystem {
	private GameContainer container;
	private Graphics graphics;
	private ComponentMapper<Transform> positionMapper;
	private ComponentMapper<AnimationCreature> animationCreatureMapper;
	
	public DebugTextSystem(GameContainer container) {
		super(Transform.class, AnimationCreature.class);
		this.container = container;
		this.graphics = container.getGraphics();
	}
	
	@Override
	protected void initialize() {
		positionMapper = new ComponentMapper<Transform>(Transform.class, world);
		animationCreatureMapper = new ComponentMapper<AnimationCreature>(AnimationCreature.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> creatures = world.getGroupManager().getEntities("CREATURE");
		Transform position = positionMapper.get(creatures.get(0));
		AnimationCreature animationCreature = animationCreatureMapper.get(creatures.get(0));
		int tileX = (int)position.getX()/animationCreature.getCurrent().getWidth();
		int tileY = (int)position.getY()/animationCreature.getCurrent().getHeight();
		String posText = "(" + tileX + ", " + tileY + ")";
		graphics.setColor(Color.yellow);
		graphics.drawString(posText, container.getWidth()-container.getDefaultFont().getWidth(posText)-10, 20);
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

}
