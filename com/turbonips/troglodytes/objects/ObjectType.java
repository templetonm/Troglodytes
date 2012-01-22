package com.turbonips.troglodytes.objects;

import java.util.ArrayList;

import org.newdawn.slick.tiled.TiledMap;

import com.turbonips.troglodytes.components.SpatialForm;

public abstract class ObjectType {
	
	public static final int WARP_OBJECT = 0;
	
	public static ObjectType create(ArrayList<SpatialForm> mapLayers, int groupID, int objectID) {
		TiledMap map = (TiledMap)mapLayers.get(0).getForm();
		String type = map.getObjectType(groupID, objectID).toLowerCase();
		
		if (type.equals("warp")) {
			String mapName = map.getObjectProperty(groupID, objectID, "Map", "0");
			int x = Integer.valueOf(map.getObjectProperty(groupID, objectID, "X", "0"));
			int y = Integer.valueOf(map.getObjectProperty(groupID, objectID, "Y", "0"));
			return new WarpObject(mapName, x, y);
		}
		
		return null;
	}
	
	public abstract void process();
	public abstract int getType();

}
