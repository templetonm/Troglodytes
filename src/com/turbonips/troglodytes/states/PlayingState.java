package com.turbonips.troglodytes.states;

import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.SystemManager;
import com.artemis.World;
import com.turbonips.troglodytes.PolymorphTrinket;
import com.turbonips.troglodytes.components.Attack;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.Direction.Dir;
import com.turbonips.troglodytes.components.HealthRegen;
import com.turbonips.troglodytes.components.Polymorph;
import com.turbonips.troglodytes.components.Secondary;
import com.turbonips.troglodytes.components.Stats;
import com.turbonips.troglodytes.components.Stats.StatType;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Location;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.VisitedMaps;
import com.turbonips.troglodytes.components.Warp;
import com.turbonips.troglodytes.systems.CollisionResolution;
import com.turbonips.troglodytes.systems.EnemyAISystem;
import com.turbonips.troglodytes.systems.EnemyBehaviorSystem;
import com.turbonips.troglodytes.systems.InputSystem;
import com.turbonips.troglodytes.systems.PlayerBehaviorSystem;
import com.turbonips.troglodytes.systems.RenderSystem;
import com.turbonips.troglodytes.systems.TrinketSystem;
import com.turbonips.troglodytes.systems.WarpSystem;

public class PlayingState extends BaseGameState {
	public static final int ID = 3;
	private World world;
	private SystemManager systemManager;
	private EntitySystem renderSystem;
	private EntitySystem inputSystem;
	private EntitySystem playerBehaviorSystem;
	private EntitySystem warpSystem;
	private EntitySystem enemyAISystem;
	private EntitySystem enemyBehaviorSystem;
	private EntitySystem trinketSystem;

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		// TODO Auto-generated method stub
		super.enter(container, game);
		world = new World();
	    systemManager = world.getSystemManager();
	    warpSystem = systemManager.setSystem(new WarpSystem());
	    renderSystem = systemManager.setSystem(new RenderSystem(container, game));
	    inputSystem = systemManager.setSystem(new InputSystem(container));
	    playerBehaviorSystem = systemManager.setSystem(new PlayerBehaviorSystem());
	    enemyAISystem = systemManager.setSystem(new EnemyAISystem());
	    enemyBehaviorSystem = systemManager.setSystem(new EnemyBehaviorSystem());
	    trinketSystem = systemManager.setSystem(new TrinketSystem());
	    systemManager.initializeAll();
	    CollisionResolution.getInstance().initialize(world);
		
		// Setup the initial player
		Entity player = world.createEntity();
		player.setGroup("PLAYER");
		player.addComponent(new Warp("testing", new Vector2f(20,20)));
		player.addComponent(new ResourceRef("playeranimation"));
		//player.addComponent();
		player.addComponent(new Movement(10, new Vector2f(2,2), new Vector2f(2,2)));
		player.addComponent(new Direction(Dir.DOWN));
		
		Entity trinket = world.createEntity();
		trinket.setGroup("TRINKET");
		trinket.addComponent(new Polymorph("playeranimation", true, true));
		trinket.addComponent(new ResourceRef("playerTrinket"));
		trinket.addComponent(new Location(new Vector2f(0,0), "THE_VOID"));
		trinket.refresh();
		
		HashMap<StatType, Integer> stats = new HashMap<StatType, Integer> ();
		stats.put(StatType.HEALTH, 100);
		stats.put(StatType.MAX_HEALTH, 100);
		stats.put(StatType.ARMOR, 25);
		stats.put(StatType.RANGE, 3);
		player.addComponent(new Stats(stats));
		player.addComponent(new VisitedMaps());
		// This position is overwritten when the player is warped
		player.addComponent(new Location(new Vector2f(0,0), ""));
		player.addComponent(new Attack(500,9));
		// It's over 9000
		player.addComponent(new Secondary(6*1000));
		player.addComponent(new HealthRegen(250));
		player.refresh();
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.init(container, game);
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
		inputSystem.process();
		playerBehaviorSystem.process();
		enemyAISystem.process();
		enemyBehaviorSystem.process();
		trinketSystem.process();
		warpSystem.process();
	}

	@Override
	public int getID() {
		return ID;
	}

}
