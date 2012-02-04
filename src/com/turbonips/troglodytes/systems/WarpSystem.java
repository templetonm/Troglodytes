package com.turbonips.troglodytes.systems;

import org.newdawn.slick.SlickException;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.turbonips.troglodytes.EntityFactory;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.WarpObject;

public class WarpSystem extends BaseEntityProcessingSystem {
	
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<WarpObject> warpMapper;

	public WarpSystem() {
		super(WarpObject.class);
	}
	
	@Override
	protected void initialize() {
		//resourceMapper = new ComponentMapper<Resource>(Resource.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		warpMapper = new ComponentMapper<WarpObject>(WarpObject.class, world);
	}

	@Override
	protected void process(Entity e) {
		WarpObject warp = warpMapper.get(e);
		if (warp != null) {
			logger.info("Processing warp " + warp.toString());			
			try {
				//e.delete();
				EntityFactory.createMap(world, warp.getMapName(), warp.getPosition());
				e.removeComponent(warp);
				//EntityFactory.createPlayer(world, warp.getPosition());
			} catch (SlickException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			//e.refresh();
		}
	}

}
