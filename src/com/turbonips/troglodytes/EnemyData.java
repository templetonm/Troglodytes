package com.turbonips.troglodytes;

// This is a support class for Enemy.
public class EnemyData {
	// Just using default constructor; this class is just a bucket for XML serialization of enemy entities.
	
	// Enemy data fields, Getters, and Setters; required for XML serialization.
	private String name;
	private boolean elite;
	private int glow;
	private int group;
	private int health;
	private int damage;
	private int cooldown;
	private int range;
	private int sight;
	private boolean flier;
	private int loot;
	private int x;
	private int y;
	private int maxSpeed;
	private String resourceRef;
	private float acceleration;
	private float deceleration;
	private String AIType;

	public String getName () {return name;}
	public boolean getElite() {return elite;}
	public int getGlow() {return glow;}
	public int getGroup() {return group;}
	public int getHealth() {return health;}
	public int getDamage() {return damage;}
	public int getCooldown() {return cooldown;}
	public int getRange() {return range;}
	public int getSight() {return sight;}
	public boolean getFlier() {return flier;}
	public int getLoot() {return loot;}
	public int getX() {return x;}
	public int getY() {return y;}
	public int getMaxSpeed() {return maxSpeed;}
	public String getResourceRef() { return resourceRef; }
	public float getAcceleration() { return acceleration; }
	public float getDeceleration() { return deceleration; }
	public String getAIType() { return AIType; }
	
	public void setName (String newName) { name = newName; }
	public void setElite (boolean newElite) { elite = newElite; }
	public void setGlow (int newGlow) { glow = newGlow; }
	public void setGroup (int newGroup) { group = newGroup; }
	public void setHealth (int newHealth) { health = newHealth; }
	public void setDamage (int newDamage) { damage = newDamage; }
	public void setCooldown (int newCooldown) { cooldown = newCooldown; }
	public void setRange (int newRange) { range = newRange; }
	public void setSight (int newSight) { sight = newSight; }
	public void setFlier (boolean newFlier) { flier = newFlier; }
	public void setLoot (int newLoot) { loot = newLoot; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public void setMaxSpeed(int maxSpeed) { this.maxSpeed = maxSpeed; }
	public void setResourceRef(String resourceRef) { this.resourceRef = resourceRef; }
	public void setAcceleration(float acceleration) { this.acceleration = acceleration; }
	public void setDeceleration(float deceleration) { this.deceleration = deceleration; }
	public void setAIType(String AIType) { this.AIType = AIType; }
}
