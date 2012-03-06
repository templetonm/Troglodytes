package com.turbonips.troglodytes.systems;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.components.Attack;
import com.turbonips.troglodytes.components.Collision;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.CreatureSound;
import com.turbonips.troglodytes.components.Renderable;

public class PlayerControlSystem extends BaseEntitySystem implements KeyListener {
	private final GameContainer container;
    private ComponentMapper<Collision> collisionMapper;
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Attack> attackMapper;
    private ComponentMapper<CreatureSound> creatureSoundMapper;
    private ComponentMapper<Renderable> renderableMapper;

    boolean key_up = false, 
    		key_down = false, 
    		key_left = false, 
    		key_right = false,
    		key_esc = false,
    		key_ctrl = false;

	public PlayerControlSystem(GameContainer container) {
		this.container = container;
	}
	
	@Override
	protected void initialize() {
		collisionMapper = new ComponentMapper<Collision>(Collision.class, world);
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		attackMapper = new ComponentMapper<Attack>(Attack.class, world);
		creatureSoundMapper = new ComponentMapper<CreatureSound>(CreatureSound.class, world);
		renderableMapper = new ComponentMapper<Renderable>(Renderable.class, world);
		container.getInput().addKeyListener(this);
	}
	
	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		if (players != null && !players.isEmpty()) {
			for (int i=0; i<players.size(); i++) {
				Entity player = players.get(i);
				Movement movement = movementMapper.get(player);
				Collision collision = collisionMapper.get(player);
				Attack attack = attackMapper.get(player);
				CreatureSound creatureSound = creatureSoundMapper.get(player);
				if (renderableMapper.get(player) != null) {
					CreatureAnimation playerCreatureAnimation = (CreatureAnimation)renderableMapper.get(player).getResource().getObject();
					
					if (movement != null) {
						movement.clearMovement();
						if (key_up) {
							if (collision == null) {
								movement.setMoveUp(true);
							} else if (!collision.isCollidingUp()) {
								movement.setMoveUp(true);
								if (creatureSound != null){
									creatureSound.setCurrent(creatureSound.getMovementSound());
								}
							} else {
								if (creatureSound != null){
									creatureSound.unsetCurrent();
								}
								if (playerCreatureAnimation != null) {
									movement.setAnimation(playerCreatureAnimation.getIdleUp());
								}
							}
						} else if (key_down) {
							if (collision == null) {
								movement.setMoveDown(true);
							} else if (!collision.isCollidingDown()) {
								movement.setMoveDown(true);
								if (creatureSound != null){
									creatureSound.setCurrent(creatureSound.getMovementSound());
								}
							} else {
								if (creatureSound != null){
									creatureSound.unsetCurrent();
								}
								if (playerCreatureAnimation != null) {
									movement.setAnimation(playerCreatureAnimation.getIdleDown());
								}
							}
						}
						if (key_left) {
							if (collision == null) {
								movement.setMoveLeft(true);
							} else if (!collision.isCollidingLeft()) {
								movement.setMoveLeft(true);
								if (creatureSound != null){
									creatureSound.setCurrent(creatureSound.getMovementSound());
								}
							} else {
								if (creatureSound != null){
									creatureSound.unsetCurrent();
								}
								if (playerCreatureAnimation != null) {
									movement.setAnimation(playerCreatureAnimation.getIdleLeft());
								}
							}
						} else if (key_right) {
							if (collision == null) {
								movement.setMoveRight(true);
							} else if (!collision.isCollidingRight()) {
								movement.setMoveRight(true);
								if (creatureSound != null){
									creatureSound.setCurrent(creatureSound.getMovementSound());
								}
							} else {
								if (creatureSound != null){
									creatureSound.unsetCurrent();
								}
								logger.info("HERE");
								if (playerCreatureAnimation != null) {
									movement.setAnimation(playerCreatureAnimation.getIdleRight());
									
								}
							}
						}
						
						if (!key_up && !key_down && !key_right && !key_left)
						{
							if (creatureSound != null){
								creatureSound.unsetCurrent();
							}
						}
					}
					
					if (attack != null) {
						attack.setAttacking(key_ctrl);
						key_ctrl = false;
					}
					
					if (key_esc) { container.exit(); }
				}
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
