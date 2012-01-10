package com.turbonips.troglodytes.entities;

import org.newdawn.slick.Image;

// This is a support class for Enemy.
public class EnemyData
{
	// Just using default constructor; this class is just a bucket for XML serialization of enemy entities.
	
	// Enemy data fields, Getters, and Setters; required for XML serialization.
	private String name;
	private int size;
	private boolean elite;
	private int glow;
	private int group;
	private int health;
	private int damage;
	private int aspd;
	private int range;
	private int sight;
	private int mspd;
	private boolean flier;
	private int loot;
	private int x;
	private int y;
	private Image image;

	public String getName () {return name;}
	public int getSize() {return size;}
	public boolean getElite() {return elite;}
	public int getGlow() {return glow;}
	public int getGroup() {return group;}
	public int getHealth() {return health;}
	public int getDamage() {return damage;}
	public int getAspd() {return aspd;}
	public int getRange() {return range;}
	public int getSight() {return sight;}
	public int getMspd() {return mspd;}
	public boolean getFlier() {return flier;}
	public int getLoot() {return loot;}
	public Image getImage() {return image;}
	public int getX() {return x;}
	public int getY() {return y;}
	
	public void setName (String newName) { name = newName; }
	public void setSize (int newSize) { size = newSize; }
	public void setElite (boolean newElite) { elite = newElite; }
	public void setGlow (int newGlow) { glow = newGlow; }
	public void setGroup (int newGroup) { group = newGroup; }
	public void setHealth (int newHealth) { health = newHealth; }
	public void setDamage (int newDamage) { damage = newDamage; }
	public void setAspd (int newAspd) { aspd = newAspd; }
	public void setRange (int newRange) { range = newRange; }
	public void setSight (int newSight) { sight = newSight; }
	public void setMspd (int newMspd) { mspd = newMspd; }
	public void setFlier (boolean newFlier) { flier = newFlier; }
	public void setLoot (int newLoot) { loot = newLoot; }
	public void setImage(Image image) { this.image = image; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
}
