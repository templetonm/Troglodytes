package com.turbonips.troglodytes.systems;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.Collision;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.PushVelocity;

public class EnemyControlSystem extends BaseEntitySystem {
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Collision> collisionMapper;
	private ComponentMapper<PushVelocity> pushMapper;
	
	public EnemyControlSystem() {
	}
	
	
	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		collisionMapper = new ComponentMapper<Collision>(Collision.class, world);
		pushMapper = new ComponentMapper<PushVelocity>(PushVelocity.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		
		for (int i=0; i<enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			if (pushMapper.get(enemy) == null) {
			Movement movement = movementMapper.get(enemy);
			Collision collision = collisionMapper.get(enemy);
			
			if (movement != null) {
				
				switch ((int)(Math.random()*30)) {
					case 0:
						movement.clearMovement();
						movement.setMoveLeft(true);
						break;
					case 1:
						movement.clearMovement();
						movement.setMoveRight(true);
						break;
					case 2:
						movement.clearMovement();
						break;
				}
				switch ((int)(Math.random()*30)) {
					case 0:
						movement.setMoveUp(true);
						break;
					case 1:
						movement.setMoveDown(true);
						break;
					case 2:
						break;
				}
				
				if (collision != null) {
					if (movement.isMoveRight()) {
						if (collision.isCollidingRight()) {
							movement.setMoveRight(false);
						}
					}
					if (movement.isMoveLeft()) {
						if (collision.isCollidingLeft()) {
							movement.setMoveLeft(false);
						}
					}
					if (movement.isMoveUp()) {
						if (collision.isCollidingUp()) {
							movement.setMoveUp(false);
						}
					}
					if (movement.isMoveDown()) {
						if (collision.isCollidingDown()) {
							movement.setMoveDown(false);
						}
					}
				}
			}
			}
		}
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

}
