package com.github.severinghams;

import java.util.HashSet;

public class IgnoreRequestCache {

	private HashSet<Long> users = new HashSet<Long>();
	
	public void ignoreUser(long l) {
		if (!users.contains(l)) {
			users.add(l);
		}
	}
	
	public void unIgnoreUser(long l) {
		if (users.contains(l)) {
			users.remove(l);
		}
	}
	
	public boolean doIgnore(long l) {
		return users.contains(l);
	}
}
