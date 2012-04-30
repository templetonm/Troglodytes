package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class Polymorph extends Component {
	private String polymorphRef;
	private String originalRef;
	
	public Polymorph(String originalRef, String polymorphRef) {
		this.setPolymorphRef(polymorphRef);
		this.setOriginalRef(originalRef);
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

}
