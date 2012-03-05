package com.turbonips.troglodytes.systems;

// TODO: We might want to pass in a tiled map ID to this instead of using the renerable mapper
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Renderable;

public class MusicSystem extends BaseEntitySystem {
	private Music music;
	private ComponentMapper<Renderable> renderableMapper;

	public MusicSystem(GameContainer container) {
	}

	@Override
	protected void initialize() {
		renderableMapper = new ComponentMapper<Renderable>(Renderable.class, world);
	}

	public void startMusic() {
		this.music.play(1f, .08f);
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> layers = world.getGroupManager().getEntities(
				"LAYER");

		if (!layers.isEmpty()) {
			TiledMap tiledMap = (TiledMap) renderableMapper.get(layers.get(0)).getResource().getObject();
			final String songTitle = tiledMap.getMapProperty("Music", null);

			if (songTitle != null) {
				final ResourceManager resourceManager = ResourceManager
						.getInstance();
				music = (Music) resourceManager.getResource(songTitle)
						.getObject();
			}
			
			if (this.music != null) {
				if (!this.music.playing()) {
					this.startMusic();
				}
			}
		}
	}
}
