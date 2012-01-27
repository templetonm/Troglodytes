package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class SpatialForm extends Component {
	
	private Object form;
	private final int type;
	
	public static final int TYPE_GROUND_LAYER = 0;
	public static final int TYPE_BACKGROUND_LAYER = 1;
	public static final int TYPE_PLAYER = 2;
	public static final int TYPE_FOREGROUND_LAYER = 3;
	public static final int TYPE_WALL_LAYER = 4;
	
	public SpatialForm(Object form, int type) {
		this.form = form;
		this.type = type;
	}
	
	public void setForm(Object form) {
		this.form = form;
	}

	public Object getForm() {
		return form;
	}

	public int getType() {
		return type;
	}
	

}
