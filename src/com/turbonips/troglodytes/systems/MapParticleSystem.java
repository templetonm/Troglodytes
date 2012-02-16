package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;

import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.ParticleComponent;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.Sliding;

public class MapParticleSystem extends BaseEntitySystem
{
	private GameContainer container;
	
	public MapParticleSystem(GameContainer container){
		this.container = container;
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
