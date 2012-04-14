package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class EnemyAI extends Component
{
	private AIType enemyAIType;
	private int sight = 0;
	
	public EnemyAI (String enemyAIType, int sight) {
		this.enemyAIType = AIType.valueOf(enemyAIType);
		this.sight = sight;
	}
	
	public enum AIType {
		DUMB,
		DUMBCHARGE,
		DUMBFIND
	}
	
	public AIType getEnemyAIType() {
		return enemyAIType;
	}
	
	public int getSight() {
		return sight;
	}
}
