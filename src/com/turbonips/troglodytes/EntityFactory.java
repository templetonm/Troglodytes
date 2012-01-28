package com.turbonips.troglodytes;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.artemis.Entity;
import com.artemis.World;
import com.turbonips.troglodytes.components.Movement;
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
		Vector2f playerStartPosition = new Vector2f(playerImage.getWidth()*25, playerImage.getHeight()*10);
		Vector2f enemyStartPosition = new Vector2f(enemyImage.getWidth()*40, enemyImage.getHeight()*4);
		
		
		
		Vector2f slidingStart = new Vector2f((float)playerImage.getWidth()/2, (float)playerImage.getHeight()/2);
		int speed = 8;
		Rectangle box = new Rectangle(speed*-15, speed*-12, speed*15, speed*12);

		switch(id) {
			case ID_PLAYER:
				Entity player = world.createEntity();
				player.setGroup("PLAYER");
				player.addComponent(new Position(playerStartPosition, speed));
				player.addComponent(resourceManager.getResource("testplayeranimation"));
				player.addComponent(new RenderType(RenderType.TYPE_PLAYER));
				player.addComponent(new Movement());
				/*player.addComponent(new Sliding(slidingStart, speed, box));
				player.addComponent(new SpatialForm(playerImage, SpatialForm.TYPE_PLAYER));
				player.addComponent(new AnimationCreature(playerSpriteSheet));
				player.addComponent(new Collision());*/
				player.refresh();
				return player;
			case ID_GROUND_LAYER:
				Entity ground = world.createEntity();
				ground.setGroup("LAYER");
				ground.addComponent(new Position(playerStartPosition, speed));
				ground.addComponent(resourceManager.getResource("trog1"));
				ground.addComponent(new RenderType(RenderType.TYPE_GROUND_LAYER));
				/*ground.addComponent(new Sliding(slidingStart, speed, box));
				ground.addComponent(new SpatialForm(startMap, SpatialForm.TYPE_GROUND_LAYER));
				ground.addComponent(new Collision());*/
				ground.refresh();
				return ground;
			case ID_BG_LAYER:
				Entity background = world.createEntity();
				background.setGroup("LAYER");
				background.addComponent(new Position(playerStartPosition, speed));
				background.addComponent(resourceManager.getResource("trog1"));
				background.addComponent(new RenderType(RenderType.TYPE_BACKGROUND_LAYER));
				/*background.addComponent(new Sliding(slidingStart, speed, box));
				background.addComponent(new SpatialForm(startMap, SpatialForm.TYPE_BACKGROUND_LAYER));
				background.addComponent(new Collision());*/
				background.refresh();
				return background;
			case ID_FG_LAYER:
				Entity foreground = world.createEntity();
				foreground.setGroup("LAYER");
				foreground.addComponent(new Position(playerStartPosition, speed));
				foreground.addComponent(resourceManager.getResource("trog1"));
				foreground.addComponent(new RenderType(RenderType.TYPE_FOREGROUND_LAYER));
				/*foreground.addComponent(new Sliding(slidingStart, speed, box));
				foreground.addComponent(new SpatialForm(startMap, SpatialForm.TYPE_FOREGROUND_LAYER));
				foreground.addComponent(new Collision());*/
				foreground.refresh();
				return foreground;
			case ID_WALL_LAYER:
				Entity wall = world.createEntity();
				wall.setGroup("LAYER");
				wall.addComponent(new Position(playerStartPosition, speed));
				wall.addComponent(resourceManager.getResource("trog0"));
				wall.addComponent(new RenderType(RenderType.TYPE_WALL_LAYER));
				/*wall.addComponent(new Sliding(slidingStart, speed, box));
				wall.addComponent(new SpatialForm(startMap, SpatialForm.TYPE_WALL_LAYER));
				wall.addComponent(new Collision());*/
				wall.refresh();
				return wall;
			case ID_ENEMY:
				Entity enemy = world.createEntity();
				enemy.setGroup("ENEMY");
				enemy.addComponent(new Position(playerStartPosition, speed));
				enemy.addComponent(new SubPosition(enemyStartPosition, 4));
				enemy.addComponent(resourceManager.getResource("testenemyanimation"));
				enemy.addComponent(new RenderType(RenderType.TYPE_ENEMY));
				enemy.addComponent(new Movement());
				//monster.addComponent(new AnimationCreature(enemySpriteSheet));
				enemy.refresh();
				break;
		}
		return null;
		
	}
	
}