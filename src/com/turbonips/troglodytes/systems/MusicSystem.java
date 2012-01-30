package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;

import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;

public class MusicSystem extends BaseEntitySystem
{
	private GameContainer container;
	private Music music;
	private boolean shouldBePlayingMusic;
	
	public MusicSystem(GameContainer container)
	{
		this.container = container;
		shouldBePlayingMusic = false;
	}
	
	public void setMusic(Music music)
	{
		this.music = music;
	}
	
	public void startMusic()
	{
		this.music.play();
		this.shouldBePlayingMusic = true;
	}
	
	@Override
	protected boolean checkProcessing()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities)
	{
		// TODO Auto-generated method stub
		if (this.music != null)
		{
			if (this.shouldBePlayingMusic)
			{
				if(!this.music.playing())
				{
					this.startMusic();
				}
			}
		}
	}

}
