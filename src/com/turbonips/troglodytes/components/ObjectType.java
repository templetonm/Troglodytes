package com.turbonips.troglodytes.components;


import org.newdawn.slick.tiled.TiledMap;

import com.artemis.Component;
import com.turbonips.troglodytes.ResourceManager;

public abstract class ObjectType extends Component {
	
	public static final int WARP_OBJECT = 0;
	
	public static ObjectType create(String mapId, int groupID, int objectID) {
		Resource mapResource = ResourceManager.getInstance().getResource(mapId);
		TiledMap map = (TiledMap)mapResource.getObject();
		String type = map.getObjectType(groupID, objectID).toLowerCase();
		
		if (type.equals("warp")) {
			String mapName = map.getObjectProperty(groupID, objectID, "Map", "0");
			int x = Integer.valueOf(map.getObjectProperty(groupID, objectID, "X", "0"));
			int y = Integer.valueOf(map.getObjectProperty(groupID, objectID, "Y", "0"));
			return new WarpObject(mapName, x, y);
		}
		
		return null;
	}
	public abstract int getType();

}
