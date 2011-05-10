/**
 * 
 */
package org.csstudio.platform.libs.dal.tests.characteristic;

public class Holder<E> {
	private E value;

	public void setValue(E value) {
		this.value = value;
	}

	public E getValue() {
		return value;
	}
}