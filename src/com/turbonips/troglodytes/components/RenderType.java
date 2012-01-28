package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class RenderType extends Component {
	public static final int TYPE_GROUND_LAYER = 0;
	public static final int TYPE_BACKGROUND_LAYER = 1;
	public static final int TYPE_PLAYER = 2;
	public static final int TYPE_FOREGROUND_LAYER = 3;
	public static final int TYPE_WALL_LAYER = 4;
	public static final int TYPE_ENEMY = 5;
	
	private int type;
	
	public RenderType(int type) {
		setType(type);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
