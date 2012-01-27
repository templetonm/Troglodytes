package com.turbonips.troglodytes.spatials;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.turbonips.troglodytes.components.Sliding;
import com.turbonips.troglodytes.components.Position;

public class Layer extends Spatial {
	private Sliding sliding;
	private Position position;
	private TiledMap tiledMap;
	private int layer;
	private final GameContainer container;

	public Layer(World world, Entity owner, Object form, int layer, GameContainer container) {
		super(world, owner);
		this.tiledMap = (TiledMap)form;
		this.layer = layer;
		this.container = container;
	}

	@Override
	public void initalize() {
		ComponentMapper<Position> positionMapper = new ComponentMapper<Position>(Position.class, world);
		ComponentMapper<Sliding> slidingMapper = new ComponentMapper<Sliding>(Sliding.class, world);
		position = positionMapper.get(owner);
		sliding = slidingMapper.get(owner);
	}

	@Override
	public void render(Graphics g) {
		if (sliding != null) {
			tiledMap.render((int)position.getX()*-1 - (int)sliding.getX() + container.getWidth()/2, (int)position.getY()*-1 - (int)sliding.getY() + container.getHeight()/2, layer);
		} else {
			tiledMap.render((int)position.getX()*-1 + container.getWidth()/2, (int)position.getY()*-1 + container.getHeight()/2 , layer);
		}
	}

}
