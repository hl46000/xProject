package com.purehero.common.io;

import java.util.LinkedList;
import java.util.List;

public class CircularList <E>  {
	private final int n; // buffer length
	private final List<E> buf; // a List implementing RandomAccess
	
	public CircularList(int capacity) {
		n = capacity + 1;
		buf = new LinkedList<E>();
	}
	
	public void add(E val) {
		if( buf.add(val)) {
			if( buf.size() >= n ) {
				buf.remove(0);
			}
		}
	}
	
	public List<E> getItems() {
		return buf;
	}
	
	public int size() {
		return buf.size();
	}
	
	public void clear() {
		buf.clear();
	}
}
