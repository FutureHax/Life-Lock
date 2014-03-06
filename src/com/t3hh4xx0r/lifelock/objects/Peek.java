package com.t3hh4xx0r.lifelock.objects;

public class Peek {
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
