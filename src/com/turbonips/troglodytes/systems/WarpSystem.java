package com.turbonips.troglodytes.systems;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.EnemyData;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.XMLSerializer;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.Warp;
import com.turbonips.troglodytes.components.Direction.Dir;

public class WarpSystem extends BaseEntityProcessingSystem {
	private ComponentMapper<Warp> warpMapper;
	private ComponentMapper<Position> positionMapper;

	public WarpSystem() {
		super(Warp.class);
	}
	
	@Override
	protected void initialize() {
		warpMapper = new ComponentMapper<Warp>(Warp.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
	}

	@Override
	protected void process(Entity e) {
		ResourceManager manager = ResourceManager.getInstance();
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> grounds = world.getGroupManager().getEntities("GROUND");
		ImmutableBag<Entity> backgrounds = world.getGroupManager().getEntities("BACKGROUND");
		ImmutableBag<Entity> foregrounds = world.getGroupManager().getEntities("FOREGROUND");
		Warp warp = warpMapper.get(e);
		Resource mapRes = manager.getResource(warp.getMapName());
		TiledMap map = (TiledMap)mapRes.getObject();
		XMLSerializer xmls = XMLSerializer.getInstance();

		for (int i=0; i<players.size(); i++) {
			Entity player = players.get(i);
			Position pos = positionMapper.get(player);
			Vector2f position = pos.getPosition();
			position.set(warp.getPosition());
		}
		for (int i=0; i<enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			enemy.delete();
		}
		for (int i=0; i<grounds.size(); i++) {
			Entity ground = grounds.get(i);
			ground.delete();
		}
		for (int i=0; i<backgrounds.size(); i++) {
			Entity background = backgrounds.get(i);
			background.delete();
		}
		for (int i=0; i<foregrounds.size(); i++) {
			Entity foregound = foregrounds.get(i);
			foregound.delete();
		}
		
		Entity ground = world.createEntity();
		ground.setGroup("GROUND");
		ground.addComponent(new ResourceRef(warp.getMapName()));
		ground.refresh();

		Entity background = world.createEntity();
		background.setGroup("BACKGROUND");
		background.addComponent(new ResourceRef(warp.getMapName()));
		background.refresh();

		Entity foreground = world.createEntity();
		foreground.setGroup("FOREGROUND");
		foreground.addComponent(new ResourceRef(warp.getMapName()));
		foreground.refresh();
		
		// Spawn enemies and particles
		for (int groupID=0; groupID<map.getObjectGroupCount(); groupID++) {
			for (int objectID=0; objectID<map.getObjectCount(groupID); objectID++) {
				int objectX = map.getObjectX(groupID, objectID);
				int objectY = map.getObjectY(groupID, objectID);
				int objectWidth = map.getObjectWidth(groupID, objectID);
				int objectHeight = map.getObjectHeight(groupID, objectID);
				String type = map.getObjectType(groupID, objectID).toLowerCase();
				
				if (type.equals("spawn")) {
					int spawnNum = Integer.valueOf(map.getObjectProperty(groupID, objectID, "Number", "0"));
					String enemyId = map.getObjectProperty(groupID, objectID, "Enemy", "");
					logger.info(enemyId);
					EnemyData enemyData = xmls.deserializeEnemyData(enemyId);
					String enemyResourceRef = enemyData.getResourceRef();
					int enemyMaxSpeed = enemyData.getMaxSpeed();
					int enemyAcc = enemyData.getAcceleration();
					int enemyDec = enemyData.getDeceleration();
					enemyData.setAcceleration(4);
							
					for (int i=0; i<spawnNum; i++) {
						Vector2f startPosition = new Vector2f(objectX+(int)(Math.random()*objectWidth), 
															  objectY+(int)(Math.random()*objectHeight));
						Entity enemy = world.createEntity();
						enemy.setGroup("ENEMY");
						enemy.addComponent(new ResourceRef(warp.getMapName()));
						enemy.addComponent(new ResourceRef(enemyResourceRef));
						enemy.addComponent(new Movement(enemyMaxSpeed, new Vector2f(enemyAcc,enemyAcc), new Vector2f(enemyDec,enemyDec)));
						enemy.addComponent(new Direction(Dir.DOWN));
						enemy.addComponent(new Position(startPosition));
						enemy.refresh();
					}
				}

				
				
			}
		}
		
		
		e.removeComponent(warp);
		e.refresh();
	}

}
