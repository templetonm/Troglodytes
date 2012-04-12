package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class EnemyAI extends Component
{
	private AIType aiType;
	
	public EnemyAI (String enemyAIType) {
		aiType = AIType.valueOf(enemyAIType);
	}
	
	public enum AIType {
		DUMB
	}
	
	public AIType getAIType() {
		return aiType;
	}
}
