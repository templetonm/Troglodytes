package com.turbonips.troglodytes.systems;

import org.apache.log4j.Logger;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Location;
import com.turbonips.troglodytes.components.ResourceRef;

public class CollisionResolution {
	
	private static final CollisionResolution instance = new CollisionResolution();
	private static final Logger logger = Logger.getLogger(CollisionResolution.class);
	private static World world = null;
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Location> locationMapper;
	private ComponentMapper<ResourceRef> resourceMapper;
	public enum CollisionDirection {
		NONE,
		UP, 
		DOWN,
		LEFT, 
		RIGHT,
		UP_RIGHT,
		UP_LEFT,
		DOWN_RIGHT,
		DOWN_LEFT
	};
	
	
	private CollisionResolution() {
	}
	
	public static CollisionResolution getInstance() {
		return instance;
	}
	
	public void initialize(World world) {
		this.world = world;
		this.movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		this.locationMapper = new ComponentMapper<Location>(Location.class, world);
		this.resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
	}
	
	// Figure out if adding the entities velocity will result in a collision
	public CollisionDirection getCollisionDirection(Entity entity, TiledMap map) {
		String entityResName = resourceMapper.get(entity).getResourceName();
		ResourceManager manager = ResourceManager.getInstance();
		Resource entityRes = manager.getResource(entityResName);
		Image entityFrame = getFrame(entityRes);
		int th = map.getTileHeight();
		int tw = map.getTileWidth();
		int eh = entityFrame.getHeight()-1;
		int ew = entityFrame.getWidth()-1;
		Movement entityMovement = movementMapper.get(entity);
		Location entityLocation = locationMapper.get(entity);
		Vector2f entityPosition = entityLocation.getPosition();
		Vector2f entityVelocity = entityMovement.getVelocity();
		Vector2f newEntityPosition = new Vector2f(entityPosition);
		newEntityPosition.add(entityVelocity);
		int step = 4;
		CollisionDirection collisionDirection = CollisionDirection.NONE;
		
		for (int x=0; x<ew; x=x+step) {
			// Up
			if (map.getTileId((int)(newEntityPosition.x+x)/tw, (int)newEntityPosition.y/th, 3) > 0) {
				
				if (map.getTileId((int)(entityPosition.x+x)/tw, (int)newEntityPosition.y/th, 3) > 0)  {
					collisionDirection = CollisionDirection.UP;
				}
			}
			
			// Down
			if (map.getTileId((int)(entityPosition.x+x)/tw, (int)(newEntityPosition.y+eh)/th, 3) > 0) {
				
				if (map.getTileId((int)(newEntityPosition.x+x)/tw, (int)(newEntityPosition.y+eh)/th, 3) > 0 ) {
					collisionDirection = CollisionDirection.DOWN;
				}
			}
		}
		
		for (int y=0; y<eh; y=y+step) {
			// Right
			if (map.getTileId((int)(newEntityPosition.x+ew)/tw, (int)(newEntityPosition.y+y)/th, 3) > 0) {
					
				if (map.getTileId((int)(newEntityPosition.x+ew)/tw, (int)(entityPosition.y+y)/th, 3) > 0 ) {
					if (collisionDirection == CollisionDirection.UP) {
						collisionDirection = CollisionDirection.UP_RIGHT;
					} else if (collisionDirection == CollisionDirection.DOWN) {
						collisionDirection = CollisionDirection.DOWN_RIGHT;
					} else {
						collisionDirection = CollisionDirection.RIGHT;
					}
				
				}
			}
			
			// Left
			if (map.getTileId((int)newEntityPosition.x/tw, (int)(newEntityPosition.y+y)/th, 3) > 0) {
					
				if (map.getTileId((int)(newEntityPosition.x)/tw, (int)(entityPosition.y+y)/th, 3) > 0 ) {
					if (collisionDirection == CollisionDirection.UP) {
						collisionDirection = CollisionDirection.UP_LEFT;
					} else if (collisionDirection == CollisionDirection.DOWN) {
						collisionDirection = CollisionDirection.DOWN_LEFT;
					} else {
						collisionDirection = CollisionDirection.LEFT;
					}
				}
			}
		
		}
		return collisionDirection;
	}
	
