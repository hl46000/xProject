package com.purehero.common.io;

import java.util.LinkedList;
import java.util.List;

/**
 * Ring list
 * <br> 링리스트 입력된 capacity 개수 만큼 항목이 유지되며, capacity 개수를 초과하면 가장 오래된 항목이 삭제되어 capacity 개수를 유지 한다. 
 * 
 * @author purehero
 *
 * @param <E>
 */
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
