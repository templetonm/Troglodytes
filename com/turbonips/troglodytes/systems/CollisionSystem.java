package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.AnimationCreature;
import com.turbonips.troglodytes.components.Collision;
import com.turbonips.troglodytes.components.Sliding;
import com.turbonips.troglodytes.components.SpatialForm;
import com.turbonips.troglodytes.components.Transform;

public class CollisionSystem extends EntitySystem {
	private final GameContainer container;
	private ComponentMapper<Transform> positionMapper;
	private ComponentMapper<Collision> collisionMapper;
	private ComponentMapper<SpatialForm> spatialFormMapper;
	private ComponentMapper<AnimationCreature> animationCreatureMapper;

	public CollisionSystem(GameContainer container) {
		super(Transform.class, Collision.class, SpatialForm.class);
		this.container = container;
	}
	
	@Override
	protected void initialize() {
		positionMapper = new ComponentMapper<Transform>(Transform.class, world);
		collisionMapper = new ComponentMapper<Collision>(Collision.class, world);
		spatialFormMapper = new ComponentMapper<SpatialForm>(SpatialForm.class, world);
		animationCreatureMapper = new ComponentMapper<AnimationCreature>(AnimationCreature.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> layers = world.getGroupManager().getEntities("LAYER");
		ImmutableBag<Entity> creatures = world.getGroupManager().getEntities("CREATURE");
		
		TiledMap tiledMap = (TiledMap)spatialFormMapper.get(layers.get(0)).getForm();

		for (int a=0; a<creatures.size(); a++) {
			Entity creature = creatures.get(a);
			Transform position = positionMapper.get(creature);
			Collision collision = collisionMapper.get(creature);
			Image sprite = (Image)animationCreatureMapper.get(creature).getCurrent().getImage(0);
			updateCollision(position, collision, tiledMap, sprite);
			
			// TODO make this only update for PLAYER groups
			for (int i=0; i<layers.size(); i++) {
				Entity layer = layers.get(i);
				collisionMapper.get(layer).setColliding(collision.isCollidingUp(), collision.isCollidingDown(), collision.isCollidingLeft(), collision.isCollidingRight());
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
	
	private void updateCollision(Transform position, Collision collision, TiledMap tiledMap, Image sprite) {
		collision.setColliding(false, false, false, false);
		
		// Bounds checks
		if (position.getX()-position.getSpeed() < 0) { 
			collision.setCollidingLeft(true);
		}
		if (position.getY()-position.getSpeed()+sprite.getHeight()/2 < 0) { 
			collision.setCollidingUp(true);
		}
		if (position.getX()+position.getSpeed() > tiledMap.getWidth()*tiledMap.getTileWidth()-sprite.getWidth()) {
			collision.setCollidingRight(true);
		}
		if (position.getY()+position.getSpeed() > tiledMap.getHeight()*tiledMap.getTileHeight()-sprite.getHeight()) {
			collision.setCollidingDown(true);
		}
		
		// 32 (in this case) represents the tile size
		// Wall checks
		if (!collision.isCollidingLeft()) {
			int topLeftY = (int)(position.getY()+sprite.getHeight()/2)/tiledMap.getTileHeight();
			int bottomLeftY = (int)(position.getY()+sprite.getHeight()-1)/tiledMap.getTileHeight();
			int topLeftX = (int)(position.getX()-position.getSpeed())/tiledMap.getTileWidth();
			int bottomLeftX = (int)(position.getX()-position.getSpeed())/tiledMap.getTileWidth();
			if ((wallCollision(tiledMap, topLeftX, topLeftY) || wallCollision(tiledMap, bottomLeftX, bottomLeftY))) {
				collision.setCollidingLeft(true);
			}
		}
		
		if (!collision.isCollidingRight()) {
			int topRightY = (int)(position.getY()+sprite.getHeight()/2)/tiledMap.getTileHeight();
			int bottomRightY = (int)(position.getY()+sprite.getHeight()-1)/tiledMap.getTileHeight();
			int topRightX = (int)(position.getX()+position.getSpeed()+sprite.getWidth()-1)/tiledMap.getTileWidth();
			int bottomRightX = (int)(position.getX()+position.getSpeed()+sprite.getWidth()-1)/tiledMap.getTileWidth();
			if ((wallCollision(tiledMap, topRightX, topRightY) || wallCollision(tiledMap, bottomRightX, bottomRightY))) {
				collision.setCollidingRight(true);
			}
		}
		
		if (!collision.isCollidingUp()) {
			int topLeftY = (int)(position.getY()-position.getSpeed()+sprite.getHeight()/2)/tiledMap.getTileHeight();
			int topRightY = (int)(position.getY()-position.getSpeed()+sprite.getHeight()/2)/tiledMap.getTileHeight();
			int topLeftX = (int)(position.getX())/tiledMap.getTileWidth();
			int topRightX = (int)(position.getX()+sprite.getWidth()-1)/tiledMap.getTileWidth();
			if ((wallCollision(tiledMap, topLeftX, topLeftY) || wallCollision(tiledMap, topRightX, topRightY))) {
				collision.setCollidingUp(true);
			}
		}
		
		if (!collision.isCollidingDown()) {
			int bottomLeftY = (int)(position.getY()+sprite.getHeight()+position.getSpeed()-1)/tiledMap.getTileHeight();
			int bottomRightY = (int)(position.getY()+sprite.getHeight()+position.getSpeed()-1)/tiledMap.getTileHeight();
			int bottomLeftX = (int)(position.getX())/tiledMap.getTileWidth();
			int bottomRightX = (int)(position.getX()+sprite.getWidth()-1)/tiledMap.getTileWidth();
			if ((wallCollision(tiledMap, bottomLeftX, bottomLeftY) || wallCollision(tiledMap, bottomRightX, bottomRightY))) {
				collision.setCollidingDown(true);
			}
		}
		
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}


}
