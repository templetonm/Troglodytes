package com.turbonips.troglodytes.systems;

import java.util.Date;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.pathfinding.Path;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Attack;
import com.turbonips.troglodytes.components.ColorChange;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.EnemyAI;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Location;
import com.turbonips.troglodytes.components.Polymorph;
import com.turbonips.troglodytes.components.ParticleComponent;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.Secondary;
import com.turbonips.troglodytes.components.Stats.StatType;
import com.turbonips.troglodytes.components.Stats;
import com.turbonips.troglodytes.states.MenuState;

public class RenderSystem extends BaseEntitySystem {
	private ComponentMapper<ResourceRef> resourceMapper;
	ComponentMapper<Location> locationMapper;
	private GameContainer container;
	private StateBasedGame game;
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Direction> directionMapper;
	private ComponentMapper<Stats> statsMapper;
	private ComponentMapper<Attack> attackMapper;
	private ComponentMapper<Secondary> secondaryMapper;
	private ComponentMapper<Polymorph> polymorphMapper;
	private ComponentMapper<ColorChange> colorChangeMapper;
	private ComponentMapper<ParticleComponent> particleComponentMapper;
	private ComponentMapper<EnemyAI> enemyAIMapper;
	
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
		locationMapper = new ComponentMapper<Location>(Location.class, world);
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		directionMapper = new ComponentMapper<Direction>(Direction.class, world);
		attackMapper = new ComponentMapper<Attack>(Attack.class, world);
		statsMapper = new ComponentMapper<Stats>(Stats.class, world);
		secondaryMapper = new ComponentMapper<Secondary>(Secondary.class, world);
		polymorphMapper = new ComponentMapper<Polymorph>(Polymorph.class, world);
		colorChangeMapper = new ComponentMapper<ColorChange>(ColorChange.class, world);
		particleComponentMapper = new ComponentMapper<ParticleComponent>(ParticleComponent.class, world);
		enemyAIMapper = new ComponentMapper<EnemyAI>(EnemyAI.class, world); 
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		Graphics g = container.getGraphics();
		ResourceManager manager = ResourceManager.getInstance();
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> enemies = world.getGroupManager().getEntities("ENEMY");
		ImmutableBag<Entity> maps = world.getGroupManager().getEntities("MAP");
		ImmutableBag<Entity> trinkets = world.getGroupManager().getEntities("TRINKET");
		ImmutableBag<Entity> enemyDeaths = world.getGroupManager().getEntities("ENEMY_DEATH");
		ImmutableBag<Entity> secondary = world.getGroupManager().getEntities("SECONDARY");
		ImmutableBag<Entity> auras = world.getGroupManager().getEntities("AURA");
		ImmutableBag<Entity> mapParticles = world.getGroupManager().getEntities("PARTICLES");
		
		Entity player = players.get(0);
		Location playerLocation = locationMapper.get(player);
		Vector2f playerPosition = playerLocation.getPosition();
		String playerResName = resourceMapper.get(player).getResourceName();
		Resource playerRes = manager.getResource(playerResName);
		Image playerFrame = getFrame(playerRes);
		int mapX = (int)playerPosition.x*-1 + container.getWidth()/2 - playerFrame.getWidth()/2;
		int mapY = (int)playerPosition.y*-1 + container.getHeight()/2 - playerFrame.getHeight()/2;
		String mapResName = resourceMapper.get(maps.get(0)).getResourceName();
		Resource mapRes = manager.getResource(mapResName);
		TiledMap tiledMap = (TiledMap)mapRes.getObject();
		
		// Draw the ground
		tiledMap.render(mapX, mapY, 0);
		
		// Draw the background
		tiledMap.render(mapX, mapY, 1);
		
		// Draw enemy deaths before everything so it doesn't cover anything but the background
		for (int i=0; i<enemyDeaths.size(); i++) {
			Entity enemyDeath = enemyDeaths.get(i);
			Location enemyDeathLocation = locationMapper.get(enemyDeath);
			Vector2f enemyDeathPosition = enemyDeathLocation.getPosition();
			int enemyDeathX = mapX + (int)enemyDeathPosition.x;
			int enemyDeathY = mapY + (int)enemyDeathPosition.y;
			ParticleComponent particleComponent = particleComponentMapper.get(enemyDeath);
			particleComponent.sumTime(world.getDelta());
			
			// If we wait for finished then all the particles are gone so just wait a certain time till pause
			// The effect lasts 250 so lets stop it anywhere above 200... I think deltas are around 10-20
			if (particleComponent.getTime() >= 200) {
				particleComponent.renderParticleSystem(enemyDeathX, enemyDeathY);
			} else {
				particleComponent.updateParticleSystem(world.getDelta());
				particleComponent.renderParticleSystem(enemyDeathX, enemyDeathY);
			}
		}
		
