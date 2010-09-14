//$Id: NumberedNode.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.ops;

/**
 * @author Gavin King
 */
public class NumberedNode extends Node {

	private long id;

	public NumberedNode() {
		super();
	}


	public NumberedNode(String name) {
		super( name );
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
