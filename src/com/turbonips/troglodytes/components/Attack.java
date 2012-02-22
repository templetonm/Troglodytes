package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class Attack extends Component {
	private boolean attacking;
	
	public Attack() {
		setAttacking(false);
	}

	public boolean isAttacking() {
		return attacking;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

}
