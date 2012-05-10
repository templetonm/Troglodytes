package com.turbonips.troglodytes.components;


import java.util.HashMap;

import com.artemis.Component;

public class Stats extends Component {
	public enum StatType { 
		HEALTH,
		MAX_HEALTH,
		ARMOR, 
		RANGE,
		MAX_SPEED,
		ACCELERATION,
		DECELERATION
	}
	private HashMap<StatType, Integer> stats = new HashMap<StatType, Integer>();
	
	public Stats(HashMap<StatType, Integer> stats) {
		setStats(stats);
	}

	public HashMap<StatType, Integer> getStats() {
		return stats;
	}

	public void setStats(HashMap<StatType, Integer> stats) {
		this.stats = stats;
	}
	
	public void applyModifiers(HashMap<StatType, Integer> modifiers) {
		for (StatType stat : stats.keySet()) {
			if (modifiers.containsKey(stat)) {
				stats.put(stat, stats.get(stat) + modifiers.get(stat));
			}
		}
	}
	
	public void removeModifiers(HashMap<StatType, Integer> modifiers) {
		for (StatType stat : stats.keySet()) {
			if (modifiers.containsKey(stat)) {
				stats.put(stat, stats.get(stat) - modifiers.get(stat));
				switch (stat) {
					case MAX_HEALTH:
						if (stats.get(StatType.HEALTH) > stats.get(StatType.MAX_HEALTH))
							stats.put(StatType.HEALTH, stats.get(StatType.MAX_HEALTH));
						break;
				}
			}
		}
	}
}
