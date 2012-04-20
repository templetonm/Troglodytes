package com.turbonips.troglodytes.systems;

import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Attack;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.Stats;
import com.turbonips.troglodytes.components.Stats.StatType;

public class EnemyBehaviorSystem extends BaseEntitySystem
{
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<ResourceRef> resourceMapper;
	private ComponentMapper<Stats> statsMapper;
	private ComponentMapper<Attack> attackMapper;
	
	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
		statsMapper = new ComponentMapper<Stats>(Stats.class, world);
		attackMapper = new ComponentMapper<Attack>(Attack.class, world);
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
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
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
			
			checkAttacking(enemy,players.get(0));
		}
	}
	
	private void checkAttacking(Entity enemy, Entity player) {
		Vector2f enemyPosition = positionMapper.get(enemy).getPosition();
		String enemyResName = resourceMapper.get(enemy).getResourceName();
		ResourceManager manager = ResourceManager.getInstance();
		Resource enemyRes = manager.getResource(enemyResName);
		Image enemyFrame = getFrame(enemyRes);
		int eh = enemyFrame.getHeight();
		int ew = enemyFrame.getWidth();
		Vector2f enemyCenter = new Vector2f(enemyPosition.x + (ew / 2), enemyPosition.y + (eh / 2));
		int MAX_DISTANCE = 64;
		
		Vector2f playerPosition = positionMapper.get(player).getPosition();
		String playerResName = resourceMapper.get(player).getResourceName();
		Resource playerRes = manager.getResource(playerResName);
		Image playerFrame = getFrame(playerRes);
		int ph = playerFrame.getHeight();
		int pw = playerFrame.getWidth();
		Vector2f playerCenter = new Vector2f(playerPosition.x + (pw / 2), playerPosition.y + (ph / 2));
		
		HashMap<StatType, Integer> playerStats = statsMapper.get(player).getStats();
		int enemyDamage = attackMapper.get(enemy).getDamage();

		if (enemyCenter.distance(playerCenter) < MAX_DISTANCE) {
			playerStats.put(StatType.HEALTH, playerStats.get(StatType.HEALTH)-enemyDamage);
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
