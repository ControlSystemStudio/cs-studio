package org.csstudio.utility.toolbox.framework.editor;

import com.google.inject.Singleton;

@Singleton
public class UniqueIdGenerator {

	private int id = 0;
	
	public synchronized int getAndIncrement() {
		int result = id;
		id++;
		return result;
	}
}
