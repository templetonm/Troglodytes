package com.turbonips.troglodytes;

import java.awt.Point;

import org.apache.log4j.Logger;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.Entity;
import com.artemis.World;
import com.turbonips.troglodytes.components.Collision;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.ParticleComponent;
import com.turbonips.troglodytes.components.Renderable;
import com.turbonips.troglodytes.components.Sliding;
import com.turbonips.troglodytes.components.SubPosition;
import com.turbonips.troglodytes.components.Renderable.RenderType;
import com.turbonips.troglodytes.components.Position;

public class EntityFactory {
	
	public static final int ID_PLAYER = 0;
	public static final int ID_GROUND_LAYER = 1;
	public static final int ID_BG_LAYER = 2;
	public static final int ID_FG_LAYER = 3;
	public static final int ID_WALL_LAYER = 4;
	public static final int ID_ENEMY = 5;
	private static final Logger logger = Logger.getLogger(EntityFactory.class);
	
	public static Entity createPlayer(World world, Point position) throws SlickException {
		ResourceManager resourceManager = ResourceManager.getInstance();
		Resource playerAnimationResource = resourceManager.getResource("testplayeranimation");
		CreatureAnimation playerAnimation = (CreatureAnimation)playerAnimationResource.getObject();
		Image playerFrame = playerAnimation.getCurrent().getCurrentFrame();
		int speed = 8;
		Rectangle slidingBox = new Rectangle(speed*-15, speed*-12, speed*15, speed*12);
		
		// Create the player
		Entity player = world.createEntity();
		player.setGroup("PLAYER");
		player.addComponent(new Position(new Vector2f(playerFrame.getWidth()*position.x, playerFrame.getHeight()*position.y), speed));
		player.addComponent(new Sliding(new Vector2f(playerFrame.getWidth()/2, playerFrame.getHeight()/2), speed, slidingBox));
		player.addComponent(new Renderable(resourceManager.getResource("testplayeranimation"), RenderType.PLAYER));
		player.addComponent(new Movement());
		player.addComponent(new Collision());
		player.refresh();
		
		return player;
	}
	
