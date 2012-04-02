package com.turbonips.troglodytes.systems;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.components.Collision;
import com.turbonips.troglodytes.components.PushVelocity;
import com.turbonips.troglodytes.components.Renderable;
import com.turbonips.troglodytes.components.Position;

public class WallCollisionSystem extends BaseEntitySystem {
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<Collision> collisionMapper;
	private ComponentMapper<Renderable> renderableMapper;
	private ComponentMapper<PushVelocity> pushVelocityMapper;

	public WallCollisionSystem() {
	}
	
	@Override
	protected void initialize() {
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		collisionMapper = new ComponentMapper<Collision>(Collision.class, world);
		renderableMapper = new ComponentMapper<Renderable>(Renderable.class, world);
		pushVelocityMapper = new ComponentMapper<PushVelocity>(PushVelocity.class, world); 
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> layers = world.getGroupManager().getEntities("LAYER");
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		
		if (!layers.isEmpty()) {
			Resource resource = renderableMapper.get(layers.get(0)).getResource();
			if (resource != null) {
				TiledMap tiledMap = (TiledMap)resource.getObject();
		
				for (int p=0; p<players.size(); p++) {
					Entity player = players.get(p);
					Position playerPosition = positionMapper.get(player);
					Collision playerCollision = collisionMapper.get(player);
					
					if (playerCollision != null && playerPosition != null) {
						Resource playerResource = renderableMapper.get(player).getResource();
						Image sprite = null;
						switch (playerResource.getType()) {
							case CREATURE_ANIMATION:
								sprite = ((CreatureAnimation)playerResource.getObject()).getIdleDown().getCurrentFrame();
								break;
							case IMAGE:
								sprite = (Image)playerResource.getObject();
								break;
							default:
								logger.error("player resource type is " + playerResource.getType());
								break;
						}
						updateCollision(player, tiledMap, sprite);
					}
				}
				
				for (int e=0; e<enemies.size(); e++) {
					Entity enemy = enemies.get(e);
					Collision enemyCollision = collisionMapper.get(enemy);
					Position enemyPosition = positionMapper.get(enemy);
					
					if (enemyCollision != null && enemyPosition != null) {
						Resource enemyResource = renderableMapper.get(enemy).getResource();
						Image sprite = null;
						switch (enemyResource.getType()) {
							case CREATURE_ANIMATION:
								sprite = ((CreatureAnimation)enemyResource.getObject()).getIdleDown().getCurrentFrame();
								break;
							case IMAGE:
								sprite = (Image)enemyResource.getObject();
								break;
							default:
								logger.error("enemy resource type is " + enemyResource.getType());
								break;
						}
						updateCollision(enemy, tiledMap, sprite);
						updatePushCollision(enemy, tiledMap, sprite);
					}
				}
			}
		}
		
	}
	
	boolean wallCollision(TiledMap map, int x, int y) {
		if (x >= map.getWidth() || x < 0) {
			return true;
		}
		if (y >= map.getHeight() || y < 0) {
			return true;
		}
		if(map.getTileId(x, y, 3) > 0) {
			return true;
		}
		return false;
	}
	
	private void updateCollision(Entity entity, TiledMap tiledMap, Image sprite) {
		Position position = positionMapper.get(entity);
		Collision collision = collisionMapper.get(entity);
		PushVelocity pushVelocity = pushVelocityMapper.get(entity);
		Vector2f speed;
		
		if (pushVelocity == null) {
			speed = new Vector2f(position.getSpeed(), position.getSpeed());
			collision.setColliding(false, false, false, false);
			
			// Bounds checks
			if (position.getX()-speed.x < 0) { 
				collision.setCollidingLeft(true);
			}
			if (position.getY()-speed.y+sprite.getHeight()/2 < 0) { 
				collision.setCollidingUp(true);
			}
			if (position.getX()+speed.x > tiledMap.getWidth()*tiledMap.getTileWidth()-sprite.getWidth()) {
				collision.setCollidingRight(true);
			}
			if (position.getY()+speed.y > tiledMap.getHeight()*tiledMap.getTileHeight()-sprite.getHeight()) {
				collision.setCollidingDown(true);
			}
			
			// Wall checks
			if (!collision.isCollidingLeft()) {
				int topLeftY = (int)(position.getY()+sprite.getHeight()/2)/tiledMap.getTileHeight();
				int bottomLeftY = (int)(position.getY()+sprite.getHeight()-1)/tiledMap.getTileHeight();
				int topLeftX = (int)(position.getX()-speed.x)/tiledMap.getTileWidth();
				int bottomLeftX = (int)(position.getX()-speed.x)/tiledMap.getTileWidth();
				if ((wallCollision(tiledMap, topLeftX, topLeftY) || wallCollision(tiledMap, bottomLeftX, bottomLeftY))) {
					collision.setCollidingLeft(true);
				}
			}
			
			if (!collision.isCollidingRight()) {
				int topRightY = (int)(position.getY()+sprite.getHeight()/2)/tiledMap.getTileHeight();
				int bottomRightY = (int)(position.getY()+sprite.getHeight()-1)/tiledMap.getTileHeight();
				int topRightX = (int)(position.getX()+speed.x+sprite.getWidth()-1)/tiledMap.getTileWidth();
				int bottomRightX = (int)(position.getX()+speed.x+sprite.getWidth()-1)/tiledMap.getTileWidth();
				if ((wallCollision(tiledMap, topRightX, topRightY) || wallCollision(tiledMap, bottomRightX, bottomRightY))) {
					collision.setCollidingRight(true);
				}
			}
			
			if (!collision.isCollidingUp()) {
				int topLeftY = (int)(position.getY()-speed.y+sprite.getHeight()/2)/tiledMap.getTileHeight();
				int topRightY = (int)(position.getY()-speed.y+sprite.getHeight()/2)/tiledMap.getTileHeight();
				int topLeftX = (int)(position.getX())/tiledMap.getTileWidth();
				int topRightX = (int)(position.getX()+sprite.getWidth()-1)/tiledMap.getTileWidth();
				if ((wallCollision(tiledMap, topLeftX, topLeftY) || wallCollision(tiledMap, topRightX, topRightY))) {
					collision.setCollidingUp(true);
				}
			}
			
			if (!collision.isCollidingDown()) {
				int bottomLeftY = (int)(position.getY()+sprite.getHeight()+speed.y-1)/tiledMap.getTileHeight();
				int bottomRightY = (int)(position.getY()+sprite.getHeight()+speed.y-1)/tiledMap.getTileHeight();
				int bottomLeftX = (int)(position.getX())/tiledMap.getTileWidth();
				int bottomRightX = (int)(position.getX()+sprite.getWidth()-1)/tiledMap.getTileWidth();
				if ((wallCollision(tiledMap, bottomLeftX, bottomLeftY) || wallCollision(tiledMap, bottomRightX, bottomRightY))) {
					collision.setCollidingDown(true);
				}
			}
		
		}
		
	}
	
