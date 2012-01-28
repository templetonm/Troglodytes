package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.SubPosition;
import com.turbonips.troglodytes.components.RenderType;
import com.turbonips.troglodytes.components.Resource;
import com.turbonips.troglodytes.components.Sliding;


public class RenderSystem extends BaseEntityProcessingSystem {
	private final GameContainer container;
	private final Graphics graphics;
	private ComponentMapper<Resource> resourceMapper;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<RenderType> renderTypeMapper;
	private ComponentMapper<Sliding> slidingMapper;
	private ComponentMapper<SubPosition> subPositionMapper;

	public RenderSystem(GameContainer container) {
		super(Position.class, Resource.class, RenderType.class);
		this.container = container;
		this.graphics = container.getGraphics();
	}
	
	@Override
	protected void initialize() {
		resourceMapper = new ComponentMapper<Resource>(Resource.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		renderTypeMapper = new ComponentMapper<RenderType>(RenderType.class, world);
		slidingMapper = new ComponentMapper<Sliding>(Sliding.class, world);
		subPositionMapper = new ComponentMapper<SubPosition>(SubPosition.class, world);
	}

	@Override
	protected void process(Entity e) {
		Resource resource = resourceMapper.get(e);
		RenderType renderType = renderTypeMapper.get(e);
		Position position = positionMapper.get(e);
		Sliding sliding = slidingMapper.get(e);
		SubPosition subPosition = subPositionMapper.get(e);
		
		/*
		 * Get player and map drawing positions
		 */
		int playerX = container.getWidth()/2;
		int playerY = container.getHeight()/2;
		int mapX = (int)position.getX()*-1 + container.getWidth()/2;
		int mapY = (int)position.getY()*-1 + container.getHeight()/2;
		int mapEntityX = mapX;
		int mapEntityY = mapY;
		
		if (sliding != null) {
			playerX -= (int)sliding.getX();
			playerY -= (int)sliding.getY();
			mapX -= (int)sliding.getX();
			mapY -= (int)sliding.getY();
		}
		// Enemys, effects, anything that can move on the map (minus player)
		if (subPosition != null) {
			mapEntityX += (int)subPosition.getX();
			mapEntityY += (int)subPosition.getY();
		}
		
		
		/*
		 * Image rendering
		 */
		if (resource.getType().equalsIgnoreCase("image")) {
			Image img = (Image)resource.getObject();
			switch (renderType.getType()) {
				case RenderType.TYPE_PLAYER:
					graphics.drawImage(img, playerX, playerY);
					break;
				case RenderType.TYPE_ENEMY:
					graphics.drawImage(img, mapEntityX, mapEntityY);
					break;
				default:
					logger.warn("Invalid render type " + renderType.getType() + " for image " + resource.getPath());
					break;
			}
			
		/*
		 * SpriteSheet rendering
		 */
		} else if (resource.getType().equalsIgnoreCase("spritesheet")) {
			SpriteSheet sheet = (SpriteSheet)resource.getObject();
			
			switch (renderType.getType()) {
				default:
					logger.warn("Invalid render type " + renderType.getType() + " for spritesheet " + resource.getPath());
					break;
			}
			
		/*
		 * CreatureAnimation rendering
		 */
		} else if (resource.getType().equalsIgnoreCase("creatureanimation")) {
			CreatureAnimation creatureAnimation = (CreatureAnimation)resource.getObject();
			switch (renderType.getType()) {
				case RenderType.TYPE_PLAYER:
					graphics.drawAnimation(creatureAnimation.getCurrent(), playerX, playerY);
					break;
				case RenderType.TYPE_ENEMY:
					graphics.drawAnimation(creatureAnimation.getCurrent(), mapEntityX, mapEntityY);
					break;
				default:
					logger.warn("Invalid render type " + renderType.getType() + " for creatureanimation " + resource.getPath());
					break;
			}
			
		/*
		 * TiledMap rendering
		 */
		} else if (resource.getType().equalsIgnoreCase("tiledmap")) {
			TiledMap map = (TiledMap)resource.getObject();

			
			switch (renderType.getType()) {
				case RenderType.TYPE_GROUND_LAYER:
					map.render(mapX, mapY, 0);
					break;
				case RenderType.TYPE_BACKGROUND_LAYER:
					map.render(mapX, mapY, 1);
					break;
				case RenderType.TYPE_FOREGROUND_LAYER:
					map.render(mapX, mapY, 2);
					break;
				case RenderType.TYPE_WALL_LAYER:
					map.render(mapX, mapY, 3);
					break;
				default:
					logger.warn("Invalid render type " + renderType.getType() + " for tiledmap " + resource.getPath());
					break;
			}
		} else {
			logger.warn("Invalid resource type " + resource.getType() + " " + resource.getPath());
		}
	}

}
