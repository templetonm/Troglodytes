package com.turbonips.troglodytes.systems;

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
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Location;
import com.turbonips.troglodytes.systems.CollisionResolution.CollisionDirection;

public class EnemyAISystem extends BaseEntitySystem {
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Direction> directionMapper;
	private ComponentMapper<EnemyAI> enemyAIMapper;
	private ComponentMapper<Location> locationMapper;
	private ComponentMapper<ResourceRef> resourceMapper;

	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		directionMapper = new ComponentMapper<Direction>(Direction.class, world);
		enemyAIMapper = new ComponentMapper<EnemyAI>(EnemyAI.class, world);
		locationMapper = new ComponentMapper<Location>(Location.class, world);
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
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
			EnemyAI enemyAI = enemyAIMapper.get(enemy);
			Location enemyLocation = locationMapper.get(enemy);
			if (enemyLocation.getMap().equals(playerLocation.getMap())) {
				if (enemyAI != null) {
					Movement movement = movementMapper.get(enemy);
					if (movement != null) {
						Vector2f velocity = movement.getVelocity();
						Vector2f acceleration = movement.getAcceleration();
						Vector2f deceleration = movement.getDeceleration();
						Vector2f tmpVelocity = new Vector2f(velocity);
						AIType enemyAIType = enemyAI.getEnemyAIType();
						if (enemyAIType == AIType.DUMB) {
							switch ((int) (Math.random() * 30)) {
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
									} else if (tmpVelocity.y < 0) {
										tmpVelocity.y += deceleration.y;
										if (tmpVelocity.y > 0) {
											tmpVelocity.y = 0;
										}
									}
							}
							switch ((int) (Math.random() * 30)) {
								case 0: // left
									tmpVelocity.x -= acceleration.x;
									break;
								case 1: // right
									tmpVelocity.x += acceleration.x;
									break;
								case 2: // neither; tend toward zero
									if (tmpVelocity.x > 0) {
										tmpVelocity.x -= deceleration.x;
										if (tmpVelocity.x < 0) {
											tmpVelocity.x = 0;
										}
									} else if (tmpVelocity.x < 0) {
										tmpVelocity.x += deceleration.x;
										if (tmpVelocity.x > 0) {
											tmpVelocity.x = 0;
										}
									}
							}
							if (tmpVelocity.distance(new Vector2f(0, 0)) > movement.getMaximumSpeed()) {
								float scale;
								// Scale the x and y velocity by the maximum-speed/current-velocity
								scale = (movement.getMaximumSpeed() / tmpVelocity.distance(new Vector2f(0, 0)));
								tmpVelocity.x = scale * tmpVelocity.x;
								tmpVelocity.y = scale * tmpVelocity.y;
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
						} else if (enemyAIType == AIType.DUMBCHARGE) {
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
								switch ((int) (Math.random() * 30)) {
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
										} else if (tmpVelocity.y < 0) {
											tmpVelocity.y += deceleration.y;
											if (tmpVelocity.y > 0) {
												tmpVelocity.y = 0;
											}
										}
								}
								switch ((int) (Math.random() * 30)) {
									case 0: // left
										tmpVelocity.x -= acceleration.x;
										break;
									case 1: // right
										tmpVelocity.x += acceleration.x;
										break;
									case 2: // neither; tend toward zero
										if (tmpVelocity.x > 0) {
											tmpVelocity.x -= deceleration.x;
											if (tmpVelocity.x < 0) {
												tmpVelocity.x = 0;
											}
										} else if (tmpVelocity.x < 0) {
											tmpVelocity.x += deceleration.x;
											if (tmpVelocity.x > 0) {
												tmpVelocity.x = 0;
											}
										}
										break;
								}
	
							}
							if (tmpVelocity.distance(new Vector2f(0, 0)) > movement.getMaximumSpeed()) {
								if (tmpVelocity.x > 0) {
									tmpVelocity.x -= movement.getDeceleration().getX();
								} else {
									tmpVelocity.x += movement.getDeceleration().getX();
								}
								if (tmpVelocity.y > 0) {
									tmpVelocity.y -= movement.getDeceleration().getY();
								} else {
									tmpVelocity.y += movement.getDeceleration().getY();
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
						} else if (enemyAIType == AIType.DUMBFIND) {							
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
									CollisionDirection collisionDirection = collisionResolution.getCollisionDirection(enemy, tiledMap);
									if (collisionDirection == CollisionDirection.UP_LEFT) {
										enemyAI.corner = Corner.TOPLEFT;
									} else if (collisionDirection == CollisionDirection.UP) {
										if (enemyAI.corner == Corner.BOTTOMLEFT) {
											enemyAI.corner = Corner.TOPLEFT;
										} else if (enemyAI.corner == Corner.BOTTOMRIGHT) {
											enemyAI.corner = Corner.TOPRIGHT;
										}
									} else if (collisionDirection == CollisionDirection.UP_RIGHT) {
										enemyAI.corner = Corner.TOPRIGHT;
									} else if (collisionDirection == CollisionDirection.RIGHT) {
										if (enemyAI.corner == Corner.TOPLEFT) {
											enemyAI.corner = Corner.TOPRIGHT;
										} else if (enemyAI.corner == Corner.BOTTOMLEFT) {
											enemyAI.corner = Corner.BOTTOMRIGHT;
										}
									} else if (collisionDirection == CollisionDirection.DOWN_RIGHT) {
										enemyAI.corner = Corner.BOTTOMRIGHT;
									} else if (collisionDirection == CollisionDirection.DOWN) {
										if (enemyAI.corner == Corner.TOPLEFT) {
											enemyAI.corner = Corner.BOTTOMLEFT;
										} else if (enemyAI.corner == Corner.TOPRIGHT) {
											enemyAI.corner = Corner.BOTTOMRIGHT;
										}
									} else if (collisionDirection == CollisionDirection.DOWN_LEFT) {
										enemyAI.corner = Corner.BOTTOMLEFT;
									} else if (collisionDirection == CollisionDirection.LEFT) {
										if (enemyAI.corner == Corner.BOTTOMRIGHT) {
											enemyAI.corner = Corner.BOTTOMLEFT;
										} else if (enemyAI.corner == Corner.TOPRIGHT) {
											enemyAI.corner = Corner.TOPLEFT;
										}
									}
									
									Path newPath = null;
									if (enemyAI.pathAge <= 0) {
										// Top Left Path : Where the path is drawn from doesn't matter that much. 
										newPath = pathFinder.findPath(null, (int) (enemyPosition.x) / tw, (int) (enemyPosition.y) / th, (int) playerPosition.x / tw, (int) playerPosition.y / th);
										enemyAI.setPath(newPath);
										enemyAI.pathAge = 60;
										enemyAI.pathStep = 1;
									}
	
									Path path = enemyAI.getPath();

									if (path != null && path.getLength() > 2 && enemyAI.pathStep < path.getLength()) {
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

										if (collisionDirection == CollisionDirection.NONE) {
											if (path.getX(enemyAI.pathStep) > (int)curEP.x) {
												tmpVelocity.x += acceleration.x;
											} else if (path.getX(enemyAI.pathStep) < (int)curEP.x) {
												tmpVelocity.x -= acceleration.x;
											} else {
												if (tmpVelocity.x > 0) {
													tmpVelocity.x -= deceleration.x;
													if (tmpVelocity.x < 0) {
														tmpVelocity.x = 0;
													}
												} else if (tmpVelocity.x < 0) {
													tmpVelocity.x += deceleration.x;
													if (tmpVelocity.x > 0) {
														tmpVelocity.x = 0;
													}
												}
											}
											if (path.getY(enemyAI.pathStep) > (int)curEP.y) {
												tmpVelocity.y += acceleration.y;
											} else if (path.getY(enemyAI.pathStep) < (int)curEP.y) {
												tmpVelocity.y -= acceleration.y;
											} else {
												if (tmpVelocity.y > 0) {
													tmpVelocity.y -= deceleration.y;
													if (tmpVelocity.y < 0) {
														tmpVelocity.y = 0;
													}
												} else if (tmpVelocity.y < 0) {
													tmpVelocity.y += deceleration.y;
													if (tmpVelocity.y > 0) {
														tmpVelocity.y = 0;
													}
												}
											}
										} else {
											if (collisionDirection == CollisionDirection.DOWN || collisionDirection == CollisionDirection.UP) {
												int y = path.getY(enemyAI.pathStep);
												int x = path.getX(enemyAI.pathStep);
												if (collisionMap.blocked(null, x+1, y)) {
													// go left
													tmpVelocity.x -= acceleration.x;
												} else {
													// go right
													tmpVelocity.x += acceleration.x;
												}
												tmpVelocity.y = 0;
											} else if (collisionDirection == CollisionDirection.LEFT || collisionDirection == CollisionDirection.RIGHT) {
												int y = path.getY(enemyAI.pathStep);
												int x = path.getX(enemyAI.pathStep);
												if (collisionMap.blocked(null, x, y+1)) {
													// go up
													tmpVelocity.y -= acceleration.y;
												} else {
													// go down
													tmpVelocity.y += acceleration.y;
												}
												tmpVelocity.x = 0;
											}
											logger.info("BLOCKED:" + collisionDirection + " - EP:" + curEP
													 + " - p:(" + path.getX(enemyAI.pathStep) + ", " + path.getY(enemyAI.pathStep) + ")");
											collisionDirection = CollisionDirection.NONE;
										}
									} else {
										// Stop moving so much if path is at an end.
										if (tmpVelocity.y > 0) {
											tmpVelocity.y -= deceleration.y;
											if (tmpVelocity.y < 0) {
												tmpVelocity.y = 0;
											}
										} else if (tmpVelocity.y < 0) {
											tmpVelocity.y += deceleration.y;
											if (tmpVelocity.y > 0) {
												tmpVelocity.y = 0;
											}
										}
									}
								}
							} else {
	
								switch ((int) (Math.random() * 30)) {
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
										} else if (tmpVelocity.y < 0) {
											tmpVelocity.y += deceleration.y;
											if (tmpVelocity.y > 0) {
												tmpVelocity.y = 0;
											}
										}
								}
								switch ((int) (Math.random() * 30)) {
									case 0: // left
										tmpVelocity.x -= acceleration.x;
										break;
									case 1: // right
										tmpVelocity.x += acceleration.x;
										break;
									case 2: // neither; tend toward zero
										if (tmpVelocity.x > 0) {
											tmpVelocity.x -= deceleration.x;
											if (tmpVelocity.x < 0) {
												tmpVelocity.x = 0;
											}
										} else if (tmpVelocity.x < 0) {
											tmpVelocity.x += deceleration.x;
											if (tmpVelocity.x > 0) {
												tmpVelocity.x = 0;
											}
										}
								}
							}
	
							if (tmpVelocity.distance(new Vector2f(0, 0)) > movement.getMaximumSpeed()) {
								if (tmpVelocity.x > 0) {
									tmpVelocity.x -= movement.getDeceleration().getX();
								} else {
									tmpVelocity.x += movement.getDeceleration().getX();
								}
								if (tmpVelocity.y > 0) {
									tmpVelocity.y -= movement.getDeceleration().getY();
								} else {
									tmpVelocity.y += movement.getDeceleration().getY();
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