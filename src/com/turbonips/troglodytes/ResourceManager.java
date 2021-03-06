package com.turbonips.troglodytes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.newdawn.slick.SlickException;



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
	
	public boolean loadMusicResources() {
		ResourceFactory resourceFactory = ResourceFactory.getInstance();
		for (String resourceId : resourceFactory.getResourceIds("music")) {
			getResource(resourceId);
		}
		return true;
	}
	
	public void unloadResource(String id) {
		resources.remove(id);
	}

}
