package com.turbonips.troglodytes.systems;

import org.apache.log4j.Logger;

import com.artemis.Component;
import com.artemis.EntitySystem;

public abstract class BaseEntitySystem extends EntitySystem {
	protected final Logger logger = Logger.getLogger(getClass());
	
	public BaseEntitySystem(Class<? extends Component>... types) {
		super(types);
	}

	public BaseEntitySystem() {
	}
}
