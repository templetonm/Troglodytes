package com.turbonips.troglodytes.components;

import org.newdawn.slick.particles.ParticleSystem;

import com.artemis.Component;

public class ParticleComponent extends Component
{
	private ParticleSystem particleSystem;
	
	public ParticleComponent(ParticleSystem particleSystem){
		this.particleSystem = particleSystem;
	}
	
	public void updateParticleSystem(int delta){//, float x, float y){
		//particleSystem.setPosition(x, y);
		particleSystem.update(delta);
	}
	
	public void renderParticleSystem(float x, float y){
		particleSystem.setPosition(x, y);
		particleSystem.render();
	}
}
