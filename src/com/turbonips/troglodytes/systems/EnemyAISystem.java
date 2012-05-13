package com.turbonips.troglodytes.systems;

import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CollisionMap;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.EnemyAI;
import com.turbonips.troglodytes.components.EnemyAI.Corner;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.Direction.Dir;
import com.turbonips.troglodytes.components.EnemyAI.AIType;
import com.turbonips.troglodytes.components.Stats;
import com.turbonips.troglodytes.components.Stats.StatType;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Location;
import com.turbonips.troglodytes.systems.CollisionResolution.CollisionDirection;

public class EnemyAISystem extends BaseEntitySystem {
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Direction> directionMapper;
	private ComponentMapper<EnemyAI> enemyAIMapper;
	private ComponentMapper<Location> locationMapper;
	private ComponentMapper<ResourceRef> resourceMapper;
	private ComponentMapper<Stats> statsMapper;
	
	private int time=0;

	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		directionMapper = new ComponentMapper<Direction>(Direction.class, world);
		enemyAIMapper = new ComponentMapper<EnemyAI>(EnemyAI.class, world);
		locationMapper = new ComponentMapper<Location>(Location.class, world);
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
		statsMapper = new ComponentMapper<Stats>(Stats.class, world);
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}
	
	protected void adjustTowardMaxSpeed(Entity enemy) {
		// If the enemy is traveling too fast then reduce the vector by the enemy deceleration rate
		HashMap<StatType, Integer> enemyStats = statsMapper.get(enemy).getStats();
		Movement movement = movementMapper.get(enemy);
		Vector2f velocity = movement.getVelocity();
		int deceleration = enemyStats.get(StatType.DECELERATION);
		int maxSpeed = enemyStats.get(StatType.MAX_SPEED);
		float currentSpeed = velocity.distance(new Vector2f(0, 0));
		
		if (currentSpeed > (float) maxSpeed) {
			// First see how close to max speed they are, if the difference is within deceleration
			// rate then simply cap it to the maxSpeed, otherwise reduce x and y based on
			// the scale of each multiplied by the deceleration rate.
			// This will reduce the whole vector by exactly the deceleration rate.
			float scale;
			
			if ((currentSpeed - maxSpeed) <= deceleration) {
				// Scale the x and y velocity by the maximum-speed/current-speed
				// The end result is a vector that is equal to maximum-speed
				scale = (maxSpeed/currentSpeed);
			} else {
				// The enemy was (hopefully) pushed by the player in order to achieve this 
				// current-speed. We want to reduce the vector by the deceleration rate.
				// Scale the x and y velocity down by the desired-speed/current-speed
				// The end result is a vector that is equal to desired-speed
				scale = ((currentSpeed-deceleration)/currentSpeed);
			}
			
			velocity.x = scale*velocity.x;
			velocity.y = scale*velocity.y;
		}
	}
	
	protected void setDirection(Entity enemy) {
		Movement movement = movementMapper.get(enemy);
		Vector2f velocity = movement.getVelocity();
		Direction direction = directionMapper.get(enemy);
		
		if (movement.getCurrentSpeed() != 0) {
			if (velocity.x > 0) {
				if (velocity.y > 0) {
					direction.setDirection(Dir.DOWN_RIGHT);
				} else if (velocity.y < 0) {
					direction.setDirection(Dir.UP_RIGHT);
				} else {
					direction.setDirection(Dir.RIGHT);
				}
			} else if (velocity.x < 0) {
				if (velocity.y > 0) {
					direction.setDirection(Dir.DOWN_LEFT);
				} else if (velocity.y < 0) {
					direction.setDirection(Dir.UP_LEFT);
				} else {
					direction.setDirection(Dir.LEFT);
				}
			} else {
				if (velocity.y > 0) {
					direction.setDirection(Dir.DOWN);
				} else {
					direction.setDirection(Dir.UP);
				}
			}
		}
	}

	protected void randomAI(Entity enemy, Entity player, Entity map) {
		HashMap<StatType, Integer> enemyStats = statsMapper.get(enemy).getStats();
		Movement movement = movementMapper.get(enemy);
		Vector2f velocity = movement.getVelocity();
		Vector2f tmpVelocity = new Vector2f(velocity);
		int acceleration = enemyStats.get(StatType.ACCELERATION);
		int deceleration = enemyStats.get(StatType.DECELERATION);
		Image enemyFrame = getFrame(enemy);
		Image playerFrame = getFrame(player);
		Vector2f enemyCenter = new Vector2f(locationMapper.get(enemy).getPosition());
		enemyCenter.x += enemyFrame.getWidth()/2;
		enemyCenter.y += enemyFrame.getHeight()/2;
		Vector2f playerCenter = new Vector2f(locationMapper.get(player).getPosition());
		playerCenter.x += playerFrame.getWidth()/2;
		playerCenter.y += playerFrame.getHeight()/2;
		ResourceManager manager = ResourceManager.getInstance();
		String mapResName = resourceMapper.get(map).getResourceName();
		Resource mapRes = manager.getResource(mapResName);
		TiledMap tiledMap = (TiledMap) mapRes.getObject();
		int tileWidth = tiledMap.getTileWidth();
		
		// Random vertical movement
		switch ((int) (Math.random() * 30)) {
			case 0: // up
				tmpVelocity.y -= acceleration;
				break;
			case 1: // down
				tmpVelocity.y += acceleration;
				break;
			case 2: // neither; tend toward zero
				if (tmpVelocity.y > 0) {
					tmpVelocity.y -= deceleration;
					if (tmpVelocity.y < 0) {
						tmpVelocity.y = 0;
					}
				} else if (tmpVelocity.y < 0) {
					tmpVelocity.y += deceleration;
					if (tmpVelocity.y > 0) {
						tmpVelocity.y = 0;
					}
				}
		}

		// Random horizontal movement
		switch ((int) (Math.random() * 30)) {
			case 0: // up
				tmpVelocity.x -= acceleration;
				break;
			case 1: // down
				tmpVelocity.x += acceleration;
				break;
			case 2: // neither; tend toward zero
				if (tmpVelocity.x > 0) {
					tmpVelocity.x -= deceleration;
					if (tmpVelocity.x < 0) {
						tmpVelocity.x = 0;
					}
				} else if (tmpVelocity.x < 0) {
					tmpVelocity.x += deceleration;
					if (tmpVelocity.x > 0) {
						tmpVelocity.x = 0;
					}
				}
		}
		
		// Find out how close the creature would be after moving
		Vector2f newEnemyCenter = new Vector2f(enemyCenter);
		newEnemyCenter.x += tmpVelocity.x;
		newEnemyCenter.y += tmpVelocity.y;
		if (playerCenter.distance(newEnemyCenter) < tileWidth) {
			// You're too close so don't move
			velocity.x = 0;
			velocity.y = 0;
		} else {
			velocity.x = tmpVelocity.x;
			velocity.y = tmpVelocity.y;
		}
		
		adjustTowardMaxSpeed(enemy);
		setDirection(enemy);
	}
	
	protected void followAI(Entity enemy, Entity player, Entity map) {
		HashMap<StatType, Integer> enemyStats = statsMapper.get(enemy).getStats();
		Movement movement = movementMapper.get(enemy);
		Vector2f velocity = movement.getVelocity();
		Vector2f tmpVelocity = new Vector2f(velocity);
		int acceleration = enemyStats.get(StatType.ACCELERATION);
		EnemyAI enemyAI = enemyAIMapper.get(enemy);
		ResourceManager manager = ResourceManager.getInstance();
		Image enemyFrame = getFrame(enemy);
		Image playerFrame = getFrame(player);
		String mapResName = resourceMapper.get(map).getResourceName();
		Resource mapRes = manager.getResource(mapResName);
		TiledMap tiledMap = (TiledMap) mapRes.getObject();
		int tileWidth = tiledMap.getTileWidth();
		Vector2f playerCenter = new Vector2f(locationMapper.get(player).getPosition());
		playerCenter.x += playerFrame.getWidth()/2;
		playerCenter.y += playerFrame.getHeight()/2;
		Vector2f enemyCenter = new Vector2f(locationMapper.get(enemy).getPosition());
		enemyCenter.x += enemyFrame.getWidth()/2;
		enemyCenter.y += enemyFrame.getHeight()/2;
		float distance = playerCenter.distance(enemyCenter);
		Vector2f enemyToPlayer = new Vector2f();
		enemyToPlayer.x = playerCenter.x - enemyCenter.x;
		enemyToPlayer.y = playerCenter.y - enemyCenter.y;
		
		// Range is in tiles
		if (distance <= enemyAI.getSight()*tileWidth) {
			float scale, accelX, accelY;
			if (Math.abs(enemyToPlayer.x) >= Math.abs(enemyToPlayer.y)){
				// X is greater so divide y by x for a fraction
				scale = Math.abs(enemyToPlayer.y/enemyToPlayer.x);
				accelY = scale*acceleration;
				// Now multiply the other by the inverse fraction
				accelX = (1.0f-scale)*acceleration;
			} else {
				// Y is greater so divide y by x for a fraction
				scale = Math.abs(enemyToPlayer.x/enemyToPlayer.y);
				accelX = scale*acceleration;
				// Now multiply the other by the inverse fraction
				accelY = (1.0f-scale)*acceleration;
			}
			if (enemyToPlayer.x > 0) {
				tmpVelocity.x += accelX;
			} else if (enemyToPlayer.x < 0) {
				tmpVelocity.x -= accelX;
			}
			if (enemyToPlayer.y > 0) {
				tmpVelocity.y += accelY;
			} else if (enemyToPlayer.y < 0) {
				tmpVelocity.y -= accelY;
			}
			
			// Find out how close the creature would be after moving
			Vector2f newEnemyCenter = new Vector2f(enemyCenter);
			newEnemyCenter.x += tmpVelocity.x;
			newEnemyCenter.y += tmpVelocity.y;
			if (playerCenter.distance(newEnemyCenter) < tileWidth) {
				// You're too close so don't move
				velocity.x = 0;
				velocity.y = 0;
			} else {
				velocity.x = tmpVelocity.x;
				velocity.y = tmpVelocity.y;
			}
			
			adjustTowardMaxSpeed(enemy);
			setDirection(enemy);
		} else {
			randomAI(enemy, player, map);
		}
	}
	
	protected void aStarAI(Entity enemy, Entity player, Entity map) {
		// Let's first load the minimum to see if we are close enough
		// to the player to ignore A-Star altogether
		Image enemyFrame = getFrame(enemy);
		int enemyWidth = enemyFrame.getWidth();
		int enemyHeight = enemyFrame.getHeight();
		Image playerFrame = getFrame(player);
		Vector2f playerCenter = new Vector2f(locationMapper.get(player).getPosition());
		playerCenter.x += playerFrame.getWidth()/2;
		playerCenter.y += playerFrame.getHeight()/2;
		Vector2f enemyCenter = new Vector2f(locationMapper.get(enemy).getPosition());
		enemyCenter.x += enemyWidth/2;
		enemyCenter.y += enemyHeight/2;
		Vector2f enemyToPlayer = new Vector2f();
		enemyToPlayer.x = playerCenter.x - enemyCenter.x;
		enemyToPlayer.y = playerCenter.y - enemyCenter.y;
		String mapResName = resourceMapper.get(map).getResourceName();
		ResourceManager manager = ResourceManager.getInstance();
		Resource mapRes = manager.getResource(mapResName);
		TiledMap tiledMap = (TiledMap) mapRes.getObject();
		int tileWidth = tiledMap.getTileWidth();
		int tileHeight = tiledMap.getTileHeight();
		Movement movement = movementMapper.get(enemy);
		Vector2f velocity = movement.getVelocity();
		CollisionMap collisionMap = new CollisionMap(tiledMap);
		EnemyAI enemyAI = enemyAIMapper.get(enemy);
		int sight = enemyAI.getSight();		
		AStarPathFinder pathFinder = new AStarPathFinder(collisionMap, sight, false);
		Path newPath;
		enemyAI.pathAge--;
		
		if (enemyAI.pathAge <= 0) {
			// Path from the enemy center to the player center
			newPath = pathFinder.findPath(null, (int) enemyCenter.x / tileWidth, (int) enemyCenter.y / tileHeight, (int) playerCenter.x / tileWidth, (int) playerCenter.y / tileHeight);
			enemyAI.setPath(newPath);
			enemyAI.pathAge = 15;
			enemyAI.pathStep = 1;
		}

		Path path = enemyAI.getPath();
		if (path != null) {
			// Iterate through the enemy path to the player
			// They always occupy the 0 step and they don't create a path if
			// they are on top of the player
			Vector2f enemyToTile = new Vector2f();
			int currentX, currentY;
			// How far ahead in the path should we check before moving? (Bigger == Smoother)
			int lookAhead = 5;
			boolean xCollide = false, yCollide = false;
			HashMap<StatType, Integer> enemyStats = statsMapper.get(enemy).getStats();
			int acceleration = enemyStats.get(StatType.ACCELERATION);
			int maxSpeed = enemyStats.get(StatType.MAX_SPEED);
			CollisionResolution collisionResolution = CollisionResolution.getInstance();
			boolean up=false, down=false, left=false, right=false;
			Vector2f tmpVelocity = new Vector2f(velocity);
			
			for (int currentIndex=1; currentIndex<lookAhead; currentIndex++) {
				// Our last look-ahead value was the last tile in the path
				if (path.getLength() == currentIndex) {
					break;
				}
				
				currentX = path.getX(currentIndex);
				currentY = path.getY(currentIndex);
				
				// Remember our first step
				if (currentIndex == 1) {
					if (currentX != path.getX(0)) {
						if (currentX > path.getX(0)) {
							right=true;
						} else {
							left=true;
						}
					} else {
						if (currentY > path.getY(0)) {
							down=true;
						} else {
							up=true;
						}
					}
				}
				
				// Does this path differ from the last in the X (if) or the Y (else)
				if (currentX != path.getX(currentIndex-1)) {
					if (yCollide) {
						// Our past path has already changed in the Y direction so stop here
						break;
					}
					
					// X changed
					xCollide=true;
					enemyToTile.x = currentX*tileWidth + tileWidth/2 - enemyCenter.x;
					enemyToTile.y = currentY*tileHeight + tileHeight/2 - enemyCenter.y;
					// Check above or below for collisions
					if (collisionMap.blocked(null, currentX, currentY-1)) {
						// We have a collision above
						// Go towards the bottom (center edge)
						enemyToTile.y = currentY*32 + tileHeight - enemyCenter.y;
					} else if (collisionMap.blocked(null, currentX, currentY+1)) {
						// We have a collision below
						// Go towards the top (center edge)
						enemyToTile.y = currentY*32 - enemyCenter.y;
					} else {
						// No surrounding collisions
						xCollide=false;
					}
				} else {
					if (xCollide) {
						// Our past path has already changed in the X direction so stop here
						break;
					}
					
					// Y changed
					yCollide=true;
					enemyToTile.x = currentX*32 + tileWidth/2 - enemyCenter.x;
					enemyToTile.y = currentY*32 + tileHeight/2 - enemyCenter.y;
					// Check left or right for collisions
					if (collisionMap.blocked(null, currentX-1, currentY)) {
						// We have a collision left
						// Go towards the right (center edge)
						enemyToTile.x = currentX*32 + tileWidth - enemyCenter.x;
					} else if (collisionMap.blocked(null, currentX+1, currentY)) {
						// We have a collision right
						// Go towards the left (center edge)
						enemyToTile.x = currentX*32 - enemyCenter.x;
					} else {
						// No surrounding collisions
						yCollide=false;
					}
				}
			}
			
			// Now travel towards enemy-to-tile vector
			float scale, accelX, accelY;
			if (Math.abs(enemyToTile.x) >= Math.abs(enemyToTile.y)){
				// X is greater so divide y by x for a fraction
				scale = Math.abs(enemyToTile.y/enemyToTile.x);
				accelY = scale*acceleration;
				// Now multiply the other by the inverse fraction
				accelX = (1.0f-scale)*acceleration;
			} else {
				// Y is greater so divide y by x for a fraction
				scale = Math.abs(enemyToTile.x/enemyToTile.y);
				accelX = scale*acceleration;
				// Now multiply the other by the inverse fraction
				accelY = (1.0f-scale)*acceleration;
			}
			if (enemyToTile.x > 0) {
				tmpVelocity.x += accelX;
			} else if (enemyToTile.x < 0) {
				tmpVelocity.x -= accelX;
			}
			if (enemyToTile.y > 0) {
				tmpVelocity.y += accelY;
			} else if (enemyToTile.y < 0) {
				tmpVelocity.y -= accelY;
			}
			
			CollisionDirection collisionDirection = collisionResolution.getCollisionDirection(enemy, tiledMap, tmpVelocity);
			if (collisionDirection != CollisionDirection.NONE) {
				boolean goFirstDirection=false;
				if (collisionDirection == CollisionDirection.LEFT) {
					if (left) {
						if (collisionMap.blocked(null, path.getX(1), path.getY(1)-1)) {
							// Up is blocked, go downward
							if (Math.abs(tmpVelocity.y) < maxSpeed) {
								tmpVelocity.y += acceleration;
							}
						} else {
							// Down is blocked, go upward
							if (Math.abs(tmpVelocity.y) < maxSpeed) {
								tmpVelocity.y -= acceleration;
							}
						}						
					} else {
						goFirstDirection=true;
					}
				} else if (collisionDirection == CollisionDirection.RIGHT) {
					if (right) {
						if (collisionMap.blocked(null, path.getX(1), path.getY(1)-1)) {
							// Up is blocked, go downward
							if (Math.abs(tmpVelocity.y) < maxSpeed) {
								tmpVelocity.y += acceleration;
							}
						} else {
							// Down is blocked, go upward
							if (Math.abs(tmpVelocity.y) < maxSpeed) {
								tmpVelocity.y -= acceleration;
							}
						}	
					} else {
						goFirstDirection=true;
					}
				} else if (collisionDirection == CollisionDirection.UP) {
					if (up) {
						if (collisionMap.blocked(null, path.getX(1)-1, path.getY(1))) {
							// Left is blocked, go right
							if (Math.abs(tmpVelocity.x) < maxSpeed) {
								tmpVelocity.x += acceleration;
							}
						} else {
							// Right is blocked, go left
							if (Math.abs(tmpVelocity.x) < maxSpeed) {
								tmpVelocity.x -= acceleration;
							}
						}
					} else {
						goFirstDirection=true;
					}
				} else if (collisionDirection == CollisionDirection.DOWN) {
					if (down) {
						if (collisionMap.blocked(null, path.getX(1)-1, path.getY(1))) {
							// Left is blocked, go right
							if (Math.abs(tmpVelocity.x) < maxSpeed) {
								tmpVelocity.x += acceleration;
							}
						} else {
							// Right is blocked, go left
							if (Math.abs(tmpVelocity.x) < maxSpeed) {
								tmpVelocity.x -= acceleration;
							}
						}
					} else {
						goFirstDirection=true;
					}
				}
				
				if (goFirstDirection) {
					if (left) {
						if (Math.abs(tmpVelocity.x) < maxSpeed) {
							tmpVelocity.x -= acceleration;
						}
					} else if (right) {
						if (Math.abs(tmpVelocity.x) < maxSpeed) {
							tmpVelocity.x += acceleration;
						}
					} else if (up) {
						if (Math.abs(tmpVelocity.y) < maxSpeed) {
							tmpVelocity.y -= acceleration;
						}
					} else {
						if (Math.abs(tmpVelocity.y) < maxSpeed) {
							tmpVelocity.y += acceleration;
						}
					}
				} else {
					// They are in a corner. Won't happen unless it's a fat creature
				}
			}
			
			// Find out how close the creature would be after moving
			Vector2f newEnemyCenter = new Vector2f(enemyCenter);
			newEnemyCenter.x += tmpVelocity.x;
			newEnemyCenter.y += tmpVelocity.y;
			if (playerCenter.distance(newEnemyCenter) < tileWidth) {
				// You're too close so don't move
				velocity.x = 0;
				velocity.y = 0;
			} else {
				velocity.x = tmpVelocity.x;
				velocity.y = tmpVelocity.y;
			}
			
			adjustTowardMaxSpeed(enemy);
			setDirection(enemy);
		} else {
			randomAI(enemy, player, map);
		}
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> e) {
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> maps = world.getGroupManager().getEntities("MAP");

		Entity player = players.get(0);
		Entity map = maps.get(0);
		Location playerLocation = locationMapper.get(player);

		// Iterate through all enemies
		for (int i = 0; i < enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			EnemyAI enemyAI = enemyAIMapper.get(enemy);
			Location enemyLocation = locationMapper.get(enemy);

			if (enemyLocation.getMap().equals(playerLocation.getMap())) {
				if (enemyAI != null) {
					Movement movement = movementMapper.get(enemy);

					if (movement != null) {
						AIType enemyAIType = enemyAI.getEnemyAIType();

						if (enemyAIType == AIType.RANDOM) {
							randomAI(enemy, player, map);
						} else if (enemyAIType == AIType.FOLLOW) {
							if (enemyAI.getTime() <= 20) {
								enemyAI.sumTime(world.getDelta());
								followAI(enemy, player, map);
							} else {
								enemyAI.clearTime();
								randomAI(enemy, player, map);
							}
						} else if (enemyAIType == AIType.ASTAR){
							if (enemyAI.getTime() <= 20) {
								enemyAI.sumTime(world.getDelta());
								aStarAI(enemy, player, map);
							} else {
								enemyAI.clearTime();
								randomAI(enemy, player, map);
							}
						}
					}
				}
			}
		}
	}

	private Image getFrame(Entity entity) {
		ResourceManager manager = ResourceManager.getInstance();
		String resName = resourceMapper.get(entity).getResourceName();
		Resource resource = manager.getResource(resName);
		
		switch (resource.getType()) {
			case CREATURE_ANIMATION:
				return ((CreatureAnimation) resource.getObject()).getCurrent().getCurrentFrame();
			case IMAGE:
				return (Image) resource.getObject();
			default:
				return null;
		}
	}
}
