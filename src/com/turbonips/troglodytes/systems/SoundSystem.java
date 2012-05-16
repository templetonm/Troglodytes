package com.turbonips.troglodytes.systems;

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
		creatureSoundMapper = new ComponentMapper<CreatureSound>(
				CreatureSound.class, world);
	}

	@Override
	protected boolean checkProcessing()
	{
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities)
	{
		ImmutableBag<Entity> players = world.getGroupManager().getEntities(
				"PLAYER");

		if (players.get(0) != null){
			for (int i = 0; i < players.size(); i++){
				Entity player = players.get(i);
				CreatureSound creatureSound = creatureSoundMapper.get(player);
				if (creatureSound != null){
					creatureSound.playSound();
				}
			}
		}
	}
}