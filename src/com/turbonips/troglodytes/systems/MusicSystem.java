package com.turbonips.troglodytes.systems;

import org.newdawn.slick.Music;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;

public class MusicSystem extends BaseEntitySystem
{
	private Music music;
	private TiledMap tiledMap;

	public MusicSystem(){
	}

	@Override
	protected void initialize(){
	}

	@Override
	protected boolean checkProcessing(){
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities){
		// Music is updated in WarpSystem; when a new map is loaded, a new music is assigned.
		if (music != null){
			if (!music.playing()){
				music.play();
			}
		}
	}
	
	public Music getMusic() {
		return music;
	}
	
	public void setMusic(Music music) {
		this.music = music;
	}
	
	public TiledMap getTiledMap() {
		return tiledMap;
	}
	
	public void setTiledMap(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}
}