package com.turbonips.troglodytes.systems;

import java.util.Date;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Emitter;
import com.turbonips.troglodytes.ParticleData;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.XMLSerializer;
import com.turbonips.troglodytes.components.Attack;
import com.turbonips.troglodytes.components.ColorChange;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.HealthRegen;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Location;
import com.turbonips.troglodytes.components.ParticleComponent;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.Secondary;
import com.turbonips.troglodytes.components.Stats;
import com.turbonips.troglodytes.components.Stats.StatType;
import com.turbonips.troglodytes.components.Warp;

public class PlayerBehaviorSystem extends BaseEntitySystem {
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Location> locationMapper;
	private ComponentMapper<ResourceRef> resourceMapper;
	private ComponentMapper<Direction> directionMapper;
	private ComponentMapper<Attack> attackMapper;
	private ComponentMapper<Secondary> secondaryMapper;
	private ComponentMapper<Stats> statsMapper;
	private ComponentMapper<HealthRegen> healthRegenMapper;
	private ComponentMapper<ColorChange> colorChangeMapper;

	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		locationMapper = new ComponentMapper<Location>(Location.class, world);
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
		directionMapper = new ComponentMapper<Direction>(Direction.class, world);
		attackMapper = new ComponentMapper<Attack>(Attack.class, world);
		statsMapper = new ComponentMapper<Stats>(Stats.class, world);
		secondaryMapper = new ComponentMapper<Secondary>(Secondary.class, world);
		healthRegenMapper = new ComponentMapper<HealthRegen>(HealthRegen.class, world);
		colorChangeMapper = new ComponentMapper<ColorChange>(ColorChange.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> maps = world.getGroupManager().getEntities("MAP");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ResourceManager manager = ResourceManager.getInstance();
		Entity map = maps.get(0);
		Entity player = players.get(0);
		Location playerLocation = locationMapper.get(player);
		Vector2f playerPosition = playerLocation.getPosition();
		HashMap<StatType, Integer> playerStats = statsMapper.get(player).getStats();

		// Health regen
		if (playerStats.get(StatType.HEALTH) < playerStats.get(StatType.MAX_HEALTH)) {
			HealthRegen healthRegen = healthRegenMapper.get(player);
			if (new Date().getTime()-healthRegen.getLastTime() > healthRegen.getTime()) {
				playerStats.put(StatType.HEALTH, playerStats.get(StatType.HEALTH)+1);
				healthRegen.setLastTime(new Date().getTime());
			}
		}

		// Collision detection
		if (map != null) {
			String groundResName = resourceMapper.get(map).getResourceName();
			Resource groundRes = manager.getResource(groundResName);
			TiledMap tiledMap = (TiledMap) groundRes.getObject();
			CollisionResolution collisionResolution = CollisionResolution.getInstance();
			Vector2f newPosition = collisionResolution.resolveWallCollisions(player, tiledMap);
			checkWarps(tiledMap, player);
			checkAttacking(player, enemies);
			checkSecondary(player, enemies);
			playerPosition.set(newPosition);
		}


	}

