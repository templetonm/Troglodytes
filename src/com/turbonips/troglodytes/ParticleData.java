package com.turbonips.troglodytes;

/*
ParticleComponentXML
We need someway to fill in info for the update and updateParticle methods: 
EG: Initial velocity parX, parY; randXOffset, randYOffset; 
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
	
	public String getType () {return type;}
	public int getInterval () {return interval;}
	public float getSize () {return size;}
	public int getMaxParticleLifespan () {return maxParticleLifespan;}
	public int getMaxParticles () {return maxParticles;}
	public float getVelocityX () {return velocityX;}
	public float getVelocityY () {return velocityY;}
	public float getSpawnOffsetX () {return spawnOffsetX;}
	public float getSpawnOffsetY () {return spawnOffsetY;}
	
	public void setType (String newType) {type = newType;}
	public void setInterval (int newInterval) {interval = newInterval;}
	public void setSize (float newSize) {size = newSize;}
	public void setMaxParticleLifespan (int newMaxParticleLifespan) {maxParticleLifespan = newMaxParticleLifespan;}
	public void setMaxParticles (int newMaxParticles) {maxParticles = newMaxParticles;}
	public void setVelocityX (float newVelocityX) {velocityX = newVelocityX;}
	public void setVelocityY (float newVelocityY) {velocityY = newVelocityY;}
	public void setSpawnOffsetX (float newSpawnOffsetX) {spawnOffsetX = newSpawnOffsetX;}
	public void setSpawnOffsetY (float newSpawnOffsetY) {spawnOffsetY = newSpawnOffsetY;}
}