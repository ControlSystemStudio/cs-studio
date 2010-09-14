//$Id: BImpl.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.xml.hbm;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@org.hibernate.annotations.Proxy( proxyClass = B.class )
@Table( name = "B" )
public class BImpl extends AImpl implements B {
	private static final long serialVersionUID = 1L;

	private Integer bId = 0;

	public BImpl() {
		super();
	}

	public Integer getBId() {
		return bId;
	}

	public void setBId(Integer bId) {
		this.bId = bId;
	}
}