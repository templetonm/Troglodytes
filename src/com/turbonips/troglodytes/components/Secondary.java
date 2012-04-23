package com.turbonips.troglodytes.components;

public class Secondary extends TimeComponent {
	private boolean secondary = false;
	
	
	public Secondary(int cooldown) {
		super(cooldown);
	}

	public boolean isSecondary() {
		return secondary;
	}

	public void setSecondary(boolean secondary) {
		this.secondary = secondary;
	}
}
