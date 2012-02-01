package com.turbonips.troglodytes.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music; //TODO: Probably remove this reference after moving the music starting point. 
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.artemis.EntitySystem;
import com.artemis.SystemManager;
import com.artemis.World;
import com.turbonips.troglodytes.EntityFactory;
import com.turbonips.troglodytes.systems.CollisionSystem;
import com.turbonips.troglodytes.systems.EnemyControlSystem;
import com.turbonips.troglodytes.systems.MovementSystem;
import com.turbonips.troglodytes.systems.MusicSystem;
import com.turbonips.troglodytes.systems.PlayerControlSystem;
import com.turbonips.troglodytes.systems.DebugTextSystem;
import com.turbonips.troglodytes.systems.LightingSystem;
import com.turbonips.troglodytes.systems.ObjectSystem;
import com.turbonips.troglodytes.systems.RenderSystem;

public class PlayingState extends BaseGameState {
	public static final int ID = 3;
	
	private World world;
	private SystemManager systemManager;
	private EntitySystem playerControlSystem;
	private EntitySystem movementSystem;
	private EntitySystem renderSystem;
	private EntitySystem collisionSystem;
	private EntitySystem lightingSystem;
	private EntitySystem objectSystem;
	private EntitySystem debugTextSystem;
	private EntitySystem enemyControlSystem;
	private EntitySystem musicSystem;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.init(container, game);
		world = new World();
	    systemManager = world.getSystemManager();
	    playerControlSystem = systemManager.setSystem(new PlayerControlSystem(container));
		renderSystem = systemManager.setSystem(new RenderSystem(container));
		collisionSystem = systemManager.setSystem(new CollisionSystem(container));
		lightingSystem = systemManager.setSystem(new LightingSystem(container));
		objectSystem = systemManager.setSystem(new ObjectSystem(container));
		debugTextSystem = systemManager.setSystem(new DebugTextSystem(container));
		movementSystem = systemManager.setSystem(new MovementSystem(container));
		enemyControlSystem = systemManager.setSystem(new EnemyControlSystem(container));
		musicSystem = systemManager.setSystem(new MusicSystem(container));
		systemManager.initializeAll();
		
		EntityFactory.create(world, EntityFactory.ID_GROUND_LAYER);
		EntityFactory.create(world, EntityFactory.ID_BG_LAYER);
		EntityFactory.create(world, EntityFactory.ID_PLAYER);
		for (int i=0; i<5000; i++) EntityFactory.create(world, EntityFactory.ID_ENEMY);
		EntityFactory.create(world, EntityFactory.ID_FG_LAYER);
		//EntityFactory.create(world, EntityFactory.ID_WALL_LAYER);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		renderSystem.process();
		movementSystem.process();
		lightingSystem.process();
		debugTextSystem.process();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		world.loopStart();
		world.setDelta(delta);
		collisionSystem.process();
		playerControlSystem.process();
		enemyControlSystem.process();
		objectSystem.process();
		musicSystem.process();
	}

	@Override
	public int getID() {
		return ID;
	}

}
