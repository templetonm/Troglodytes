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

	@Override
	protected void processEntities(ImmutableBag<Entity> e) {
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> maps = world.getGroupManager().getEntities("MAP");
		
		ResourceManager manager = ResourceManager.getInstance();
		Entity player = players.get(0);
		Entity map = maps.get(0);
		Location playerLocation = locationMapper.get(player);

		for (int i = 0; i < enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			HashMap<StatType, Integer> enemyStats = statsMapper.get(enemy).getStats();
			EnemyAI enemyAI = enemyAIMapper.get(enemy);
			Location enemyLocation = locationMapper.get(enemy);
			if (enemyLocation.getMap().equals(playerLocation.getMap())) {
				if (enemyAI != null) {
					Movement movement = movementMapper.get(enemy);
					if (movement != null) {
						Vector2f velocity = movement.getVelocity();
						int acceleration = enemyStats.get(StatType.ACCELERATION);
						int deceleration = enemyStats.get(StatType.DECELERATION);
						int maxSpeed = enemyStats.get(StatType.MAX_SPEED);
						Vector2f tmpVelocity = new Vector2f(velocity);
						AIType enemyAIType = enemyAI.getEnemyAIType();
						if (enemyAIType == AIType.DUMBFIND) {
							Vector2f playerPosition = playerLocation.getPosition();
							Vector2f enemyPosition = enemyLocation.getPosition();
							Vector2f positionDifference = new Vector2f(playerPosition.x - enemyPosition.x, playerPosition.y - enemyPosition.y);
							Vector2f positionCheck = new Vector2f(positionDifference);
							if (positionCheck.x < 0) {
								positionCheck.x = -positionCheck.x;
							}
							if (positionCheck.y < 0) {
								positionCheck.y = -positionCheck.y;
							}
							float range = enemyAI.getSight() * 32;

							if (positionCheck.x < range && positionCheck.y < range) {
								if (map != null) {
									enemyAI.pathAge--;
									String enemyResName = resourceMapper.get(enemy).getResourceName();
									Resource enemyRes = manager.getResource(enemyResName);
									Image enemyFrame = getFrame(enemyRes);
									int eh = enemyFrame.getHeight();
									int ew = enemyFrame.getWidth();
									String groundResName = resourceMapper.get(map).getResourceName();
									Resource groundRes = manager.getResource(groundResName);
									TiledMap tiledMap = (TiledMap) groundRes.getObject();
									int tw = tiledMap.getTileWidth();
									int th = tiledMap.getTileHeight();
									CollisionMap collisionMap = new CollisionMap(tiledMap);
									AStarPathFinder pathFinder = new AStarPathFinder(collisionMap, (int) range, false);

									CollisionResolution collisionResolution = CollisionResolution.getInstance();

									Path newPath = null;
									if (enemyAI.pathAge <= 0) {
										// Top Left Path : Where the path is drawn from doesn't matter that much. 
										newPath = pathFinder.findPath(null, (int) (enemyPosition.x) / tw, (int) (enemyPosition.y) / th, (int) playerPosition.x / tw, (int) playerPosition.y / th);
										enemyAI.setPath(newPath);
										enemyAI.pathAge = 60;
										enemyAI.pathStep = 1;
									}

									Path path = enemyAI.getPath();

									if (path != null && path.getLength() > 1 && enemyAI.pathStep < path.getLength()-1) {
										Vector2f curEP;
										if (enemyAI.corner == Corner.TOPLEFT) {
											curEP = new Vector2f((int) (enemyPosition.x / tw), (int) (enemyPosition.y / th));
										} else if (enemyAI.corner == Corner.TOPRIGHT) {
											curEP = new Vector2f((int) ((enemyPosition.x + ew-1) / tw), (int) (enemyPosition.y / th));
										} else if (enemyAI.corner == Corner.BOTTOMLEFT) {
											curEP = new Vector2f((int) (enemyPosition.x / tw), (int) ((enemyPosition.y + eh-1) / th));
										} else {
											curEP = new Vector2f((int) ((enemyPosition.x + ew-1) / tw), (int) ((enemyPosition.y + eh-1) / th));
										}

										if ((int) curEP.x == path.getX(enemyAI.pathStep) && (int) curEP.y == path.getY(enemyAI.pathStep)) {
											if (enemyAI.pathStep < path.getLength() - 2) {
												enemyAI.pathStep++;
											}
										}
										
											int lookahead = 0;
											for (int j = 0; j < 3; j++) {
												if (path.getLength() > enemyAI.pathStep+j+1) {
													if (path.getX(enemyAI.pathStep+j-1) != path.getX(enemyAI.pathStep+j)) {
														if (!collisionMap.blocked(null, path.getX(enemyAI.pathStep+j), path.getY(enemyAI.pathStep+j)+1) &&
															!collisionMap.blocked(null, path.getX(enemyAI.pathStep+j), path.getY(enemyAI.pathStep+j)-1)){
															lookahead = j+1;
														} else {
															break;
														}
													} else {
														if (!collisionMap.blocked(null, path.getX(enemyAI.pathStep+j)+1, path.getY(enemyAI.pathStep+j)) &&
															!collisionMap.blocked(null, path.getX(enemyAI.pathStep+j)-1, path.getY(enemyAI.pathStep+j))){
															lookahead = j+1;
														} else {
															break;
														}
													}
												} else {
													break;
												}
											}
											
											if (path.getX(enemyAI.pathStep+lookahead) > (int)curEP.x) {
												tmpVelocity.x += acceleration;
											} else if (path.getX(enemyAI.pathStep+lookahead) < (int)curEP.x) {
												tmpVelocity.x -= acceleration;
											} else {
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
											if (path.getY(enemyAI.pathStep+lookahead) > (int)curEP.y) {
												tmpVelocity.y += acceleration;
											} else if (path.getY(enemyAI.pathStep+lookahead) < (int)curEP.y) {
												tmpVelocity.y -= acceleration;
											} else {
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
											if (tmpVelocity.distance(new Vector2f(0, 0)) > maxSpeed) {
												if (tmpVelocity.x > 0) {
													tmpVelocity.x -= deceleration;
												} else {
													tmpVelocity.x += deceleration;
												}
												if (tmpVelocity.y > 0) {
													tmpVelocity.y -= deceleration;
												} else {
													tmpVelocity.y += deceleration;
												}
											}
											
											velocity.x = tmpVelocity.x;
											velocity.y = tmpVelocity.y;
											CollisionDirection collisionDirection = collisionResolution.getCollisionDirection(enemy, tiledMap);
											if (collisionDirection == CollisionDirection.DOWN || collisionDirection == CollisionDirection.UP) {
												int xNext = path.getX(enemyAI.pathStep);
												if (enemyPosition.x < xNext) {
													tmpVelocity.x += maxSpeed;
													if (tmpVelocity.distance(new Vector2f(0, 0)) > maxSpeed) {
														tmpVelocity.x = maxSpeed;
													}
													tmpVelocity.y = 0;
												} else {
													tmpVelocity.x -= acceleration;
													if (tmpVelocity.distance(new Vector2f(0, 0)) > maxSpeed) {
														tmpVelocity.x = -maxSpeed;
													}
													tmpVelocity.y = 0;
												}
											} else if (collisionDirection == CollisionDirection.LEFT || collisionDirection == CollisionDirection.RIGHT) {
												int yNext = path.getY(enemyAI.pathStep);
												if (enemyPosition.y < yNext) {
													tmpVelocity.y += acceleration;
													if (tmpVelocity.distance(new Vector2f(0, 0)) > maxSpeed) {
														tmpVelocity.y = maxSpeed;
													}
													tmpVelocity.x = 0;
												} else {
													tmpVelocity.y -= acceleration;
													logger.info(acceleration);
													if (tmpVelocity.distance(new Vector2f(0, 0)) > maxSpeed) {
														tmpVelocity.y = -maxSpeed;
													}
													tmpVelocity.x = 0;
												}
											} else if (collisionDirection != CollisionDirection.NONE) {
												logger.info("RJ pretty sure bout dis");
												/*int yNext = path.getY(enemyAI.pathStep);
												if (enemyPosition.y + eh >= yNext + th) {
													tmpVelocity.y -= acceleration;
													if (tmpVelocity.distance(new Vector2f(0, 0)) > maxSpeed) {
														tmpVelocity.y = -maxSpeed;
														tmpVelocity.x = 0;
													}
												} else {
													tmpVelocity.y += acceleration;
													if (tmpVelocity.distance(new Vector2f(0, 0)) > maxSpeed) {
														tmpVelocity.y = maxSpeed;
														tmpVelocity.x = 0;
													}
												}
												int xNext = path.getX(enemyAI.pathStep);
												if (enemyPosition.x + ew >= xNext + tw) {
													tmpVelocity.x -= acceleration;
													if (tmpVelocity.distance(new Vector2f(0, 0)) > maxSpeed) {
														tmpVelocity.x = -maxSpeed;
														tmpVelocity.y = 0;
													}
												} else {
													tmpVelocity.x += acceleration;
													if (tmpVelocity.distance(new Vector2f(0, 0)) > maxSpeed) {
														tmpVelocity.x = maxSpeed;
														tmpVelocity.y = 0;
													}
												}*/
											}
											logger.info("AFTER " + tmpVelocity + " " + collisionDirection);											
									} else {
										// DO SOMETHING IF WE DON'T HAVE A PATH
									}
								}
							// Random walk
							} else {
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
								switch ((int) (Math.random() * 30)) {
									case 0: // left
										tmpVelocity.x -= acceleration;
										break;
									case 1: // right
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
							}
							
							if (tmpVelocity.distance(new Vector2f(0, 0)) > maxSpeed) {
								if (tmpVelocity.x > 0) {
									tmpVelocity.x -= deceleration;
								} else {
									tmpVelocity.x += deceleration;
								}
								if (tmpVelocity.y > 0) {
									tmpVelocity.y -= deceleration;
								} else {
									tmpVelocity.y += deceleration;
								}
							}
							velocity.x = tmpVelocity.x;
							velocity.y = tmpVelocity.y;

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
					}
				}
			}
		}
	}

	private Image getFrame(Resource resource) {
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