package com.turbonips.troglodytes.components;

import org.newdawn.slick.Sound;

import com.artemis.Component;

public class CreatureSound extends Component
{
	private Sound movementSound;
	private Sound attackSound;
	private Sound deathSound;
	private Sound current;
	private int movementInterval = 100;
	private int movementIntervalCurrent;
	// private int attackLength;
	
	public CreatureSound(Sound movementSound)
	{
		this.movementSound = movementSound;
	}
	
	public void unsetCurrent()
	{
		current = null;
	}
	
	public void setCurrent(Sound current)
	{
		if (this.current != current)
		{
			this.current = current;
			if (current == movementSound)
			{
				movementIntervalCurrent = movementInterval;
			}
		}
	}
	
	public void forceSetCurrent(Sound current)
	{
		this.current = current;
	}
	
	public void playSound()
	{
		if (current != null) {
			if (!current.playing()) {
				if (current == movementSound) {
					if (movementIntervalCurrent == movementInterval) {
						current.play();
						movementIntervalCurrent--;
					} else {
						if (movementIntervalCurrent == 0)
							movementIntervalCurrent = movementInterval;
						else
							movementIntervalCurrent--;
					}
				} else {
					current.play();
				}
			}
		}
	}
	
	public void forcePlaySound()
	{
		if (current != null)
		{
			current.stop();
			current.play();
		}
	}
	
	public Sound getMovementSound()
	{
		return movementSound;
	}
	
	public Sound getAttackSound()
	{
		return attackSound;
	}
	
	public Sound getDeathSound()
	{
		return deathSound;
	}
	
	public Sound getCurrent()
	{
		return current;
	}
	
	public void setMovementSound(Sound movementSound)
	{
		this.movementSound = movementSound;
	}
	
	public void setMovementInterval(int movementInterval)
	{
		this.movementInterval = movementInterval;
	}
	
	public void setAttackSound(Sound attackSound)
	{
		this.attackSound = attackSound;
	}
}
