package org.csstudio.archive.reader.fastarchiver;

import org.epics.vtype.VType;

public class FaVInt implements VType{
	private int value;
	
	public FaVInt (int value){
		this.value = value;
	}

	public Object getValue() {
		return value;
	}
	

}
