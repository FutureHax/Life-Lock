package com.t3hh4xx0r.lifelock.objects;

import java.io.Serializable;

public class Peek implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5006164022138938948L;
	long unlockedTime, lockedTime;

	public Peek(long lockTime) {
		this.lockedTime = lockTime;
	}
	
	public Peek(long lockTime, long unlockTime) {
		this.lockedTime = lockTime;
		this.unlockedTime = unlockTime;
	}

	public long getUnlockTime() {
		return unlockedTime;
	}

	public void setUnlockTime(long unlockTime) {
		this.unlockedTime = unlockTime;
	}

	public long getLockedTime() {
		return lockedTime;
	}
	
	public int getSecondsSinceLastPeek() {
		return Long.valueOf((unlockedTime - lockedTime) / 1000).intValue();
	}
}
