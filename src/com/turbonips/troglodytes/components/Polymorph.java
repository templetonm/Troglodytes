package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class Polymorph extends Component {
	private String polymorphRef;
	private String trinketRef;
	private boolean existsOnPlayer;
	private boolean active;
	
	public Polymorph(String polymorphRef, boolean active, boolean existsOnPlayer) {
		this.setPolymorphRef(polymorphRef);
		this.setActive(active);
		this.setExistsOnPlayer(existsOnPlayer);
	}
	
	public Polymorph(String polymorphRef) {
		this.setPolymorphRef(polymorphRef);
		this.setActive(false);
		this.setExistsOnPlayer(false);
	}

	public String getPolymorphRef() {
		return polymorphRef;
	}

	public void setPolymorphRef(String polymorphRef) {
		this.polymorphRef = polymorphRef;
	}

	public boolean existsOnPlayer() {
		return existsOnPlayer;
	}

	public void setExistsOnPlayer(boolean existsOnPlayer) {
		this.existsOnPlayer = existsOnPlayer;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getTrinketRef() {
		return trinketRef;
	}

	public void setTrinketRef(String trinketRef) {
		this.trinketRef = trinketRef;
	}

}
