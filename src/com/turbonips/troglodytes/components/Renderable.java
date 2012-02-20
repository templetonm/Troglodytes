package com.turbonips.troglodytes.components;

import com.artemis.Component;
import com.turbonips.troglodytes.Resource;

public class Renderable extends Component {
	public enum RenderType {
		GROUND_LAYER,
		BACKGROUND_LAYER,
		PLAYER,
		FOREGROUND_LAYER,
		WALL_LAYER,
		ENEMY,
		PARTICLE_SYSTEM
	}
	
	private RenderType renderType;
	private Resource resource;
	
	public Renderable(Resource resource, RenderType renderType) {
		this.setResource(resource);
		this.setRenderType(renderType);
	}

	public RenderType getRenderType() {
		return renderType;
	}


	public void setRenderType(RenderType renderType) {
		this.renderType = renderType;
	}


	public Resource getResource() {
		return resource;
	}


	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
