package com.turbonips.troglodytes.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.SystemManager;
import com.artemis.World;
import com.turbonips.troglodytes.components.WarpObject;
import com.turbonips.troglodytes.systems.CollisionSystem;
import com.turbonips.troglodytes.systems.EnemyControlSystem;
import com.turbonips.troglodytes.systems.MapParticleSystem;
import com.turbonips.troglodytes.systems.MovementSystem;
import com.turbonips.troglodytes.systems.MusicSystem;
import com.turbonips.troglodytes.systems.ObjectCollisionSystem;
import com.turbonips.troglodytes.systems.PlayerControlSystem;
import com.turbonips.troglodytes.systems.DebugTextSystem;
import com.turbonips.troglodytes.systems.LightingSystem;
import com.turbonips.troglodytes.systems.RenderSystem;
import com.turbonips.troglodytes.systems.WarpSystem;

public class PlayingState extends BaseGameState {
	public static final int ID = 3;
	
	private World world;
	private SystemManager systemManager;
	private EntitySystem playerControlSystem;
	private EntitySystem movementSystem;
	private EntitySystem renderSystem;
	private EntitySystem collisionSystem;
	private EntitySystem lightingSystem;
	private EntitySystem objectCollisionSystem;
	private EntitySystem debugTextSystem;
	private EntitySystem enemyControlSystem;
	private EntitySystem musicSystem;
	private EntitySystem mapParticleSystem;
	private EntitySystem warpSystem;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.init(container, game);
		world = new World();
	    systemManager = world.getSystemManager();
	    playerControlSystem = systemManager.setSystem(new PlayerControlSystem(container));
		renderSystem = systemManager.setSystem(new RenderSystem(container));
		collisionSystem = systemManager.setSystem(new CollisionSystem());
		lightingSystem = systemManager.setSystem(new LightingSystem(container));
		objectCollisionSystem = systemManager.setSystem(new ObjectCollisionSystem());
		debugTextSystem = systemManager.setSystem(new DebugTextSystem(container));
		movementSystem = systemManager.setSystem(new MovementSystem());
		enemyControlSystem = systemManager.setSystem(new EnemyControlSystem());
		warpSystem = systemManager.setSystem(new WarpSystem());
		musicSystem = systemManager.setSystem(new MusicSystem(container));
		mapParticleSystem = systemManager.setSystem(new MapParticleSystem());
		systemManager.initializeAll();
		
		Entity player = world.createEntity();
		player.setGroup("PLAYER");
		player.addComponent(new WarpObject("trog1",15,15));
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
		warpSystem.process();
		collisionSystem.process();
		objectCollisionSystem.process();
		warpSystem.process();
		playerControlSystem.process();
		enemyControlSystem.process();
		musicSystem.process();
		mapParticleSystem.process();
	}

	@Override
	public int getID() {
		return ID;
	}

}
