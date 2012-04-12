package com.turbonips.troglodytes.systems;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Attack;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.Direction.Dir;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.Warp;

public class PlayerBehaviorSystem extends BaseEntitySystem {
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<ResourceRef> resourceMapper;
	private ComponentMapper<Direction> directionMapper;
	private ComponentMapper<Attack> attackMapper;

	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
		directionMapper = new ComponentMapper<Direction>(Direction.class, world);
		attackMapper = new ComponentMapper<Attack>(Attack.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> grounds = world.getGroupManager().getEntities("GROUND");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ResourceManager manager = ResourceManager.getInstance();
		Entity ground = grounds.get(0);
		Entity player = players.get(0);
		Position playerPos = positionMapper.get(player);
		Vector2f playerPosition = playerPos.getPosition();
		
		// Collision detection
		if (ground != null) {
			String groundResName = resourceMapper.get(ground).getResourceName();
			Resource groundRes = manager.getResource(groundResName);
			TiledMap map = (TiledMap)groundRes.getObject();
			Vector2f newPosition = checkWallCollisions(map,player);
			checkWarps(map,player);
			checkAttacking(player,enemies);
			playerPosition.set(newPosition);
		}
	}
	
	private Vector2f checkWallCollisions(TiledMap map, Entity player) {
		boolean collision = false;
		String playerResName = resourceMapper.get(player).getResourceName();
		ResourceManager manager = ResourceManager.getInstance();
		Resource playerRes = manager.getResource(playerResName);
		Image playerFrame = getFrame(playerRes);
		int th = map.getTileHeight();
		int tw = map.getTileWidth();
		int ph = playerFrame.getHeight();
		int pw = playerFrame.getWidth();
		Movement movement = movementMapper.get(player);
		Position playerPos = positionMapper.get(player);
		Vector2f playerPosition = playerPos.getPosition();
		Vector2f playerVelocity = movement.getVelocity();
		Vector2f newPlayerPosition = new Vector2f(playerPosition);
		newPlayerPosition.add(playerVelocity);
		Integer wx = ((int)(newPlayerPosition.x/tw) * tw),
				wy = ((int)(newPlayerPosition.y/th) * th);
		
		// Upper left
		if (map.getTileId((int)newPlayerPosition.x/tw, (int)newPlayerPosition.y/th, 3) > 0) {
			collision = true;
			// Wall lower right
			wx = ((int)(newPlayerPosition.x/tw) * tw) + tw;
			wy = ((int)(newPlayerPosition.y/th) * th) + th;
			logger.info("Upper left");
			
			if (map.getTileId((int)playerPosition.x/tw, (int)newPlayerPosition.y/th, 3) > 0 ||
				map.getTileId((int)((playerPosition.x)/tw + 0.5f), (int)newPlayerPosition.y/th, 3) > 0) {
				newPlayerPosition.y = wy;
			}
				
			else if (map.getTileId((int)newPlayerPosition.x/tw, (int)(playerPosition.y)/th, 3) > 0 ||
					 map.getTileId((int)newPlayerPosition.x/tw, (int)((playerPosition.y)/th + 0.5f), 3) > 0) {
				newPlayerPosition.x = wx;
			}
		}
		
		// Upper right
		if (map.getTileId((int)(newPlayerPosition.x+pw-1)/tw, (int)newPlayerPosition.y/th, 3) > 0) {
			collision = true;
			wx = ((int)(newPlayerPosition.x/tw) * tw);
			wy = ((int)(newPlayerPosition.y/th) * th) + th;
			logger.info("Upper right");
			
			if (map.getTileId((int)(playerPosition.x+pw-1)/tw, (int)newPlayerPosition.y/th, 3) > 0 ||
				map.getTileId((int)((playerPosition.x+pw-1)/tw - 0.5f), (int)newPlayerPosition.y/th, 3) > 0) {
				newPlayerPosition.y = wy;
			}
				
			else if (map.getTileId((int)(newPlayerPosition.x+pw-1)/tw, (int)playerPosition.y/th, 3) > 0 ||
					 map.getTileId((int)(newPlayerPosition.x+pw-1)/tw, (int)(playerPosition.y/th + 0.5f), 3) > 0) {
				newPlayerPosition.x = wx;
			}
		}
		
		// Lower left
		if (map.getTileId((int)newPlayerPosition.x/tw, (int)(newPlayerPosition.y+ph-1)/th, 3) > 0) {
			collision = true;
			wx = ((int)(newPlayerPosition.x/tw) * tw) + tw;
			wy = ((int)(newPlayerPosition.y/th) * th);
			logger.info("Lower left");
			
			
			if (map.getTileId((int)(playerPosition.x)/tw, (int)(newPlayerPosition.y+ph-1)/th, 3) > 0 ||
				map.getTileId((int)((playerPosition.x)/tw + 0.5f), (int)(newPlayerPosition.y+ph-1)/th, 3) > 0) {
				newPlayerPosition.y = wy;
			}
				
			else if (map.getTileId((int)(newPlayerPosition.x)/tw, (int)(playerPosition.y+ph-1)/th, 3) > 0 ||
					 map.getTileId((int)(newPlayerPosition.x)/tw, (int)((playerPosition.y+ph-1)/th - 0.5f), 3) > 0) {
				newPlayerPosition.x = wx;
			}
		}
		
		// Lower right
		if (map.getTileId((int)(newPlayerPosition.x+pw-1)/tw, (int)(newPlayerPosition.y+ph-1)/th, 3) > 0) {
			collision = true;
			wx = ((int)(newPlayerPosition.x/tw) * tw);
			wy = ((int)(newPlayerPosition.y/th) * th);
			logger.info("Lower right");
			
			if (map.getTileId((int)(playerPosition.x+pw-1)/tw, (int)(newPlayerPosition.y+ph-1)/th, 3) > 0 ||
				map.getTileId((int)((playerPosition.x+pw-1)/tw - 0.5f), (int)(newPlayerPosition.y+ph-1)/th, 3) > 0) {
				newPlayerPosition.y = wy;
			}
				
			else if (map.getTileId((int)(newPlayerPosition.x+pw-1)/tw, (int)(playerPosition.y+ph-1)/th, 3) > 0 ||
					 map.getTileId((int)(newPlayerPosition.x+pw-1)/tw, (int)((playerPosition.y+ph-1)/th - 0.5f), 3) > 0) {
				newPlayerPosition.x = wx;
			}
		}
//		if (collision) {
//			resourceMapper.get(player).setResourceName("testenemyimage");
//		} else {
//			resourceMapper.get(player).setResourceName("testplayerimage");
//		}
		
		return newPlayerPosition;
	}
	
