package com.turbonips.troglodytes.systems;

import java.util.HashMap;

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
import com.turbonips.troglodytes.components.Stats.StatType;
import com.turbonips.troglodytes.components.Movement;
import com.turbonips.troglodytes.components.Location;
import com.turbonips.troglodytes.components.StatModifiers;
import com.turbonips.troglodytes.components.Stats;

public class TrinketSystem extends BaseEntitySystem {
	private ComponentMapper<Location> locationMapper;
	private ComponentMapper<ResourceRef> resourceMapper;
	private ComponentMapper<Polymorph> polymorphMapper;
	private ComponentMapper<Stats> statsMapper;
	private ComponentMapper<StatModifiers> statModifiersMapper;

	@Override
	protected void initialize() {
		locationMapper = new ComponentMapper<Location>(Location.class, world);
		resourceMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
		polymorphMapper = new ComponentMapper<Polymorph>(Polymorph.class, world);
		statsMapper = new ComponentMapper<Stats>(Stats.class, world);
		statModifiersMapper = new ComponentMapper<StatModifiers>(StatModifiers.class, world);
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
		
		for (int i=0; i<trinkets.size(); i++) {
			Entity trinket = trinkets.get(i);
			Location trinketLocation = locationMapper.get(trinket);
			Vector2f trinketPosition = trinketLocation.getPosition();
			trinketPosition = new Vector2f(trinketPosition.x+16, trinketPosition.y+16);
			
			if (trinketLocation.getMap().equals(playerLocation.getMap())) {
				if (trinketPosition.distance(playerPosition) <= 32) {
					// You picked up a polymorph trinket
					if (polymorphMapper.get(trinket) != null) {
						
						// Delete the trinket if you've already pick up the same trinket
						for (int a=0; a<trinkets.size(); a++) {
							if (polymorphMapper.get(trinkets.get(a)) != null && a != i) {
								if (polymorphMapper.get(trinkets.get(a)).existsOnPlayer()) {
									if (resourceMapper.get(trinkets.get(a)).getResourceName() != null) {
										logger.info(polymorphMapper.get(trinkets.get(a)).getTrinketRef() + " " + polymorphMapper.get(trinket).getTrinketRef());
										if (resourceMapper.get(trinkets.get(a)).getResourceName().equals(
											resourceMapper.get(trinket).getResourceName())) {
											trinket.delete();
											return;
										}
									}
								}
							}
						}
						
						// Remove previous polymorph modifiers and set it to inactive
						for (int a=0; a<trinkets.size(); a++) {
							if (polymorphMapper.get(trinkets.get(a)) != null && a != i) {
								if (polymorphMapper.get(trinkets.get(a)).isActive()) {
									StatModifiers statModifiers = statModifiersMapper.get(trinkets.get(a));
									HashMap<StatType, Integer> modifiers;
									if (statModifiers != null) {
										modifiers = statModifiers.getModifiers();
										statsMapper.get(player).removeModifiers(modifiers);
									}
									polymorphMapper.get(trinkets.get(a)).setActive(false);
								}
							}
						}
						Polymorph polymorph = polymorphMapper.get(trinket);
						polymorph.setActive(true);
						polymorph.setExistsOnPlayer(true);
						// Don't make a map named THE_VOID
						trinketLocation.setMap("THE_VOID");
					} else {
						trinket.delete();
					}
					// Add new trinket modifiers
					StatModifiers statModifiers = statModifiersMapper.get(trinket);
					HashMap<StatType, Integer> modifiers = statModifiers.getModifiers();
					statsMapper.get(player).applyModifiers(modifiers);
				}
			}
			
			// Find the active polymorph trinket and apply it to you
			if (polymorphMapper.get(trinket) != null) {
				Polymorph polymorph = polymorphMapper.get(trinket);
				if (polymorph.isActive() && polymorph.existsOnPlayer()) {
					playerRes.setResourceName(polymorph.getPolymorphRef());
				}
			}
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