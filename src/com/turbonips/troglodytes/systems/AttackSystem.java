package com.turbonips.troglodytes.systems;

import org.newdawn.slick.geom.Vector2f;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.Attack;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;

public class AttackSystem extends BaseEntitySystem {
	private ComponentMapper<Attack> attackMapper;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<Movement> movementMapper;

	public AttackSystem() {
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		attackMapper = new ComponentMapper<Attack>(Attack.class, world);
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
	}

	@Override
	protected boolean checkProcessing() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
			for (int p=0; p<players.size(); p++) {
				Entity player = players.get(p);
				
				Attack attack = attackMapper.get(players.get(p));
				if (attack != null) {
					if (attack.isAttacking()) {
						Position playerPosition = positionMapper.get(player);
						if (playerPosition != null) {
							for (int i=0; i<enemies.size(); i++) {
								Entity enemy = enemies.get(i);
								Position enemyPosition = positionMapper.get(enemy);
								if (enemyPosition != null) {
									if (enemyPosition.getPosition().distance(playerPosition.getPosition()) < 64) {
										Movement playerMovement = movementMapper.get(player);
										Vector2f deltaPosition = new Vector2f(playerPosition.getX() - enemyPosition.getX(), 
																			  playerPosition.getY() - enemyPosition.getY());
										logger.info(deltaPosition);

										if (playerMovement.isMoveLeft()) {
											if (deltaPosition.x > 0) {
												logger.info("ATTACKING LEFT");
												enemy.delete();
											}
										} else if (playerMovement.isMoveRight()) {
											if (deltaPosition.x < 0) {
												logger.info("ATTACKING RIGHT");
												enemy.delete();
											}
										} else if (playerMovement.isMoveUp()) {
											if (deltaPosition.y < 0) {
												logger.info("ATTACKING UP");
												enemy.delete();
											}
										} else if (playerMovement.isMoveDown()) {
											if (deltaPosition.y > 0) {
												logger.info("ATTACKING DOWN");
												enemy.delete();
											}
										}
										
										attack.setAttacking(false);
									}
								}
							}
						}
					}
				}
			}
		}
}