	private void checkWarps(TiledMap map, Entity player) {
		boolean collision = false;
		String playerResName = resourceMapper.get(player).getResourceName();
		ResourceManager manager = ResourceManager.getInstance();
		Resource playerRes = manager.getResource(playerResName);
		Image playerFrame = getFrame(playerRes);
		int ph = playerFrame.getHeight();
		int pw = playerFrame.getWidth();
		Movement movement = movementMapper.get(player);
		Position playerPos = positionMapper.get(player);
		Vector2f playerPosition = playerPos.getPosition();
		Vector2f playerVelocity = movement.getVelocity();
		Vector2f newPlayerPosition = new Vector2f(playerPosition);
		newPlayerPosition.add(playerVelocity);
		
		for (int i=0; i<map.getObjectGroupCount(); i++){
			for (int j=0; j<map.getObjectCount(i); j++) {
				if (map.getObjectType(i,j).toLowerCase().equals("warp")) {
					// Warp x location
					int x = map.getObjectX(i,j);
					// Warp y location
					int y = map.getObjectY(i,j);
					// Warp width
					int w = map.getObjectWidth(i,j);
					// Warp height
					int h = map.getObjectHeight(i,j);
					String warpMap = map.getObjectProperty(i, j, "Map", "");
					int warpX = Integer.valueOf(map.getObjectProperty(i, j, "X", ""));
					int warpY = Integer.valueOf(map.getObjectProperty(i, j, "Y", ""));
					Vector2f warpPosition = new Vector2f(warpX, warpY);
					
					
					// Upper left
					if (newPlayerPosition.x > x && newPlayerPosition.x < x + w && newPlayerPosition.y > y && newPlayerPosition.y < y + h) {
						collision = true;
					}
					
					// Upper right
					if (newPlayerPosition.x + pw > x && newPlayerPosition.x + pw < x + w && newPlayerPosition.y > y && newPlayerPosition.y < y + h) {
						collision = true;
					}
					
					// Lower left
					if (newPlayerPosition.x > x && newPlayerPosition.x < x + w && newPlayerPosition.y + ph > y && newPlayerPosition.y + ph < y + h) {
						collision = true;
					}
					
					// Lower right
					if (newPlayerPosition.x + pw > x && newPlayerPosition.x + pw < x + w && newPlayerPosition.y + ph > y && newPlayerPosition.y + ph < y + h) {
						collision = true;
					}
					
					if (collision) {
						player.addComponent(new Warp(warpMap, warpPosition));
						player.refresh();
						//resourceMapper.get(player).setResourceName("testenemyimage");
					}
				}
			}
		}
	}
	
