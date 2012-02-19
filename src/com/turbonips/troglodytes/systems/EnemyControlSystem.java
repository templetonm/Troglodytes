package com.turbonips.troglodytes.systems;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.Collision;
import com.turbonips.troglodytes.components.Movement;

public class EnemyControlSystem extends BaseEntitySystem {
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Collision> collisionMapper;
	
	public EnemyControlSystem() {
	}
	
	
	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		collisionMapper = new ComponentMapper<Collision>(Collision.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		
		for (int i=0; i<enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			Movement movement = movementMapper.get(enemy);
			Collision collision = collisionMapper.get(enemy);
			
			if (movement != null) {
				
				switch ((int)(Math.random()*40)) {
					case 0:
						movement.clearMovement();
						movement.setMoveUp(true);
						break;
					case 1:
						movement.clearMovement();
						movement.setMoveDown(true);
						break;
					case 2:
						movement.clearMovement();
						movement.setMoveLeft(true);
						break;
					case 3:
						movement.clearMovement();
						movement.setMoveRight(true);
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

	@Override
	protected boolean checkProcessing() {
		return true;
	}

}
