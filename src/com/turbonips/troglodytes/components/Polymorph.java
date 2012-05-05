package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class Polymorph extends Component {
	private String polymorphRef;
	private String originalRef;
	private boolean existsOnPlayer;
	private boolean active;
	
	public Polymorph(String originalRef, String polymorphRef, boolean active, boolean existsOnPlayer) {
		this.setPolymorphRef(polymorphRef);
		this.setOriginalRef(originalRef);
		this.setActive(active);
		this.setExistsOnPlayer(existsOnPlayer);
	}
	
	public Polymorph(String originalRef, String polymorphRef) {
		this.setPolymorphRef(polymorphRef);
		this.setOriginalRef(originalRef);
		this.setActive(false);
		this.setExistsOnPlayer(false);
	}

	public String getPolymorphRef() {
		return polymorphRef;
	}

	public void setPolymorphRef(String polymorphRef) {
		this.polymorphRef = polymorphRef;
	}

	public String getOriginalRef() {
		return originalRef;
	}

	public void setOriginalRef(String originalRef) {
		this.originalRef = originalRef;
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

}
