package com.turbonips.troglodytes.systems;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.components.Attack;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.Renderable;

public class AttackSystem extends BaseEntitySystem {
	private ComponentMapper<Attack> attackMapper;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Renderable> renderableMapper;

	public AttackSystem() {
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		attackMapper = new ComponentMapper<Attack>(Attack.class, world);
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		renderableMapper = new ComponentMapper<Renderable>(Renderable.class, world);
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
				Resource playerResource = null;
				if (renderableMapper.get(players.get(p)) != null) {
					 playerResource = renderableMapper.get(players.get(p)).getResource();
				
				Image playerImage = getImage(player);
				Position playerPosition = positionMapper.get(player);
				Vector2f playerCenter = new Vector2f(playerPosition.getPosition().x+playerImage.getWidth()/2 - 1,
													 playerPosition.getPosition().y+playerImage.getHeight()/2 - 1);
				
				if (attack != null) {
					if (attack.isAttacking()) {
							for (int i=0; i<enemies.size(); i++) {
								Entity enemy = enemies.get(i);
								Image enemyImage = getImage(enemy);
								Position enemyPosition = positionMapper.get(enemy);
								Vector2f enemyCenter = new Vector2f(enemyPosition.getPosition().x+enemyImage.getWidth()/2 - 1,
																	enemyPosition.getPosition().y+enemyImage.getHeight()/2 - 1);
								
								if (enemyPosition != null) {									
									if (enemyCenter.distance(playerCenter) < 128) {
										Movement playerMovement = movementMapper.get(player);
										Vector2f deltaPosition = new Vector2f(playerCenter.getX() - enemyCenter.getX(), 
																			  playerCenter.getY() - enemyCenter.getY());
										CreatureAnimation playerAnimation = (CreatureAnimation)playerResource.getObject();
										
										if (playerMovement.isMoveLeft() || playerMovement.isIdleLeft(playerAnimation)) {
											if (deltaPosition.x >= (playerImage.getWidth()+enemyImage.getWidth())/2) {
												logger.info("ATTACKING LEFT");
												enemyPosition.setX(enemyPosition.getX()-100);
											}
										} else if (playerMovement.isMoveRight() || playerMovement.isIdleRight(playerAnimation)) {
											if (deltaPosition.x <= (playerImage.getWidth()+enemyImage.getWidth())/-2) {
												logger.info("ATTACKING RIGHT");
												enemyPosition.setX(enemyPosition.getX()+100);
											}
										} else if (playerMovement.isMoveUp() || playerMovement.isIdleUp(playerAnimation)) {
											logger.info(deltaPosition.y + " >= " + (playerImage.getHeight()+enemyImage.getHeight())/2);
											if (deltaPosition.y >= (playerImage.getHeight()+enemyImage.getHeight())/2) {
												logger.info("ATTACKING UP");
												enemyPosition.setY(enemyPosition.getY()-100);
											}
										} else if (playerMovement.isMoveDown() || playerMovement.isIdleDown(playerAnimation)) {
											if (deltaPosition.y <= (playerImage.getHeight()+enemyImage.getHeight())/-2) {
												logger.info("ATTACKING DOWN");
												enemyPosition.setY(enemyPosition.getY()+100);
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
	
	private Image getImage(Entity entity) {
		Resource resource = renderableMapper.get(entity).getResource();
		Image sprite = null;

		if (resource != null) {
			switch (resource.getType()) {
				case CREATURE_ANIMATION:
					sprite = ((CreatureAnimation) resource
							.getObject()).getIdleDown()
							.getCurrentFrame();
					break;
				case IMAGE:
					sprite = (Image) resource.getObject();
					break;
				default:
					logger.error("player resource type is " + resource.getType());
					break;
			}
		}
		
		return sprite;
	}
}
