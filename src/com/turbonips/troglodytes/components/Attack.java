package com.turbonips.troglodytes.components;

public class Attack extends TimeComponent {
	private boolean attacking = false;

	public Attack() {
		super();
	}

	public boolean isAttacking() {
		return attacking;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}
}
