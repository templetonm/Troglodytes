package com.turbonips.troglodytes.states;


import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.EntitySystem;
import com.artemis.SystemManager;
import com.artemis.World;
import com.turbonips.troglodytes.EntityFactory;
import com.turbonips.troglodytes.components.SpatialForm;
import com.turbonips.troglodytes.systems.CollisionSystem;
import com.turbonips.troglodytes.systems.ControlSystem;
import com.turbonips.troglodytes.systems.RenderSystem;

public class PlayingState extends BaseGameState {
	public static final int ID = 3;
	
	private World world;
	private SystemManager systemManager;
	private EntitySystem controlSystem;
	private EntitySystem renderSystem;
	private EntitySystem collisionSystem;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.init(container, game);
		world = new World();
	    systemManager = world.getSystemManager();
		controlSystem = systemManager.setSystem(new ControlSystem(container));
		renderSystem = systemManager.setSystem(new RenderSystem(container));
		collisionSystem = systemManager.setSystem(new CollisionSystem(container));
		systemManager.initializeAll();
		
		
		EntityFactory.create(world, EntityFactory.ID_GROUND_LAYER);
		EntityFactory.create(world, EntityFactory.ID_BG_LAYER);
		EntityFactory.create(world, EntityFactory.ID_PLAYER);
		EntityFactory.create(world, EntityFactory.ID_FG_LAYER);
		EntityFactory.create(world, EntityFactory.ID_WALL_LAYER);
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
		collisionSystem.process();
	}

	@Override
	public int getID() {
		return ID;
	}

}
