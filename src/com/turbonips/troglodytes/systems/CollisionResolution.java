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
		int accuracy = 3;
		
		// Upper left
		if (map.getTileId((int)newEntityPosition.x/tw, (int)newEntityPosition.y/th, 3) > 0) {
			wx = ((int)(newEntityPosition.x/tw) * tw) + tw;
			wy = ((int)(newEntityPosition.y/th) * th) + th;
			
			if (map.getTileId((int)entityPosition.x/tw, (int)newEntityPosition.y/th, 3) > 0)  {
				newEntityPosition.y = wy;
			}
				
			else if (map.getTileId((int)newEntityPosition.x/tw, (int)(entityPosition.y)/th, 3) > 0)  {
				newEntityPosition.x = wx;
			} 
		}
		
		// Upper right
		if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)newEntityPosition.y/th, 3) > 0) {
			wx = ((int)(newEntityPosition.x/tw) * tw);
			wy = ((int)(newEntityPosition.y/th) * th) + th;
			
			if (map.getTileId((int)(entityPosition.x+ew-1)/tw, (int)newEntityPosition.y/th, 3) > 0) {
				newEntityPosition.y = wy;
			}
				
			else if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)entityPosition.y/th, 3) > 0 ) {
				newEntityPosition.x = wx;
			}
		}
		
		// Lower right
		if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)(newEntityPosition.y+eh-1)/th, 3) > 0) {
			wx = ((int)(newEntityPosition.x/tw) * tw);
			wy = ((int)(newEntityPosition.y/th) * th);
			
			if (map.getTileId((int)(entityPosition.x+ew-1)/tw, (int)(newEntityPosition.y+eh-1)/th, 3) > 0 ) {
				newEntityPosition.y = wy;
			}
				
			else if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)(entityPosition.y+eh-1)/th, 3) > 0 ) {
				newEntityPosition.x = wx;
			}
		}
		
		// Lower left
		if (map.getTileId((int)newEntityPosition.x/tw, (int)(newEntityPosition.y+eh-1)/th, 3) > 0) {
			wx = ((int)(newEntityPosition.x/tw) * tw) + tw;
			wy = ((int)(newEntityPosition.y/th) * th);
			
			if (map.getTileId((int)(entityPosition.x)/tw, (int)(newEntityPosition.y+eh-1)/th, 3) > 0 ) {
				newEntityPosition.y = wy;
			}
				
			else if (map.getTileId((int)(newEntityPosition.x)/tw, (int)(entityPosition.y+eh-1)/th, 3) > 0 ) {
				newEntityPosition.x = wx;
			}
		}
		
		for (int i=2; i<= accuracy; i++) {
			// Lower center right
			if (map.getTileId((int)(newEntityPosition.x+ew/i-1)/tw, (int)(newEntityPosition.y+eh-1)/th, 3) > 0) {
				logger.info("Lower center right");
				wx = ((int)(newEntityPosition.x/tw) * tw) + tw/i;
				wy = ((int)(newEntityPosition.y/th) * th);
				
				if (map.getTileId((int)(entityPosition.x+ew/i-1)/tw, (int)(newEntityPosition.y+eh-1)/th, 3) > 0)  {
					newEntityPosition.y = wy;
				}
					
				else if (map.getTileId((int)(newEntityPosition.x+ew/i-1)/tw, (int)(entityPosition.y+eh-1)/th, 3) > 0)  {
					newEntityPosition.x = wx;
				}
			}
			
			// Lower center left
			if (map.getTileId((int)(newEntityPosition.x+ew-ew/i)/tw, (int)(newEntityPosition.y+eh-1)/th, 3) > 0) {
				logger.info("Lower center left");
				wx = ((int)(newEntityPosition.x/tw) * tw) + tw/i;
				wy = ((int)(newEntityPosition.y/th) * th);
				
				if (map.getTileId((int)(entityPosition.x+ew-ew/i)/tw, (int)(newEntityPosition.y+eh-1)/th, 3) > 0)  {
					newEntityPosition.y = wy;
				}
					
				else if (map.getTileId((int)(newEntityPosition.x+ew-ew/i)/tw, (int)(entityPosition.y+eh-1)/th, 3) > 0)  {
					newEntityPosition.x = wx;
				}
			}
			
			// Upper center right
			if (map.getTileId((int)(newEntityPosition.x+ew/i-1)/tw, (int)newEntityPosition.y/th, 3) > 0) {
				logger.info("Upper center right");
				wx = ((int)(newEntityPosition.x/tw) * tw) + tw/i;
				wy = ((int)(newEntityPosition.y/th) * th) + th;
				
				if (map.getTileId((int)(entityPosition.x+ew/i-1)/tw, (int)newEntityPosition.y/th, 3) > 0)  {
					newEntityPosition.y = wy;
				}
					
				else if (map.getTileId((int)(newEntityPosition.x+ew/i-1)/tw, (int)(entityPosition.y)/th, 3) > 0)  {
					newEntityPosition.x = wx;
				}
			}
			
			// Upper center left
			if (map.getTileId((int)(newEntityPosition.x+ew-ew/i)/tw, (int)newEntityPosition.y/th, 3) > 0) {
				logger.info("Upper center left");
				wx = ((int)(newEntityPosition.x/tw) * tw) + tw/i;
				wy = ((int)(newEntityPosition.y/th) * th) + th;
				
				if (map.getTileId((int)(entityPosition.x+ew-ew/i)/tw, (int)newEntityPosition.y/th, 3) > 0)  {
					newEntityPosition.y = wy;
				}
					
				else if (map.getTileId((int)(newEntityPosition.x+ew-ew/i)/tw, (int)(entityPosition.y)/th, 3) > 0)  {
					newEntityPosition.x = wx;
				}
			}
			
			// Center upper left
			if (map.getTileId((int)(newEntityPosition.x)/tw, (int)(newEntityPosition.y+eh/i-1)/th, 3) > 0) {
				logger.info("Upper center right");
				wx = ((int)(newEntityPosition.x/tw) * tw) + tw;
				wy = ((int)(newEntityPosition.y/th) * th) + th/i;
				
				if (map.getTileId((int)(entityPosition.x)/tw, (int)(newEntityPosition.y+eh/i-1)/th, 3) > 0 ) {
					newEntityPosition.y = wy;
				}
					
				else if (map.getTileId((int)(newEntityPosition.x)/tw, (int)(entityPosition.y+eh/i-1)/th, 3) > 0 ) {
					newEntityPosition.x = wx;
				}
			}
			
			// Center lower left
			if (map.getTileId((int)(newEntityPosition.x)/tw, (int)(newEntityPosition.y+eh-eh/i)/th, 3) > 0) {
				wx = ((int)(newEntityPosition.x/tw) * tw) + tw;
				wy = ((int)(newEntityPosition.y/th) * th) + th/i;
				
				if (map.getTileId((int)(entityPosition.x)/tw, (int)(newEntityPosition.y+eh-eh/i)/th, 3) > 0 ) {
					newEntityPosition.y = wy;
				}
					
				else if (map.getTileId((int)(newEntityPosition.x)/tw, (int)(entityPosition.y+eh-eh/i)/th, 3) > 0 ) {
					newEntityPosition.x = wx;
				}
			}
			
			// Center upper right
			if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)(newEntityPosition.y+eh/i-1)/th, 3) > 0) {
				wx = ((int)(newEntityPosition.x/tw) * tw);
				wy = ((int)(newEntityPosition.y/th) * th) + th/i;
				
				if (map.getTileId((int)(entityPosition.x+ew-1)/tw, (int)(newEntityPosition.y+eh/i-1)/th, 3) > 0) {
					newEntityPosition.y = wy;
				}
					
				else if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)(entityPosition.y+eh/i-1)/th, 3) > 0 ) {
					newEntityPosition.x = wx;
				}
			}
			
			// Center lower right
			if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)(newEntityPosition.y+eh-eh/i)/th, 3) > 0) {
				wx = ((int)(newEntityPosition.x/tw) * tw);
				wy = ((int)(newEntityPosition.y/th) * th) + th/i;
				
				if (map.getTileId((int)(entityPosition.x+ew-1)/tw, (int)(newEntityPosition.y+eh-eh/i)/th, 3) > 0) {
					newEntityPosition.y = wy;
				}
					
				else if (map.getTileId((int)(newEntityPosition.x+ew-1)/tw, (int)(entityPosition.y+eh-eh/i)/th, 3) > 0 ) {
					newEntityPosition.x = wx;
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
