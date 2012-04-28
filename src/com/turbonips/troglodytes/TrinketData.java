package com.turbonips.troglodytes;

public class TrinketData {
	private String name;
	private String type;
	private String resourceRef;
	
	public enum TrinketType {
		none,
		polymorph
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TrinketType getType() {
		type = type.toLowerCase();
		
		if (type.equals("polymorph")) {
			return TrinketType.polymorph;
		} else {
			return TrinketType.none;
		}		
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getResourceRef() {
		return resourceRef;
	}
	public void setResourceRef(String resourceRef) {
		this.resourceRef = resourceRef;
	}
}
