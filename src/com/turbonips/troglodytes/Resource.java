package com.turbonips.troglodytes;

public class Resource {
	
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
