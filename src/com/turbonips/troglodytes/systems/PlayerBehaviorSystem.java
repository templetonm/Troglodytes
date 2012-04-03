package com.turbonips.troglodytes.systems;

import org.newdawn.slick.geom.Vector2f;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;

public class PlayerBehaviorSystem extends BaseEntitySystem {
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Position> positionMapper;

	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		for (int i = 0; i < players.size(); i++) {
			Entity player = players.get(i);
			Movement movement = movementMapper.get(player);
			Position pos = positionMapper.get(player);
			Vector2f position = pos.getPosition();
			Vector2f velocity = movement.getVelocity();
			position.add(velocity);
		}
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}
}
