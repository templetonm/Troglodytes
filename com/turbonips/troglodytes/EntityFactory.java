package com.turbonips.troglodytes;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.Entity;
import com.artemis.World;
import com.turbonips.troglodytes.components.Sliding;
import com.turbonips.troglodytes.components.SpatialForm;
import com.turbonips.troglodytes.components.Transform;

public class EntityFactory {
	public static Entity createLayer(World world, TiledMap tiledMap, int speed, Rectangle box, int layerNum) {
		Entity layer = world.createEntity();
		layer.setGroup("MAP");
		layer.addComponent(new Transform(new Vector2f(0, 0), speed));
		layer.addComponent(new Sliding(new Vector2f(0, 0), speed, box));
		layer.addComponent(new SpatialForm(tiledMap, layerNum));
		layer.refresh();
		return layer;
	}
	
	public static Entity createPlayer(World world, int speed, Rectangle box, Image playerImage) {
		Entity player = world.createEntity();
		player.setGroup("CREATURES");
		player.addComponent(new Transform(new Vector2f(0, 0), speed));
		player.addComponent(new Sliding(new Vector2f(0, 0), speed,  box));
		player.addComponent(new SpatialForm(playerImage, SpatialForm.TYPE_PLAYER));
		player.refresh();
		return player;
	}
}