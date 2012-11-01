package org.csstudio.utility.toolbox.func;

public final class None<T> extends Option<T> {
	
	public None() {
	}
	
	@Override
	public boolean hasValue() {
		return false;
	}

	@Override
	public T get() {
		throw new IllegalStateException();
	}
	
	public String toString() {
		return "";
	}
	
	public boolean equals(Object other) {
		return  (other == null || other.getClass() != None.class) ? false : true;
	}
	
	public int hashCode() {
		return -1;
	}

}