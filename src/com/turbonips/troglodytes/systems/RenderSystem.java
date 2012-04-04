package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.ResourceRef;

public class RenderSystem extends BaseEntitySystem {
	private ComponentMapper<ResourceRef> resourceMapper;
	ComponentMapper<Position> positionMapper;
	private GameContainer container;
	private ComponentMapper<Movement> movementMapper;
	
	public RenderSystem(GameContainer container) {
		this.container = container;
	}

	@Override
	protected void initialize() {
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
	}


	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		Graphics g = container.getGraphics();
		ResourceManager manager = ResourceManager.getInstance();
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> grounds = world.getGroupManager().getEntities("GROUND");
		ImmutableBag<Entity> backgrounds = world.getGroupManager().getEntities("BACKGROUND");
		ImmutableBag<Entity> foregrounds = world.getGroupManager().getEntities("FOREGROUND");
		Entity player = players.get(0);
		Position pos = positionMapper.get(player);
		Vector2f position = pos.getPosition();
		String playerResName = resourceMapper.get(player).getResourceName();
		Resource playerRes = manager.getResource(playerResName);
		Image playerFrame = getPlayerFrame(playerRes);
		
		// Draw the ground
		for (int i=0; i<grounds.size(); i++) {
			Entity ground = grounds.get(i);
			String mapResName = resourceMapper.get(ground).getResourceName();
			Resource mapRes = manager.getResource(mapResName);
			TiledMap map = (TiledMap)mapRes.getObject();
			map.render((int)position.x*-1 + container.getWidth()/2 - playerFrame.getWidth()/2, (int)position.y*-1 + container.getHeight()/2 - playerFrame.getHeight()/2, 0);
		}
		
		// Draw the background
		for (int i=0; i<backgrounds.size(); i++) {
			Entity background = backgrounds.get(i);
			String mapResName = resourceMapper.get(background).getResourceName();
			Resource mapRes = manager.getResource(mapResName);
			TiledMap map = (TiledMap)mapRes.getObject();
			map.render((int)position.x*-1 + container.getWidth()/2 - playerFrame.getWidth()/2, (int)position.y*-1 + container.getHeight()/2 - playerFrame.getHeight()/2, 1);
		}
		
		// Draw the player
		int playerCenterX = container.getWidth()/2 - playerFrame.getWidth()/2;
		int playerCenterY = container.getHeight()/2 - playerFrame.getHeight()/2;
		switch (playerRes.getType()) {
			case CREATURE_ANIMATION:
				Movement movement = movementMapper.get(player);
				Vector2f velocity = movement.getVelocity();
				CreatureAnimation playerAnim = (CreatureAnimation)playerRes.getObject();
				if (movement.getCurrentSpeed() == 0) {
					playerAnim.setIdle();
				} else {
					// Animate in the x direction
					if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
						if (velocity.x < 0) {
							playerAnim.setCurrent(playerAnim.getMoveLeft(), movement.getCurrentSpeed()/10);
						} else {
							playerAnim.setCurrent(playerAnim.getMoveRight(), movement.getCurrentSpeed()/10);
						}
					// Animate in the y direction
					} else {
						if (velocity.y < 0) {
							playerAnim.setCurrent(playerAnim.getMoveUp(), movement.getCurrentSpeed()/10);
						} else {
							playerAnim.setCurrent(playerAnim.getMoveDown(), movement.getCurrentSpeed()/10);
						}
					}
				}
				g.drawAnimation(playerAnim.getCurrent(), playerCenterX, playerCenterY);
				break;
			case IMAGE:
				g.drawImage(playerFrame, playerCenterX, playerCenterY);
				break;
		}
		
		// Draw the foreground
		for (int i=0; i<foregrounds.size(); i++) {
			Entity foreground = foregrounds.get(i);
			String mapResName = resourceMapper.get(foreground).getResourceName();
			Resource mapRes = manager.getResource(mapResName);
			TiledMap map = (TiledMap)mapRes.getObject();
			map.render((int)position.x*-1 + container.getWidth()/2 - playerFrame.getWidth()/2, (int)position.y*-1 + container.getHeight()/2 - playerFrame.getHeight()/2, 2);
		}			
	}
	
	@Override
	protected boolean checkProcessing() {
		return true;
	}
	
	private Image getPlayerFrame(Resource playerResource) {
		switch (playerResource.getType()) {
			case CREATURE_ANIMATION:
				return ((CreatureAnimation)playerResource.getObject()).getCurrent().getCurrentFrame();
			case IMAGE:
				return (Image)playerResource.getObject();
			default:
				return null;
		}
	}
		
}
