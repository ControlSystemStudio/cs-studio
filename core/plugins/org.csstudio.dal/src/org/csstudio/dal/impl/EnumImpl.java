/**
 * 
 */
package org.csstudio.dal.impl;

/**
 * Defautl implementation of DAL enum object.
 * 
 * @author ikriznar
 *
 */
public final class EnumImpl implements org.csstudio.dal.Enum {
	
	private int index;
	private Object value;
	private String description;
	
	public EnumImpl(int index, Object value, String description) {
		this.index=index;
		this.value=value;
		this.description=description;
	}
	
	public String description() {
		return description;
	}
	public int index() {
		return index;
	}
	public Object value() {
		return value;
	}
	

}
