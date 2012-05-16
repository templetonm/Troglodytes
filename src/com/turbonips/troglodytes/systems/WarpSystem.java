package com.turbonips.troglodytes.systems;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.Music;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.SystemManager;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.EnemyData;
import com.turbonips.troglodytes.PolymorphTrinket;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.TrinketData;
import com.turbonips.troglodytes.TrinketData.TrinketType;
import com.turbonips.troglodytes.XMLSerializer;
import com.turbonips.troglodytes.components.Attack;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.EnemyAI;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.ParticleComponent;
import com.turbonips.troglodytes.components.Polymorph;
import com.turbonips.troglodytes.components.Location;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.StatModifiers;
import com.turbonips.troglodytes.components.Stats;
import com.turbonips.troglodytes.components.TrinketDrop;
import com.turbonips.troglodytes.components.VisitedMaps;
import com.turbonips.troglodytes.components.Warp;
import com.turbonips.troglodytes.components.Direction.Dir;
import com.turbonips.troglodytes.components.Stats.StatType;

public class WarpSystem extends BaseEntityProcessingSystem {
	private ComponentMapper<Warp> warpMapper;
	private ComponentMapper<Location> locationMapper;
	private ComponentMapper<VisitedMaps> visitedMapsMapper;

	public WarpSystem() {
		super(Warp.class);
	}

	@Override
	protected void initialize() {
		warpMapper = new ComponentMapper<Warp>(Warp.class, world);
		locationMapper = new ComponentMapper<Location>(Location.class, world);
		visitedMapsMapper = new ComponentMapper<VisitedMaps>(VisitedMaps.class, world);
	}

