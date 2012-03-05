package com.turbonips.troglodytes.systems;

import org.newdawn.slick.Image;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Collision;
import com.turbonips.troglodytes.components.ObjectType;
import com.turbonips.troglodytes.components.Renderable;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.WarpObject;

public class ObjectCollisionSystem extends BaseEntitySystem {
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<Renderable> renderableMapper;
	private ComponentMapper<Collision> collisionMapper;

	public ObjectCollisionSystem() {
	}

	@Override
	protected void initialize() {
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		renderableMapper = new ComponentMapper<Renderable>(Renderable.class, world);
		collisionMapper = new ComponentMapper<Collision>(Collision.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> layers = world.getGroupManager().getEntities("LAYER");
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		
		if (!layers.isEmpty()) {
			Resource mapResource = renderableMapper.get(layers.get(0)).getResource();
			if (mapResource != null) {
				for (int p = 0; p < players.size(); p++) {
					Entity player = players.get(p);
					Position playerPosition = positionMapper.get(player);
					
					if (renderableMapper.get(player) != null) {
						ObjectType objectType = getObjectType(player, mapResource.getId());
						if (objectType != null) {
							switch (objectType.getType()) {
								case ObjectType.WARP_OBJECT:
									WarpObject warpObject = (WarpObject) objectType;
									logger.info("Adding warp to player " + warpObject.toString());
									player.addComponent(warpObject);
									player.refresh();
									break;
							}
						}
					}
				}
			}
		}

	}

	ObjectType createObjectType(String mapId, int x, int y) {
		TiledMap map = (TiledMap) ResourceManager.getInstance()
				.getResource(mapId).getObject();

		for (int groupID = 0; groupID < map.getObjectGroupCount(); groupID++) {
			for (int objectID = 0; objectID < map.getObjectCount(groupID); objectID++) {
				int ox = map.getObjectX(groupID, objectID);
				int oy = map.getObjectY(groupID, objectID);
				int oh = map.getObjectHeight(groupID, objectID);
				int ow = map.getObjectWidth(groupID, objectID);

				if (x > ox && x < ox + ow) {
					if (y > oy && y < oy + oh) {
						return ObjectType.create(mapId, groupID, objectID);
					}
				}
			}
		}

		return null;
	}

	private ObjectType getObjectType(Entity player, String mapId) {
		int topLeftY;
		int bottomLeftY;
		int topLeftX;
		int bottomLeftX;
		int topRightY;
		int bottomRightY;
		int topRightX;
		int bottomRightX;
		
		Image playerImage = getImage(player);
		Position playerPosition = positionMapper.get(player);

		ObjectType objectType = null;
		// Object checks

		// Left

		if (playerImage != null && playerPosition != null) {
			topLeftY = (int) (playerPosition.getY() + playerImage.getHeight() / 2);
			bottomLeftY = (int) (playerPosition.getY() + playerImage.getHeight() - 1);
			topLeftX = (int) (playerPosition.getX());
			bottomLeftX = (int) (playerPosition.getX());
			if (objectType == null)
				objectType = createObjectType(mapId, topLeftX, topLeftY);
			if (objectType == null)
				objectType = createObjectType(mapId, bottomLeftX, bottomLeftY);

			// Right
			topRightY = (int) (playerPosition.getY() + playerImage.getHeight() / 2);
			bottomRightY = (int) (playerPosition.getY() + playerImage.getHeight() - 1);
			topRightX = (int) (playerPosition.getX() + playerImage.getWidth() - 1);
			bottomRightX = (int) (playerPosition.getX() + playerImage.getWidth() - 1);
			if (objectType == null)
				objectType = createObjectType(mapId, topRightX, topRightY);
			if (objectType == null)
				objectType = createObjectType(mapId, bottomRightX, bottomRightY);

			// Up
			topLeftY = (int) (playerPosition.getY() + playerImage.getHeight() / 2);
			topRightY = (int) (playerPosition.getY() + playerImage.getHeight() / 2);
			topLeftX = (int) (playerPosition.getX());
			topRightX = (int) (playerPosition.getX() + playerImage.getWidth() - 1);
			if (objectType == null)
				objectType = createObjectType(mapId, topLeftX, topLeftY);
			if (objectType == null)
				objectType = createObjectType(mapId, topRightX, topRightY);

			// Down
			bottomLeftY = (int) (playerPosition.getY() + playerImage.getHeight() - 1);
			bottomRightY = (int) (playerPosition.getY() + playerImage.getHeight() - 1);
			bottomLeftX = (int) (playerPosition.getX());
			bottomRightX = (int) (playerPosition.getX() + playerImage.getWidth() - 1);
			if (objectType == null)
				objectType = createObjectType(mapId, bottomLeftX, bottomLeftY);
			if (objectType == null)
				objectType = createObjectType(mapId, bottomRightX, bottomRightY);
		}

		return objectType;
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

	@Override
	protected boolean checkProcessing() {
		return true;
	}

}
