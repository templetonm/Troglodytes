package com.turbonips.troglodytes.systems;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.components.Position;

public class DebugTextSystem extends BaseEntitySystem {
	private GameContainer container;
	private Graphics graphics;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<CreatureAnimation> animationCreatureMapper;
	
	public DebugTextSystem(GameContainer container) {
		super(Position.class, CreatureAnimation.class);
		this.container = container;
		this.graphics = container.getGraphics();
	}
	
	@Override
	protected void initialize() {
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		animationCreatureMapper = new ComponentMapper<CreatureAnimation>(CreatureAnimation.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> creatures = world.getGroupManager().getEntities("PLAYER");
		Position position = positionMapper.get(creatures.get(0));
		CreatureAnimation animationCreature = animationCreatureMapper.get(creatures.get(0));
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