	@Override
	protected void process(Entity e) {
		ResourceManager manager = ResourceManager.getInstance();
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> maps = world.getGroupManager().getEntities("MAP");
		ImmutableBag<Entity> enemyDeaths = world.getGroupManager().getEntities("ENEMY_DEATH");
		ImmutableBag<Entity> secondary = world.getGroupManager().getEntities("SECONDARY");
		ImmutableBag<Entity> auras = world.getGroupManager().getEntities("AURA");
		ImmutableBag<Entity> particles = world.getGroupManager().getEntities("PARTICLES");
		
		Warp warp = warpMapper.get(e);
		Resource mapRes = manager.getResource(warp.getMapName());
		TiledMap tiledMap = (TiledMap) mapRes.getObject();
		XMLSerializer xmls = XMLSerializer.getInstance();
		Entity player = players.get(0);
		Vector2f playerPosition = locationMapper.get(player).getPosition();
		
		playerPosition.set(new Vector2f(warp.getPosition().x * tiledMap.getTileWidth(), warp.getPosition().y * tiledMap.getTileHeight()));
		locationMapper.get(player).setMap(warp.getMapName());
		
		logger.info(warp.getPosition());
		logger.info(locationMapper.get(player).getMap());
		
		// Delete existing map
		for (int i=0; i<maps.size(); i++) {
			maps.get(i).delete();
		}
		
		// Delete existing enemy death effects
		for (int i=0; i<enemyDeaths.size(); i++) {
			Entity enemyDeath = enemyDeaths.get(i);
			enemyDeath.delete();
		}
		
		// Delete secondary effect
		if (secondary.get(0) != null) {
			secondary.get(0).delete();
		}

		// Add the new map
		Entity map = world.createEntity();
		map.setGroup("MAP");
		map.addComponent(new ResourceRef(warp.getMapName()));
		map.refresh();
		
		// Spawn enemies, particles and trinkets
		if (!visitedMapsMapper.get(players.get(0)).getMaps().contains(warp.getMapName())) {
			for (int groupID = 0; groupID < tiledMap.getObjectGroupCount(); groupID++) {
				for (int objectID = 0; objectID < tiledMap.getObjectCount(groupID); objectID++) {
					int objectX = tiledMap.getObjectX(groupID, objectID);
					int objectY = tiledMap.getObjectY(groupID, objectID);
					int objectWidth = tiledMap.getObjectWidth(groupID, objectID);
					int objectHeight = tiledMap.getObjectHeight(groupID, objectID);
					String type = tiledMap.getObjectType(groupID, objectID).toLowerCase();
	
					if (type.equals("spawn")) {
						String spawnNumSt = tiledMap.getObjectProperty(groupID, objectID, "Number", "0");
						int spawnNum;
						if (spawnNumSt.contains("-")) {
							int minSpawnNum = Integer.valueOf(spawnNumSt.split("-")[0]);
							int maxSpawnNum = Integer.valueOf(spawnNumSt.split("-")[1]);
							spawnNum = (int)(Math.random() * (maxSpawnNum-minSpawnNum)) + minSpawnNum;
						} else {
							spawnNum = Integer.valueOf(tiledMap.getObjectProperty(groupID, objectID, "Number", "0"));
						}
						
						String[] enemyIds = tiledMap.getObjectProperty(groupID, objectID, "Enemy", "").split(",");
						if (enemyIds.length == 0) enemyIds[0] = tiledMap.getObjectProperty(groupID, objectID, "Enemy", "");
						
							for (int i = 0; i < spawnNum; i++) {
								String enemyId = enemyIds[(int)(Math.random() * (enemyIds.length))].replace(" ", "");
								EnemyData enemyData = (EnemyData)xmls.deserializeData("resources/enemyXMLs/" + enemyId);
								String enemyResourceRef = enemyData.getResourceRef();
								String enemyAIType = enemyData.getAIType();
								int sight = enemyData.getSight();
								Vector2f startPosition = new Vector2f(objectX + (int) (Math.random() * objectWidth), objectY + (int) (Math.random() * objectHeight));
								Entity enemy = world.createEntity();
								enemy.setGroup("ENEMY");
								enemy.addComponent(new ResourceRef(enemyResourceRef));
								HashMap<StatType, Integer> enemyStats = new HashMap<StatType, Integer> ();
								enemyStats.put(StatType.HEALTH, enemyData.getHealth());
								enemyStats.put(StatType.MAX_HEALTH, enemyData.getHealth());
								enemyStats.put(StatType.RANGE, enemyData.getRange());
								enemyStats.put(StatType.MAX_SPEED, (int)enemyData.getMaxSpeed());
								enemyStats.put(StatType.ACCELERATION, (int)enemyData.getAcceleration());
								enemyStats.put(StatType.DECELERATION, (int)enemyData.getDeceleration());
								enemyStats.put(StatType.DAMAGE, enemyData.getDamage());
								enemyStats.put(StatType.SIGHT, enemyData.getSight());
								enemyStats.put(StatType.ATTACK_COOLDOWN, enemyData.getCooldown());
								
								// Pick either a rare or not rare trinket
								String selectedTrinkets = enemyData.getTrinkets();
								if (Math.random() * 100 < 5)
									selectedTrinkets = enemyData.getRareTrinkets();
								
								String selectedTrinket;
								if (selectedTrinkets.contains(",")) {
									int randomIndex = (int)(Math.random() * selectedTrinkets.split(",").length);
									selectedTrinket = selectedTrinkets.split(",")[randomIndex];
								} else {
									selectedTrinket = selectedTrinkets;
								}
								
								// TODO move sight over to enemy stats
								enemy.addComponent(new Movement(new Vector2f(0,0)));
								enemy.addComponent(new Direction(Dir.DOWN));
								enemy.addComponent(new Location(startPosition, warp.getMapName()));
								enemy.addComponent(new EnemyAI(enemyAIType, sight));
								enemy.addComponent(new Attack());
								enemy.addComponent(new Stats(enemyStats));
								if (Math.random() * 10 < 5)  {
									enemy.addComponent(new TrinketDrop(selectedTrinket, world));
								}
								enemy.refresh();
						}
					// Spawn trinkets
					} else if (type.equals("trinketspawn")) {
						Vector2f startPosition = new Vector2f(objectX + (int) (Math.random() * objectWidth), objectY + (int) (Math.random() * objectHeight));
						// There should be a common place for creating trinkets (we do this same logic in TrinketDrop)
						File folder = new File("resources/trinketXMLs/");
						ArrayList<String> fileNames = new ArrayList<String>();
						for (File file : folder.listFiles()) {
							if (file.isFile()) fileNames.add(file.getName());
						}
						logger.info("resources/trinketXMLs/" + fileNames.get((int)(Math.random() * fileNames.size())));
						TrinketData trinketData = (TrinketData)xmls.deserializeData("resources/trinketXMLs/" + fileNames.get((int)(Math.random() * fileNames.size())));
						TrinketType trinketType = trinketData.getType();
						Entity trinket = world.createEntity();
						trinket.setGroup("TRINKET");
						trinket.addComponent(new ResourceRef(trinketData.getResourceRef()));
						trinket.addComponent(new Location(startPosition, warp.getMapName()));
						switch (trinketType) {
							case polymorph:
								PolymorphTrinket polymorphTrinketData = (PolymorphTrinket)trinketData;
								trinket.addComponent(new Polymorph(polymorphTrinketData.getNewResourceRef()));
								break;
						}
						HashMap<StatType, Integer> modifiers = new HashMap<StatType, Integer>();
						modifiers.put(StatType.MAX_HEALTH, trinketData.getAddHealth());
						modifiers.put(StatType.RANGE, trinketData.getAddRange());
						modifiers.put(StatType.ARMOR, trinketData.getAddArmor());
						modifiers.put(StatType.MAX_SPEED, trinketData.getAddSpeed());
						modifiers.put(StatType.DAMAGE, trinketData.getAddDamage());
						modifiers.put(StatType.SIGHT, trinketData.getAddSight());
						modifiers.put(StatType.ATTACK_COOLDOWN, trinketData.getAddAttackCooldown());
						modifiers.put(StatType.HEALTH_COOLDOWN, trinketData.getAddHealthCooldown());
						trinket.addComponent(new StatModifiers(modifiers));
					} else if (type.equals("particle")) {
						String particleId = tiledMap.getObjectProperty(groupID, objectID, "Name", "");
						String resourcePath = manager.getResource(particleId).getPath();
						boolean light = Boolean.valueOf(tiledMap.getObjectProperty(groupID, objectID, "Light", "false"));
						//ParticleSystem ps = (ParticleSystem)manager.getResource(particleId).getObject();
						try {
							ParticleSystem ps = ParticleIO.loadConfiguredSystem(resourcePath);
							Entity part = world.createEntity();
							part.setGroup("PARTICLES");
							part.addComponent(new ParticleComponent(ps, true, light));
							part.addComponent(new Location(new Vector2f(objectX, objectY), warp.getMapName()));
						} catch (Exception ex) {
							// blah
						}
					}
				}
			}
			visitedMapsMapper.get(players.get(0)).getMaps().add(warp.getMapName());
		}
		e.removeComponent(warp);
		e.refresh();
		
		// Add player aura particle effect if it doesn't already exist
		/*if (auras.get(0) == null) {
			try {
				ParticleSystem ps = ParticleIO.loadConfiguredSystem("resources/particleXMLs/aura.xml");
				Entity playerAura = world.createEntity();
				playerAura.setGroup("AURA");
				playerAura.addComponent(new ParticleComponent(ps, true));				
			} catch (IOException ex) {
				// Throw slick exception
			}
		}*/
		
		// Hey, music system!
		String songTitle = tiledMap.getMapProperty("Music", null);
		if (songTitle != null) {
			SystemManager systemManager = world.getSystemManager();
			MusicSystem musicSystem = systemManager.getSystem(MusicSystem.class);
			if(musicSystem.getTiledMap() != tiledMap) {
				musicSystem.setTiledMap(tiledMap);
			}

			if (musicSystem.getTiledMap() != null) {
				Music music = (Music) manager.getResource(songTitle).getObject();
				if (musicSystem.getMusic() != music) {
					musicSystem.setMusic(music);
				}
			}
		}
	}
}
