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
import com.turbonips.troglodytes.components.SpatialForm;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.objects.ObjectType;
import com.turbonips.troglodytes.objects.WarpObject;

public class ObjectSystem extends BaseEntitySystem {
	private final GameContainer container;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<SpatialForm> spatialFormMapper;
	private ComponentMapper<CreatureAnimation> animationCreatureMapper;

	public ObjectSystem(GameContainer container) {
		super(Position.class, SpatialForm.class);
		this.container = container;
	}
	
	@Override
	protected void initialize() {
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		spatialFormMapper = new ComponentMapper<SpatialForm>(SpatialForm.class, world);
		animationCreatureMapper = new ComponentMapper<CreatureAnimation>(CreatureAnimation.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ResourceManager resourceManager = ResourceManager.getInstance();
		ImmutableBag<Entity> layers = world.getGroupManager().getEntities("LAYER");
		ImmutableBag<Entity> creatures = world.getGroupManager().getEntities("CREATURE");
		ArrayList<SpatialForm> mapLayers = new ArrayList<SpatialForm>();
		ArrayList<Entity> mapEntities = new ArrayList<Entity>();
		
		for (int i=0; i<layers.size(); i++) {
			mapLayers.add(spatialFormMapper.get(layers.get(i)));
			mapEntities.add(layers.get(i));
		}
		
		for (int a=0; a<creatures.size(); a++) {
			Entity creature = creatures.get(a);
			Image sprite = animationCreatureMapper.get(creature).getCurrent().getImage(0);
			Position position = positionMapper.get(creature);
			ObjectType objectType = getObjectType(position, mapLayers, sprite);
			// TODO: When we use a warp object we'll want to unload the current map & monsters OR
			// 		 manage memory in some intelligent way
			if (objectType != null) {
				switch (objectType.getType()) {
					case ObjectType.WARP_OBJECT:
						WarpObject warpObject = (WarpObject)objectType;
						logger.info("Warping to " + warpObject.getMapName() + "," + warpObject.getX()*sprite.getWidth() + "," + + warpObject.getY()*sprite.getHeight());
						position.setPosition(warpObject.getX()*sprite.getWidth(), warpObject.getY()*sprite.getHeight());
						TiledMap newMap = (TiledMap)resourceManager.getResource(warpObject.getMapName()).getObject();
						for (Entity entity : mapEntities) {
							int oldType = spatialFormMapper.get(entity).getType();
							entity.removeComponent(spatialFormMapper.get(entity));
							entity.addComponent(new SpatialForm(newMap, oldType));
							Position layerPosition = positionMapper.get(entity);
							layerPosition.setPosition(warpObject.getX()*sprite.getWidth(), warpObject.getY()*sprite.getHeight());
						}
						break;
				}
			}
		}
		
	}
	
	ObjectType createObjectType(ArrayList<SpatialForm> mapLayers, int x, int y) {
		TiledMap map = (TiledMap)mapLayers.get(0).getForm();
		
		for (int groupID=0; groupID<map.getObjectGroupCount(); groupID++) {
			for (int objectID=0; objectID<map.getObjectCount(groupID); objectID++) {
				int ox = map.getObjectX(groupID, objectID);
				int oy = map.getObjectY(groupID, objectID);
				int oh = map.getObjectHeight(groupID, objectID);
				int ow = map.getObjectWidth(groupID, objectID);
				
				if (x > ox && x < ox+ow) {
					if (y > oy && y < oy+oh) {
						return ObjectType.create(mapLayers, groupID, objectID);
					}
				}
			}
		}
		
		return null;
	}
	
	private ObjectType getObjectType(Position position, ArrayList<SpatialForm> mapLayers, Image sprite) {
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
		if (objectType == null) objectType = createObjectType(mapLayers,topLeftX, topLeftY);
		if (objectType == null) objectType = createObjectType(mapLayers, bottomLeftX, bottomLeftY);

		// Right
		topRightY = (int)(position.getY()+sprite.getHeight()/2);
		bottomRightY = (int)(position.getY()+sprite.getHeight()-1);
		topRightX = (int)(position.getX()+sprite.getWidth()-1);
		bottomRightX = (int)(position.getX()+sprite.getWidth()-1);
		if (objectType == null) objectType = createObjectType(mapLayers, topRightX, topRightY);
		if (objectType == null) objectType = createObjectType(mapLayers, bottomRightX, bottomRightY);

		// Up
		topLeftY = (int)(position.getY()+sprite.getHeight()/2);
		topRightY = (int)(position.getY()+sprite.getHeight()/2);
		topLeftX = (int)(position.getX());
		topRightX = (int)(position.getX()+sprite.getWidth()-1);
		if (objectType == null) objectType = createObjectType(mapLayers, topLeftX, topLeftY);
		if (objectType == null) objectType = createObjectType(mapLayers, topRightX, topRightY);
		
		// Down
		bottomLeftY = (int)(position.getY()+sprite.getHeight()-1);
		bottomRightY = (int)(position.getY()+sprite.getHeight()-1);
		bottomLeftX = (int)(position.getX());
		bottomRightX = (int)(position.getX()+sprite.getWidth()-1);
		if (objectType == null) objectType = createObjectType(mapLayers, bottomLeftX, bottomLeftY);
		if (objectType == null) objectType = createObjectType(mapLayers, bottomRightX, bottomRightY);
		
		return objectType;
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}


}
