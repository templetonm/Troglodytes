package com.turbonips.troglodytes.systems;

import org.newdawn.slick.SlickException;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.EntityFactory;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.Resource;
import com.turbonips.troglodytes.components.WarpObject;

public class WarpSystem extends BaseEntitySystem {
	private ComponentMapper<WarpObject> warpMapper;

	public WarpSystem() {	}
	
	@Override
	protected void initialize() {
		warpMapper = new ComponentMapper<WarpObject>(WarpObject.class, world);
	}
	
	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> layers = world.getGroupManager().getEntities("LAYER");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		
		if (!players.isEmpty()) {
			WarpObject warp = warpMapper.get(players.get(0));
			if (warp != null) {
				logger.info("Processing warp " + warp.toString());

				for (int i=0; i<layers.size(); i++) {
					Entity layer = layers.get(i);
					layer.delete();
				}
				
				for (int i=0; i<players.size(); i++) {
					Entity player = players.get(i);
					player.delete();
				}
				
				for (int i=0; i<enemies.size(); i++) {
					Entity enemy = enemies.get(i);
					enemy.delete();
				}
	
				try {
					EntityFactory.createMap(world, warp.getMapName(), warp.getPosition());
					EntityFactory.createPlayer(world, warp.getPosition());
				} catch (SlickException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		}
	}

}
