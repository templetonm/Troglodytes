package com.turbonips.troglodytes.components;

import java.util.Date;

import com.artemis.Component;

public class Secondary extends Component {
	private boolean secondary = false;
	private int cooldown;
	private long lastSecondaryTime;
	
	public Secondary(int cooldown) {
		this.cooldown = cooldown;
		this.lastSecondaryTime = 0;
	}
	
	public long getLastSecondaryTime() {
		return lastSecondaryTime;
	}
	
	public void setLastSecondaryTime(long lastSecondaryTime) {
		this.lastSecondaryTime = lastSecondaryTime;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public boolean isSecondary() {
		return secondary;
	}

	public void setSecondary(boolean secondary) {
		this.secondary = secondary;
	}
}
