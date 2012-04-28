package com.turbonips.troglodytes;

public class Trinkets {
	private Trinkets instance = new Trinkets();
	
	private Trinkets() {
	}
	
	public Trinkets getInstance() {
		return instance;
	}
	
}
