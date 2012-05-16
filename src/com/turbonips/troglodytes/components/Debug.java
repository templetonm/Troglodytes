package com.turbonips.troglodytes.components;

import com.artemis.Component;

public class Debug extends Component {
	boolean showWalls = false,
			showPaths = false;
	public Debug() {
	}
	public boolean isShowWalls() {
		return showWalls;
	}
	public void setShowWalls(boolean showWalls) {
		this.showWalls = showWalls;
	}
	public boolean isShowPaths() {
		return showPaths;
	}
	public void setShowPaths(boolean showPaths) {
		this.showPaths = showPaths;
	}

}
