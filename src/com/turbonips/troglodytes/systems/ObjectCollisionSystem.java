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
			Resource resource = renderableMapper.get(layers.get(0)).getResource();
			if (resource != null) {
				for (int p = 0; p < players.size(); p++) {
					Entity player = players.get(p);
					Position playerPosition = positionMapper.get(player);
					if (renderableMapper.get(player) != null) {

						for (int e=0; e<enemies.size(); e++) {
							Entity enemy = enemies.get(e);
							if (renderableMapper.get(enemy) != null) {
								updateCollidingWithEnemy(player, enemy);
							}
						}

						/*
						if (objectType != null) {
							switch (objectType.getType()) {
								case ObjectType.WARP_OBJECT:
									WarpObject warpObject = (WarpObject) objectType;
									logger.info("Adding warp to player " + warpObject.toString());
									player.addComponent(warpObject);
									player.refresh();
									break;
							}
						}*/
					}
				}
			}
		}

	}

	/*
	ObjectType createObjectType(String mapId, int x, int y) {
		TiledMap map = (TiledMap) ResourceManager.getInstance().getResource(mapId).getObject();

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
	}*/
	
	// Player position is the upper left of image
	private void updateCollidingWithEnemy(Entity player, Entity enemy) {
		Position playerPosition = positionMapper.get(player);
		Position enemyPosition = positionMapper.get(enemy);
		Image playerImage = getSprite(player);
		Image enemyImage = getSprite(enemy);
		Collision playerCollision = collisionMapper.get(player);
		int pTopLeftY;
		int pBottomLeftY;
		int pTopLeftX;
		int pBottomLeftX;
		int pTopRightY;
		int pBottomRightY;
		int pTopRightX;
		int pBottomRightX;
		int eTopLeftY;
		int eBottomLeftY;
		int eTopLeftX;
		int eBottomLeftX;
		int eTopRightY;
		int eBottomRightY;
		int eTopRightX;
		int eBottomRightX;
		int pLeft;
		int eLeft;
		int pRight;
		int eRight;
		int pTop;
		int eTop;
		int pBottom;
		int eBottom;
		int pSpeed;
		
		if (playerImage != null && enemyImage != null) {
			
			// Player going left
			pTop = (int) (playerPosition.getY());
			eTop = (int) (enemyPosition.getY());
			// Adding the player image size to the position is 1 pixel greater than
			// the actual height since the actual position value IS counted in the size
			pBottom = (int) (playerPosition.getY() + playerImage.getHeight()-1);
			eBottom = (int) (enemyPosition.getY() + enemyImage.getHeight()-1);
			pLeft = (int) (playerPosition.getX());
			eRight = (int) (enemyPosition.getX() + enemyImage.getWidth()-1);
			pRight = (int) (playerPosition.getX() + playerImage.getWidth()-1);
			eLeft = (int) (enemyPosition.getX());
			pSpeed = (int) (playerPosition.getSpeed());
			

			
			if (pTop <= eBottom && pBottom >= eTop)
			{
				if (pLeft - pSpeed <= eRight && !(pRight < eLeft)) {
					playerCollision.setCollidingLeft(true);
				}
				if (pRight + pSpeed >= eLeft && !(pLeft > eRight)) {
					playerCollision.setCollidingRight(true);
				}
			}
			
			if (pLeft <= eRight && pRight >= eLeft)
			{
				if (pTop - pSpeed <= eBottom && !(pBottom < eTop)) {
					playerCollision.setCollidingUp(true);
				}
				if (pBottom + pSpeed >= eTop && !(pTop > eBottom)) {
					playerCollision.setCollidingDown(true);
				}
			}
			// Player going right
			/*if (pTopY <= eBottomY && pBottomY >= eTopY && pRightX+pSpeed >= eLeftX && !(pLeftX-pSpeed <= eRightX))
			{
				playerCollision.setCollidingRight(true);
			}
			
			// Player going down
			if (pRightX >= eLeftX && pLeftX <= eRightX && pBottomY+pSpeed >= eTopY && !(pTopY-pSpeed <= eBottomY))
			{
				playerCollision.setCollidingDown(true);
			}
			
			// Player going up
			if (pRightX >= eLeftX && pLeftX <= eRightX && pTopY-pSpeed <= eBottomY && !(pBottomY+pSpeed >= eTopY))
			{
				playerCollision.setCollidingUp(true);
			}*/
		}
		
		/*
		if (sprite != null && position != null) {
			topLeftY = (int) (position.getY() + sprite.getHeight() / 2);
			bottomLeftY = (int) (position.getY() + sprite.getHeight() - 1);
			topLeftX = (int) (position.getX());
			bottomLeftX = (int) (position.getX());
			if (objectType == null)
				objectType = createObjectType(mapId, topLeftX, topLeftY);
			if (objectType == null)
				objectType = createObjectType(mapId, bottomLeftX, bottomLeftY);

			// Right
			topRightY = (int) (position.getY() + sprite.getHeight() / 2);
			bottomRightY = (int) (position.getY() + sprite.getHeight() - 1);
			topRightX = (int) (position.getX() + sprite.getWidth() - 1);
			bottomRightX = (int) (position.getX() + sprite.getWidth() - 1);
			if (objectType == null)
				objectType = createObjectType(mapId, topRightX, topRightY);
			if (objectType == null)
				objectType = createObjectType(mapId, bottomRightX, bottomRightY);

			// Up
			topLeftY = (int) (position.getY() + sprite.getHeight() / 2);
			topRightY = (int) (position.getY() + sprite.getHeight() / 2);
			topLeftX = (int) (position.getX());
			topRightX = (int) (position.getX() + sprite.getWidth() - 1);
			if (objectType == null)
				objectType = createObjectType(mapId, topLeftX, topLeftY);
			if (objectType == null)
				objectType = createObjectType(mapId, topRightX, topRightY);

			// Down
			bottomLeftY = (int) (position.getY() + sprite.getHeight() - 1);
			bottomRightY = (int) (position.getY() + sprite.getHeight() - 1);
			bottomLeftX = (int) (position.getX());
			bottomRightX = (int) (position.getX() + sprite.getWidth() - 1);
			if (objectType == null)
				objectType = createObjectType(mapId, bottomLeftX, bottomLeftY);
			if (objectType == null)
				objectType = createObjectType(mapId, bottomRightX, bottomRightY);
		}*/
	}

	private boolean isCollidingWithWarp(String mapId, Entity player) {
		boolean colliding = false;
		
		int topLeftY;
		int bottomLeftY;
		int topLeftX;
		int bottomLeftX;
		int topRightY;
		int bottomRightY;
		int topRightX;
		int bottomRightX;
		
		
		return colliding;
		
		/*
		TiledMap map = (TiledMap) ResourceManager.getInstance().getResource(mapId).getObject();
		ObjectType objectType = null;
		// Object checks

		// Left

		if (sprite != null && position != null) {
			topLeftY = (int) (position.getY() + sprite.getHeight() / 2);
			bottomLeftY = (int) (position.getY() + sprite.getHeight() - 1);
			topLeftX = (int) (position.getX());
			bottomLeftX = (int) (position.getX());
			if (objectType == null)
				objectType = createObjectType(mapId, topLeftX, topLeftY);
			if (objectType == null)
				objectType = createObjectType(mapId, bottomLeftX, bottomLeftY);

			// Right
			topRightY = (int) (position.getY() + sprite.getHeight() / 2);
			bottomRightY = (int) (position.getY() + sprite.getHeight() - 1);
			topRightX = (int) (position.getX() + sprite.getWidth() - 1);
			bottomRightX = (int) (position.getX() + sprite.getWidth() - 1);
			if (objectType == null)
				objectType = createObjectType(mapId, topRightX, topRightY);
			if (objectType == null)
				objectType = createObjectType(mapId, bottomRightX, bottomRightY);

			// Up
			topLeftY = (int) (position.getY() + sprite.getHeight() / 2);
			topRightY = (int) (position.getY() + sprite.getHeight() / 2);
			topLeftX = (int) (position.getX());
			topRightX = (int) (position.getX() + sprite.getWidth() - 1);
			if (objectType == null)
				objectType = createObjectType(mapId, topLeftX, topLeftY);
			if (objectType == null)
				objectType = createObjectType(mapId, topRightX, topRightY);

			// Down
			bottomLeftY = (int) (position.getY() + sprite.getHeight() - 1);
			bottomRightY = (int) (position.getY() + sprite.getHeight() - 1);
			bottomLeftX = (int) (position.getX());
			bottomRightX = (int) (position.getX() + sprite.getWidth() - 1);
			if (objectType == null)
				objectType = createObjectType(mapId, bottomLeftX, bottomLeftY);
			if (objectType == null)
				objectType = createObjectType(mapId, bottomRightX, bottomRightY);
		}

		return objectType;*/
	}
	
	private Image getSprite(Entity entity) {
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
