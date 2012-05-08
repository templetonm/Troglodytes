package com.turbonips.troglodytes;

/*
ParticleComponentXML
Right now this only works for linear particle systems. I think that is good enough for now, but it is not ideal.
*/

public class ParticleData
{
	private String type;
	private int interval;
	private float size;
	private int maxParticleLifespan;
	private int maxParticles;
	private float velocityX;
	private float velocityY;
	private float spawnOffsetX;
	private float spawnOffsetY;
	private boolean spawnOffsetRandom;
	private float spawnOffsetPercentNegative;
	private boolean velocityXRandom;
	private boolean velocityYRandom;
	private float velocityPercentNegative;
	private float colorR;
	private float colorG;
	private float colorB;
	private float colorA;
	private float colorChangeR;
	private float colorChangeG;
	private float colorChangeB;
	private float colorChangeA;
	private float velocitySpeed;
	private boolean intervalRandom;
	private String resourceRef;
	
	public String getType () {return type;}
	public int getInterval () {return interval;}
	public float getSize () {return size;}
	public int getMaxParticleLifespan () {return maxParticleLifespan;}
	public int getMaxParticles () {return maxParticles;}
	public float getVelocityX () {return velocityX;}
	public float getVelocityY () {return velocityY;}
	public float getSpawnOffsetX () {return spawnOffsetX;}
	public float getSpawnOffsetY () {return spawnOffsetY;}
	public boolean getSpawnOffsetRandom () {return spawnOffsetRandom;}
	public float getSpawnOffsetPercentNegative () {return spawnOffsetPercentNegative;}
	public boolean getVelocityXRandom () {return velocityXRandom;}
	public boolean getVelocityYRandom () {return velocityYRandom;}
	public float getVelocityPercentNegative () {return velocityPercentNegative;}
	public float getColorR () {return colorR;}
	public float getColorG () {return colorG;}
	public float getColorB () {return colorB;}
	public float getColorA () {return colorA;}
	public float getColorChangeR () {return colorChangeR;}
	public float getColorChangeG () {return colorChangeG;}
	public float getColorChangeB () {return colorChangeB;}
	public float getColorChangeA () {return colorChangeA;}
	public float getVelocitySpeed () {return velocitySpeed;}
	public boolean getIntervalRandom () {return intervalRandom;}
	public String getResourceRef() {return resourceRef;}
	
	public void setType (String newType) {type = newType;}
	public void setInterval (int newInterval) {interval = newInterval;}
	public void setSize (float newSize) {size = newSize;}
	public void setMaxParticleLifespan (int newMaxParticleLifespan) {maxParticleLifespan = newMaxParticleLifespan;}
	public void setMaxParticles (int newMaxParticles) {maxParticles = newMaxParticles;}
	public void setVelocityX (float newVelocityX) {velocityX = newVelocityX;}
	public void setVelocityY (float newVelocityY) {velocityY = newVelocityY;}
	public void setSpawnOffsetX (float newSpawnOffsetX) {spawnOffsetX = newSpawnOffsetX;}
	public void setSpawnOffsetY (float newSpawnOffsetY) {spawnOffsetY = newSpawnOffsetY;}
	public void setSpawnOffsetRandom (boolean newSpawnOffsetRandom) {spawnOffsetRandom = newSpawnOffsetRandom;}
	public void setSpawnOffsetPercentNegative (float newSpawnOffsetPercentNegative) {spawnOffsetPercentNegative = newSpawnOffsetPercentNegative;}
	public void setVelocityXRandom(boolean newVelocityXRandom) {velocityXRandom = newVelocityXRandom;}
	public void setVelocityYRandom(boolean newVelocityYRandom) {velocityYRandom = newVelocityYRandom;}
	public void setVelocityPercentNegative (float newVelocityPercentNegative) {velocityPercentNegative = newVelocityPercentNegative;}
	public void setColorR (float newColorR) {colorR = newColorR;}
	public void setColorG (float newColorG) {colorG = newColorG;}
	public void setColorB (float newColorB) {colorB = newColorB;}
	public void setColorA (float newColorA) {colorA = newColorA;}
	public void setColorChangeR (float newColorChangeR) {colorChangeR = newColorChangeR;}
	public void setColorChangeG (float newColorChangeG) {colorChangeG = newColorChangeG;}
	public void setColorChangeB (float newColorChangeB) {colorChangeB = newColorChangeB;}
	public void setColorChangeA (float newColorChangeA) {colorChangeA = newColorChangeA;}
	public void setVelocitySpeed (float newVelocitySpeed) {velocitySpeed = newVelocitySpeed;}
	public void setIntervalRandom (boolean newIntervalRandom) {intervalRandom = newIntervalRandom;}
	public void setResourceRef (String newResourceRef) {resourceRef = newResourceRef;}
}