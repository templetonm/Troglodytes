package com.turbonips.troglodytes.systems;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.ResourceRef;

public class EnemyBehaviorSystem extends BaseEntitySystem
{
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<ResourceRef> resourceMapper;
	
	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
	}

	@Override
	protected boolean checkProcessing()
	{
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities)
	{
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> grounds = world.getGroupManager().getEntities("GROUND");
		ResourceManager manager = ResourceManager.getInstance();
		Entity ground = grounds.get(0);
		
		for (int i = 0; i < enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			Movement movement = movementMapper.get(enemy);
			Position position = positionMapper.get(enemy);
			Vector2f enemyPosition = position.getPosition();
			Vector2f enemyVelocity = movement.getVelocity();
			Vector2f newEnemyPosition = new Vector2f(enemyPosition);
			String enemyResName = resourceMapper.get(enemy).getResourceName();
			Resource enemyRes = manager.getResource(enemyResName);
			Image enemyFrame = getFrame(enemyRes);
			newEnemyPosition.add(enemyVelocity);
			
			// Collision detection
			if (ground != null) {
				String groundResName = resourceMapper.get(ground).getResourceName();
				Resource groundRes = manager.getResource(groundResName);
				TiledMap map = (TiledMap)groundRes.getObject();
				CollisionResolution collisionResolution = CollisionResolution.getInstance();
				Vector2f newPosition = collisionResolution.resolveWallCollisions(enemy, map);
				enemyPosition.set(newPosition);
			}
		}
	}

	private Image getFrame(Resource resource) {
		switch (resource.getType()) {
			case CREATURE_ANIMATION:
				return ((CreatureAnimation)resource.getObject()).getCurrent().getCurrentFrame();
			case IMAGE:
				return (Image)resource.getObject();
			default:
				return null;
		}
	}
}
