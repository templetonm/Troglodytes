package com.turbonips.troglodytes.components;

import org.newdawn.slick.util.pathfinding.Path;

import com.artemis.Component;

public class EnemyAI extends Component
{
	private AIType enemyAIType;
	private int sight = 0;
	private Path path = null;
	public int pathAge = 0;
	public int pathStep = 0;
	public Corner corner = Corner.TOPLEFT;
	
	public enum Corner {
		TOPLEFT,
		TOPRIGHT,
		BOTTOMLEFT,
		BOTTOMRIGHT
	}
	
	public EnemyAI (String enemyAIType, int sight) {
		this.enemyAIType = AIType.valueOf(enemyAIType.toUpperCase());
		this.sight = sight;
	}
	
	public enum AIType {
		RANDOM,
		FOLLOW,
		ASTAR
	}
	
	public AIType getEnemyAIType() {
		return enemyAIType;
	}
	
	public int getSight() {
		return sight;
	}
	
	public void setPath(Path path) {
		this.path = path;
	}
	
	public Path getPath() {
		return path;
	}
}
