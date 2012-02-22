package com.turbonips.troglodytes.systems;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.Resource.ResourceType;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.Renderable;
import com.turbonips.troglodytes.components.Sliding;

public class MovementSystem extends BaseEntitySystem {
    private ComponentMapper<Position> positionMapper;
    private ComponentMapper<Sliding> slidingMapper;
	private ComponentMapper<Renderable> renderableMapper;
	private ComponentMapper<Movement> movementMapper;
	
	public MovementSystem() {
		super(Position.class);
	}
	
	@Override
	protected void initialize() {
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		slidingMapper = new ComponentMapper<Sliding>(Sliding.class, world);
		renderableMapper = new ComponentMapper<Renderable>(Renderable.class, world);
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
	}
	

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");

		for (int p=0; p<players.size(); p++) {
			Entity player = players.get(p);
			Movement playerMovement = movementMapper.get(player);
			Renderable playerRenderable = renderableMapper.get(player);
			
			if (playerRenderable != null) {
				Resource playerResource = playerRenderable.getResource();
				
				// Update the player animation
				if (playerMovement != null) {
					if (playerResource != null) {
						if (playerResource.getType() == ResourceType.CREATURE_ANIMATION) {
							CreatureAnimation creatureAnimation = (CreatureAnimation)playerResource.getObject();
							if (playerMovement.isMoveUp()) {
								playerMovement.setAnimation(creatureAnimation.getMoveUp());
							} else if (playerMovement.isMoveDown()) {
								playerMovement.setAnimation(creatureAnimation.getMoveDown());
							} 
							if (playerMovement.isMoveLeft()) {
								playerMovement.setAnimation(creatureAnimation.getMoveLeft());
							} else if (playerMovement.isMoveRight()) {
								playerMovement.setAnimation(creatureAnimation.getMoveRight());
							}
							
							if (!playerMovement.isMoving()) {
								playerMovement.setIdle(creatureAnimation);
							}
						}
						
					}
				}
				
				// Transform all player entities for player movement
				if (playerMovement != null && entities != null) {
					for (int i = 0; i < players.size(); i++) {
						Entity entity = players.get(i);
						Position entityPosition = positionMapper.get(entity);
						Sliding entitySliding = slidingMapper.get(entity);
						
						if (entityPosition != null) {
							if (playerMovement.isMoveUp()) {
								entityPosition.setY(entityPosition.getY()-entityPosition.getSpeed());
								if (entitySliding != null) {
									if (entitySliding.getY() < entitySliding.getBox().getHeight()) {
										entitySliding.setY(entitySliding.getY() + entitySliding.getSpeed());
									}
								}
							} else if (playerMovement.isMoveDown()) {
								entityPosition.setY(entityPosition.getY()+entityPosition.getSpeed());
								if (entitySliding != null) {
									if (entitySliding.getY() > entitySliding.getBox().getY()) {
										entitySliding.setY(entitySliding.getY() - entitySliding.getSpeed());
									}
								}
							}
							if (playerMovement.isMoveLeft()) {
								entityPosition.setX(entityPosition.getX()-entityPosition.getSpeed());
								if (entitySliding != null) {
									if (entitySliding.getX() < entitySliding.getBox().getHeight()) {
										entitySliding.setX(entitySliding.getX() + entitySliding.getSpeed());
									}
								}
							} else if (playerMovement.isMoveRight()) {
								entityPosition.setX(entityPosition.getX()+entityPosition.getSpeed());
								if (entitySliding != null) {
									if (entitySliding.getX() > entitySliding.getBox().getX()) {
										entitySliding.setX(entitySliding.getX() - entitySliding.getSpeed());
									}
								}
							}
						}
					}
				}
			}
		}
		
		// Move and animate enemies
		for (int i=0; i<enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			Movement enemyMovement = movementMapper.get(enemy);
			Position enemyPosition = positionMapper.get(enemy);
			if (enemyMovement != null && enemyPosition != null) {
				Resource enemyResource = renderableMapper.get(enemy).getResource();
				CreatureAnimation enemyAnimation = null;
				if (enemyResource.getType() == ResourceType.CREATURE_ANIMATION) {
					enemyAnimation = (CreatureAnimation)enemyResource.getObject();
				}
				if (enemyMovement.isMoveUp()) {
					enemyPosition.setY(enemyPosition.getY()-enemyPosition.getSpeed());
					if (enemyAnimation != null) { 
						enemyMovement.setAnimation(enemyAnimation.getMoveUp());
					}
				} else if (enemyMovement.isMoveDown()) {
					enemyPosition.setY(enemyPosition.getY()+enemyPosition.getSpeed());
					if (enemyAnimation != null) { 
						enemyMovement.setAnimation(enemyAnimation.getMoveDown());
					}
				}
				if (enemyMovement.isMoveLeft()) {
					enemyPosition.setX(enemyPosition.getX()-enemyPosition.getSpeed());
					if (enemyAnimation != null) { 
						enemyMovement.setAnimation(enemyAnimation.getMoveLeft());
					}
				} else if (enemyMovement.isMoveRight()) {
					enemyPosition.setX(enemyPosition.getX()+enemyPosition.getSpeed());
					if (enemyAnimation != null) { 
						enemyMovement.setAnimation(enemyAnimation.getMoveRight());
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
