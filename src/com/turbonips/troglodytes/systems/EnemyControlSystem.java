package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.SubPosition;

public class EnemyControlSystem extends BaseEntitySystem {
    private ComponentMapper<SubPosition> subPositionMapper;
	private ComponentMapper<Movement> movementMapper;
	private GameContainer container;
	
	public EnemyControlSystem(GameContainer container) {
		this.container = container;
	}
	
	
	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		subPositionMapper = new ComponentMapper<SubPosition>(SubPosition.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		
		for (int i=0; i<enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			Movement movement = movementMapper.get(enemy);
			
			if (movement != null) {
				
				switch ((int)(Math.random()*20)) {
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
			}
		}
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

}
