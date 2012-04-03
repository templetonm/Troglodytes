package com.turbonips.troglodytes.systems;

import org.newdawn.slick.geom.Vector2f;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.Warp;

public class WarpSystem extends BaseEntityProcessingSystem {
	private ComponentMapper<Warp> warpMapper;

	public WarpSystem() {
		super(Warp.class);
	}
	
	@Override
	protected void initialize() {
		warpMapper = new ComponentMapper<Warp>(Warp.class, world);
	}

	@Override
	protected void process(Entity e) {
		ResourceManager manager = ResourceManager.getInstance();
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> grounds = world.getGroupManager().getEntities("GROUND");
		ImmutableBag<Entity> backgrounds = world.getGroupManager().getEntities("BACKGROUND");
		ImmutableBag<Entity> foregrounds = world.getGroupManager().getEntities("FOREGROUND");

		Warp warp = warpMapper.get(e);

		for (int i=0; i<players.size(); i++) {
			Entity player = players.get(i);
			player.delete();
		}
		for (int i=0; i<enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			enemy.delete();
		}
		for (int i=0; i<grounds.size(); i++) {
			Entity ground = grounds.get(i);
			ground.delete();
		}
		for (int i=0; i<backgrounds.size(); i++) {
			Entity background = backgrounds.get(i);
			background.delete();
		}
		for (int i=0; i<foregrounds.size(); i++) {
			Entity foregound = foregrounds.get(i);
			foregound.delete();
		}
		
		Entity player = world.createEntity();
		player.setGroup("PLAYER");
		player.addComponent(new ResourceRef("testplayerimage"));
		player.addComponent(new Movement(20, new Vector2f(2,2), new Vector2f(2,2)));
		player.refresh();
		
		Entity ground = world.createEntity();
		ground.setGroup("GROUND");
		ground.addComponent(new ResourceRef(warp.getMapName()));
		ground.refresh();

		Entity background = world.createEntity();
		background.setGroup("BACKGROUND");
		background.addComponent(new ResourceRef(warp.getMapName()));
		background.refresh();

		Entity foreground = world.createEntity();
		foreground.setGroup("FOREGROUND");
		foreground.addComponent(new ResourceRef(warp.getMapName()));
		foreground.refresh();
		
		e.removeComponent(warp);
		e.refresh();
	}

}
