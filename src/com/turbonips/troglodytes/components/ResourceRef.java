package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class ResourceRef extends Component {
	
	private String resourceName;

	public ResourceRef(String resourceName) {
		setResourceName(resourceName);
	}
	
	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
}
