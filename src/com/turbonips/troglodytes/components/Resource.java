package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class Resource extends Component {
	
	private final String path;
	private final String id;
	private final Object object;
	private final String type;
	
	public Resource(String id, String type, String path, Object object) {
		this.path = path;
		this.id = id;
		this.type = type;
		this.object = object;
	}

	public String getPath() {
		return path;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public Object getObject() {
		return object;
	}

}
