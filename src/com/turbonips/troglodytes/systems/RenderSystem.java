package com.turbonips.troglodytes.systems;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Attack;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Position;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.Direction.Dir;
import com.turbonips.troglodytes.components.Secondary;
import com.turbonips.troglodytes.components.Stats.StatType;
import com.turbonips.troglodytes.components.Stats;
import com.turbonips.troglodytes.states.MenuState;

public class RenderSystem extends BaseEntitySystem {
	private ComponentMapper<ResourceRef> resourceMapper;
	ComponentMapper<Position> positionMapper;
	private GameContainer container;
	private StateBasedGame game;
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Direction> directionMapper;
	private ComponentMapper<Stats> statsMapper;
	private ComponentMapper<Attack> attackMapper;
	private ComponentMapper<Secondary> secondaryMapper;
	
	public RenderSystem(GameContainer container, StateBasedGame game) {
		this.container = container;
		this.game = game;
	}

	@Override
	protected void initialize() {
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
		positionMapper = new ComponentMapper<Position>(Position.class, world);
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		directionMapper = new ComponentMapper<Direction>(Direction.class, world);
		attackMapper = new ComponentMapper<Attack>(Attack.class, world);
		statsMapper = new ComponentMapper<Stats>(Stats.class, world);
		secondaryMapper = new ComponentMapper<Secondary>(Secondary.class, world);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		Graphics g = container.getGraphics();
		ResourceManager manager = ResourceManager.getInstance();
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> grounds = world.getGroupManager().getEntities("GROUND");
		ImmutableBag<Entity> backgrounds = world.getGroupManager().getEntities("BACKGROUND");
		ImmutableBag<Entity> foregrounds = world.getGroupManager().getEntities("FOREGROUND");
		Entity player = players.get(0);
		Position playerPos = positionMapper.get(player);
		Vector2f playerPosition = playerPos.getPosition();
		String playerResName = resourceMapper.get(player).getResourceName();
		Resource playerRes = manager.getResource(playerResName);
		Image playerFrame = getFrame(playerRes);
		int mapX = (int)playerPosition.x*-1 + container.getWidth()/2 - playerFrame.getWidth()/2;
		int mapY = (int)playerPosition.y*-1 + container.getHeight()/2 - playerFrame.getHeight()/2;
		
		// Draw the ground
		for (int i=0; i<grounds.size(); i++) {
			Entity ground = grounds.get(i);
			String mapResName = resourceMapper.get(ground).getResourceName();
			Resource mapRes = manager.getResource(mapResName);
			TiledMap map = (TiledMap)mapRes.getObject();
			map.render(mapX, mapY, 0);
		}
		
		// Draw the background
		for (int i=0; i<backgrounds.size(); i++) {
			Entity background = backgrounds.get(i);
			String mapResName = resourceMapper.get(background).getResourceName();
			Resource mapRes = manager.getResource(mapResName);
			TiledMap map = (TiledMap)mapRes.getObject();
			map.render(mapX, mapY, 1);
		}
		
		// Draw the player
		int playerCenterX = container.getWidth()/2 - playerFrame.getWidth()/2;
		int playerCenterY = container.getHeight()/2 - playerFrame.getHeight()/2;
		drawCreatureEntity(player, playerCenterX, playerCenterY);
		
		// Player Health Bar
		Stats stats = statsMapper.get(player);
		HashMap<StatType, Integer> playerStats = stats.getStats();
		int health = playerStats.get(StatType.HEALTH);
		
		if (health <= 0) {
			game.enterState(MenuState.ID);
		}

		
		int maxHealth = playerStats.get(StatType.MAX_HEALTH);
		int armor = playerStats.get(StatType.ARMOR);
		
		// Secondary cooldown time bar
		long secondaryCooldown = secondaryMapper.get(player).getCooldown();
		long currentSecondaryCooldown = new Date().getTime()-secondaryMapper.get(player).getLastSecondaryTime();
		
		float secondaryBarWidth = 32 * 1.0f;
		float secondaryPer = (float)currentSecondaryCooldown / (float)secondaryCooldown;
		if (secondaryPer <= 1) {
			g.setColor(Color.black);
			g.fillRect(container.getWidth()/2 - secondaryBarWidth/2 - 1, container.getHeight()/2 + 31 + 12, secondaryBarWidth+2, 5);
			
			g.setColor(new Color(255,127,0));
			g.fillRect(container.getWidth()/2 - secondaryBarWidth/2, container.getHeight()/2 + 32 + 12, secondaryBarWidth*secondaryPer, 3);
		}
		
		// Attack cooldown time bar
		long attackCooldown = attackMapper.get(player).getCooldown();
		long currentAttackCooldown = new Date().getTime()-attackMapper.get(player).getLastAttackTime();
		
		float attackBarWidth = 32 * .5f;
		float attackPer = (float)currentAttackCooldown / (float)attackCooldown;
		if (attackPer <= 1) {
			g.setColor(Color.black);
			g.fillRect(container.getWidth()/2 - attackBarWidth/2 - 1, container.getHeight()/2 + 31 + 19, attackBarWidth+2, 5);
			g.setColor(new Color(255,255,0));
			g.fillRect(container.getWidth()/2 - attackBarWidth/2, container.getHeight()/2 + 32 + 19, attackBarWidth*attackPer, 3);
		}
		

		
		// Draw enemies
		for (int i=0; i<enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			Position enemyPos = positionMapper.get(enemy);
			Vector2f enemyPosition = enemyPos.getPosition();
			int enemyX = mapX + (int)enemyPosition.x;
			int enemyY = mapY + (int)enemyPosition.y;
			drawCreatureEntity(enemy, enemyX, enemyY);
		}
		
		// Draw the foreground
		for (int i=0; i<foregrounds.size(); i++) {
			Entity foreground = foregrounds.get(i);
			String mapResName = resourceMapper.get(foreground).getResourceName();
			Resource mapRes = manager.getResource(mapResName);
			TiledMap map = (TiledMap)mapRes.getObject();
			map.render(mapX, mapY, 2);
			//map.render(mapX, mapY, 3);
		}
		
		// Draw UI
		Image healthIconImage = (Image) manager.getResource("healthicon").getObject();
		g.drawImage(healthIconImage, 3, 3);
		
		Image armorIconImage = (Image) manager.getResource("armoricon").getObject();
		g.drawImage(armorIconImage, 3, healthIconImage.getHeight() + 3);
		
		g.setColor(Color.white);
		g.drawString(health + "/" + maxHealth, healthIconImage.getWidth() + 5, 3);
		g.drawString(String.valueOf(armor), armorIconImage.getWidth() + 5, healthIconImage.getHeight() + 3);
		
		/*
		 * 
		 */
	}
	
