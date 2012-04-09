package com.turbonips.troglodytes.systems;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.geom.Vector2f;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.Direction.Dir;
import com.turbonips.troglodytes.components.Movement;

public class InputSystem extends BaseEntitySystem implements KeyListener {
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Direction> directionMapper;
	private GameContainer container;

	boolean key_up = false, 
			key_down = false, 
			key_left = false, 
			key_right = false, 
			key_esc = false, 
			key_ctrl = false;

	public InputSystem(GameContainer container) {
		this.container = container;
	}

	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		directionMapper = new ComponentMapper<Direction>(Direction.class, world);
		container.getInput().addKeyListener(this);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		
		for (int i = 0; i < players.size(); i++) {
			Entity player = players.get(i);

			Movement movement = movementMapper.get(player);
			if (movement != null) {
				Vector2f velocity = movement.getVelocity();
				Vector2f acceleration = movement.getAcceleration();
				Vector2f deceleration = movement.getDeceleration();
				Vector2f tmpVelocity = new Vector2f(velocity);
				if (key_up) {
					tmpVelocity.y -= acceleration.y;
				} else if (key_down) {
					tmpVelocity.y += acceleration.y;
				}
				if (key_left) {
					tmpVelocity.x -= acceleration.x;
				} else if (key_right) {
					tmpVelocity.x += acceleration.x;
				}
				if (!key_up && !key_down) {
					if (tmpVelocity.y > 0) {
						tmpVelocity.y -= deceleration.y;
						if (tmpVelocity.y < 0)
							tmpVelocity.y = 0;
					}
					else if (tmpVelocity.y < 0) {
						tmpVelocity.y += deceleration.y;
						if (tmpVelocity.y > 0)
							tmpVelocity.y = 0;
					}
				}
				if (!key_left && !key_right) {
					// Neither key is pressed. Tend to zero velocity
					if (tmpVelocity.x > 0) {
						tmpVelocity.x -= deceleration.x;
						if (tmpVelocity.x < 0)
							tmpVelocity.x = 0;
					}
					// Neither key is pressed. Tend to zero velocity
					else if (tmpVelocity.x < 0) {
						tmpVelocity.x += deceleration.x;
						if (tmpVelocity.x > 0)
							tmpVelocity.x = 0;
					}
				}
				if (tmpVelocity.distance(new Vector2f(0,0))>movement.getMaximumSpeed()) {
					float scale;
					// Scale the x and y velocity by the maximum-speed/current-velocity
					scale = (movement.getMaximumSpeed()/tmpVelocity.distance(new Vector2f(0,0)));
					tmpVelocity.x = scale*tmpVelocity.x;
					tmpVelocity.y = scale*tmpVelocity.y;
				}
				velocity.x = tmpVelocity.x;
				velocity.y = tmpVelocity.y;
				
				Direction direction = directionMapper.get(player);
				ArrayList<Dir> directions = direction.getDirections();
				if (movement.getCurrentSpeed() != 0) {
					directions.clear();
					if (velocity.x > 0) {
						directions.add(Dir.RIGHT);
					} else if (velocity.x < 0) {
						directions.add(Dir.LEFT);
					}
					if (velocity.y > 0) {
						directions.add(Dir.DOWN);
					} else if (velocity.y < 0) {
						directions.add(Dir.UP);
					}
				}
			}
		
		}
		if (key_esc) {
			container.exit();
		}
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	public void keyPressed(int key, char c) {
		switch (key) {
			case Input.KEY_UP:
				key_up = true;
				break;
			case Input.KEY_DOWN:
				key_down = true;
				break;
			case Input.KEY_LEFT:
				key_left = true;
				break;
			case Input.KEY_RIGHT:
				key_right = true;
				break;
			case Input.KEY_ESCAPE:
				key_esc = true;
				break;
			case Input.KEY_RCONTROL:
			case Input.KEY_LCONTROL:
				key_ctrl = true;
				break;
		}
	}

	@Override
	public void keyReleased(int key, char c) {
		switch (key) {
			case Input.KEY_UP:
				key_up = false;
				break;
			case Input.KEY_DOWN:
				key_down = false;
				break;
			case Input.KEY_LEFT:
				key_left = false;
				break;
			case Input.KEY_RIGHT:
				key_right = false;
				break;
			case Input.KEY_ESCAPE:
				key_esc = false;
				break;
			case Input.KEY_RCONTROL:
			case Input.KEY_LCONTROL:
				key_ctrl = false;
				break;
		}
	}

	@Override
	public void setInput(Input input) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void inputEnded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputStarted() {
		// TODO Auto-generated method stub

	}
}
