package com.turbonips.troglodytes.systems;

import java.util.ArrayList;
import java.util.Random;

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
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.Direction.Dir;
import com.turbonips.troglodytes.components.EnemyAI.AIType;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;

public class EnemyAISystem extends BaseEntitySystem
{
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Direction> directionMapper;
	private ComponentMapper<EnemyAI> enemyAIMapper;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<ResourceRef> resourceMapper;
	
	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement> (Movement.class, world);
		directionMapper = new ComponentMapper<Direction> (Direction.class, world);
		enemyAIMapper = new ComponentMapper<EnemyAI> (EnemyAI.class, world);
		positionMapper = new ComponentMapper<Position> (Position.class, world);
		resourceMapper = new ComponentMapper<ResourceRef> (ResourceRef.class, world);
	}
	
	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> e) {
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> grounds = world.getGroupManager().getEntities("GROUND");
		ResourceManager manager = ResourceManager.getInstance();
		Entity player = players.get(0);
		Entity ground = grounds.get(0);
		
		for (int i = 0; i < enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			EnemyAI enemyAI = enemyAIMapper.get(enemy);
			
			if (enemyAI != null) {
				Movement movement = movementMapper.get(enemy);
				if (movement != null) {
					Vector2f velocity = movement.getVelocity();
					Vector2f acceleration = movement.getAcceleration();
					Vector2f deceleration = movement.getDeceleration();
					Vector2f tmpVelocity = new Vector2f(velocity);
					AIType enemyAIType = enemyAI.getEnemyAIType();
					
					if (enemyAIType == AIType.DUMB) {
						switch ((int) (Math.random()*30)) {
							case 0: // up
								tmpVelocity.y -= acceleration.y;
								break;
							case 1: // down
								tmpVelocity.y += acceleration.x;
								break;
							case 2: // neither; tend toward zero
								if (tmpVelocity.y > 0) {
									tmpVelocity.y -= deceleration.y;
									if (tmpVelocity.y < 0) {
										tmpVelocity.y = 0;
									}
								} 
								else if (tmpVelocity.y < 0) {
									tmpVelocity.y += deceleration.y;
									if (tmpVelocity.y > 0) {
										tmpVelocity.y = 0;
									}
								}
						}
						switch ((int) (Math.random()*30)) {
							case 0: // left
								tmpVelocity.x -= acceleration.x;
								break;
							case 1: // right
								tmpVelocity.x += acceleration.x;
								break;
							case 2: //neither; tend toward zero
								if (tmpVelocity.x > 0) {
									tmpVelocity.x -= deceleration.x;
									if (tmpVelocity.x < 0) {
										tmpVelocity.x = 0;
									}
								}
								else if (tmpVelocity.x < 0) {
									tmpVelocity.x += deceleration.x;
									if (tmpVelocity.x > 0) {
										tmpVelocity.x = 0;
									}
								}
						}
						if (tmpVelocity.distance(new Vector2f(0,0))>movement.getMaximumSpeed()) {
							float scale;
							// Scale the x and y velocity by the maximum-speed/current-velocity
							scale = (movement.getMaximumSpeed()/tmpVelocity.distance(new Vector2f(0,0)));
							tmpVelocity.x = scale*tmpVelocity.x;
							tmpVelocity.y = scale*tmpVelocity.y;
						}
						velocity.x = tmpVelocity.x;
						velocity.y = tmpVelocity.y;
						
						Direction direction = directionMapper.get(enemy);
						ArrayList<Dir> directions = direction.getDirections();
						if (movement.getCurrentSpeed() != 0) {
							directions.clear();
							if (velocity.x > 0) {
								directions.add(Dir.RIGHT);
							} else if (velocity.x < 0) {
								directions.add(Dir.LEFT);
							}
							if (velocity.y > 0) {
								directions.add(Dir.DOWN);
							} else if (velocity.y < 0) {
								directions.add(Dir.UP);
							}
						}
					} else if (enemyAIType == AIType.DUMBCHARGE) {
						Position playerPos = positionMapper.get(player);
						Position enemyPos = positionMapper.get(enemy);
						Vector2f playerPosition = playerPos.getPosition();
						Vector2f enemyPosition = enemyPos.getPosition();
						Vector2f positionDifference = new Vector2f(playerPosition.x - enemyPosition.x, playerPosition.y - enemyPosition.y);
						Vector2f positionCheck = new Vector2f(positionDifference);
						if (positionCheck.x < 0) {
							positionCheck.x = -positionCheck.x;
						}
						if (positionCheck.y < 0) {
							positionCheck.y = -positionCheck.y;
						}
						float range = enemyAI.getSight()*32;
						
						if (positionCheck.x < range && positionCheck.y < range) {
							if (positionDifference.y > 0) {
								tmpVelocity.y += acceleration.y;
							} else if (positionDifference.y < 0) {
								tmpVelocity.y -= acceleration.y;
							}
							if (positionDifference.x > 0) {
								tmpVelocity.x += acceleration.x;
							} else if (positionDifference.x < 0) {
								tmpVelocity.x -= acceleration.x;
							}
						} else {
						
							switch ((int) (Math.random()*30)) {
								case 0: // up
									tmpVelocity.y -= acceleration.y;
									break;
								case 1: // down
									tmpVelocity.y += acceleration.x;
									break;
								case 2: // neither; tend toward zero
									if (tmpVelocity.y > 0) {
										tmpVelocity.y -= deceleration.y;
										if (tmpVelocity.y < 0) {
											tmpVelocity.y = 0;
										}
									} 
									else if (tmpVelocity.y < 0) {
										tmpVelocity.y += deceleration.y;
										if (tmpVelocity.y > 0) {
											tmpVelocity.y = 0;
										}
									}
							}
							switch ((int) (Math.random()*30)) {
								case 0: // left
									tmpVelocity.x -= acceleration.x;
									break;
								case 1: // right
									tmpVelocity.x += acceleration.x;
									break;
								case 2: //neither; tend toward zero
									if (tmpVelocity.x > 0) {
										tmpVelocity.x -= deceleration.x;
										if (tmpVelocity.x < 0) {
											tmpVelocity.x = 0;
										}
									}
									else if (tmpVelocity.x < 0) {
										tmpVelocity.x += deceleration.x;
										if (tmpVelocity.x > 0) {
											tmpVelocity.x = 0;
										}
									}
							}
						}
						if (tmpVelocity.distance(new Vector2f(0,0))>movement.getMaximumSpeed()) {
							float scale;
							// Scale the x and y velocity by the maximum-speed/current-velocity
							scale = (movement.getMaximumSpeed()/tmpVelocity.distance(new Vector2f(0,0)));
							tmpVelocity.x = scale*tmpVelocity.x;
							tmpVelocity.y = scale*tmpVelocity.y;
						}
						velocity.x = tmpVelocity.x;
						velocity.y = tmpVelocity.y;
						
						Direction direction = directionMapper.get(enemy);
						ArrayList<Dir> directions = direction.getDirections();
						if (movement.getCurrentSpeed() != 0) {
							directions.clear();
							if (velocity.x > 0) {
								directions.add(Dir.RIGHT);
							} else if (velocity.x < 0) {
								directions.add(Dir.LEFT);
							}
							if (velocity.y > 0) {
								directions.add(Dir.DOWN);
							} else if (velocity.y < 0) {
								directions.add(Dir.UP);
							}
						}
					} else if (enemyAIType == AIType.DUMBFIND) {
						Position playerPos = positionMapper.get(player);
						Position enemyPos = positionMapper.get(enemy);
						Vector2f playerPosition = playerPos.getPosition();
						Vector2f enemyPosition = enemyPos.getPosition();
						Vector2f positionDifference = new Vector2f(playerPosition.x - enemyPosition.x, playerPosition.y - enemyPosition.y);
						Vector2f positionCheck = new Vector2f(positionDifference);
						if (positionCheck.x < 0) {
							positionCheck.x = -positionCheck.x;
						}
						if (positionCheck.y < 0) {
							positionCheck.y = -positionCheck.y;
						}
						float range = enemyAI.getSight()*32;
						
						if (positionCheck.x < range && positionCheck.y < range) {
							if (ground != null) {
								enemyAI.pathAge--;
								String enemyResName = resourceMapper.get(enemy).getResourceName();
								Resource enemyRes = manager.getResource(enemyResName);
								Image enemyFrame = getFrame(enemyRes);
								int eh = enemyFrame.getHeight();
								int ew = enemyFrame.getWidth();
								String groundResName = resourceMapper.get(ground).getResourceName();
								Resource groundRes = manager.getResource(groundResName);
								TiledMap tiledMap = (TiledMap)groundRes.getObject();
								int tw = tiledMap.getTileWidth();
								int th = tiledMap.getTileHeight();
								CollisionMap map = new CollisionMap(tiledMap);
								AStarPathFinder pathFinder = new AStarPathFinder(map, (int)range, false);
								
								Path newPath = null;
								if (enemyAI.pathAge <= 0) {
									// Middle Path
									newPath = pathFinder.findPath(null, (int)(enemyPosition.x+ew/2-1)/tw, (int)(enemyPosition.y+eh/2-1)/th, (int)playerPosition.x/tw, (int)playerPosition.y/th);
									enemyAI.setPath(newPath);
									enemyAI.pathAge = 60;
									enemyAI.pathStep = 1;
								}
								
								Path path = enemyAI.getPath();
								
								if (path != null && path.getLength() > 1 && enemyAI.pathStep < path.getLength()) {
									Vector2f curEP = new Vector2f((int)(enemyPosition.x+ew/2-1)/tw, (int)(enemyPosition.y+eh/2-1)/th);									
									if ((int)curEP.x == path.getX(enemyAI.pathStep) && (int)curEP.y == path.getY(enemyAI.pathStep)) {
										if (enemyAI.pathStep < path.getLength()-2) {
											enemyAI.pathStep++;
										}
									}
									
									if (path.getX(enemyAI.pathStep) > (int)(enemyPosition.x+ew/2-1)/tw) {
										tmpVelocity.x += acceleration.x;
									} else if (path.getX(enemyAI.pathStep) < (int)(enemyPosition.x+ew/2-1)/tw) {
										tmpVelocity.x -= acceleration.x;
									}
									if (path.getY(enemyAI.pathStep) > (int)(enemyPosition.y+eh/2-1)/th) {
										tmpVelocity.y += acceleration.y;
									} else if (path.getY(enemyAI.pathStep) < (int)(enemyPosition.y+eh/2-1)/th) {
										tmpVelocity.y -= acceleration.y;
									}
								}
							}
						} else {
						
							switch ((int) (Math.random()*30)) {
								case 0: // up
									tmpVelocity.y -= acceleration.y;
									break;
								case 1: // down
									tmpVelocity.y += acceleration.x;
									break;
								case 2: // neither; tend toward zero
									if (tmpVelocity.y > 0) {
										tmpVelocity.y -= deceleration.y;
										if (tmpVelocity.y < 0) {
											tmpVelocity.y = 0;
										}
									} 
									else if (tmpVelocity.y < 0) {
										tmpVelocity.y += deceleration.y;
										if (tmpVelocity.y > 0) {
											tmpVelocity.y = 0;
										}
									}
							}
							switch ((int) (Math.random()*30)) {
								case 0: // left
									tmpVelocity.x -= acceleration.x;
									break;
								case 1: // right
									tmpVelocity.x += acceleration.x;
									break;
								case 2: //neither; tend toward zero
									if (tmpVelocity.x > 0) {
										tmpVelocity.x -= deceleration.x;
										if (tmpVelocity.x < 0) {
											tmpVelocity.x = 0;
										}
									}
									else if (tmpVelocity.x < 0) {
										tmpVelocity.x += deceleration.x;
										if (tmpVelocity.x > 0) {
											tmpVelocity.x = 0;
										}
									}
							}
						}
						
						if (tmpVelocity.distance(new Vector2f(0,0))>movement.getMaximumSpeed()) {
							float scale;
							// Scale the x and y velocity by the maximum-speed/current-velocity
							scale = (movement.getMaximumSpeed()/tmpVelocity.distance(new Vector2f(0,0)));
							tmpVelocity.x = scale*tmpVelocity.x;
							tmpVelocity.y = scale*tmpVelocity.y;
						}
						velocity.x = tmpVelocity.x;
						velocity.y = tmpVelocity.y;
						
						Direction direction = directionMapper.get(enemy);
						ArrayList<Dir> directions = direction.getDirections();
						if (movement.getCurrentSpeed() != 0) {
							directions.clear();
							if (velocity.x > 0) {
								directions.add(Dir.RIGHT);
							} else if (velocity.x < 0) {
								directions.add(Dir.LEFT);
							}
							if (velocity.y > 0) {
								directions.add(Dir.DOWN);
							} else if (velocity.y < 0) {
								directions.add(Dir.UP);
							}
						}
					}
				}
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
	
}
