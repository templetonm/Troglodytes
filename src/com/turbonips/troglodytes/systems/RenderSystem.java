package com.turbonips.troglodytes.systems;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.ParticleComponent;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.SubPosition;
import com.turbonips.troglodytes.components.RenderType;
import com.turbonips.troglodytes.components.Resource;
import com.turbonips.troglodytes.components.Sliding;
import com.turbonips.troglodytes.components.Resource.ResourceType;

public class RenderSystem extends BaseEntitySystem {
	private final GameContainer container;
	private final Graphics graphics;
	private ComponentMapper<Resource> resourceMapper;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<RenderType> renderTypeMapper;
	private ComponentMapper<Sliding> slidingMapper;
	private ComponentMapper<SubPosition> subPositionMapper;
	private ComponentMapper<Movement> movementMapper;

	public RenderSystem(GameContainer container) {
		this.container = container;
		this.graphics = container.getGraphics();
	}

	@Override
	protected void initialize() {
		resourceMapper = new ComponentMapper<Resource>(Resource.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		renderTypeMapper = new ComponentMapper<RenderType>(RenderType.class,
				world);
		slidingMapper = new ComponentMapper<Sliding>(Sliding.class, world);
		subPositionMapper = new ComponentMapper<SubPosition>(SubPosition.class,
				world);
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
	}

	private void processEntity(Entity e) {
		Resource resource = resourceMapper.get(e);
		RenderType renderType = renderTypeMapper.get(e);
		Position position = positionMapper.get(e);
		Sliding sliding = slidingMapper.get(e);
		SubPosition subPosition = subPositionMapper.get(e);

		if (position != null) {
			/*
			 * Get player and map drawing positions
			 */
			int playerX = container.getWidth() / 2;
			int playerY = container.getHeight() / 2;
			int mapX = (int) position.getX() * -1 + container.getWidth() / 2;
			int mapY = (int) position.getY() * -1 + container.getHeight() / 2;

			if (sliding != null) {
				playerX -= (int) sliding.getX();
				playerY -= (int) sliding.getY();
				mapX -= (int) sliding.getX();
				mapY -= (int) sliding.getY();
			}
			int mapEntityX = mapX;
			int mapEntityY = mapY;

			// Enemys, effects, anything that can move on the map (minus player)
			if (subPosition != null) {
				mapEntityX += (int) subPosition.getX();
				mapEntityY += (int) subPosition.getY();
			}

			/*
			 * Map Particle System Rendering
			 */
			if (resource == null
					&& renderType.getType() == RenderType.TYPE_MAPPARTICLESYSTEM) {
				e.getComponent(ParticleComponent.class).renderParticleSystem(
						mapEntityX, mapEntityY);

				/*
				 * Image rendering
				 */
				if (resource.getType() == ResourceType.IMAGE) {
					Image img = (Image) resource.getObject();
					switch (renderType.getType()) {
					case RenderType.TYPE_PLAYER:
						graphics.drawImage(img, playerX, playerY);
						break;
					case RenderType.TYPE_ENEMY:
						graphics.drawImage(img, mapEntityX, mapEntityY);
						break;
					default:
						logger.warn("Invalid render type "
								+ renderType.getType() + " for image "
								+ resource.getPath());
						break;
					}

					/*
					 * SpriteSheet rendering
					 */
				} else if (resource.getType() == ResourceType.SPRITE_SHEET) {
					SpriteSheet sheet = (SpriteSheet) resource.getObject();

					switch (renderType.getType()) {
					default:
						logger.warn("Invalid render type "
								+ renderType.getType() + " for spritesheet "
								+ resource.getPath());
						break;
					}

					/*
					 * CreatureAnimation rendering
					 */
				} else if (resource.getType() == ResourceType.CREATURE_ANIMATION) {
					CreatureAnimation creatureAnimation = (CreatureAnimation) resource
							.getObject();
					Animation curAnimation = creatureAnimation.getIdleDown();
					Movement movement = movementMapper.get(e);
					if (movement != null) {
						if (movement.getAnimation() != null)
							curAnimation = movement.getAnimation();
					}

					switch (renderType.getType()) {
					case RenderType.TYPE_PLAYER:
						graphics.drawAnimation(curAnimation, playerX, playerY);
						break;
					case RenderType.TYPE_ENEMY:
						graphics.drawAnimation(curAnimation, mapEntityX,
								mapEntityY);
						break;
					default:
						logger.warn("Invalid render type "
								+ renderType.getType()
								+ " for creatureanimation "
								+ resource.getPath());
						break;
					}

					/*
					 * TiledMap rendering
					 */
				} else if (resource.getType() == ResourceType.TILED_MAP) {
					TiledMap map = (TiledMap) resource.getObject();

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
						logger.warn("Invalid render type "
								+ renderType.getType() + " for tiledmap "
								+ resource.getPath());
						break;
					}
				} else {
					logger.warn("Invalid resource type " + resource.getType()
							+ " " + resource.getPath());
				}
			}
		}
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entites) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities(
				"PLAYER");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities(
				"ENEMY");
		ImmutableBag<Entity> mapParticleSystems = world.getGroupManager()
				.getEntities("MAPPARTICLESYSTEM");
		ImmutableBag<Entity> layers = world.getGroupManager().getEntities(
				"LAYER");
		ArrayList<Entity> backgroundLayers = new ArrayList<Entity>();
		ArrayList<Entity> groundLayers = new ArrayList<Entity>();
		ArrayList<Entity> foregroundLayers = new ArrayList<Entity>();
		ArrayList<Entity> wallLayers = new ArrayList<Entity>();

		// TODO This isn't ideal. I think we want a ground, background
		// foreground and wall group
		for (int i = 0; i < layers.size(); i++) {
			Entity layer = layers.get(i);
			RenderType renderType = renderTypeMapper.get(layer);
			if (renderType != null) {
				if (renderType.getType() == RenderType.TYPE_GROUND_LAYER) {
					groundLayers.add(layer);
				}
				if (renderType.getType() == RenderType.TYPE_BACKGROUND_LAYER) {
					backgroundLayers.add(layer);
				}
				if (renderType.getType() == RenderType.TYPE_FOREGROUND_LAYER) {
					foregroundLayers.add(layer);
				}
				if (renderType.getType() == RenderType.TYPE_WALL_LAYER) {
					wallLayers.add(layer);
				}
			}
		}

		for (Entity layer : groundLayers) {
			processEntity(layer);
		}

		for (Entity layer : backgroundLayers) {
			processEntity(layer);
		}

		for (int i = 0; i < players.size(); i++) {
			Entity player = players.get(i);
			processEntity(player);
		}

		for (int i = 0; i < enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			processEntity(enemy);
		}

		for (int i = 0; i < mapParticleSystems.size(); i++) {
			Entity mps = mapParticleSystems.get(i);
			processEntity(mps);
		}

		for (Entity layer : foregroundLayers) {
			processEntity(layer);
		}

		for (Entity layer : wallLayers) {
			processEntity(layer);
		}

	}

}