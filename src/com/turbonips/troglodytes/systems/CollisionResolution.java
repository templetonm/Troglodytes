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
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.ResourceRef;

public class CollisionResolution {
	
	private static final CollisionResolution instance = new CollisionResolution();
	private static final Logger logger = Logger.getLogger(CollisionResolution.class);
	private static World world = null;
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<ResourceRef> resourceMapper;
	
	private CollisionResolution() {
	}
	
	public static CollisionResolution getInstance() {
		return instance;
	}
	
	public void initialize(World world) {
		this.world = world;
		this.movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		this.positionMapper = new ComponentMapper<Position>(Position.class, world);
		this.resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
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
		int eh = entityFrame.getHeight();
		int ew = entityFrame.getWidth();
		Movement entityMovement = movementMapper.get(entity);
		Position entityPos = positionMapper.get(entity);
		Vector2f entityPosition = entityPos.getPosition();
		Vector2f entityVelocity = entityMovement.getVelocity();
		Vector2f newEntityPosition = new Vector2f(entityPosition);
		newEntityPosition.add(entityVelocity);
		Integer wx = ((int)(newEntityPosition.x/tw) * tw),
				wy = ((int)(newEntityPosition.y/th) * th);
		
		// Upper left
		if (map.getTileId((int)newEntityPosition.x/tw, (int)newEntityPosition.y/th, 3) > 0) {
			wx = ((int)(newEntityPosition.x/tw) * tw) + tw;
			wy = ((int)(newEntityPosition.y/th) * th) + th;
			logger.info("upper left");
			
			if (map.getTileId((int)entityPosition.x/tw, (int)newEntityPosition.y/th, 3) > 0 ||
				map.getTileId((int)((entityPosition.x)/tw + 0.5f), (int)newEntityPosition.y/th, 3) > 0) {
				newEntityPosition.y = wy;
			}
				
			if (map.getTileId((int)newEntityPosition.x/tw, (int)(entityPosition.y)/th, 3) > 0 ||
					 map.getTileId((int)newEntityPosition.x/tw, (int)((entityPosition.y)/th + 0.5f), 3) > 0) {
				newEntityPosition.x = wx;
			} 
		}
		
		// Upper right
		if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)newEntityPosition.y/th, 3) > 0) {
			wx = ((int)(newEntityPosition.x/tw) * tw);
			wy = ((int)(newEntityPosition.y/th) * th) + th;
			logger.info("upper right");
			
			if (map.getTileId((int)(entityPosition.x+ew-1)/tw, (int)newEntityPosition.y/th, 3) > 0 ||
				map.getTileId((int)((entityPosition.x+ew-1)/tw - 0.5f), (int)newEntityPosition.y/th, 3) > 0) {
				newEntityPosition.y = wy;
			}
				
			if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)entityPosition.y/th, 3) > 0 ||
					 map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)(entityPosition.y/th + 0.5f), 3) > 0) {
				newEntityPosition.x = wx;
			}
		}
		
		// Center right
		if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)(newEntityPosition.y+(eh-1)/2)/th, 3) > 0) {
			wx = ((int)(newEntityPosition.x/tw) * tw);
			wy = ((int)(newEntityPosition.y/th) * th) + th/2;
			logger.info("upper center right");
			
			if (map.getTileId((int)(entityPosition.x+ew-1)/tw, (int)(newEntityPosition.y+(eh-1)/2)/th, 3) > 0 ||
				map.getTileId((int)((entityPosition.x+ew-1)/tw - 0.5f), (int)(newEntityPosition.y+(eh-1)/2)/th, 3) > 0) {
				newEntityPosition.y = wy;
			}
				
			if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)(entityPosition.y+(eh-1)/2)/th, 3) > 0 ||
				map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)((entityPosition.y+(eh-1)/2)/th + 0.5f), 3) > 0) {
				newEntityPosition.x = wx;
			}
		}
		
		// Center left
		if (map.getTileId((int)(newEntityPosition.x)/tw, (int)(newEntityPosition.y+(eh-1)/2)/th, 3) > 0) {
			wx = ((int)(newEntityPosition.x/tw) * tw) + tw;
			wy = ((int)(newEntityPosition.y/th) * th) + th/2;
			logger.info("upper center left");
			
			if (map.getTileId((int)(entityPosition.x)/tw, (int)(newEntityPosition.y+(eh-1)/2)/th, 3) > 0 ||
				map.getTileId((int)((entityPosition.x)/tw + 0.5f), (int)(newEntityPosition.y+(eh-1)/2)/th, 3) > 0) {
				newEntityPosition.y = wy;
			}
				
			if (map.getTileId((int)(newEntityPosition.x)/tw, (int)(entityPosition.y+(eh-1)/2)/th, 3) > 0 ||
				map.getTileId((int)(newEntityPosition.x)/tw, (int)((entityPosition.y+(eh-1)/2)/th + 0.5f), 3) > 0) {
				newEntityPosition.x = wx;
			}
		}
		
		// Lower left
		if (map.getTileId((int)newEntityPosition.x/tw, (int)(newEntityPosition.y+eh-1)/th, 3) > 0) {
			wx = ((int)(newEntityPosition.x/tw) * tw) + tw;
			wy = ((int)(newEntityPosition.y/th) * th);
			logger.info("lower left");
			
			if (map.getTileId((int)(entityPosition.x)/tw, (int)(newEntityPosition.y+eh-1)/th, 3) > 0 ||
				map.getTileId((int)((entityPosition.x)/tw + 0.5f), (int)(newEntityPosition.y+eh-1)/th, 3) > 0) {
				newEntityPosition.y = wy;
			}
				
			if (map.getTileId((int)(newEntityPosition.x)/tw, (int)(entityPosition.y+eh-1)/th, 3) > 0 ||
					 map.getTileId((int)(newEntityPosition.x)/tw, (int)((entityPosition.y+eh-1)/th - 0.5f), 3) > 0) {
				newEntityPosition.x = wx;
			}
		}
		
		// Lower right
		if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)(newEntityPosition.y+eh-1)/th, 3) > 0) {
			wx = ((int)(newEntityPosition.x/tw) * tw);
			wy = ((int)(newEntityPosition.y/th) * th);
			logger.info("lower right");
			
			if (map.getTileId((int)(entityPosition.x+ew-1)/tw, (int)(newEntityPosition.y+eh-1)/th, 3) > 0 ||
				map.getTileId((int)((entityPosition.x+ew-1)/tw - 0.5f), (int)(newEntityPosition.y+eh-1)/th, 3) > 0) {
				newEntityPosition.y = wy;
			}
				
			if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)(entityPosition.y+eh-1)/th, 3) > 0 ||
					 map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)((entityPosition.y+eh-1)/th - 0.5f), 3) > 0) {
				newEntityPosition.x = wx;
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
