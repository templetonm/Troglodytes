package com.turbonips.troglodytes.systems;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.ParticleComponent;

public class MapParticleSystem extends BaseEntitySystem
{
	private ComponentMapper<ParticleComponent> particleComponentMapper;
	
	public MapParticleSystem(){
	}

	@Override
	protected void initialize(){
		particleComponentMapper = new ComponentMapper<ParticleComponent>(ParticleComponent.class, world);
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
			Entity e = psys.get(i);
			ParticleComponent particleComponent = particleComponentMapper.get(e);
			particleComponent.updateParticleSystem(world.getDelta());
		}
	}

}