		// Draw enemies
		for (int i=0; i<enemies.size(); i++) {
			Entity enemy = enemies.get(i);
			Location enemyLocation = locationMapper.get(enemy);
			if (enemyLocation.getMap().equals(playerLocation.getMap())) {
				Vector2f enemyPosition = enemyLocation.getPosition();
				int enemyX = mapX + (int)enemyPosition.x;
				int enemyY = mapY + (int)enemyPosition.y;
				drawCreatureEntity(enemy, enemyX, enemyY);
				if (enemyAIMapper.get(enemy) != null) {
					Path enemyPath = enemyAIMapper.get(enemy).getPath();
					if (enemyPath != null) {
						for (int a=0; a<enemyPath.getLength(); a++) {
							g.setColor(new Color(0,0,255,100));
							//g.drawRect(enemyPath.getX(a)*32, enemyPath.getY(a)*32, 32, 32);
							//g.fillRect(mapX + enemyPath.getX(a)*32, mapY + enemyPath.getY(a)*32,32,32);
						}
					}
				}
			}
		}

		int playerCenterX = container.getWidth()/2 - playerFrame.getWidth()/2;
		int playerCenterY = container.getHeight()/2 - playerFrame.getHeight()/2;
		
		// Draw aura under the player but over the enemies
		if (auras.get(0) != null) {
			ParticleComponent auraParticles = particleComponentMapper.get(auras.get(0));
			auraParticles.updateParticleSystem(world.getDelta());
			auraParticles.renderParticleSystem(playerCenterX+playerFrame.getWidth()/2, playerCenterY+playerFrame.getHeight()/2);
		}
		
		// Draw the player
		drawCreatureEntity(player, playerCenterX, playerCenterY);
		
		// Draw trinkets on map
		for (int i=0; i<trinkets.size(); i++) {
			Entity trinket = trinkets.get(i);
			Location trinketLocation = locationMapper.get(trinket);
			Vector2f trinketPosition = trinketLocation.getPosition();
			
			if (trinketLocation.getMap().equals(playerLocation.getMap())) {
				String trinketResName = resourceMapper.get(trinket).getResourceName();
				Resource trinketRes = manager.getResource(trinketResName);
				Image trinketImage = (Image)trinketRes.getObject();
				
				int trinketX = mapX + (int)trinketPosition.x;
				int trinketY = mapY + (int)trinketPosition.y;
				trinketImage.draw(trinketX, trinketY);
			}
		}
		
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
		long attackCooldown = playerStats.get(StatType.ATTACK_COOLDOWN);
		long currentAttackCooldown = new Date().getTime()-attackMapper.get(player).getLastTime();
		
		float attackBarWidth = longestBarWidth/3;
		float attackPer = (float)currentAttackCooldown / (float)attackCooldown;
		if (attackPer <= 1) {
			g.setColor(Color.black);
			g.fillRect(container.getWidth()/2 - attackBarWidth/2 - 1, container.getHeight()/2 + playerFrame.getHeight()/2 + barHeight*2 + barSpacing*2, attackBarWidth+2, barHeight);
			g.setColor(new Color(255,255,0));
			g.fillRect(container.getWidth()/2 - attackBarWidth/2, container.getHeight()/2 + playerFrame.getHeight()/2 + barHeight*2 + barSpacing*2 + 1, attackBarWidth*attackPer, barHeight-2);
		}
		
		// Draw secondary particles right before the foreground
		if (secondary.get(0) != null) {
			ParticleComponent secondaryParticles = particleComponentMapper.get(secondary.get(0));
			secondaryParticles.updateParticleSystem(world.getDelta());
			secondaryParticles.renderParticleSystem(playerCenterX+playerFrame.getWidth()/2, playerCenterY+playerFrame.getHeight()/2);
			
			if (secondaryParticles.getEmitterFinished()) {
				secondary.get(0).delete();
			}
		}
		
		// Draw the foreground
		tiledMap.render(mapX, mapY, 2);
		
		// I think we want to map particles here in case we want torches to flicker over foreground for example
		for (int i=0; i<mapParticles.size(); i++) {
			Entity particle = mapParticles.get(i);
			Location particleLoc = locationMapper.get(particle);
			
			if (particleLoc.getMap().equals(playerLocation.getMap())) {
				ParticleComponent particles = particleComponentMapper.get(particle);
				Vector2f particlePos = particleLoc.getPosition();
				int particleX = mapX + (int)particlePos.x;
				int particleY = mapY + (int)particlePos.y;
				particles.updateParticleSystem(world.getDelta());
				particles.renderParticleSystem(particleX, particleY);
			}
		}
		
		// Draw the collision layer
		tiledMap.render(mapX, mapY, 3);
		
