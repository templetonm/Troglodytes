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

public class PlayerCollisionSystem extends BaseEntitySystem {
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<Renderable> renderableMapper;
	private ComponentMapper<Collision> collisionMapper;

	public PlayerCollisionSystem() {
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
						for (int e=0; e<enemies.size(); e++) {
							Entity enemy = enemies.get(e);
							if (renderableMapper.get(enemy) != null) {
								updateCollidingWithEnemy(player, enemy);
							}
						}
					}
				}
			}
		}

	}
	
	// Player position is the upper left of image
	private void updateCollidingWithEnemy(Entity player, Entity enemy) {
		Position playerPosition = positionMapper.get(player);
		Collision playerCollision = collisionMapper.get(player);
		Position enemyPosition = positionMapper.get(enemy);
		Collision enemyCollision = collisionMapper.get(enemy);
		Image playerImage = getImage(player);
		Image enemyImage = getImage(enemy);
		int pLeft;
		int eLeft;
		int pRight;
		int eRight;
		int pTop;
		int eTop;
		int pBottom;
		int eBottom;
		int pSpeed;
		int eSpeed;
		
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
			eSpeed = (int) (enemyPosition.getSpeed());
			
			// Moving left or right
			if (pTop <= eBottom && pBottom >= eTop)
			{
				// Player left
				if (pLeft - pSpeed <= eRight && !(pRight < eLeft)) {
					playerCollision.setCollidingLeft(true);
				}
				// Player right
				if (pRight + pSpeed >= eLeft && !(pLeft > eRight)) {
					playerCollision.setCollidingRight(true);
				}
			}
			// Moving up or down
			if (pLeft <= eRight && pRight >= eLeft)
			{
				// Player up
				if (pTop - pSpeed <= eBottom && !(pBottom < eTop)) {
					playerCollision.setCollidingUp(true);
				}
				// Player down
				else if (pBottom + pSpeed >= eTop && !(pTop > eBottom)) {
					playerCollision.setCollidingDown(true);
				}
			}
		}
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
