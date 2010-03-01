/**
 * 
 */
package org.csstudio.platform.internal.dal;

class Holder<E> {
	private E value;

	public void setValue(E value) {
		this.value = value;
	}

	public E getValue() {
		return value;
	}
}