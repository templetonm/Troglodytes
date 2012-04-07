package com.turbonips.troglodytes.components;


import java.util.HashMap;

import com.artemis.Component;

public class Stats extends Component {
	public enum StatType { 
		HEALTH,
		MAX_HEALTH,
		ARMOR
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
}
