package com.turbonips.troglodytes;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.Entity;
import com.artemis.World;
import com.turbonips.troglodytes.components.Collision;
import com.turbonips.troglodytes.components.Sliding;
import com.turbonips.troglodytes.components.SpatialForm;
import com.turbonips.troglodytes.components.Transform;

public class EntityFactory {
	public static Entity createLayer(World world, TiledMap tiledMap, int speed, Vector2f slidingStart, Rectangle box, int layerId) {
		Entity layer = world.createEntity();
		layer.setGroup("LAYER");
		layer.addComponent(new Transform(new Vector2f(0, 0), speed));
		layer.addComponent(new Sliding(slidingStart, speed, box));
		layer.addComponent(new SpatialForm(tiledMap, layerId));
		layer.addComponent(new Collision());
		layer.refresh();
		return layer;
	}
	
	public static Entity createPlayer(World world, int speed, Vector2f slidingStart, Rectangle box, Image playerImage) {
		Entity player = world.createEntity();
		player.setGroup("CREATURE");
		player.addComponent(new Transform(new Vector2f(0, 0), speed));
		player.addComponent(new Sliding(slidingStart, speed, box));
		player.addComponent(new SpatialForm(playerImage, SpatialForm.TYPE_PLAYER));
		player.addComponent(new Collision());
		player.refresh();
		return player;
	}
}