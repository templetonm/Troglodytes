package com.turbonips.troglodytes.components;

import java.util.Date;

import com.artemis.Component;

public class Attack extends Component {
	private boolean attacking = false;
	private int cooldown;
	private int damage;
	private long lastAttackTime;

	public Attack(int cooldown, int damage) {
		this.cooldown = cooldown;
		this.damage = damage;
		this.lastAttackTime = 0;
	}
	
	public long getLastAttackTime() {
		return lastAttackTime;
	}
	
	public void setLastAttackTime(long lastAttackTime) {
		this.lastAttackTime = lastAttackTime;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public boolean isAttacking() {
		return attacking;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}
}
