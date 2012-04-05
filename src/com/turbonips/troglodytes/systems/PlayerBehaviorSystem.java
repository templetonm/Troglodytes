package com.turbonips.troglodytes.systems;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.ResourceRef;

public class PlayerBehaviorSystem extends BaseEntitySystem {
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
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> grounds = world.getGroupManager().getEntities("GROUND");
		ResourceManager manager = ResourceManager.getInstance();
		Entity ground = grounds.get(0);
		Entity player = players.get(0);
		Movement movement = movementMapper.get(player);
		Position pos = positionMapper.get(player);
		Vector2f position = pos.getPosition();
		Vector2f velocity = movement.getVelocity();
		
		// Collision detection
		// 1/2 Width & height of wall
		// 1/2 Width & height of player
		
		if (ground != null) {
			String groundResName = resourceMapper.get(ground).getResourceName();
			Resource groundRes = manager.getResource(groundResName);
			TiledMap map = (TiledMap)groundRes.getObject();
			
			// Figure out where the new player position will be
			Vector2f tmpPosition = new Vector2f(position);
			tmpPosition.add(velocity);
			
			Vector2f upperLeft = new Vector2f(tmpPosition.x, tmpPosition.y);
			Vector2f upperRight = new Vector2f(tmpPosition.x+32, tmpPosition.y);
			Vector2f lowerLeft = new Vector2f(tmpPosition.x, tmpPosition.y+64);
			Vector2f lowerRight = new Vector2f(tmpPosition.x+32, tmpPosition.y+64);
					
			// Upper left hand corner
			if (map.getTileId((int)upperLeft.x/32, (int)upperLeft.y/32, 3) > 0) {
				// Wall position in pickles intersected corner (lower right)
				Vector2f wallPosition = new Vector2f((int)upperLeft.x/32 * 32.0f + 32, (int)upperLeft.y/32 * 32.0f + 32);
				Vector2f vectorChange = new Vector2f(Math.abs(wallPosition.x-tmpPosition.x), Math.abs(wallPosition.y-tmpPosition.y));
				logger.info(vectorChange);
				if (velocity.x > 0) {
					tmpPosition.x -= vectorChange.x;
				} else if (velocity.x < 0) {
					tmpPosition.x += vectorChange.x;
				}
				if (velocity.y > 0) {
					tmpPosition.y -= vectorChange.y;
				} else if (velocity.y < 0) {
					tmpPosition.y += vectorChange.y;
				}
			}
			// Upper right hand corner
			else if (map.getTileId((int)upperRight.x/32, (int)upperRight.y/32, 3) > 0) {
				// Wall position in pickles intersected corner (lower left)
				Vector2f wallPosition = new Vector2f((int)upperRight.x/32 * 32.0f, (int)upperRight.y/32 * 32.0f + 32);
				Vector2f vectorChange = new Vector2f(Math.abs(wallPosition.x-tmpPosition.x-32-1), Math.abs(wallPosition.y-tmpPosition.y));
				logger.info(vectorChange);
				if (velocity.x > 0) {
					tmpPosition.x -= vectorChange.x;
				} else if (velocity.x < 0) {
					tmpPosition.x += vectorChange.x;
				}
				if (velocity.y > 0) {
					tmpPosition.y -= vectorChange.y;
				} else if (velocity.y < 0) {
					tmpPosition.y += vectorChange.y;
				}
			}
			// Lower left hand corner
			else if (map.getTileId((int)lowerLeft.x/32, (int)lowerLeft.y/32, 3) > 0) {
				// Wall position in pickles intersected corner (upper right)
				Vector2f wallPosition = new Vector2f((int)lowerLeft.x/32 * 32.0f + 32, (int)lowerLeft.y/32 * 32.0f);
				Vector2f vectorChange = new Vector2f(Math.abs(wallPosition.x-tmpPosition.x), Math.abs(wallPosition.y-tmpPosition.y-64-1));
				logger.info(vectorChange);
				if (velocity.x > 0) {
					tmpPosition.x -= vectorChange.x;
				} else if (velocity.x < 0) {
					tmpPosition.x += vectorChange.x;
				}
				if (velocity.y > 0) {
					tmpPosition.y -= vectorChange.y;
				} else if (velocity.y < 0) {
					tmpPosition.y += vectorChange.y;
				}
			}
			// Lower right hand corner
			else if (map.getTileId((int)lowerRight.x/32, (int)lowerRight.y/32, 3) > 0) {
				// Wall position in pickles intersected corner (upper left)
				Vector2f wallPosition = new Vector2f((int)lowerRight.x/32 * 32.0f, (int)lowerRight.y/32 * 32.0f);
				Vector2f vectorChange = new Vector2f(Math.abs(wallPosition.x-tmpPosition.x-32-1), Math.abs(wallPosition.y-tmpPosition.y-64-1));
				logger.info(vectorChange);
				if (velocity.x > 0) {
					tmpPosition.x -= vectorChange.x;
				} else if (velocity.x < 0) {
					tmpPosition.x += vectorChange.x;
				}
				if (velocity.y > 0) {
					tmpPosition.y -= vectorChange.y;
				} else if (velocity.y < 0) {
					tmpPosition.y += vectorChange.y;
				}
			}
			//if (tmpPosition.x < 0) velocity.x = 0;
			//if (tmpPosition.y < 0) velocity.y = 0;
			//map.getTileId(x, y, layerIndex)
			position.x = tmpPosition.x;
			position.y = tmpPosition.y;
		}
		

	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}
}
