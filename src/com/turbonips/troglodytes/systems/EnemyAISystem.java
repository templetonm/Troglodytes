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

public class EnemyAISystem extends BaseEntitySystem
{
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Direction> directionMapper;
	private ComponentMapper<EnemyAI> enemyAIMapper;
	
	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement> (Movement.class, world);
		directionMapper = new ComponentMapper<Direction> (Direction.class, world);
		enemyAIMapper = new ComponentMapper<EnemyAI> (EnemyAI.class, world);
	}
	
	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> e) {
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		
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
					AIType enemyAIType = enemyAI.getAIType();
					
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
					}
					
				}
			}
		}
	}

}
