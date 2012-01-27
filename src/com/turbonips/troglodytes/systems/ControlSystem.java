package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.turbonips.troglodytes.components.Collision;
import com.turbonips.troglodytes.components.Sliding;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.AnimationCreature;

public class ControlSystem extends BaseEntityProcessingSystem implements KeyListener {
	private final GameContainer container;
    private ComponentMapper<Position> positionMapper;
    private ComponentMapper<Sliding> slidingMapper;
    private ComponentMapper<Collision> collisionMapper;
	private ComponentMapper<AnimationCreature> animationCreatureMapper;
    
    boolean key_up = false, 
    		key_down = false, 
    		key_left = false, 
    		key_right = false,
    		key_esc = false;

	public ControlSystem(GameContainer container) {
		super(Position.class);
		this.container = container;
	}
	
	@Override
	protected void initialize() {
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		slidingMapper = new ComponentMapper<Sliding>(Sliding.class, world);
		collisionMapper = new ComponentMapper<Collision>(Collision.class, world);
		animationCreatureMapper = new ComponentMapper<AnimationCreature>(AnimationCreature.class, world);
		container.getInput().addKeyListener(this);
	}

	@Override
	protected void process(Entity e) {
		Position position = positionMapper.get(e);
		Sliding sliding = slidingMapper.get(e);
		AnimationCreature animationCreature = animationCreatureMapper.get(e);
		Collision collision = collisionMapper.get(e);
		
		boolean collisionUp = false,
				collisionDown = false,
				collisionLeft = false,
				collisionRight = false;
		
		if (key_esc) container.exit();
		
		if (collision != null) {
			collisionUp = collision.isCollidingUp();
			collisionDown = collision.isCollidingDown();
			collisionLeft = collision.isCollidingLeft();
			collisionRight = collision.isCollidingRight();
		}
		
		
		if (key_up) {
			if (!collisionUp) {
				position.setPosition(position.getX(), position.getY()-position.getSpeed());
				
				if (sliding != null) {
					if (sliding.getY() < sliding.getBox().getHeight()) {
						sliding.setY(sliding.getY() + sliding.getSpeed());
					}
				}
			}
			
			if (animationCreature != null) {
				animationCreature.setCurrent(animationCreature.getMoveUp());
			}
		} else if (key_down) {
			if (!collisionDown) {
				position.setPosition(position.getX(), position.getY()+position.getSpeed());
				
				if (sliding != null) {
					if (sliding.getY() > sliding.getBox().getY()) {
						sliding.setY(sliding.getY() - sliding.getSpeed());
					}
				}
			}
			
			if (animationCreature != null) {
				animationCreature.setCurrent(animationCreature.getMoveDown());
			}
		} 
		if (key_left) {
			if (!collisionLeft) {
				position.setPosition(position.getX()-position.getSpeed(), position.getY());
				
				if (sliding != null) {
					if (sliding.getX() < sliding.getBox().getHeight()) {
						sliding.setX(sliding.getX() + sliding.getSpeed());
					}
				}
			}
			
			if (animationCreature != null) {
				animationCreature.setCurrent(animationCreature.getMoveLeft());
			}
		} else if (key_right) {
			if (!collisionRight) {
				position.setPosition(position.getX()+position.getSpeed(), position.getY());
				
				if (sliding != null) {
					if (sliding.getX() > sliding.getBox().getX()) {
						sliding.setX(sliding.getX() - sliding.getSpeed());
					}
				}
			}
			
			if (animationCreature != null) {
				animationCreature.setCurrent(animationCreature.getMoveRight());
			}
		}
		
		if (!key_up && !key_down && !key_left && !key_right) {
			if (animationCreature != null) {
				animationCreature.setIdle();
			}
		}
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
