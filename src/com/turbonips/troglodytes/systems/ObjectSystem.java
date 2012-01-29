package com.turbonips.troglodytes.systems;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Resource;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.objects.ObjectType;
import com.turbonips.troglodytes.objects.WarpObject;

public class ObjectSystem extends BaseEntitySystem {
	private final GameContainer container;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<Resource> resourceMapper;

	public ObjectSystem(GameContainer container) {
		this.container = container;
	}
	
	@Override
	protected void initialize() {
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		resourceMapper = new ComponentMapper<Resource>(Resource.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ResourceManager resourceManager = ResourceManager.getInstance();
		ImmutableBag<Entity> layers = world.getGroupManager().getEntities("LAYER");
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		
		if (!layers.isEmpty()) {
			Resource mapResource = resourceMapper.get(layers.get(0));
			
			for (int p=0; p<players.size(); p++) {
				Entity player = players.get(p);
				Position position = positionMapper.get(player);
				Resource playerResource = resourceMapper.get(player);
				Image sprite = null;
				if (playerResource.getType().equalsIgnoreCase("creatureanimation")) {
					sprite = ((CreatureAnimation)playerResource.getObject()).getIdleDown().getCurrentFrame();
				} else if (playerResource.getType().equalsIgnoreCase("image")) {
					sprite = (Image)playerResource.getObject();
				} else {
					logger.error("player resource type is " + playerResource.getType());
				}
				
				
				
				ObjectType objectType = getObjectType(position, mapResource.getId(), sprite);
				if (objectType != null) {
					switch (objectType.getType()) {
						case ObjectType.WARP_OBJECT:
							WarpObject warpObject = (WarpObject)objectType;
							logger.info("Warping to " + warpObject.getMapName() + "," + warpObject.getX()*sprite.getWidth() + "," + + warpObject.getY()*sprite.getHeight());
							position.setPosition(warpObject.getX()*sprite.getWidth(), warpObject.getY()*sprite.getHeight());							
							String newMapName = warpObject.getMapName();
							for (int i=0; i<layers.size(); i++) {
								Entity layer = layers.get(i);
								Resource layerResource = resourceMapper.get(layer);
								String oldMapName = layerResource.getId();
								resourceManager.unloadResource(oldMapName);
								layer.removeComponent(layerResource);
								layer.addComponent(resourceManager.getResource(newMapName));
								Position layerPosition = positionMapper.get(layer);
								layerPosition.setPosition(warpObject.getX()*sprite.getWidth(), warpObject.getY()*sprite.getHeight());
							}
							for (int i=0; i<enemies.size(); i++) {
								Entity enemy = enemies.get(i);
								world.deleteEntity(enemy);
							}
							break;
					}
				}
			}
		}
		
	}
	
	ObjectType createObjectType(String mapId, int x, int y) {
		TiledMap map = (TiledMap)ResourceManager.getInstance().getResource(mapId).getObject();
		
		for (int groupID=0; groupID<map.getObjectGroupCount(); groupID++) {
			for (int objectID=0; objectID<map.getObjectCount(groupID); objectID++) {
				int ox = map.getObjectX(groupID, objectID);
				int oy = map.getObjectY(groupID, objectID);
				int oh = map.getObjectHeight(groupID, objectID);
				int ow = map.getObjectWidth(groupID, objectID);
				
				if (x > ox && x < ox+ow) {
					if (y > oy && y < oy+oh) {
						return ObjectType.create(mapId, groupID, objectID);
					}
				}
			}
		}
		
		return null;
	}
	
	private ObjectType getObjectType(Position position, String mapId, Image sprite) {
		int topLeftY;
		int bottomLeftY;
		int topLeftX;
		int bottomLeftX;
		int topRightY;
		int bottomRightY;
		int topRightX;
		int bottomRightX;
		
		ObjectType objectType = null;
		// Object checks
		
		// Left
		topLeftY = (int)(position.getY()+sprite.getHeight()/2);
		bottomLeftY = (int)(position.getY()+sprite.getHeight()-1);
		topLeftX = (int)(position.getX());
		bottomLeftX = (int)(position.getX());
		if (objectType == null) objectType = createObjectType(mapId,topLeftX, topLeftY);
		if (objectType == null) objectType = createObjectType(mapId, bottomLeftX, bottomLeftY);

		// Right
		topRightY = (int)(position.getY()+sprite.getHeight()/2);
		bottomRightY = (int)(position.getY()+sprite.getHeight()-1);
		topRightX = (int)(position.getX()+sprite.getWidth()-1);
		bottomRightX = (int)(position.getX()+sprite.getWidth()-1);
		if (objectType == null) objectType = createObjectType(mapId, topRightX, topRightY);
		if (objectType == null) objectType = createObjectType(mapId, bottomRightX, bottomRightY);

		// Up
		topLeftY = (int)(position.getY()+sprite.getHeight()/2);
		topRightY = (int)(position.getY()+sprite.getHeight()/2);
		topLeftX = (int)(position.getX());
		topRightX = (int)(position.getX()+sprite.getWidth()-1);
		if (objectType == null) objectType = createObjectType(mapId, topLeftX, topLeftY);
		if (objectType == null) objectType = createObjectType(mapId, topRightX, topRightY);
		
		// Down
		bottomLeftY = (int)(position.getY()+sprite.getHeight()-1);
		bottomRightY = (int)(position.getY()+sprite.getHeight()-1);
		bottomLeftX = (int)(position.getX());
		bottomRightX = (int)(position.getX()+sprite.getWidth()-1);
		if (objectType == null) objectType = createObjectType(mapId, bottomLeftX, bottomLeftY);
		if (objectType == null) objectType = createObjectType(mapId, bottomRightX, bottomRightY);
		
		return objectType;
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}


}