	/*
	 * Resolves wall collisions and returns the new player position with velocity
	 */
	public Vector2f resolveWallCollisions(Entity entity, TiledMap map) {
		String entityResName = resourceMapper.get(entity).getResourceName();
		ResourceManager manager = ResourceManager.getInstance();
		Resource entityRes = manager.getResource(entityResName);
		Image entityFrame = getFrame(entityRes);
		int th = map.getTileHeight();
		int tw = map.getTileWidth();
		int eh = entityFrame.getHeight()-1;
		int ew = entityFrame.getWidth()-1;
		Movement entityMovement = movementMapper.get(entity);
		Location entityLocation = locationMapper.get(entity);
		Vector2f entityPosition = entityLocation.getPosition();
		Vector2f entityVelocity = entityMovement.getVelocity();
		Vector2f newEntityPosition = new Vector2f(entityPosition);
		newEntityPosition.add(entityVelocity);
		int step = 4;
		
		for (int x=0; x<ew; x=x+step) {
			// Up
			if (map.getTileId((int)(newEntityPosition.x+x)/tw, (int)newEntityPosition.y/th, 3) > 0) {
				
				if (map.getTileId((int)(entityPosition.x+x)/tw, (int)newEntityPosition.y/th, 3) > 0)  {
					newEntityPosition.y = (int)(newEntityPosition.y/th)*th + th;
				}
			}
			
			// Down
			if (map.getTileId((int)(entityPosition.x+x)/tw, (int)(newEntityPosition.y+eh)/th, 3) > 0) {
				
				if (map.getTileId((int)(newEntityPosition.x+x)/tw, (int)(newEntityPosition.y+eh)/th, 3) > 0 ) {
					newEntityPosition.y = (int)((newEntityPosition.y+eh)/th)*th - (eh+1);
				}
			}
		}
		
		for (int y=0; y<eh; y=y+step) {
			// Right
			if (map.getTileId((int)(newEntityPosition.x+ew)/tw, (int)(newEntityPosition.y+y)/th, 3) > 0) {
					
				if (map.getTileId((int)(newEntityPosition.x+ew)/tw, (int)(entityPosition.y+y)/th, 3) > 0 ) {
					newEntityPosition.x = (int)((newEntityPosition.x+ew)/tw)*tw - (ew+1);
				}
			}
			
			// Left
			if (map.getTileId((int)newEntityPosition.x/tw, (int)(newEntityPosition.y+y)/th, 3) > 0) {
					
				if (map.getTileId((int)(newEntityPosition.x)/tw, (int)(entityPosition.y+y)/th, 3) > 0 ) {
					newEntityPosition.x = (int)(newEntityPosition.x/tw)*tw + tw;
				}
			}
		
		}

		return newEntityPosition;
	}
	
	public Vector2f resolveEntityCollision(Entity entity, Vector2f newEntityPosition, Entity otherEntity) {
		String entityResName = resourceMapper.get(entity).getResourceName();
		ResourceManager manager = ResourceManager.getInstance();
		Resource entityRes = manager.getResource(entityResName);
		Image entityFrame = getFrame(entityRes);
		Resource otherEntityRes = manager.getResource(entityResName);
		Image otherEntityFrame = getFrame(otherEntityRes);
		int eh = entityFrame.getHeight()-1;
		int ew = entityFrame.getWidth()-1;
		int oeh = otherEntityFrame.getHeight()-1;
		int oew = otherEntityFrame.getWidth()-1;
		Location entityLocation = locationMapper.get(entity);
		Vector2f entityPosition = entityLocation.getPosition();
		Vector2f otherEntityPosition = locationMapper.get(otherEntity).getPosition();
		int step = 1;
		
		for (int x=0; x<ew; x=x+step) {
			// Up
			if (newEntityPosition.x+x > otherEntityPosition.x && newEntityPosition.x < otherEntityPosition.x+oew &&
				newEntityPosition.y+eh > otherEntityPosition.y+oeh && newEntityPosition.y < otherEntityPosition.y+oeh) {
				
				if (entityPosition.x+x > otherEntityPosition.x && entityPosition.x < otherEntityPosition.x+oew &&
					newEntityPosition.y+eh > otherEntityPosition.y+oeh && newEntityPosition.y < otherEntityPosition.y+oeh) {
					newEntityPosition.y = otherEntityPosition.y+oeh+1;
				}
			}
			
			// Down
			if (newEntityPosition.x+x > otherEntityPosition.x && newEntityPosition.x < otherEntityPosition.x+oew &&
				newEntityPosition.y+eh > otherEntityPosition.y && newEntityPosition.y+eh < otherEntityPosition.y+oeh) {
				
				if (entityPosition.x+x > otherEntityPosition.x && entityPosition.x < otherEntityPosition.x+oew &&
					newEntityPosition.y+eh > otherEntityPosition.y && newEntityPosition.y+eh < otherEntityPosition.y+oeh) {
					newEntityPosition.y = otherEntityPosition.y-eh-1;
				}
			}
		}
		

		for (int y=0; y<eh; y=y+step) {
			// Left
			if (newEntityPosition.y+y > otherEntityPosition.y && newEntityPosition.y < otherEntityPosition.y+oeh &&
				newEntityPosition.x+ew > otherEntityPosition.x+oew && newEntityPosition.x < otherEntityPosition.x+oew) {
				
				if (entityPosition.y+y > otherEntityPosition.y && entityPosition.y < otherEntityPosition.y+oeh &&
					newEntityPosition.x+ew > otherEntityPosition.x+oew && newEntityPosition.x < otherEntityPosition.x+oew) {
					newEntityPosition.x = otherEntityPosition.x+oew+1;
				}
			}
			
			// Right
			if (newEntityPosition.y+y > otherEntityPosition.y && newEntityPosition.y < otherEntityPosition.y+oeh &&
				newEntityPosition.x+ew > otherEntityPosition.x && newEntityPosition.x+ew < otherEntityPosition.x+oew) {
				
				if (entityPosition.y+y > otherEntityPosition.y && entityPosition.y < otherEntityPosition.y+oeh &&
					newEntityPosition.x+ew > otherEntityPosition.x && newEntityPosition.x+ew < otherEntityPosition.x+oew) {
					newEntityPosition.x = otherEntityPosition.x-ew-1;
				}
			}
		}

		return newEntityPosition;
	}
	
	
	
	
	private Image getFrame(Resource resource) {
		switch (resource.getType()) {
			case CREATURE_ANIMATION:
				return ((CreatureAnimation)resource.getObject()).getCurrent().getCurrentFrame();
			case IMAGE:
				return (Image)resource.getObject();
			default:
				return null;
		}
	}

	
}
