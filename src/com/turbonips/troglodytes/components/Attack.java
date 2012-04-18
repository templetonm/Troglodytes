package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class Attack extends Component {
	
	private boolean attacking=false;
	private int cooldown;

	public Attack(int cooldown) {
		this.cooldown = cooldown;
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
