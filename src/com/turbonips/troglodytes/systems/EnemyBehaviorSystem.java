package com.turbonips.troglodytes.systems;

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

public class EnemyBehaviorSystem extends BaseEntitySystem
{
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<ResourceRef> resourceMapper;
	
	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
	}

	@Override
	protected boolean checkProcessing()
	{
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities)
	{
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> grounds = world.getGroupManager().getEntities("GROUND");
		ResourceManager manager = ResourceManager.getInstance();
		Entity ground = grounds.get(0);
		
		for (int i = 0; i < enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			Movement movement = movementMapper.get(enemy);
			Position position = positionMapper.get(enemy);
			Vector2f enemyPosition = position.getPosition();
			Vector2f enemyVelocity = movement.getVelocity();
			Vector2f newEnemyPosition = new Vector2f(enemyPosition);
			String enemyResName = resourceMapper.get(enemy).getResourceName();
			Resource enemyRes = manager.getResource(enemyResName);
			Image enemyFrame = getFrame(enemyRes);
			newEnemyPosition.add(enemyVelocity);
			
			// Collision detection
			if (ground != null) {
				String groundResName = resourceMapper.get(ground).getResourceName();
				Resource groundRes = manager.getResource(groundResName);
				TiledMap map = (TiledMap)groundRes.getObject();
				int th = map.getTileHeight();
				int tw = map.getTileWidth();
				int eh = enemyFrame.getHeight();
				int ew = enemyFrame.getWidth();
				Integer wx = ((int)(newEnemyPosition.x/tw) * tw),
						wy = ((int)(newEnemyPosition.y/th) * th);
				boolean collision = false;
		
				// Upper left
				if (map.getTileId((int)newEnemyPosition.x/tw, (int)newEnemyPosition.y/th, 3) > 0) {
					collision = true;
					// Wall lower right
					wx = ((int)(newEnemyPosition.x/tw) * tw) + tw;
					wy = ((int)(newEnemyPosition.y/th) * th) + th;
					logger.info("Upper left");
					
					if (map.getTileId((int)enemyPosition.x/tw, (int)newEnemyPosition.y/th, 3) > 0 ||
						map.getTileId((int)((enemyPosition.x)/tw + 0.5f), (int)newEnemyPosition.y/th, 3) > 0) {
						newEnemyPosition.y = wy;
					}
						
					else if (map.getTileId((int)newEnemyPosition.x/tw, (int)(enemyPosition.y)/th, 3) > 0 ||
							 map.getTileId((int)newEnemyPosition.x/tw, (int)((enemyPosition.y)/th + 0.5f), 3) > 0) {
						newEnemyPosition.x = wx;
					}
				}
				
				// Upper right
				if (map.getTileId((int)(newEnemyPosition.x+ew-1)/tw, (int)newEnemyPosition.y/th, 3) > 0) {
					collision = true;
					wx = ((int)(newEnemyPosition.x/tw) * tw);
					wy = ((int)(newEnemyPosition.y/th) * th) + th;
					logger.info("Upper right");
					
					if (map.getTileId((int)(enemyPosition.x+ew-1)/tw, (int)newEnemyPosition.y/th, 3) > 0 ||
						map.getTileId((int)((enemyPosition.x+ew-1)/tw - 0.5f), (int)newEnemyPosition.y/th, 3) > 0) {
						newEnemyPosition.y = wy;
					}
						
					else if (map.getTileId((int)(newEnemyPosition.x+ew-1)/tw, (int)enemyPosition.y/th, 3) > 0 ||
							 map.getTileId((int)(newEnemyPosition.x+ew-1)/tw, (int)(enemyPosition.y/th + 0.5f), 3) > 0) {
						newEnemyPosition.x = wx;
					}
				}
				
				// Lower left
				if (map.getTileId((int)newEnemyPosition.x/tw, (int)(newEnemyPosition.y+eh-1)/th, 3) > 0) {
					collision = true;
					wx = ((int)(newEnemyPosition.x/tw) * tw) + tw;
					wy = ((int)(newEnemyPosition.y/th) * th);
					logger.info("Lower left");
					
					
					if (map.getTileId((int)(enemyPosition.x)/tw, (int)(newEnemyPosition.y+eh-1)/th, 3) > 0 ||
						map.getTileId((int)((enemyPosition.x)/tw + 0.5f), (int)(newEnemyPosition.y+eh-1)/th, 3) > 0) {
						newEnemyPosition.y = wy;
					}
						
					else if (map.getTileId((int)(newEnemyPosition.x)/tw, (int)(enemyPosition.y+eh-1)/th, 3) > 0 ||
							 map.getTileId((int)(newEnemyPosition.x)/tw, (int)((enemyPosition.y+eh-1)/th - 0.5f), 3) > 0) {
						newEnemyPosition.x = wx;
					}
				}
				
				// Lower right
				if (map.getTileId((int)(newEnemyPosition.x+ew-1)/tw, (int)(newEnemyPosition.y+eh-1)/th, 3) > 0) {
					collision = true;
					wx = ((int)(newEnemyPosition.x/tw) * tw);
					wy = ((int)(newEnemyPosition.y/th) * th);
					logger.info("Lower right");
					
					if (map.getTileId((int)(enemyPosition.x+ew-1)/tw, (int)(newEnemyPosition.y+eh-1)/th, 3) > 0 ||
						map.getTileId((int)((enemyPosition.x+ew-1)/tw - 0.5f), (int)(newEnemyPosition.y+eh-1)/th, 3) > 0) {
						newEnemyPosition.y = wy;
					}
						
					else if (map.getTileId((int)(newEnemyPosition.x+ew-1)/tw, (int)(enemyPosition.y+eh-1)/th, 3) > 0 ||
							 map.getTileId((int)(newEnemyPosition.x+ew-1)/tw, (int)((enemyPosition.y+eh-1)/th - 0.5f), 3) > 0) {
						newEnemyPosition.x = wx;
					}
				}
			}
				
			enemyPosition.set(newEnemyPosition);
		}
	}

	private Image getFrame(Resource resource) {
		switch (resource.getType()) {
			case CREATURE_ANIMATION:
				return ((CreatureAnimation)resource.getObject()).getCurrent().getCurrentFrame();
			case IMAGE:
				return (Image)resource.getObject();
			default:
				return null;
		}
	}
}
