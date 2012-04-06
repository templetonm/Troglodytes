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
			Vector2f bestPosition = new Vector2f(position);
			bestPosition.add(velocity);
			Vector2f[] character = new Vector2f[6];
			character[0] = new Vector2f(bestPosition.x, bestPosition.y);
			character[1] = new Vector2f(bestPosition.x+32, bestPosition.y);
			character[2] = new Vector2f(bestPosition.x, bestPosition.y+32);
			character[3] = new Vector2f(bestPosition.x+32, bestPosition.y+32);
//			character[4] = new Vector2f(tmpPosition.x, tmpPosition.y+64);
//			character[5] = new Vector2f(tmpPosition.x+32, tmpPosition.y+64);

			// Don't forget +1 for the vector change going right or down
			// Keep track of the shortest distance if there are multiple collision spots
			for (int i=0; i<=3; i++) {
				Vector2f tmpPosition = new Vector2f(position);
				tmpPosition.add(velocity);

				if (map.getTileId((int)character[i].x/32, (int)character[i].y/32, 3) > 0) {
					// Our position is inside of a tile
					// Now lets figure out which direction we are coming from
					Vector2f vectorChange = new Vector2f(0,0);
					Vector2f wallPosition = new Vector2f((int)character[i].x/32 * 32.0f, (int)character[i].y/32 * 32.0f);
					if (velocity.x > 0) {
						if (velocity.y > 0) {
							// Character going right and down
							// x+,y+ wall-bottom-left
							// First we get vector change
							logger.info("Right-Down");
							vectorChange.set(Math.abs(wallPosition.x-character[i].x),Math.abs(wallPosition.y-character[i].y));
							// Next we see if we can move entirely in the x or the y otherwise use the vector change
							if (map.getTileId((int)(character[i].x-vectorChange.x-1)/32, (int)character[i].y/32, 3) == 0) {
								// Running into right wall
								tmpPosition.x -= vectorChange.x;
							} else if (map.getTileId((int)(character[i].x)/32, (int)(character[i].y-vectorChange.y-1)/32, 3) == 0) {
								// Running into lower wall
								tmpPosition.y -= vectorChange.y;
							} else {
								// Running into lower right corner
								tmpPosition.x -= vectorChange.x+1;
								tmpPosition.y -= vectorChange.y+1;
							}
						} else if (velocity.y < 0) {
							// Character going right and up
							// x+,y- wall-bottom-left
							logger.info("Right-Up");
							vectorChange.set(Math.abs(wallPosition.x-character[i].x),Math.abs(wallPosition.y-character[i].y+32));
							if (map.getTileId((int)(character[i].x-vectorChange.x-1)/32, (int)character[i].y/32, 3) == 0) {
								// Running into right wall
								tmpPosition.x -= vectorChange.x;
							} else if (map.getTileId((int)(character[i].x)/32, (int)(character[i].y+vectorChange.y)/32, 3) == 0) {
								// Running into upper wall
								tmpPosition.y += vectorChange.y;
							} else {
								// Running into upper right corner
								tmpPosition.x -= vectorChange.x;
								tmpPosition.y += vectorChange.y;
							}
						} else {
							// Character going right
							// x+,0
							vectorChange.set(Math.abs(wallPosition.x-character[i].x),0);
							tmpPosition.x -= vectorChange.x;
						}
					} else if (velocity.x < 0) {
						if (velocity.y > 0) {
							// Character going left and down
							// x-,y+ wall-top-right
							logger.info("Left-Down");
							vectorChange.set(Math.abs(wallPosition.x+32-character[i].x),Math.abs(wallPosition.y-character[i].y));
							if (map.getTileId((int)(character[i].x+vectorChange.x)/32, (int)character[i].y/32, 3) == 0) {
								// Running into left wall
								tmpPosition.x += vectorChange.x;
							} else if (map.getTileId((int)(character[i].x)/32, (int)(character[i].y-vectorChange.y-1)/32, 3) == 0) {
								// Running into lower wall
								tmpPosition.y -= vectorChange.y;
							} else {
								// Running into lower left corner
								tmpPosition.x += vectorChange.x;
								tmpPosition.y -= vectorChange.y+1;
							}
						} else if (velocity.y < 0) {
							// Character going left and up
							// x-,y- wall-bottom-right
							logger.info("Left-Up");
							vectorChange.set(Math.abs(wallPosition.x+32-character[i].x),Math.abs(wallPosition.y+32-character[i].y));
							if (map.getTileId((int)(character[i].x+vectorChange.x)/32, (int)character[i].y/32, 3) == 0) {
								// Running into left wall
								tmpPosition.x += vectorChange.x;
							} else if (map.getTileId((int)(character[i].x)/32, (int)(character[i].y+vectorChange.y)/32, 3) == 0) {
								// Running into upper wall
								tmpPosition.y += vectorChange.y;
							} else {
								// Running into upper left corner
								tmpPosition.x += vectorChange.x;
								tmpPosition.y += vectorChange.y;
								
							}
						} else {
							// Character going left
							// x-,0
							vectorChange.set(Math.abs(wallPosition.x-character[i].x+32),0);
							tmpPosition.x += vectorChange.x;
						}
					} else {
						if (velocity.y > 0) {
							// Character going down
							// 0,y+
							vectorChange.set(0,Math.abs(wallPosition.y-character[i].y));
							tmpPosition.y -= vectorChange.y+1;
						} else if (velocity.y < 0) {
							// Character going up
							// 0+,y-
							vectorChange.set(0,Math.abs(wallPosition.y-character[i].y+32));
							tmpPosition.y += vectorChange.y;
						} else {
							// 0,0
							// I don't think it's possible to collide with zero velocity and I doubt we'll get here anyway
						}
					}
					//logger.info(i + " " + tmpPosition);
					if (Math.abs(tmpPosition.x-position.x) < Math.abs(bestPosition.x-position.x)) {
						bestPosition.x = tmpPosition.x;
					}
					if (Math.abs(tmpPosition.y-position.y) < Math.abs(bestPosition.y-position.y)) {
						bestPosition.y = tmpPosition.y;
					}
				}
			}
			
			
			position.set(bestPosition);
		}
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}
}
