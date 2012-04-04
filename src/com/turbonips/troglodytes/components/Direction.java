package com.turbonips.troglodytes.components;

import java.util.ArrayList;

import com.artemis.Component;

public class Direction extends Component {
	public enum Dir { 
		UP, 
		DOWN,
		LEFT, 
		RIGHT
	}
	private ArrayList<Dir> directions = new ArrayList<Dir>();
	
	public Direction (Dir ... dirs) {
		for (Dir dir : dirs) {
			directions.add(dir);
		}
	}
	
	public ArrayList<Dir> getDirections() {
		return directions;
	}
}
