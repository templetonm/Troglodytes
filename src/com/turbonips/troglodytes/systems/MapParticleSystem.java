package com.turbonips.troglodytes.systems;

import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.ParticleComponent;

public class MapParticleSystem extends BaseEntitySystem
{
	
	public MapParticleSystem(){
	}

	@Override
	protected boolean checkProcessing()
	{
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities)
	{
		// Update
		ImmutableBag<Entity> psys = world.getGroupManager().getEntities("MAPPARTICLESYSTEM");
		for (int i = 0; i < psys.size(); i++)
		{
			Entity entity = psys.get(i);
			ParticleComponent particlecomponent = entity.getComponent(ParticleComponent.class);
			particlecomponent.updateParticleSystem(world.getDelta());
		}
	}

}
