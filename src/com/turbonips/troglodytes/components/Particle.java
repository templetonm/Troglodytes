package com.turbonips.troglodytes.components;

import org.newdawn.slick.particles.ParticleSystem;

import com.artemis.Component;

public class Particle extends Component
{
	private ParticleSystem particleSystem;

	public Particle(ParticleSystem particleSystem)
	{
		this.particleSystem = particleSystem;
	}

	public void updateParticleSystem(int delta)
	{
		particleSystem.update(delta);
	}

	public void renderParticleSystem(float x, float y)
	{
		particleSystem.setPosition(x, y);
		particleSystem.render();
	}
}