package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.artemis.utils.Bag;
import com.turbonips.troglodytes.components.Transform;
import com.turbonips.troglodytes.components.SpatialForm;
import com.turbonips.troglodytes.spatials.Layer;
import com.turbonips.troglodytes.spatials.Player;
import com.turbonips.troglodytes.spatials.Spatial;


public class RenderSystem extends EntityProcessingSystem {
	
	private final GameContainer container;
	private final Graphics graphics;
	private final Bag<Spatial> spatials;
	private ComponentMapper<SpatialForm> spatialFormMapper;

	public RenderSystem(GameContainer container) {
		super(Transform.class, SpatialForm.class);
		this.container = container;
		this.graphics = container.getGraphics();
		
		spatials = new Bag<Spatial>();
	}
	
	@Override
	protected void initialize() {
		spatialFormMapper = new ComponentMapper<SpatialForm>(SpatialForm.class, world);
	}
	
	@Override
	protected void added(Entity e) {
		Spatial spatial = createSpatial(e);
		if (spatial != null) {
			spatial.initalize();
			spatials.set(e.getId(), spatial);
		}
	}
	
	@Override
	protected void removed(Entity e) {
		spatials.remove(e.getId());
	}
	
	private Spatial createSpatial(Entity e) {
		SpatialForm spatialForm = spatialFormMapper.get(e);
		Object form = spatialForm.getForm();
		int spatialType = spatialForm.getType();
		
		switch(spatialType) {
			case SpatialForm.TYPE_PLAYER:
				return new Player(world, e, form, container);
			case SpatialForm.TYPE_GROUND_LAYER:
				return new Layer(world, e, form, 0);
			case SpatialForm.TYPE_BACKGROUND_LAYER:
				return new Layer(world, e, form, 1);
			case SpatialForm.TYPE_FOREGROUND_LAYER:
				return new Layer(world, e, form, 2);
			case SpatialForm.TYPE_WALL_LAYER:
				return new Layer(world, e, form, 3);
			default:
				return null;
		}
	}

	@Override
	protected void process(Entity e) {
		Spatial spatial = spatials.get(e.getId());
		spatial.render(graphics);		
	}

}
