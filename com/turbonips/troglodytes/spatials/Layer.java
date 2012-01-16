package com.turbonips.troglodytes.spatials;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.turbonips.troglodytes.components.Sliding;
import com.turbonips.troglodytes.components.Transform;

public class Layer extends Spatial {
	private Sliding sliding;
	private Transform position;
	private TiledMap tiledMap;
	private int layer;

	public Layer(World world, Entity owner, Object form, int layer) {
		super(world, owner);
		tiledMap = (TiledMap)form;
		this.layer = layer;
	}

	@Override
	public void initalize() {
		ComponentMapper<Transform> transformMapper = new ComponentMapper<Transform>(Transform.class, world);
		ComponentMapper<Sliding> slidingMapper = new ComponentMapper<Sliding>(Sliding.class, world);
		position = transformMapper.get(owner);
		sliding = slidingMapper.get(owner);
	}

	@Override
	public void render(Graphics g) {
		if (sliding != null) {
			tiledMap.render((int)position.getX()*-1 - (int)sliding.getX(), (int)position.getY()*-1 - (int)sliding.getY(), layer);
		} else {
			tiledMap.render((int)position.getX()*-1, (int)position.getY()*-1, layer);
		}
	}

}