	private void checkAttacking(Entity player, ImmutableBag<Entity> enemies) {
		Vector2f playerPosition = positionMapper.get(player).getPosition();
		String playerResName = resourceMapper.get(player).getResourceName();
		ResourceManager manager = ResourceManager.getInstance();
		Resource playerRes = manager.getResource(playerResName);
		Image playerFrame = getFrame(playerRes);
		int ph = playerFrame.getHeight();
		int pw = playerFrame.getWidth();
		Vector2f playerCenter = new Vector2f(playerPosition.x+(pw/2),playerPosition.y+(ph/2));
		int MAX_DISTANCE = 64;
		Direction playerDirection = directionMapper.get(player);

		for (int i=0; i<enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			Vector2f enemyPosition = positionMapper.get(enemy).getPosition();
			String enemyResName = resourceMapper.get(enemy).getResourceName();
			Resource enemyRes = manager.getResource(enemyResName);
			Image enemyFrame = getFrame(enemyRes);
			int eh = enemyFrame.getHeight();
			int ew = enemyFrame.getWidth();
			Vector2f enemyCenter = new Vector2f(enemyPosition.x+(ew/2),enemyPosition.y+(eh/2));
			
			if (playerCenter.distance(enemyCenter) < MAX_DISTANCE) {
				Vector2f playerToEnemy = new Vector2f(enemyCenter.x-playerCenter.x,playerCenter.y-enemyCenter.y);
				
				switch (playerDirection.getDirection()) {
					case UP:
						if (playerToEnemy.y >= 0) {
							enemy.delete();
						}
						break;
					case DOWN:
						if (playerToEnemy.y <= 0) {
							enemy.delete();
						}
						break;
					case LEFT:
						if (playerToEnemy.x <= 0) {
							enemy.delete();
						}
						break;
					case RIGHT:
						if (playerToEnemy.x >= 0) {
							enemy.delete();
						}
						break;
						
					case UP_RIGHT:
						if (playerToEnemy.x < 0) {
							if (Math.abs(playerToEnemy.x) <= Math.abs(playerToEnemy.y)) {
								enemy.delete();
							}
						} else if (playerToEnemy.x > 0) {
							if (Math.abs(playerToEnemy.x) >= Math.abs(playerToEnemy.y) || 
								playerToEnemy.y > 0) {
								enemy.delete();
							}
						} else if (playerToEnemy.y >= 0) {
							enemy.delete();
						}
						break;
						
					case UP_LEFT:
						if (playerToEnemy.x > 0) {
							if (Math.abs(playerToEnemy.x) >= Math.abs(playerToEnemy.y)) {
								enemy.delete();
							}
						} else if (playerToEnemy.x < 0) {
							if (Math.abs(playerToEnemy.x) >= Math.abs(playerToEnemy.y) || 
								playerToEnemy.y > 0) {
								enemy.delete();
							}
						} else if (playerToEnemy.y >= 0) {
							enemy.delete();
						}
						break;
						
					case DOWN_LEFT:
						if (playerToEnemy.x > 0) {
							if (Math.abs(playerToEnemy.y) >= Math.abs(playerToEnemy.x)) {
								enemy.delete();
							}
						} else if (playerToEnemy.x < 0) {
							if (Math.abs(playerToEnemy.y) >= Math.abs(playerToEnemy.x) || 
								playerToEnemy.x < 0) {
								enemy.delete();
							}
						} else if (playerToEnemy.x <= 0) {
							enemy.delete();
						}
						break;
						
					case DOWN_RIGHT:
						if (playerToEnemy.x < 0) {
							if (Math.abs(playerToEnemy.y) >= Math.abs(playerToEnemy.x)) {
								enemy.delete();
							}
						} else if (playerToEnemy.x > 0) {
							if (Math.abs(playerToEnemy.y) <= Math.abs(playerToEnemy.x) || 
								playerToEnemy.x > 0) {
								enemy.delete();
							}
						} else if (playerToEnemy.x <= 0) {
							enemy.delete();
						}
						break;
				}
				// Attack up
				// Attack down
				// Attack left
				// Attack right
				// Attack up right
				// Attack down right
				// Attack up left
				// Attack down left
			}
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

	@Override
	protected boolean checkProcessing() {
		return true;
	}
}
