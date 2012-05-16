package com.turbonips.troglodytes.systems;

import java.util.Date;
import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.geom.Vector2f;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.components.Attack;
import com.turbonips.troglodytes.components.CreatureSound;
import com.turbonips.troglodytes.components.Debug;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.Direction.Dir;
import com.turbonips.troglodytes.components.Stats;
import com.turbonips.troglodytes.components.Stats.StatType;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Polymorph;
import com.turbonips.troglodytes.components.Secondary;
import com.turbonips.troglodytes.components.StatModifiers;

public class InputSystem extends BaseEntitySystem implements KeyListener {
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Direction> directionMapper;
	private ComponentMapper<Attack> attackMapper;
	private ComponentMapper<Secondary> secondaryMapper;
	private ComponentMapper<Polymorph> polymorphMapper;
	private ComponentMapper<CreatureSound> creatureSoundMapper;
	private ComponentMapper<Debug> debugMapper;
	private GameContainer container;

	boolean key_up = false, 
			key_down = false, 
			key_left = false, 
			key_right = false, 
			key_esc = false, 
			key_attack = false,
			key_secondary = false,
			key_a = false,
			key_d = false,
			key_w = false,
			key_p = false;
	private ComponentMapper<StatModifiers> statModifiersMapper;
	private ComponentMapper<Stats> statsMapper;
	

	public InputSystem(GameContainer container) {
		this.container = container;
	}

	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		directionMapper = new ComponentMapper<Direction>(Direction.class, world);
		attackMapper = new ComponentMapper<Attack>(Attack.class, world);
		secondaryMapper = new ComponentMapper<Secondary>(Secondary.class, world);
		polymorphMapper = new ComponentMapper<Polymorph>(Polymorph.class, world);
		statModifiersMapper = new ComponentMapper<StatModifiers>(StatModifiers.class, world);
		statsMapper = new ComponentMapper<Stats>(Stats.class, world);
		creatureSoundMapper = new ComponentMapper<CreatureSound>(CreatureSound.class, world);
		debugMapper = new ComponentMapper<Debug>(Debug.class, world);
		container.getInput().addKeyListener(this);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		
		Entity player = players.get(0);
		Attack playerAttack = attackMapper.get(player);
		Secondary playerSecondary = secondaryMapper.get(player);
		Movement movement = movementMapper.get(player);
		CreatureSound creatureSound = creatureSoundMapper.get(player);
		HashMap<StatType, Integer> playerStats = statsMapper.get(player).getStats();
		if (movement != null) {
			Vector2f velocity = movement.getVelocity();
			int acceleration = playerStats.get(StatType.ACCELERATION);
			int deceleration = playerStats.get(StatType.DECELERATION);
			int maxSpeed = playerStats.get(StatType.MAX_SPEED);
			Vector2f tmpVelocity = new Vector2f(velocity);
			if (key_up) {
				tmpVelocity.y -= acceleration;
			} else if (key_down) {
				tmpVelocity.y += acceleration;
			}
			if (key_left) {
				tmpVelocity.x -= acceleration;
			} else if (key_right) {
				tmpVelocity.x += acceleration;
			}
			if (!key_up && !key_down) {
				if (tmpVelocity.y > 0) {
					tmpVelocity.y -= deceleration;
					if (tmpVelocity.y < 0)
						tmpVelocity.y = 0;
				}
				else if (tmpVelocity.y < 0) {
					tmpVelocity.y += deceleration;
					if (tmpVelocity.y > 0)
						tmpVelocity.y = 0;
				}
			}
			if (!key_left && !key_right) {
				// Neither key is pressed. Tend to zero velocity
				if (tmpVelocity.x > 0) {
					tmpVelocity.x -= deceleration;
					if (tmpVelocity.x < 0)
						tmpVelocity.x = 0;
				}
				// Neither key is pressed. Tend to zero velocity
				else if (tmpVelocity.x < 0) {
					tmpVelocity.x += deceleration;
					if (tmpVelocity.x > 0)
						tmpVelocity.x = 0;
				}
			}
			if (tmpVelocity.distance(new Vector2f(0,0))>maxSpeed) {
				float scale;
				// Scale the x and y velocity by the maximum-speed/current-velocity
				scale = (maxSpeed/tmpVelocity.distance(new Vector2f(0,0)));
				tmpVelocity.x = scale*tmpVelocity.x;
				tmpVelocity.y = scale*tmpVelocity.y;
			}
			velocity.x = tmpVelocity.x;
			velocity.y = tmpVelocity.y;
			
			Direction direction = directionMapper.get(player);
			if (movement.getCurrentSpeed() != 0) {
				if (velocity.x > 0) {
					if (velocity.y > 0) {
						direction.setDirection(Dir.DOWN_RIGHT);
					} else if (velocity.y < 0) {
						direction.setDirection(Dir.UP_RIGHT);
					} else {
						direction.setDirection(Dir.RIGHT);
					}
				} else if (velocity.x < 0) {
					if (velocity.y > 0) {
						direction.setDirection(Dir.DOWN_LEFT);
					} else if (velocity.y < 0) {
						direction.setDirection(Dir.UP_LEFT);
					} else {
						direction.setDirection(Dir.LEFT);
					}
				} else {
					if (velocity.y > 0) {
						direction.setDirection(Dir.DOWN);
					} else {
						direction.setDirection(Dir.UP);
					}
				}
			}
			
			int attackCooldown = playerStats.get(StatType.ATTACK_COOLDOWN);
			if (attackCooldown < 0) attackCooldown = 0;
			if (key_attack && new Date().getTime()-playerAttack.getLastTime() > attackCooldown) {
				playerAttack.setAttacking(true);
				playerAttack.setLastTime(new Date().getTime());
			} else {
				playerAttack.setAttacking(false);
			}
			
			if (key_secondary && new Date().getTime()-playerSecondary.getLastTime() > playerSecondary.getTime()) {
				playerSecondary.setSecondary(true);
				playerSecondary.setLastTime(new Date().getTime());
			} else {
				playerSecondary.setSecondary(false);
			}
			
			// MovementSound
			if (movement.getCurrentSpeed() != 0) {
				if (creatureSound != null){
					creatureSound.setCurrent(creatureSound.getMovementSound());
				}
			} else {
				if (creatureSound != null) {
					creatureSound.unsetCurrent();
				}
			}
		}
		
