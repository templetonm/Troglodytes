package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.Collision;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Resource;
import com.turbonips.troglodytes.components.Sliding;
import com.turbonips.troglodytes.components.Position;

public class PlayerControlSystem extends BaseEntitySystem implements KeyListener {
	private final GameContainer container;
    private ComponentMapper<Position> positionMapper;
    private ComponentMapper<Sliding> slidingMapper;
    private ComponentMapper<Collision> collisionMapper;
	private ComponentMapper<Resource> resourceMapper;
	private ComponentMapper<Movement> movementMapper;
    
    boolean key_up = false, 
    		key_down = false, 
    		key_left = false, 
    		key_right = false,
    		key_esc = false;

	public PlayerControlSystem(GameContainer container) {
		super(Position.class, Movement.class);
		this.container = container;
	}
	
	@Override
	protected void initialize() {
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		slidingMapper = new ComponentMapper<Sliding>(Sliding.class, world);
		collisionMapper = new ComponentMapper<Collision>(Collision.class, world);
		resourceMapper = new ComponentMapper<Resource>(Resource.class, world);
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		container.getInput().addKeyListener(this);
	}
	
	
	// Adding a player component may be better then getting all player groups
	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		if (players != null && !players.isEmpty()) {
			for (int i=0; i<players.size(); i++) {
				Entity player = players.get(i);
				Movement movement = movementMapper.get(player);
				Collision collision = collisionMapper.get(player);
				
				if (movement != null) {
					movement.clearMovement();
					if (key_up) {
						if (collision == null) {
							movement.setMoveUp(true);
						} else if (!collision.isCollidingUp()) {
							movement.setMoveUp(true);
						}
					} else if (key_down) {
						if (collision == null) {
							movement.setMoveDown(true);
						} else if (!collision.isCollidingDown()) {
							movement.setMoveDown(true);
						}
					}
					if (key_left) {
						if (collision == null) {
							movement.setMoveLeft(true);
						} else if (!collision.isCollidingLeft()) {
							movement.setMoveLeft(true);
						}
					} else if (key_right) {
						if (collision == null) {
							movement.setMoveRight(true);
						} else if (!collision.isCollidingRight()) {
							movement.setMoveRight(true);
						}
					}
				}
				if (key_esc) { container.exit(); }
			}
		} else {
			logger.warn("There are no player entities");
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
