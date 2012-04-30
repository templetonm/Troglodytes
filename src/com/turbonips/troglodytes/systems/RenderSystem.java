package com.turbonips.troglodytes.systems;

import java.util.Date;
import java.util.HashMap;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
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
	
	// Bars below the player/enemy
	int barSpacing = 1;
	int barHeight = 6;
	float longestBarWidth = 32 * 1.5f;
	
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
		ImmutableBag<Entity> trinkets = world.getGroupManager().getEntities("TRINKET");
		
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
		
		// Draw trinkets on map
		for (int i=0; i<trinkets.size(); i++) {
			Entity trinket = trinkets.get(i);
			Position trinketPos = positionMapper.get(trinket);
			Vector2f trinketPosition = trinketPos.getPosition();
			
			if (trinketPos.getMap().equals(playerPos.getMap())) {
				String trinketResName = resourceMapper.get(trinket).getResourceName();
				Resource trinketRes = manager.getResource(trinketResName);
				Image trinketImage = (Image)trinketRes.getObject();
				
				int trinketX = mapX + (int)trinketPosition.x;
				int trinketY = mapY + (int)trinketPosition.y;
				trinketImage.draw(trinketX, trinketY);
			}
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
		long secondaryCooldown = secondaryMapper.get(player).getTime();
		long currentSecondaryCooldown = new Date().getTime()-secondaryMapper.get(player).getLastTime();
		
		float secondaryBarWidth = longestBarWidth*(2.0f/3.0f);
		float secondaryPer = (float)currentSecondaryCooldown / (float)secondaryCooldown;
		if (secondaryPer <= 1) {
			g.setColor(Color.black);
			g.fillRect(container.getWidth()/2 - secondaryBarWidth/2 - 1, container.getHeight()/2 + playerFrame.getHeight()/2 + barHeight + barSpacing, secondaryBarWidth+2, barHeight);
			g.setColor(new Color(255,127,0));
			g.fillRect(container.getWidth()/2 - secondaryBarWidth/2, container.getHeight()/2 + playerFrame.getHeight()/2 + barHeight + barSpacing + 1, secondaryBarWidth*secondaryPer, barHeight-2);
		}
		
		// Attack cooldown time bar
		long attackCooldown = attackMapper.get(player).getTime();
		long currentAttackCooldown = new Date().getTime()-attackMapper.get(player).getLastTime();
		
		float attackBarWidth = longestBarWidth/3;
		float attackPer = (float)currentAttackCooldown / (float)attackCooldown;
		if (attackPer <= 1) {
			g.setColor(Color.black);
			g.fillRect(container.getWidth()/2 - attackBarWidth/2 - 1, container.getHeight()/2 + playerFrame.getHeight()/2 + barHeight*2 + barSpacing*2, attackBarWidth+2, barHeight);
			g.setColor(new Color(255,255,0));
			g.fillRect(container.getWidth()/2 - attackBarWidth/2, container.getHeight()/2 + playerFrame.getHeight()/2 + barHeight*2 + barSpacing*2 + 1, attackBarWidth*attackPer, barHeight-2);
		}

		// Draw enemies
		for (int i=0; i<enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			Position enemyPos = positionMapper.get(enemy);
			if (enemyPos.getMap().equals(playerPos.getMap())) {
				Vector2f enemyPosition = enemyPos.getPosition();
				int enemyX = mapX + (int)enemyPosition.x;
				int enemyY = mapY + (int)enemyPosition.y;
				drawCreatureEntity(enemy, enemyX, enemyY);
			}
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
		

		
		// Draw Upper Left UI
		float bigBarWidth = 200;
		float bigBarHeight = 17;
		if (health < 0) health = 0;
		float healthPer = (float)(health) / (float)maxHealth;
		if (healthPer < 0) healthPer = 0;
		//float armorPer = (float)(armor) / (float)maxHealth;
		Image healthIconImage = (Image) manager.getResource("healthicon").getObject();
		
		// Upper left health icon
		
		g.drawImage(healthIconImage, 5, 6);
		// Upper left bar
		g.setColor(new Color(50,50,50,125));
		g.fillRect(healthIconImage.getWidth()+8, 5, bigBarWidth+2, bigBarHeight+2);
		g.setColor(new Color(255,0,0,150));
		g.fillRect(healthIconImage.getWidth()+9, 6, bigBarWidth*healthPer, bigBarHeight);
		/*g.setColor(new Color(0,0,255,100));
		g.fillRect(bigBarWidth*healthPer+6, 4, bigBarWidth*armorPer, bigBarHeight);*/
		
		
		//Image armorIconImage = (Image) manager.getResource("armoricon").getObject();
		//g.drawImage(armorIconImage, 3, healthIconImage.getHeight() + 3);
		
		g.setColor(Color.white);
		int healthTextWidth = g.getFont().getWidth(health + "/" + maxHealth);
		g.drawString(health + "/" + maxHealth, 7 + bigBarWidth/2 - healthTextWidth/2 + healthIconImage.getWidth(), 5);
		//g.drawString(String.valueOf(armor), armorIconImage.getWidth() + 5, healthIconImage.getHeight() + 3);
		
		
		
		//if (health > 0 && health != maxHealth) {

		//}
		
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
		float barWidth = longestBarWidth;
		float per = (float)health / (float)maxHealth;
		
		if (health > 0 && health != maxHealth) {
			g.setColor(new Color(0,0,0));
			g.fillRect(x + entityFrame.getWidth()/2 - barWidth/2 - 1, y + entityFrame.getHeight(), barWidth+2, barHeight);
			g.setColor(new Color(255,0,0));
			g.fillRect(x + entityFrame.getWidth()/2 - barWidth/2, y + entityFrame.getHeight()+1, barWidth*per, barHeight-2);
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
