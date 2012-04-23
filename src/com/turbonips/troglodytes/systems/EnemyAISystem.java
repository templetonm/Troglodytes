package com.turbonips.troglodytes.systems;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.geom.Vector2f;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.EnemyAI;
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
	
	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement> (Movement.class, world);
		directionMapper = new ComponentMapper<Direction> (Direction.class, world);
		enemyAIMapper = new ComponentMapper<EnemyAI> (EnemyAI.class, world);
		positionMapper = new ComponentMapper<Position> (Position.class, world);
	}
	
	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> e) {
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		Entity player = players.get(0);
		
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
					} else if (enemyAIType == AIType.DUMBANDCHARGE) {
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
