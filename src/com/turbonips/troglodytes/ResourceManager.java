package com.turbonips.troglodytes;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.newdawn.slick.SlickException;

import com.turbonips.troglodytes.components.Resource;

public class ResourceManager {
	private static final ResourceManager instance = new ResourceManager();
	private final Logger logger = Logger.getLogger(getClass());
	private HashMap<String, Resource> resources = new HashMap<String, Resource>();
	
	private ResourceManager() {
	}
	
	public static ResourceManager getInstance() {
		return instance;
	}
	
	public Resource getResource(String id) {
		Resource resource = null;
		ResourceFactory resourceFactory = ResourceFactory.getInstance();
		
		try {
			resource = resources.get(id);
			//resource = resourceFactory.create(id);
			if (resource == null) {
				// Create the resource
				resource = resourceFactory.create(id);
				resources.put(id, resource);
			}
			
		} catch(SlickException ex) {
			logger.error(ex);
		}
		return resource;
	}
	
	public void unloadResource(String id) {
		resources.put(id, null);
	}

}
