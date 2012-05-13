package com.turbonips.troglodytes.systems;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
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
import com.turbonips.troglodytes.components.Polymorph;
import com.turbonips.troglodytes.components.Location;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.StatModifiers;
import com.turbonips.troglodytes.components.Stats;
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
								
								// TODO move sight over to enemy stats
								enemy.addComponent(new Movement(new Vector2f(0,0)));
								enemy.addComponent(new Direction(Dir.DOWN));
								enemy.addComponent(new Location(startPosition, warp.getMapName()));
								enemy.addComponent(new EnemyAI(enemyAIType, sight));
								enemy.addComponent(new Attack());
								enemy.addComponent(new Stats(enemyStats));
								enemy.refresh();
						}
					// Spawn trinkets
					} else if (type.equals("trinketspawn")) {
						Vector2f startPosition = new Vector2f(objectX + (int) (Math.random() * objectWidth), objectY + (int) (Math.random() * objectHeight));
						
						File folder = new File("resources/trinketXMLs/");
						ArrayList<String> fileNames = new ArrayList<String>();
						for (File file : folder.listFiles()) {
							if (file.isFile()) fileNames.add(file.getName());
						}
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
						modifiers.put(StatType.ATTACK_COOLDOWN, trinketData.getAddCooldown());
						trinket.addComponent(new StatModifiers(modifiers));
					}
				}
			}
			
			if (!visitedMapsMapper.get(players.get(0)).getMaps().contains(warp.getMapName())) {
				for (int groupID = 0; groupID < tiledMap.getObjectGroupCount(); groupID++) {
					for (int objectID = 0; objectID < tiledMap.getObjectCount(groupID); objectID++) {
						int objectX = tiledMap.getObjectX(groupID, objectID);
						int objectY = tiledMap.getObjectY(groupID, objectID);
						int objectWidth = tiledMap.getObjectWidth(groupID, objectID);
						int objectHeight = tiledMap.getObjectHeight(groupID, objectID);
						String type = tiledMap.getObjectType(groupID, objectID).toLowerCase();
					}
				}
			}
			visitedMapsMapper.get(players.get(0)).getMaps().add(warp.getMapName());
		}
		e.removeComponent(warp);
		e.refresh();
	}
}
