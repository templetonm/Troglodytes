package com.turbonips.troglodytes.components;

import org.newdawn.slick.particles.ParticleSystem;

import com.artemis.Component;

public class ParticleComponent extends Component {
	private ParticleSystem particleSystem;
	private int timeSum;
	private boolean light = false;
	
	private boolean emitterFinite = false;
	private boolean emitterFinished = false;

	public ParticleComponent(ParticleSystem particleSystem) {
		this.particleSystem = particleSystem;
	}

	public ParticleComponent(ParticleSystem particleSystem, boolean emitterFinite) {
		this.particleSystem = particleSystem;
		this.emitterFinite = emitterFinite;
	}
	
	public ParticleComponent(ParticleSystem particleSystem, boolean emitterFinite, boolean light) {
		this.particleSystem = particleSystem;
		this.emitterFinite = emitterFinite;
		this.light = light;
	}
	
	public void updateParticleSystem(int delta) {
		particleSystem.update(delta);
		
		if (emitterFinite && particleSystem.getParticleCount() == 0) {
			emitterFinished = true;
		}
	}

	public void renderParticleSystem(float x, float y) {
		particleSystem.setPosition(x, y);
		particleSystem.render();
	}
	
	public void sumTime(int delta) {
		timeSum += delta;
	}
	
	public int getTime() {
		return timeSum;
	}
	
	public boolean getEmitterFinished() {
		return emitterFinished;
	}
	
	public void setEmitterFinished(boolean emitterFinished) {
		this.emitterFinished = emitterFinished;
	}

	public boolean isLight() {
		return light;
	}

	public void setLight(boolean light) {
		this.light = light;
	}
}