package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.turbonips.troglodytes.components.Sliding;
import com.turbonips.troglodytes.components.Transform;

public class ControlSystem extends EntityProcessingSystem implements KeyListener {
	private final GameContainer container;
    private ComponentMapper<Transform> positionMapper;
    private ComponentMapper<Sliding> slidingMapper;
    
    boolean key_up = false, 
    		key_down = false, 
    		key_left = false, 
    		key_right = false;

	public ControlSystem(GameContainer container) {
		super(Transform.class);
		this.container = container;
	}
	
	@Override
	protected void initialize() {
		positionMapper = new ComponentMapper<Transform>(Transform.class, world);
		slidingMapper = new ComponentMapper<Sliding>(Sliding.class, world);
		container.getInput().addKeyListener(this);
	}

	@Override
	protected void process(Entity e) {
		Transform position = positionMapper.get(e);
		Sliding sliding = slidingMapper.get(e);
		if (key_up) {
			position.setPosition(position.getX(), position.getY()-position.getSpeed());
			
			if (sliding != null) {
				if (sliding.getY() < sliding.getBox().getHeight()) {
					sliding.setY(sliding.getY() + sliding.getSpeed());
				}
			}
		} else if (key_down) {
			position.setPosition(position.getX(), position.getY()+position.getSpeed());
			
			if (sliding != null) {
				if (sliding.getY() > sliding.getBox().getY()) {
					sliding.setY(sliding.getY() - sliding.getSpeed());
				}
			}
		} 
		if (key_left) {
			position.setPosition(position.getX()-position.getSpeed(), position.getY());
			
			if (sliding != null) {
				if (sliding.getX() < sliding.getBox().getHeight()) {
					sliding.setX(sliding.getX() + sliding.getSpeed());
				}
			}
		} else if (key_right) {
			position.setPosition(position.getX()+position.getSpeed(), position.getY());
			
			if (sliding != null) {
				if (sliding.getX() > sliding.getBox().getX()) {
					sliding.setX(sliding.getX() - sliding.getSpeed());
				}
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
