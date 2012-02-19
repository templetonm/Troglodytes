package com.turbonips.troglodytes.systems;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.Resource;

public class DebugTextSystem extends BaseEntitySystem {
	private GameContainer container;
	private Graphics graphics;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<Resource> resourceMapper;
	
	public DebugTextSystem(GameContainer container) {
		this.container = container;
		this.graphics = container.getGraphics();
	}
	
	@Override
	protected void initialize() {
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		resourceMapper = new ComponentMapper<Resource>(Resource.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		
		if (!players.isEmpty()) {
			Position position = positionMapper.get(players.get(0));
			Resource playerResource = resourceMapper.get(players.get(0));
			
			Image sprite = null;
			if (playerResource != null) {
				switch (playerResource.getType()) {
					case CREATURE_ANIMATION:
						sprite = ((CreatureAnimation)playerResource.getObject()).getIdleDown().getCurrentFrame();
						break;
					case IMAGE:
						sprite = (Image)playerResource.getObject();
						break;
					default:
						logger.error("player resource type is " + playerResource.getType());
						break;
				}
				int tileX = (int)position.getX()/sprite.getWidth();
				int tileY = (int)position.getY()/sprite.getHeight();
				String posText = "(" + tileX + ", " + tileY + ")";
				graphics.setColor(Color.yellow);
				graphics.drawString(posText, container.getWidth()-container.getDefaultFont().getWidth(posText)-10, 15);
			}
		}
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

}
