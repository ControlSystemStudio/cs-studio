package org.csstudio.utility.toolbox.func;

public abstract class Option<T> {

	public abstract boolean hasValue();

	public abstract T get();

	public T getOrElse(T alternative) {
		return hasValue() ? get() : alternative;
	}

}