	private void updatePushCollision(Entity entity, TiledMap tiledMap, Image sprite) {
		Position position = positionMapper.get(entity);
		Collision collision = collisionMapper.get(entity);
		PushVelocity pushVelocity = pushVelocityMapper.get(entity);
		Vector2f speed;
		
		if (pushVelocity != null) {
			speed = pushVelocity.getVelocity();

			collision.setColliding(false, false, false, false);
			
			// Bounds checks
			if (position.getX()+speed.x < 0) { 
				collision.setCollidingLeft(true);
			}
			if (position.getY()+speed.y+sprite.getHeight()/2 < 0) { 
				collision.setCollidingUp(true);
			}
			if (position.getX()+speed.x > tiledMap.getWidth()*tiledMap.getTileWidth()-sprite.getWidth()) {
				collision.setCollidingRight(true);
			}
			if (position.getY()+speed.y > tiledMap.getHeight()*tiledMap.getTileHeight()-sprite.getHeight()) {
				collision.setCollidingDown(true);
			}
			
			// Wall checks
			if (!collision.isCollidingLeft()) {
				int topLeftY = (int)(position.getY()+sprite.getHeight()/2)/tiledMap.getTileHeight();
				int bottomLeftY = (int)(position.getY()+sprite.getHeight()-1)/tiledMap.getTileHeight();
				int topLeftX = (int)(position.getX()+speed.x)/tiledMap.getTileWidth();
				int bottomLeftX = (int)(position.getX()+speed.x)/tiledMap.getTileWidth();
				if ((wallCollision(tiledMap, topLeftX, topLeftY) || wallCollision(tiledMap, bottomLeftX, bottomLeftY))) {
					collision.setCollidingLeft(true);
				}
			}
			
			if (!collision.isCollidingRight()) {
				int topRightY = (int)(position.getY()+sprite.getHeight()/2)/tiledMap.getTileHeight();
				int bottomRightY = (int)(position.getY()+sprite.getHeight()-1)/tiledMap.getTileHeight();
				int topRightX = (int)(position.getX()+speed.x+sprite.getWidth()-1)/tiledMap.getTileWidth();
				int bottomRightX = (int)(position.getX()+speed.x+sprite.getWidth()-1)/tiledMap.getTileWidth();
				if ((wallCollision(tiledMap, topRightX, topRightY) || wallCollision(tiledMap, bottomRightX, bottomRightY))) {
					collision.setCollidingRight(true);
				}
			}
			
			if (!collision.isCollidingUp()) {
				int topLeftY = (int)(position.getY()+speed.y+sprite.getHeight()/2)/tiledMap.getTileHeight();
				int topRightY = (int)(position.getY()+speed.y+sprite.getHeight()/2)/tiledMap.getTileHeight();
				int topLeftX = (int)(position.getX())/tiledMap.getTileWidth();
				int topRightX = (int)(position.getX()+sprite.getWidth()-1)/tiledMap.getTileWidth();
				if ((wallCollision(tiledMap, topLeftX, topLeftY) || wallCollision(tiledMap, topRightX, topRightY))) {
					collision.setCollidingUp(true);
				}
			}
			
			if (!collision.isCollidingDown()) {
				int bottomLeftY = (int)(position.getY()+sprite.getHeight()+speed.y-1)/tiledMap.getTileHeight();
				int bottomRightY = (int)(position.getY()+sprite.getHeight()+speed.y-1)/tiledMap.getTileHeight();
				int bottomLeftX = (int)(position.getX())/tiledMap.getTileWidth();
				int bottomRightX = (int)(position.getX()+sprite.getWidth()-1)/tiledMap.getTileWidth();
				if ((wallCollision(tiledMap, bottomLeftX, bottomLeftY) || wallCollision(tiledMap, bottomRightX, bottomRightY))) {
					collision.setCollidingDown(true);
				}
			}
		}
	}


	@Override
	protected boolean checkProcessing() {
		return true;
	}


}
