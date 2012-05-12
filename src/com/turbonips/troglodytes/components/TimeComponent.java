package com.turbonips.troglodytes.components;

import com.artemis.Component;

public abstract class TimeComponent extends Component {
	private int time;
	private long lastTime;

	public TimeComponent(int time) {
		this.time = time;
		this.lastTime = 0;
	}
	
	public TimeComponent() {
		this.time = 0;
		this.lastTime = 0;
	}
	
	public int getTime() {
		return time;
	}
	
	public long getLastTime() {
		return lastTime;
	}
	
	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public void setTime(int time) {
		this.time = time;
	}

}
