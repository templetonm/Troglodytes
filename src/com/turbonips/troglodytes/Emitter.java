package com.turbonips.troglodytes;

import org.newdawn.slick.Image;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;

public class Emitter implements ParticleEmitter
{
    private float x;
    private float y;
    private int interval = 1000;
    private int timer;
    private float size = 10;
    private int maxParticleLifespan = 1000;
    private int maxParticles = 1;
    private float velocityX = 0;
    private float velocityY = 0;
    private float spawnOffsetX = 0;
    private float spawnOffsetY = 0;
    
    private boolean enabled = false;
    
	public Emitter(int x, int y)//, float size)
	{
        this.x = x;
        this.y = y;
        this.size = 10;
        this.timer = this.interval;
	}
	
	@Override
	public boolean completed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Image getImage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public boolean isOriented()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetState()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	@Override
	public void update(ParticleSystem system, int delta)
	{
		timer -= delta;
		if (timer <= 0)
		{
			timer = interval;
			for (int i = 0; i < maxParticles; i++)
			{
				velocityX = 0;
				velocityY = -5;
				
				spawnOffsetX = 10*(float)Math.random();
				if (i%2 == 0)
				{
					spawnOffsetX = -spawnOffsetX;
				}
				
				Particle p = system.getNewParticle(this, maxParticleLifespan);
                p.setColor(1, 1, 1, 0.5f);
                p.setPosition(x + spawnOffsetX, y + spawnOffsetY);
                p.setSize(size);
                p.setVelocity(velocityX, velocityY, 0.005f);
			}
		}
	}

	@Override
	public void updateParticle(Particle particle, int delta)
	{
		// TODO: Add 'changes in particle behavior' section (so they don't fly in straight path, change size, etc);
        float c = 0.002f * delta;
        particle.adjustColor(c, c, c, -c / 10); 
	}

	@Override
	public boolean useAdditive()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean usePoints(ParticleSystem system)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void wrapUp()
	{
		// TODO Auto-generated method stub

	}

    public void moveEmitter(float newX, float newY) {
        this.x = newX;
        this.y = newY;
    }
}
