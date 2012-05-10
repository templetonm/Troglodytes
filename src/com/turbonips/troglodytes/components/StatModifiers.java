package com.turbonips.troglodytes.components;

import java.util.HashMap;

import com.artemis.Component;
import com.turbonips.troglodytes.components.Stats.StatType;

public class StatModifiers extends Component {
	private HashMap<StatType, Integer> statModifiers = new HashMap<StatType, Integer>();
	
	public StatModifiers(HashMap<StatType, Integer> stats) {
		setModifiers(stats);
	}

	public HashMap<StatType, Integer> getModifiers() {
		return statModifiers;
	}

	public void setModifiers(HashMap<StatType, Integer> modifiers) {
		this.statModifiers = modifiers;
	}

}
