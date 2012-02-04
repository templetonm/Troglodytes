package com.turbonips.troglodytes.systems;

import org.apache.log4j.Logger;

import com.artemis.Component;
import com.artemis.EntityProcessingSystem;

public abstract class BaseEntityProcessingSystem extends EntityProcessingSystem {
	protected final Logger logger = Logger.getLogger(getClass());
	
	public BaseEntityProcessingSystem(Class<? extends Component> requiredType, Class<? extends Component>... otherTypes) {
		super(requiredType, otherTypes);
	}
}
