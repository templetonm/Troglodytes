package com.turbonips.troglodytes;

import java.awt.Point;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.artemis.Entity;
import com.artemis.World;
import com.turbonips.troglodytes.components.Collision;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Resource;
import com.turbonips.troglodytes.components.Sliding;
import com.turbonips.troglodytes.components.SubPosition;
import com.turbonips.troglodytes.components.RenderType;
import com.turbonips.troglodytes.components.Position;

public class EntityFactory {
	
	public static final int ID_PLAYER = 0;
	public static final int ID_GROUND_LAYER = 1;
	public static final int ID_BG_LAYER = 2;
	public static final int ID_FG_LAYER = 3;
	public static final int ID_WALL_LAYER = 4;
	public static final int ID_ENEMY = 5;

	public static Entity create(World world, int id) throws SlickException {
		ResourceManager resourceManager = ResourceManager.getInstance();		
		Image playerImage = (Image)resourceManager.getResource("testplayerimage").getObject();
		Image enemyImage = (Image)resourceManager.getResource("testenemyimage").getObject();
		String startMap = "trog1";
		
		Vector2f playerStartPosition = new Vector2f(playerImage.getWidth()*20, playerImage.getHeight()*20);
		Vector2f enemyStartPosition = new Vector2f(enemyImage.getWidth()*40, enemyImage.getHeight()*20);
		
		Vector2f slidingStart = new Vector2f((float)playerImage.getWidth()/2, (float)playerImage.getHeight()/2);
		int speed = 8;
		Rectangle box = new Rectangle(speed*-15, speed*-12, speed*15, speed*12);

		switch(id) {
			case ID_PLAYER:
				Entity player = world.createEntity();
				player.setGroup("PLAYER");
				player.addComponent(new Position(playerStartPosition, speed));
				player.addComponent(new Sliding(slidingStart, speed, box));
				player.addComponent(resourceManager.getResource("testplayeranimation"));
				//player.addComponent(resourceManager.getResource("testplayerimage"));
				player.addComponent(new RenderType(RenderType.TYPE_PLAYER));
				player.addComponent(new Movement());
				player.addComponent(new Collision());
				player.refresh();
				return player;
			case ID_GROUND_LAYER:
				Entity ground = world.createEntity();
				ground.setGroup("LAYER");
				ground.addComponent(new Position(playerStartPosition, speed));
				ground.addComponent(new Sliding(slidingStart, speed, box));
				ground.addComponent(resourceManager.getResource(startMap));
				ground.addComponent(new RenderType(RenderType.TYPE_GROUND_LAYER));
				ground.refresh();
				return ground;
			case ID_BG_LAYER:
				Entity background = world.createEntity();
				background.setGroup("LAYER");
				background.addComponent(new Position(playerStartPosition, speed));
				background.addComponent(new Sliding(slidingStart, speed, box));
				background.addComponent(resourceManager.getResource(startMap));
				background.addComponent(new RenderType(RenderType.TYPE_BACKGROUND_LAYER));
				background.refresh();
				return background;
			case ID_FG_LAYER:
				Entity foreground = world.createEntity();
				foreground.setGroup("LAYER");
				foreground.addComponent(new Position(playerStartPosition, speed));
				foreground.addComponent(new Sliding(slidingStart, speed, box));
				foreground.addComponent(resourceManager.getResource(startMap));
				foreground.addComponent(new RenderType(RenderType.TYPE_FOREGROUND_LAYER));
				foreground.refresh();
				return foreground;
			case ID_WALL_LAYER:
				Entity wall = world.createEntity();
				wall.setGroup("LAYER");
				wall.addComponent(new Position(playerStartPosition, speed));
				wall.addComponent(new Sliding(slidingStart, speed, box));
				wall.addComponent(resourceManager.getResource(startMap));
				wall.addComponent(new RenderType(RenderType.TYPE_WALL_LAYER));
				wall.refresh();
				return wall;
			case ID_ENEMY:
				Entity enemy = world.createEntity();
				enemy.setGroup("ENEMY");
				enemy.addComponent(new Position(playerStartPosition, speed));
				enemy.addComponent(new Sliding(slidingStart, speed, box));
				enemy.addComponent(new SubPosition(enemyStartPosition, 4));
				enemy.addComponent(resourceManager.getResource("testenemyanimation"));
				enemy.addComponent(new RenderType(RenderType.TYPE_ENEMY));
				enemy.addComponent(new Movement());
				enemy.addComponent(new Collision());
				enemy.refresh();
				break;
		}
		return null;
	}
	
	public static void createPlayer(World world, Point position) throws SlickException {
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
		player.addComponent(playerAnimationResource);
		player.addComponent(new RenderType(RenderType.TYPE_PLAYER));
		player.addComponent(new Movement());
		player.addComponent(new Collision());
		player.refresh();
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
		ground.addComponent(resourceManager.getResource(mapId));
		ground.addComponent(new RenderType(RenderType.TYPE_GROUND_LAYER));
		ground.refresh();
		
		// Create the background
		Entity background = world.createEntity();
		background.setGroup("LAYER");
		background.addComponent(new Position(new Vector2f(playerFrame.getWidth()*position.x, playerFrame.getHeight()*position.y), speed));
		background.addComponent(new Sliding(new Vector2f(playerFrame.getWidth()/2, playerFrame.getHeight()/2), speed, slidingBox));
		background.addComponent(resourceManager.getResource(mapId));
		background.addComponent(new RenderType(RenderType.TYPE_BACKGROUND_LAYER));
		background.refresh();

		
		// Create the foreground
		Entity foreground = world.createEntity();
		foreground.setGroup("LAYER");
		foreground.addComponent(new Position(new Vector2f(playerFrame.getWidth()*position.x, playerFrame.getHeight()*position.y), speed));
		foreground.addComponent(new Sliding(new Vector2f(playerFrame.getWidth()/2, playerFrame.getHeight()/2), speed, slidingBox));
		foreground.addComponent(resourceManager.getResource(mapId));
		foreground.addComponent(new RenderType(RenderType.TYPE_FOREGROUND_LAYER));
		foreground.refresh();
	}
	
}