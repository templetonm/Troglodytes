package com.turbonips.troglodytes.systems;

import org.newdawn.slick.Sound;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.CreatureSound;

public class SoundSystem extends BaseEntitySystem
{
	private ComponentMapper<CreatureSound> creatureSoundMapper;

	@Override
	protected void initialize()
	{
		creatureSoundMapper = new ComponentMapper<CreatureSound>(CreatureSound.class, world);
	}
	
	@Override
	protected boolean checkProcessing()
	{
		return true;
	}

	private void processEntity(Entity e)
	{
		CreatureSound creatureSound = creatureSoundMapper.get(e);
		
		if (creatureSound != null)
		{
			creatureSound.playSound();
		}
	}
	
	@Override
	protected void processEntities(ImmutableBag<Entity> entities)
	{
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		
		if (players.get(0) != null)
		{
			for (int i = 0; i < players.size(); i++) {
				Entity player = players.get(i);
				processEntity(player);
			}
		}
	}
}