		// Cycle through different polymorph trinkets
		if (key_a || key_d) {
			ImmutableBag<Entity> trinkets = world.getGroupManager().getEntities("TRINKET");
			int activeIndex = 0, rightIndex = 0, leftIndex = 0;
			for (int i=0; i<trinkets.size(); i++) {
				Entity trinket = trinkets.get(i);
				Polymorph polymorph = polymorphMapper.get(trinket);
				if (polymorph != null && polymorph.existsOnPlayer()) {
					if (polymorph.isActive()) {
						activeIndex = i;
						polymorph.setActive(false);
						
						for (int r=activeIndex+1; r<trinkets.size(); r++) {
							Entity rightTrinket = trinkets.get(r);
							Polymorph rightPolymorph = polymorphMapper.get(rightTrinket);
							if (rightPolymorph != null && rightPolymorph.existsOnPlayer()) {
								rightIndex = r;
								break;
							}
						}
						
						for (int l=activeIndex-1; l>0; l--) {
							Entity leftTrinket = trinkets.get(l);
							Polymorph leftPolmorph = polymorphMapper.get(leftTrinket);
							if (leftPolmorph != null && leftPolmorph.existsOnPlayer()) {
								leftIndex = l;
								break;
							}
						}
					}
				}
			}
			
			// Remove previous polymorph modifiers
			StatModifiers statModifiers = statModifiersMapper.get(trinkets.get(activeIndex));
			HashMap<StatType, Integer> modifiers;
			if (statModifiers != null) {
				modifiers = statModifiers.getModifiers();
				statsMapper.get(player).removeModifiers(modifiers);
			}
			
			Entity polymorphEntity;
			if (key_a) {
				polymorphEntity = trinkets.get(leftIndex);
			} else {
				if (rightIndex < activeIndex) rightIndex = activeIndex;
				polymorphEntity = trinkets.get(rightIndex);
			}
			
			// Add new polymorph modifiers
			statModifiers = statModifiersMapper.get(polymorphEntity);
			if (statModifiers != null) {
				modifiers = statModifiers.getModifiers();
				statsMapper.get(player).applyModifiers(modifiers);
			}
			polymorphMapper.get(polymorphEntity).setActive(true);
			key_a = false;
			key_d = false;
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
		Entity player = world.getGroupManager().getEntities("PLAYER").get(0);
		Debug debug = debugMapper.get(player);
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
			case Input.KEY_Z:
				key_attack = true;
				break;
			case Input.KEY_X:
				key_secondary = true;
				break;
			case Input.KEY_A:
				key_a = true;
				break;
			case Input.KEY_D:
				key_d = true;
				break;
			case Input.KEY_W:
				if (debug != null) {
					if (debug.isShowWalls()) {
						debug.setShowWalls(false);
					} else {
						debug.setShowWalls(true);
					}
				}
				break;
			case Input.KEY_P:
				if (debug != null) {
					if (debug.isShowPaths()) {
						debug.setShowPaths(false);
					} else {
						debug.setShowPaths(true);
					}
				}
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
			case Input.KEY_Z:
				key_attack = false;
				break;
			case Input.KEY_X:
				key_secondary = false;
				break;
			case Input.KEY_A:
				key_a = false;
				break;
			case Input.KEY_D:
				key_d = false;
				break;
		}
	}

	@Override
	public void setInput(Input input) {
	}

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void inputEnded() {
	}

	@Override
	public void inputStarted() {
	}
}