	public static void createMap(World world, String mapId, Point position) throws SlickException {
		ResourceManager resourceManager = ResourceManager.getInstance();
		Resource playerAnimationResource = resourceManager.getResource("testplayeranimation");
		CreatureAnimation playerAnimation = (CreatureAnimation)playerAnimationResource.getObject();
		Image playerFrame = playerAnimation.getCurrent().getCurrentFrame();
		int speed = 8;
		Rectangle slidingBox = new Rectangle(speed*-15, speed*-12, speed*15, speed*12);
		
		// Create the ground
		Entity ground = world.createEntity();
		ground.setGroup("LAYER");
		ground.addComponent(new Position(new Vector2f(playerFrame.getWidth()*position.x, playerFrame.getHeight()*position.y), speed));
		ground.addComponent(new Sliding(new Vector2f(playerFrame.getWidth()/2, playerFrame.getHeight()/2), speed, slidingBox));
		ground.addComponent(new Renderable(resourceManager.getResource(mapId), RenderType.GROUND_LAYER));
		ground.refresh();
		
		// Create the background
		Entity background = world.createEntity();
		background.setGroup("LAYER");
		background.addComponent(new Position(new Vector2f(playerFrame.getWidth()*position.x, playerFrame.getHeight()*position.y), speed));
		background.addComponent(new Sliding(new Vector2f(playerFrame.getWidth()/2, playerFrame.getHeight()/2), speed, slidingBox));
		background.addComponent(new Renderable(resourceManager.getResource(mapId), RenderType.BACKGROUND_LAYER));
		background.refresh();

		
		// Create the foreground
		Entity foreground = world.createEntity();
		foreground.setGroup("LAYER");
		foreground.addComponent(new Position(new Vector2f(playerFrame.getWidth()*position.x, playerFrame.getHeight()*position.y), speed));
		foreground.addComponent(new Sliding(new Vector2f(playerFrame.getWidth()/2, playerFrame.getHeight()/2), speed, slidingBox));
		foreground.addComponent(new Renderable(resourceManager.getResource(mapId), RenderType.FOREGROUND_LAYER));
		foreground.refresh();
		
		TiledMap tiledMap = (TiledMap)resourceManager.getResource(mapId).getObject();
		for (int g=0; g<tiledMap.getObjectGroupCount(); g++) {
			for (int i=0; i<tiledMap.getObjectCount(g); i++) {
				if (tiledMap.getObjectType(g, i).equalsIgnoreCase("spawn")) {
					int spawnNum = Integer.valueOf(tiledMap.getObjectProperty(g, i, "Number", "0"));
					
					
					for (int n=0; n<spawnNum; n++) {
						String enemyId = tiledMap.getObjectProperty(g, i, "Enemy", "");
						int objectX = tiledMap.getObjectX(g, i);
						int objectY = tiledMap.getObjectY(g, i);
						int objectWidth = tiledMap.getObjectWidth(g, i);
						int objectHeight = tiledMap.getObjectHeight(g, i);
						Resource enemyResource = resourceManager.getResource(enemyId);
						
						Vector2f enemyStartPosition = new Vector2f(objectWidth*32, objectHeight*32);
						enemyStartPosition = new Vector2f(objectX+(int)(Math.random()*objectWidth), objectY+(int)(Math.random()*objectHeight));
						
						
						Entity enemy = world.createEntity();
						enemy.setGroup("ENEMY");
						enemy.addComponent(new Position(new Vector2f(playerFrame.getWidth()*position.x, playerFrame.getHeight()*position.y), speed));
						enemy.addComponent(new Sliding(new Vector2f(playerFrame.getWidth()/2, playerFrame.getHeight()/2), speed, slidingBox));
						enemy.addComponent(new SubPosition(enemyStartPosition,4));
						enemy.addComponent(new Renderable(enemyResource, RenderType.ENEMY));
						enemy.addComponent(new Movement());
						enemy.addComponent(new Collision());
						enemy.refresh();
					}
				}
				else if(tiledMap.getObjectType(g, i).equalsIgnoreCase("particleSpawn")) {
					int spawnNum = Integer.valueOf(tiledMap.getObjectProperty(g, i, "Number", "0"));

					String particletype = tiledMap.getObjectProperty(g, i, "type", "");
					int objectX = tiledMap.getObjectX(g, i);
					int objectY = tiledMap.getObjectY(g, i);
					int objectWidth = tiledMap.getObjectWidth(g, i);
					int objectHeight = tiledMap.getObjectHeight(g, i);
					Vector2f particleSpawnPoint = new Vector2f(objectX, objectY);// + objectWidth*32, objectY + objectHeight*32);
					
					Image particleImage = (Image)resourceManager.getResource(particletype).getObject();
					ParticleSystem particleSys = new ParticleSystem(particleImage);
					Emitter pem = new Emitter(100,100);
					pem.setEnabled(true);
					particleSys.addEmitter(pem);

					Entity particleSystem = world.createEntity();
					particleSystem.setGroup("MAPPARTICLESYSTEM");
					particleSystem.addComponent(new Position(new Vector2f(playerFrame.getWidth()*position.x, playerFrame.getHeight()*position.y), speed));
					particleSystem.addComponent(new Sliding(new Vector2f(playerFrame.getWidth()/2, playerFrame.getHeight()/2), speed, slidingBox));
					particleSystem.addComponent(new SubPosition(particleSpawnPoint,0));
					particleSystem.addComponent(new ParticleComponent(particleSys));
					//particleSystem.addComponent(new RenderType(RenderType.TYPE_MAPPARTICLESYSTEM));
					particleSystem.refresh();
				}
			}
		}
		
	}
	
}