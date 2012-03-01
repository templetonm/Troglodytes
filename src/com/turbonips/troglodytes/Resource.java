package com.turbonips.troglodytes;

public class Resource {
	
	private final String path;
	private final String id;
	private final Object object;
	public enum ResourceType { 
		IMAGE, 
		TILED_MAP,
		SPRITE_SHEET, 
		CREATURE_ANIMATION, 
		MUSIC,
		SOUND
	}
	private final ResourceType type;
	
	public Resource(String id, ResourceType type, String path, Object object) {
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

	public ResourceType getType() {
		return type;
	}

	public Object getObject() {
		return object;
	}

}
