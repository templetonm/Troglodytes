package com.turbonips.troglodytes.components;

import java.util.HashMap;

import org.newdawn.slick.geom.Vector2f;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.turbonips.troglodytes.PolymorphTrinket;
import com.turbonips.troglodytes.TrinketData;
import com.turbonips.troglodytes.XMLSerializer;
import com.turbonips.troglodytes.TrinketData.TrinketType;
import com.turbonips.troglodytes.components.Stats.StatType;

public class TrinketDrop extends Component {
	private Entity trinket;
	
	public TrinketDrop(String trinketRef, World world) {
		XMLSerializer xmls = XMLSerializer.getInstance();
		TrinketData trinketData = (TrinketData)xmls.deserializeData("resources/trinketXMLs/" + trinketRef);
		TrinketType trinketType = trinketData.getType();
		trinket = world.createEntity();
		trinket.setGroup("TRINKET");
		trinket.addComponent(new ResourceRef(trinketData.getResourceRef()));
		trinket.addComponent(new Location(new Vector2f(0,0), "THE_VOID"));
		switch (trinketType) {
			case polymorph:
				PolymorphTrinket polymorphTrinketData = (PolymorphTrinket)trinketData;
				trinket.addComponent(new Polymorph(polymorphTrinketData.getNewResourceRef()));
				break;
		}
		HashMap<StatType, Integer> modifiers = new HashMap<StatType, Integer>();
		modifiers.put(StatType.MAX_HEALTH, trinketData.getAddHealth());
		modifiers.put(StatType.RANGE, trinketData.getAddRange());
		modifiers.put(StatType.ARMOR, trinketData.getAddArmor());
		modifiers.put(StatType.MAX_SPEED, trinketData.getAddSpeed());
		modifiers.put(StatType.DAMAGE, trinketData.getAddDamage());
		modifiers.put(StatType.SIGHT, trinketData.getAddSight());
		modifiers.put(StatType.ATTACK_COOLDOWN, trinketData.getAddAttackCooldown());
		modifiers.put(StatType.HEALTH_COOLDOWN, trinketData.getAddHealthCooldown());
		trinket.addComponent(new StatModifiers(modifiers));
	}

	public Entity getTrinket() {
		return trinket;
	}

	public void setTrinket(Entity trinket) {
		this.trinket = trinket;
	}
	
	

}
