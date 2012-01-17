package com.turbonips.troglodytes.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.SystemManager;
import com.artemis.World;
import com.turbonips.troglodytes.EntityFactory;
import com.turbonips.troglodytes.components.Sliding;
import com.turbonips.troglodytes.components.SpatialForm;
import com.turbonips.troglodytes.components.Transform;
import com.turbonips.troglodytes.systems.ControlSystem;
import com.turbonips.troglodytes.systems.RenderSystem;

public class PlayingState extends BaseGameState {
	public static final int ID = 3;
	
	private World world;
	private SystemManager systemManager;
	private EntitySystem controlSystem;
	private EntitySystem renderSystem;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.init(container, game);
		world = new World();
	    systemManager = world.getSystemManager();
		controlSystem = systemManager.setSystem(new ControlSystem(container));
		renderSystem = systemManager.setSystem(new RenderSystem(container));
		systemManager.initializeAll();
		
		
		// TODO: We can add ground and background to the entity system together by editing RenderSystem
		int speed = 8;
		Rectangle box = new Rectangle(speed*-15, speed*-10, speed*15, speed*10);
		TiledMap tiledMap = new TiledMap("resources/maps/trog2.tmx");
		// TODO: Implement animations
		Image playerImage = new Image("resources/graphics/player.png");
		
		
		EntityFactory.createLayer(world, tiledMap, speed, box, SpatialForm.TYPE_GROUND_LAYER);
		EntityFactory.createLayer(world, tiledMap, speed, box, SpatialForm.TYPE_BACKGROUND_LAYER);
		EntityFactory.createPlayer(world, speed, box, playerImage);
		EntityFactory.createLayer(world, tiledMap, speed, box, SpatialForm.TYPE_FOREGROUND_LAYER);
		//EntityFactory.createLayer(world, tiledMap, speed, box, SpatialForm.TYPE_WALL_LAYER);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		renderSystem.process();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		world.loopStart();
		world.setDelta(delta);
		controlSystem.process();
	}

	@Override
	public int getID() {
		return ID;
	}

}
