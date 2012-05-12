package com.turbonips.troglodytes.components;

import java.util.ArrayList;

import com.artemis.Component;

public class VisitedMaps extends Component {
	
	private ArrayList<String> maps = new ArrayList<String>();

	public ArrayList<String> getMaps() {
		return maps;
	}

	public void setMaps(ArrayList<String> maps) {
		this.maps = maps;
	}

}