	@Override
	protected boolean checkProcessing() {
		return true;
	}
	
	private void drawCreatureEntity(Entity entity, int x, int y) {
		Graphics g = container.getGraphics();
		ResourceManager manager = ResourceManager.getInstance();
		Movement movement = movementMapper.get(entity);
		String resName = resourceMapper.get(entity).getResourceName();
		Resource res = manager.getResource(resName);
		
		switch (res.getType()) {
			case CREATURE_ANIMATION:
				CreatureAnimation entityAnim = (CreatureAnimation)res.getObject();
				Direction direction = directionMapper.get(entity);
				float speed = movement.getCurrentSpeed()/10;
				entityAnim.setIdle();
				Animation animation = null;
				
				switch (direction.getDirection()) {
					case UP:
					case UP_LEFT:
					case UP_RIGHT:
						if (movement.getCurrentSpeed() != 0) {
							animation = entityAnim.getMoveUp();
						} else {
							animation = entityAnim.getIdleUp();
						}
						break;
					case DOWN:
					case DOWN_RIGHT:
					case DOWN_LEFT:
						if (movement.getCurrentSpeed() != 0) {
							animation = entityAnim.getMoveDown();
						} else {
							animation = entityAnim.getIdleDown();
						}
						break;
					case LEFT:
						if (movement.getCurrentSpeed() != 0) {
							animation = entityAnim.getMoveLeft();
						} else {
							animation = entityAnim.getIdleLeft();
						}
						break;
					case RIGHT:
						if (movement.getCurrentSpeed() != 0) {
							animation = entityAnim.getMoveRight();
						} else {
							animation = entityAnim.getIdleRight();
						}
						break;
					}
					g.drawAnimation(animation, x, y);
					break;
			case IMAGE:
				Image entityImg = (Image)res.getObject();
				g.drawImage(entityImg, x, y);
				break;
		}
		
		Image entityFrame = getFrame(res);
		HashMap<StatType, Integer> stats = statsMapper.get(entity).getStats();
		int maxHealth = stats.get(StatType.MAX_HEALTH);
		int health = stats.get(StatType.HEALTH);
		float barWidth = 32 * 1.5f;
		float per = (float)health / (float)maxHealth;
		
		if (health > 0 && health != maxHealth) {
			g.setColor(new Color(0,0,0));
			g.fillRect(x + entityFrame.getWidth()/2 - barWidth/2 - 1, y + entityFrame.getHeight()+4, barWidth+2, 5);
			g.setColor(new Color(255,0,0));
			g.fillRect(x + entityFrame.getWidth()/2 - barWidth/2, y + entityFrame.getHeight()+5, barWidth*per, 3);
		}
	}
	
	private Image getFrame(Resource resource) {
		switch (resource.getType()) {
			case CREATURE_ANIMATION:
				return ((CreatureAnimation)resource.getObject()).getCurrent().getCurrentFrame();
			case IMAGE:
				return (Image)resource.getObject();
			default:
				return null;
		}
	}
		
}
