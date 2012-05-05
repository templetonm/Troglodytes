package com.turbonips.troglodytes.systems;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.turbonips.troglodytes.CollisionMap;
import com.turbonips.troglodytes.CreatureAnimation;
import com.turbonips.troglodytes.PolymorphTrinket;
import com.turbonips.troglodytes.Resource;
import com.turbonips.troglodytes.ResourceManager;
import com.turbonips.troglodytes.components.Direction;
import com.turbonips.troglodytes.components.EnemyAI;
import com.turbonips.troglodytes.components.Polymorph;
import com.turbonips.troglodytes.components.ResourceRef;
import com.turbonips.troglodytes.components.Direction.Dir;
import com.turbonips.troglodytes.components.EnemyAI.AIType;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Location;

public class TrinketSystem extends BaseEntitySystem {
	private ComponentMapper<Movement> movementMapper;
	private ComponentMapper<Direction> directionMapper;
	private ComponentMapper<EnemyAI> enemyAIMapper;
	private ComponentMapper<Location> locationMapper;
	private ComponentMapper<ResourceRef> resourceMapper;
	private ComponentMapper<Polymorph> polymorphMapper;

	@Override
	protected void initialize() {
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		locationMapper = new ComponentMapper<Location>(Location.class, world);
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
		polymorphMapper = new ComponentMapper<Polymorph>(Polymorph.class, world);
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> e) {
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> trinkets = world.getGroupManager().getEntities("TRINKET");
		ResourceManager manager = ResourceManager.getInstance();
		Entity player = players.get(0);
		Location playerLocation = locationMapper.get(player);
		Vector2f playerPosition = playerLocation.getPosition();
		ResourceRef playerRes = resourceMapper.get(player);
		Image playerFrame = getFrame(manager.getResource(playerRes.getResourceName()));
		int pw = playerFrame.getWidth();
		int ph = playerFrame.getHeight();
		playerPosition = new Vector2f(playerPosition.getX()+pw/2, playerPosition.getY()+ph/2);
		boolean activePolymorphTrinket = false;
		
		for (int i=0; i<trinkets.size(); i++) {
			Entity trinket = trinkets.get(i);
			Location trinketLocation = locationMapper.get(trinket);
			Vector2f trinketPosition = trinketLocation.getPosition();
			trinketPosition = new Vector2f(trinketPosition.x+16, trinketPosition.y+16);
			
			if (trinketLocation.getMap().equals(playerLocation.getMap())) {
				if (trinketPosition.distance(playerPosition) <= 32) {
					// You picked up a polymorph trinket
					if (polymorphMapper.get(trinket) != null) {
						for (int a=0; a<trinkets.size(); a++) {
							if (a != i) {
								polymorphMapper.get(trinkets.get(a)).setActive(false);
							}
						}
						Polymorph polymorph = polymorphMapper.get(trinket);
						polymorph.setActive(true);
						polymorph.setExistsOnPlayer(true);
						// Don't make a map named THE_VOID lol
						trinketLocation.setMap("THE_VOID");
					} else {
						trinket.delete();
					}
				}
			}
			
			// Find the active polymorph trinket and apply it to you
			if (polymorphMapper.get(trinket) != null) {
				Polymorph polymorph = polymorphMapper.get(trinket);
				if (polymorph.isActive() && polymorph.existsOnPlayer()) {
					playerRes.setResourceName(polymorph.getPolymorphRef());
					activePolymorphTrinket = true;
				}
			}
		}
		
		// TODO: This shouldn't always be playeranimation
		// If there is no active polymorph trinket, set the player resource to playeranimation
		if (activePolymorphTrinket == false) {
			playerRes.setResourceName("playeranimation");
		}
		

	}

	private Image getFrame(Resource resource) {
		switch (resource.getType()) {
			case CREATURE_ANIMATION:
				return ((CreatureAnimation) resource.getObject()).getCurrent().getCurrentFrame();
			case IMAGE:
				return (Image) resource.getObject();
			default:
				return null;
		}
	}

}