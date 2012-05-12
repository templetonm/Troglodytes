package com.turbonips.troglodytes.systems;

import java.util.Date;
import java.util.HashMap;

import org.newdawn.slick.Color;
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
import com.turbonips.troglodytes.components.ColorChange;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Location;
import com.turbonips.troglodytes.components.ParticleComponent;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.Stats;
import com.turbonips.troglodytes.components.Stats.StatType;

public class EnemyBehaviorSystem extends BaseEntitySystem {
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Location> locationMapper;
	private ComponentMapper<ResourceRef> resourceMapper;
	private ComponentMapper<Stats> statsMapper;
	private ComponentMapper<Attack> attackMapper;
	private ComponentMapper<ColorChange> colorChangeMapper;
	private ComponentMapper<ParticleComponent> particleComponentMapper;

	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		locationMapper = new ComponentMapper<Location>(Location.class, world);
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
		statsMapper = new ComponentMapper<Stats>(Stats.class, world);
		attackMapper = new ComponentMapper<Attack>(Attack.class, world);
		colorChangeMapper = new ComponentMapper<ColorChange>(ColorChange.class, world);
		particleComponentMapper = new ComponentMapper<ParticleComponent>(ParticleComponent.class, world);
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> enemyDeaths = world.getGroupManager().getEntities("ENEMY_DEATH");
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> maps = world.getGroupManager().getEntities("MAP");
		ResourceManager manager = ResourceManager.getInstance();
		Entity map = maps.get(0);
		Location playerLocation = locationMapper.get(players.get(0));

		// Clean up any finished enemyDeath entities.
		for (int i = 0; i < enemyDeaths.size(); i++) {
			Entity enemyDeath = enemyDeaths.get(i);
			ParticleComponent pc = particleComponentMapper.get(enemyDeath);
			if (pc.getEmitterFinished()) {
				enemyDeath.delete();
			}
		}
		
		for (int i = 0; i < enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			Movement movement = movementMapper.get(enemy);
			Location enemyLocation = locationMapper.get(enemy);
			if (playerLocation.getMap().equals(enemyLocation.getMap())) {
				Vector2f enemyPosition = enemyLocation.getPosition();
				Vector2f enemyVelocity = movement.getVelocity();
				Vector2f newEnemyPosition = new Vector2f(enemyPosition);
				newEnemyPosition.add(enemyVelocity);
	
				// Collision detection
				if (map != null) {
					String groundResName = resourceMapper.get(map).getResourceName();
					Resource groundRes = manager.getResource(groundResName);
					TiledMap tiledMap = (TiledMap)groundRes.getObject();
					CollisionResolution collisionResolution = CollisionResolution.getInstance();
					Vector2f newPosition = collisionResolution.resolveWallCollisions(enemy, tiledMap);
					enemyPosition.set(newPosition);
				}
	
				checkAttacking(enemy, players.get(0));
			}
		}
	}

	private void checkAttacking(Entity enemy, Entity player) {
		// Enemy cooldown
		Attack enemyAttack = attackMapper.get(enemy);
		Vector2f enemyPosition = locationMapper.get(enemy).getPosition();
		String enemyResName = resourceMapper.get(enemy).getResourceName();
		ResourceManager manager = ResourceManager.getInstance();
		Resource enemyRes = manager.getResource(enemyResName);
		Image enemyFrame = getFrame(enemyRes);
		int eh = enemyFrame.getHeight();
		int ew = enemyFrame.getWidth();
		Vector2f enemyCenter = new Vector2f(enemyPosition.x + (ew / 2), enemyPosition.y + (eh / 2));

		Vector2f playerPosition = locationMapper.get(player).getPosition();
		String playerResName = resourceMapper.get(player).getResourceName();
		Resource playerRes = manager.getResource(playerResName);
		Image playerFrame = getFrame(playerRes);
		int ph = playerFrame.getHeight();
		int pw = playerFrame.getWidth();
		Vector2f playerCenter = new Vector2f(playerPosition.x + (pw / 2), playerPosition.y + (ph / 2));

		HashMap<StatType, Integer> playerStats = statsMapper.get(player).getStats();
		HashMap<StatType, Integer> enemyStats = statsMapper.get(enemy).getStats();
		int enemyDamage = enemyStats.get(StatType.DAMAGE);
		int enemyRange = enemyStats.get(StatType.RANGE);
		int enemyCooldown = enemyStats.get(StatType.ATTACK_COOLDOWN);
		int playerArmor = playerStats.get(StatType.ARMOR);
		int damageReduction = playerArmor/5; 

		if (enemyCenter.distance(playerCenter) < enemyRange*32) {
			if (new Date().getTime()-enemyAttack.getLastTime() > enemyCooldown) {
				enemyAttack.setLastTime(new Date().getTime());
				
				if (damageReduction < enemyDamage) {
					playerStats.put(StatType.HEALTH, playerStats.get(StatType.HEALTH) - enemyDamage + damageReduction);
				}
				if (colorChangeMapper.get(player) == null) {
					ColorChange colorChange = new ColorChange(250, new Color(255,0,0));
					colorChange.setLastTime(new Date().getTime());
					player.addComponent(colorChange);
				}
			}
		}
	}

	private Image getFrame(Resource resource) {
		switch (resource.getType()) {
			case CREATURE_ANIMATION:
				return ((CreatureAnimation) resource.getObject()).getCurrent().getCurrentFrame();
			case IMAGE:
				return (Image) resource.getObject();
			default:
				return null;
		}
	}
}