	private void checkWarps(TiledMap map, Entity player) {
		String playerResName = resourceMapper.get(player).getResourceName();
		ResourceManager manager = ResourceManager.getInstance();
		Resource playerRes = manager.getResource(playerResName);
		Image playerFrame = getFrame(playerRes);
		int ph = playerFrame.getHeight()-1;
		int pw = playerFrame.getWidth()-1;
		Movement movement = movementMapper.get(player);
		Location playerLocation = locationMapper.get(player);
		Vector2f playerPosition = playerLocation.getPosition();
		Vector2f playerVelocity = movement.getVelocity();
		Vector2f newPlayerPosition = new Vector2f(playerPosition);
		newPlayerPosition.add(playerVelocity);

		for (int i = 0; i < map.getObjectGroupCount(); i++) {
			for (int j = 0; j < map.getObjectCount(i); j++) {
				boolean collision = false;
				if (map.getObjectType(i, j).toLowerCase().equals("warp")) {
					
					// Warp x location
					int wx = map.getObjectX(i, j);
					// Warp y location
					int wy = map.getObjectY(i, j);
					// Warp width
					int ww = map.getObjectWidth(i, j);
					// Warp height
					int wh = map.getObjectHeight(i, j);
					String warpMap = map.getObjectProperty(i, j, "Map", "");
					int warpX = Integer.valueOf(map.getObjectProperty(i, j, "X", ""));
					int warpY = Integer.valueOf(map.getObjectProperty(i, j, "Y", ""));
					Vector2f warpPosition = new Vector2f(warpX, warpY);

					int step = 5;
					for (int x=0; x<pw; x=x+step) {
						// Up
						if (newPlayerPosition.x+x > wx && newPlayerPosition.x < wx+ww &&
							newPlayerPosition.y+ph > wy+wh && newPlayerPosition.y < wy+wh) {
							
							if (playerPosition.x+x > wx && playerPosition.x < wx+ww &&
								newPlayerPosition.y+ph > wy+wh && newPlayerPosition.y < wy+wh) {
								//newPlayerPosition.y = wy+wh+1;
							}
							collision = true;
						}
						
						// Down
						if (newPlayerPosition.x+x > wx && newPlayerPosition.x < wx+ww &&
							newPlayerPosition.y+ph > wy && newPlayerPosition.y+ph < wy+wh) {
							
							if (playerPosition.x+x > wx && playerPosition.x < wx+ww &&
								newPlayerPosition.y+ph > wy && newPlayerPosition.y+ph < wy+wh) {
								//newPlayerPosition.y = wy-ph-1;
							}
							collision = true;
						}
					}
					

					for (int y=0; y<ph; y=y+step) {
						// Left
						if (newPlayerPosition.y+y > wy && newPlayerPosition.y < wy+wh &&
							newPlayerPosition.x+pw > wx+ww && newPlayerPosition.x < wx+ww) {
							
							if (wy+y > wy && wy < wy+wh &&
								newPlayerPosition.x+pw > wx+ww && newPlayerPosition.x < wx+ww) {
								//newPlayerPosition.x = wx+ww+1;
							}
							collision = true;
						}
						
						// Right
						if (newPlayerPosition.y+y > wy && newPlayerPosition.y < wy+wh &&
							newPlayerPosition.x+pw > wx && newPlayerPosition.x+pw < wx+ww) {
							
							if (wy+y > wy && wy < wy+wh &&
								newPlayerPosition.x+pw > wx && newPlayerPosition.x+pw < wx+ww) {
								//newPlayerPosition.x = wx-pw-1;
							}
							collision = true;
						}
					}
					
					if (collision) {
						logger.info(warpMap);
						player.addComponent(new Warp(warpMap, warpPosition));
						player.refresh();
						// resourceMapper.get(player).setResourceName("testenemyimage");
					}
				}
			}
		}
	}

	private void checkSecondary(Entity player, ImmutableBag<Entity> enemies) {
		Secondary playerSecondary = secondaryMapper.get(player);
		if (playerSecondary.isSecondary()) {
			Vector2f playerPosition = locationMapper.get(player).getPosition();
			String playerResName = resourceMapper.get(player).getResourceName();
			ResourceManager manager = ResourceManager.getInstance();
			Resource playerRes = manager.getResource(playerResName);
			Image playerFrame = getFrame(playerRes);
			int ph = playerFrame.getHeight();
			int pw = playerFrame.getWidth();
			Vector2f playerCenter = new Vector2f(playerPosition.x + (pw / 2), playerPosition.y + (ph / 2));
			int MAX_DISTANCE = 128;
			int playerDamage = 10;

			for (int i = 0; i < enemies.size(); i++) {
				Entity enemy = enemies.get(i);
				Vector2f enemyPosition = locationMapper.get(enemy).getPosition();
				String enemyResName = resourceMapper.get(enemy).getResourceName();
				Resource enemyRes = manager.getResource(enemyResName);
				Image enemyFrame = getFrame(enemyRes);
				int eh = enemyFrame.getHeight();
				int ew = enemyFrame.getWidth();
				Vector2f enemyCenter = new Vector2f(enemyPosition.x + (ew / 2), enemyPosition.y + (eh / 2));
				Movement enemyMovement = movementMapper.get(enemy);
				Vector2f enemyVelocity = enemyMovement.getVelocity();
				boolean secondaryEnemy = false;
				double secondaryKnockBack = 40;

				// Make sure we don't set the velocity > the entity width or height (necessary for collision)
				if (ew < eh && secondaryKnockBack > ew) {
					secondaryKnockBack = ew;
				}
				if (eh < ew && secondaryKnockBack > eh) {
					secondaryKnockBack = eh;
				}

				double secondaryKnockBackX = (secondaryKnockBack/Math.sqrt(2));

				if (playerCenter.distance(enemyCenter) < MAX_DISTANCE && locationMapper.get(enemy).getMap().equals(locationMapper.get(player).getMap())) {
					Vector2f playerToEnemy = new Vector2f(enemyCenter.x - playerCenter.x, enemyCenter.y - playerCenter.y);
					float scale = (float) (secondaryKnockBack/playerCenter.distance(enemyCenter));
					enemyVelocity.x = scale * playerToEnemy.x;
					enemyVelocity.y = scale * playerToEnemy.y;

					HashMap<StatType, Integer> enemyStats = statsMapper.get(enemy).getStats();
					enemyStats.put(StatType.HEALTH, enemyStats.get(StatType.HEALTH)-playerDamage);
					if (colorChangeMapper.get(enemy) == null) {
						ColorChange colorChange = new ColorChange(250, new Color(255,0,0));
						colorChange.setLastTime(new Date().getTime());
						enemy.addComponent(colorChange);
					}

					if (enemyStats.get(StatType.HEALTH) <= 0) {
						enemy.delete();
						// Create enemy death entity for particle effect
						Entity enemyDeath = world.createEntity();
						enemyDeath.setGroup("ENEMY_DEATH");
						XMLSerializer xmls = XMLSerializer.getInstance();
						ResourceManager rm = ResourceManager.getInstance();
						ParticleData particleData = (ParticleData)xmls.deserializeData("resources/particleXMLs/deathsplat");
						String particleResourceRef = particleData.getResourceRef();
						Emitter pem = new Emitter(particleData);
						Image particleImage = (Image)rm.getResource(particleResourceRef).getObject();
						ParticleSystem ps = new ParticleSystem(particleImage, 1000);
						pem.setEnabled(true);
						ps.addEmitter(pem);
						enemyDeath.addComponent(new ParticleComponent(ps, true));
						enemyDeath.addComponent(new Location(new Vector2f(enemyPosition.getX() + ew/2, enemyPosition.getY() + eh/2), null));

					}
				}
			}
		}
	}