		// If lighting is turned on
		String dark = tiledMap.getMapProperty("Dark", "false");
		if (!dark.equals("false")) {
			// Draw the player light
			int lightSize =  playerStats.get(StatType.SIGHT);
			drawLight(player, lightSize, container.getWidth()/2, container.getHeight()/2);
		}
		
		// Draw Upper Left UI
		float bigBarWidth = 200;
		float bigBarHeight = 17;
		if (health < 0) health = 0;
		float healthPer = (float)(health) / (float)maxHealth;
		if (healthPer < 0) healthPer = 0;
		Image healthIconImage = (Image) manager.getResource("healthicon").getObject();
		
		// Upper left health icon
		g.drawImage(healthIconImage, 5, 6);
		// Upper left bar
		g.setColor(new Color(50,50,50,125));
		g.fillRect(healthIconImage.getWidth()+8, 5, bigBarWidth+2, bigBarHeight+2);
		g.setColor(new Color(255,0,0,150));
		g.fillRect(healthIconImage.getWidth()+9, 6, bigBarWidth*healthPer, bigBarHeight);

		// Upper left armor icon
		Image armorIconImage = (Image) manager.getResource("armoricon").getObject();
		g.drawImage(armorIconImage, 5, healthIconImage.getHeight() + 8);
		g.setColor(Color.white);
		int healthTextWidth = g.getFont().getWidth(health + "/" + maxHealth);
		g.drawString(health + "/" + maxHealth, 7 + bigBarWidth/2 - healthTextWidth/2 + healthIconImage.getWidth(), 5);
		g.drawString(String.valueOf(armor), armorIconImage.getWidth() + 10, 
					 healthIconImage.getHeight() + 8);
		
		// Polymorph Trinket UI
		int nPolymorph = 0;
		for (int i=0; i<trinkets.size(); i++) {
			Entity trinket = trinkets.get(i);
			if (polymorphMapper.get(trinket) != null) {
				Polymorph polymorph = polymorphMapper.get(trinket);
				ResourceRef trinketRef = resourceMapper.get(trinket);
				if (polymorph.existsOnPlayer()) {
					Image polymorphImage = (Image)manager.getResource(trinketRef.getResourceName()).getObject();
					int selWidth = 32, selHeight = 32;
					int notSelWidth = 32, notSelHeight = 32;
					
					if (polymorph.isActive()) {
						int x = container.getWidth()/2-selWidth/2 + nPolymorph*selWidth;
						int y = container.getHeight()-selHeight;
						g.drawImage(polymorphImage, x, y, x+selWidth, y+selHeight, 0, 0, polymorphImage.getWidth(), polymorphImage.getHeight(), Color.white);						
					} else {
						int x = container.getWidth()/2-notSelWidth/2 + nPolymorph*selWidth;
						int y = container.getHeight()-notSelHeight;
						g.drawImage(polymorphImage, x, y, x+notSelWidth, y+notSelHeight, 0, 0, polymorphImage.getWidth(), polymorphImage.getHeight(), Color.gray);
					}
					
					nPolymorph+= 1;
				}
			}
		}
	}
	
	@Override
	protected boolean checkProcessing() {
		return true;
	}
	
	private void drawLight(Entity entity, int lightSize, int x, int y) {
		Graphics g = container.getGraphics();
		ResourceManager manager = ResourceManager.getInstance();
		
		float invSize = 1f / lightSize;
		g.clearAlphaMap();
		g.scale(lightSize, lightSize);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		Image light = (Image)manager.getResource("light").getObject();
		light.drawCentered((x) * invSize, (y) * invSize);
		g.scale(invSize, invSize);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_DST_ALPHA);
		g.setColor(new Color(0,0,0,255));
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		g.setDrawMode(Graphics.MODE_NORMAL);
	}
	
	private void drawCreatureEntity(Entity entity, int x, int y) {
		Graphics g = container.getGraphics();
		ResourceManager manager = ResourceManager.getInstance();
		Movement movement = movementMapper.get(entity);
		String resName = resourceMapper.get(entity).getResourceName();
		Resource res = manager.getResource(resName);
		Image entityFrame = getFrame(res);
		
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
				
					Color color = Color.white;
					if (colorChangeMapper.get(entity) != null) {
						ColorChange colorChange = colorChangeMapper.get(entity);
						if (new Date().getTime()-colorChange.getLastTime() > colorChange.getTime()) {
							entity.removeComponent(colorChange);
						} else {
							color = colorChange.getColor();
						}
					}
					g.drawAnimation(animation, x, y, color);
					break;
			case IMAGE:
				Image entityImg = (Image)res.getObject();
				g.drawImage(entityImg, x, y);
				break;
		}
		
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
