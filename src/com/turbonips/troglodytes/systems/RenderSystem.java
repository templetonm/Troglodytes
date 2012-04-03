package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.ResourceRef;

public class RenderSystem extends BaseEntitySystem {
	private ComponentMapper<ResourceRef> resourceMapper;
	private GameContainer container;
	
	public RenderSystem(GameContainer container) {
		super(ResourceRef.class);
		this.container = container;
	}

	@Override
	protected void initialize() {
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		Graphics g = container.getGraphics();
		ResourceManager manager = ResourceManager.getInstance();
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> grounds = world.getGroupManager().getEntities("GROUND");
		ImmutableBag<Entity> backgrounds = world.getGroupManager().getEntities("BACKGROUND");
		ImmutableBag<Entity> foregrounds = world.getGroupManager().getEntities("FOREGROUND");
		for (int i=0; i<grounds.size(); i++) {
			Entity ground = grounds.get(i);
			String mapResName = resourceMapper.get(ground).getResourceName();
			Resource mapRes = manager.getResource(mapResName);
			TiledMap map = (TiledMap)mapRes.getObject();
			map.render(0, 0, 0);
		}
		
		for (int i=0; i<players.size(); i++) {
			Entity player = players.get(i);
			String playerResName = resourceMapper.get(player).getResourceName();
			Resource playerRes = manager.getResource(playerResName);
			Image playerSprite = (Image)playerRes.getObject();
			g.drawImage(playerSprite, container.getWidth()/2, container.getHeight()/2);
		}
		
		for (int i=0; i<backgrounds.size(); i++) {
			Entity background = backgrounds.get(i);
			String mapResName = resourceMapper.get(background).getResourceName();
			Resource mapRes = manager.getResource(mapResName);
			TiledMap map = (TiledMap)mapRes.getObject();
			map.render(0, 0, 1);
		}
		
		for (int i=0; i<foregrounds.size(); i++) {
			Entity foreground = foregrounds.get(i);
			String mapResName = resourceMapper.get(foreground).getResourceName();
			Resource mapRes = manager.getResource(mapResName);
			TiledMap map = (TiledMap)mapRes.getObject();
			map.render(0, 0, 2);
		}			
	}
		
}