	private void checkAttacking(Entity player, ImmutableBag<Entity> enemies) {
		Attack playerAttack = attackMapper.get(player);
		if (playerAttack.isAttacking()) {

			Vector2f playerPosition = locationMapper.get(player).getPosition();
			String playerResName = resourceMapper.get(player).getResourceName();
			ResourceManager manager = ResourceManager.getInstance();
			Resource playerRes = manager.getResource(playerResName);
			Image playerFrame = getFrame(playerRes);
			int ph = playerFrame.getHeight();
			int pw = playerFrame.getWidth();
			Vector2f playerCenter = new Vector2f(playerPosition.x + (pw / 2), playerPosition.y + (ph / 2));
			Direction playerDirection = directionMapper.get(player);
			HashMap<StatType, Integer> playerStats = statsMapper.get(player).getStats();
			int playerDamage = 10;

			for (int i = 0; i < enemies.size(); i++) {
				Entity enemy = enemies.get(i);
				Vector2f enemyPosition = locationMapper.get(enemy).getPosition();
				String enemyResName = resourceMapper.get(enemy).getResourceName();
				Resource enemyRes = manager.getResource(enemyResName);
				Image enemyFrame = getFrame(enemyRes);
				int eh = enemyFrame.getHeight();
				int ew = enemyFrame.getWidth();
				Vector2f enemyCenter = new Vector2f(enemyPosition.x + (ew / 2), enemyPosition.y + (eh / 2));
				Movement enemyMovement = movementMapper.get(enemy);
				Vector2f enemyVelocity = enemyMovement.getVelocity();
				boolean attackEnemy = false;
				double attackingKnockBack = 20;
				double attackingKnockBackX = (attackingKnockBack/Math.sqrt(2));

				if (playerCenter.distance(enemyCenter) < playerStats.get(StatType.RANGE)*32 && locationMapper.get(enemy).getMap().equals(locationMapper.get(player).getMap())) {
					Vector2f playerToEnemy = new Vector2f(enemyCenter.x - playerCenter.x, playerCenter.y - enemyCenter.y);

					switch (playerDirection.getDirection()) {
						case UP:
							if (playerToEnemy.y >= 0) {
								enemyVelocity.y -= attackingKnockBack;
								attackEnemy = true;
							}
							break;
						case DOWN:
							if (playerToEnemy.y <= 0) {
								enemyVelocity.y += attackingKnockBack;
								attackEnemy = true;
							}
							break;
						case LEFT:
							if (playerToEnemy.x <= 0) {
								enemyVelocity.x -= attackingKnockBack;
								attackEnemy = true;
							}
							break;
						case RIGHT:
							if (playerToEnemy.x >= 0) {
								enemyVelocity.x += attackingKnockBack;
								attackEnemy = true;
							}
							break;

						case UP_RIGHT:
							if (playerToEnemy.x < 0) {
								if (Math.abs(playerToEnemy.x) <= Math.abs(playerToEnemy.y)) {
									attackEnemy = true;
								}
							} else if (playerToEnemy.x > 0) {
								if (Math.abs(playerToEnemy.x) >= Math.abs(playerToEnemy.y) || playerToEnemy.y > 0) {
									attackEnemy = true;
								}
							} else if (playerToEnemy.y >= 0) {
								attackEnemy = true;
							}

							if (attackEnemy) {
								enemyVelocity.x += attackingKnockBackX;
								enemyVelocity.y -= attackingKnockBackX;
							}
							break;

						case UP_LEFT:
							if (playerToEnemy.x > 0) {
								if (Math.abs(playerToEnemy.x) >= Math.abs(playerToEnemy.y)) {
									attackEnemy = true;
								}
							} else if (playerToEnemy.x < 0) {
								if (Math.abs(playerToEnemy.x) >= Math.abs(playerToEnemy.y) || playerToEnemy.y > 0) {
									attackEnemy = true;
								}
							} else if (playerToEnemy.y >= 0) {
								attackEnemy = true;
							}

							if (attackEnemy) {
								enemyVelocity.x -= attackingKnockBackX;
								enemyVelocity.y -= attackingKnockBackX;
							}
							break;

						case DOWN_LEFT:
							if (playerToEnemy.x > 0) {
								if (Math.abs(playerToEnemy.y) >= Math.abs(playerToEnemy.x)) {
									attackEnemy = true;
								}
							} else if (playerToEnemy.x < 0) {
								if (Math.abs(playerToEnemy.y) >= Math.abs(playerToEnemy.x) || playerToEnemy.x < 0) {
									attackEnemy = true;
								}
							} else if (playerToEnemy.x <= 0) {
								attackEnemy = true;
							}

							if (attackEnemy) {
								enemyVelocity.x -= attackingKnockBackX;
								enemyVelocity.y += attackingKnockBackX;
							}
							break;

						case DOWN_RIGHT:
							if (playerToEnemy.x < 0) {
								if (Math.abs(playerToEnemy.y) >= Math.abs(playerToEnemy.x)) {
									attackEnemy = true;
								}
							} else if (playerToEnemy.x > 0) {
								if (Math.abs(playerToEnemy.y) <= Math.abs(playerToEnemy.x) || playerToEnemy.x > 0) {
									attackEnemy = true;
								}
							} else if (playerToEnemy.x <= 0) {
								attackEnemy = true;
							}

							if (attackEnemy) {
								enemyVelocity.x += attackingKnockBackX;
								enemyVelocity.y += attackingKnockBackX;
							}
							break;
					}
				}

				if (attackEnemy) {
					HashMap<StatType, Integer> enemyStats = statsMapper.get(enemy).getStats();
					enemyStats.put(StatType.HEALTH, enemyStats.get(StatType.HEALTH)-playerDamage);
					if (colorChangeMapper.get(enemy) == null) {
						ColorChange colorChange = new ColorChange(250, new Color(255,0,0));
						colorChange.setLastTime(new Date().getTime());
						enemy.addComponent(colorChange);
					}
					if (enemyStats.get(StatType.HEALTH) <= 0) {
						enemy.delete();
						// Create enemy death entity for particle effect
						Entity enemyDeath = world.createEntity();
						enemyDeath.setGroup("ENEMY_DEATH");
						XMLSerializer xmls = XMLSerializer.getInstance();
						ResourceManager rm = ResourceManager.getInstance();
						ParticleData particleData = (ParticleData)xmls.deserializeData("resources/particleXMLs/deathsplat");
						String particleResourceRef = particleData.getResourceRef();
						Emitter pem = new Emitter(particleData);
						Image particleImage = (Image)rm.getResource(particleResourceRef).getObject();
						ParticleSystem ps = new ParticleSystem(particleImage, 1000);
						pem.setEnabled(true);
						ps.addEmitter(pem);
						enemyDeath.addComponent(new ParticleComponent(ps, true));
						enemyDeath.addComponent(new Location(new Vector2f(enemyPosition.getX() + ew/2, enemyPosition.getY() + eh/2), null));
					}
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

	@Override
	protected boolean checkProcessing() {
		return true;
	}
}