package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Resource;

public class MusicSystem extends BaseEntitySystem {
	private Music music;
	private GameContainer container;
	private ComponentMapper<Resource> resourceMapper;
	
	public MusicSystem(GameContainer container)
	{
		this.container = container;
	}
	
	@Override
	protected void initialize() {
		resourceMapper = new ComponentMapper<Resource>(Resource.class, world);
	}
	
	public void startMusic()
	{
		this.music.play();
	}
	
	@Override
	protected boolean checkProcessing()
	{
		return true;
	}
	
	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> layers = world.getGroupManager().getEntities("LAYER");
		
		if (!layers.isEmpty()) {
			TiledMap tiledMap = (TiledMap)resourceMapper.get(layers.get(0)).getObject();
			String songTitle = tiledMap.getMapProperty("Music", null);
			
			if (songTitle != null) {
				ResourceManager resourceManager = ResourceManager.getInstance(); 
				this.music = (Music)resourceManager.getResource(songTitle).getObject();
			
				if (this.music != null) {
					if(!this.music.playing()) {
						this.startMusic();
					}
				}
			}
		}
	}
	
}
