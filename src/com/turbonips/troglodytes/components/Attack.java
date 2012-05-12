package com.turbonips.troglodytes.components;

public class Attack extends TimeComponent {
	private boolean attacking = false;
	private int damage;

	public Attack(int cooldown, int damage) {
		super(cooldown);
		this.damage = damage;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public boolean isAttacking() {
		return attacking;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}
}
