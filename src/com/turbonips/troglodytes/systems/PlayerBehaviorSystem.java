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
			
			Vector2f[] character = new Vector2f[6];
			character[0] = new Vector2f(tmpPosition.x, tmpPosition.y);
			character[1] = new Vector2f(tmpPosition.x+32, tmpPosition.y);
			character[2] = new Vector2f(tmpPosition.x, tmpPosition.y+32);
			character[3] = new Vector2f(tmpPosition.x+32, tmpPosition.y+32);
			character[4] = new Vector2f(tmpPosition.x, tmpPosition.y+64);
			character[5] = new Vector2f(tmpPosition.x+32, tmpPosition.y+64);

			for (int x=0; x<=5; x++) {
				if (map.getTileId((int)character[x].x/32, (int)character[x].y/32, 3) > 0) {
					// Our position is inside of a tile
					// Now lets figure out which direction we are coming from
					Vector2f vectorChange = new Vector2f(0,0);
					Vector2f wallPosition = new Vector2f((int)character[x].x/32 * 32.0f, (int)character[x].y/32 * 32.0f);
					if (velocity.x > 0) {
						if (velocity.y > 0) {
							// Character going right and down
							// x+,y+ wall-bottom-left
							vectorChange.set(Math.abs(wallPosition.x-character[x].x),Math.abs(wallPosition.y-character[x].y));
							if (velocity.x > velocity.y) {
								// Modify y
								tmpPosition.y -= vectorChange.y;
							} else if (velocity.x < velocity.y){
								// Modify x
								tmpPosition.x -= vectorChange.x;
							} else {
								tmpPosition.x -= vectorChange.x;
								tmpPosition.y -= vectorChange.y;
							}
						} else if (velocity.y < 0) {
							// Character going right and up
							// x+,y- wall-top-left
							vectorChange.set(Math.abs(wallPosition.x-character[x].x),Math.abs(wallPosition.y-character[x].y+32));
							if (velocity.x > velocity.y) {
								// Modify y
								tmpPosition.y += vectorChange.y;
							} else if (velocity.x < velocity.y){
								// Modify x
								tmpPosition.x -= vectorChange.x;
							} else {
								tmpPosition.x -= vectorChange.x;
								tmpPosition.y += vectorChange.y;
							}
						} else {
							// Character going right
							// x+,0
							vectorChange.set(Math.abs(wallPosition.x-character[x].x),0);
							tmpPosition.x -= vectorChange.x;
						}
					} else if (velocity.x < 0) {
						if (velocity.y > 0) {
							// Character going left and down
							// x-,y+ wall-bottom-right
							vectorChange.set(Math.abs(wallPosition.x-character[x].x),Math.abs(wallPosition.y-character[x].y));
							if (velocity.x > velocity.y) {
								// Modify y
								tmpPosition.y -= vectorChange.y;
							} else if (velocity.x < velocity.y){
								// Modify x
								tmpPosition.x += vectorChange.x;
							} else {
								tmpPosition.x += vectorChange.x;
								tmpPosition.y -= vectorChange.y;
							}
						} else if (velocity.y < 0) {
							// Character going left and up
							// x-,y- wall-top-right
							vectorChange.set(Math.abs(wallPosition.x-character[x].x),Math.abs(wallPosition.y-character[x].y+32));
							if (velocity.x > velocity.y) {
								// Modify y
								tmpPosition.y += vectorChange.y;
							} else if (velocity.x < velocity.y){
								// Modify x
								tmpPosition.x += vectorChange.x;
							} else {
								tmpPosition.x += vectorChange.x;
								tmpPosition.y += vectorChange.y;
							}
						} else {
							// Character going left
							// x-,0
							vectorChange.set(Math.abs(wallPosition.x-character[x].x+32),0);
							tmpPosition.x += vectorChange.x;
						}
					} else {
						if (velocity.y > 0) {
							// Character going down... fuck the coordinates being in the upper left!
							// 0,y+
							vectorChange.set(0,Math.abs(wallPosition.y-character[x].y));
							tmpPosition.y -= vectorChange.y;
						} else if (velocity.y < 0) {
							// Character going up
							// 0+,y-
							vectorChange.set(0,Math.abs(wallPosition.y-character[x].y+32));
							tmpPosition.y += vectorChange.y;
						} else {
							// 0,0
							// I don't think it's possible to collide with zero velocity...
						}
					}
					if (vectorChange.x != 0 | vectorChange.y != 0) {
						// Does this break out of the for loop like it should or just the inner if statement...
						logger.info(velocity);
						logger.info(vectorChange);
						break;
					}
				}
			}

			position.x = tmpPosition.x;
			position.y = tmpPosition.y;
		}
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}
}
