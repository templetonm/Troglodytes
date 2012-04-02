package com.turbonips.troglodytes.systems;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.components.Attack;
import com.turbonips.troglodytes.components.Collision;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.PushVelocity;
import com.turbonips.troglodytes.components.Renderable;

public class PushSystem extends BaseEntitySystem {
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<PushVelocity> pushVelocityMapper;
	private ComponentMapper<Renderable> renderableMapper;
	private ComponentMapper<Collision> collisionMapper;

	public PushSystem() {
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		renderableMapper = new ComponentMapper<Renderable>(Renderable.class, world);
		pushVelocityMapper = new ComponentMapper<PushVelocity>(PushVelocity.class, world);
		collisionMapper = new ComponentMapper<Collision>(Collision.class, world);
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		for (int e=0; e<enemies.size(); e++) {
			Entity enemy = enemies.get(e);
			Position enemyPosition = positionMapper.get(enemy);
			PushVelocity pushVelocity = pushVelocityMapper.get(enemy);
			Collision collision = collisionMapper.get(enemy);
			if (pushVelocity != null) {
				if (pushVelocity.getVelocity().x == 0 &&
					pushVelocity.getVelocity().y == 0) {
					enemy.removeComponent(pushVelocity);
				} else {
					if (!collision.isCollidingLeft() && pushVelocity.getVelocity().x < 0 ||
						!collision.isCollidingRight() && pushVelocity.getVelocity().x > 0) {
						enemyPosition.setX(enemyPosition.getX() + pushVelocity.getVelocity().x);
					}

					if (!collision.isCollidingUp() && pushVelocity.getVelocity().y < 0 ||
						!collision.isCollidingDown() && pushVelocity.getVelocity().y > 0) {
						enemyPosition.setY(enemyPosition.getY() + pushVelocity.getVelocity().y);
					}
					
					if (pushVelocity.getVelocity().x > 0) {
						pushVelocity.setVelocity(new Vector2f(pushVelocity.getVelocity().x - 1, pushVelocity.getVelocity().y));
					} else if (pushVelocity.getVelocity().x < 0) {
						pushVelocity.setVelocity(new Vector2f(pushVelocity.getVelocity().x + 1, pushVelocity.getVelocity().y));
					}
					
					if (pushVelocity.getVelocity().y > 0) {
						pushVelocity.setVelocity(new Vector2f(pushVelocity.getVelocity().x, pushVelocity.getVelocity().y - 1));
					} else if (pushVelocity.getVelocity().y < 0) {
						pushVelocity.setVelocity(new Vector2f(pushVelocity.getVelocity().x, pushVelocity.getVelocity().y + 1));
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
