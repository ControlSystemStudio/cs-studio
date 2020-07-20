//$Id: ZImpl.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.xml.hbm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;

@Entity
@Inheritance( strategy = InheritanceType.JOINED )
@org.hibernate.annotations.Proxy( proxyClass = Z.class )
@Table( name = "Z" )
public class ZImpl implements Z {
	private static final long serialVersionUID = 1L;

	private Integer zId = null;
	private B b = null;

	@Id
	@GeneratedValue
	@Column( name = "zID" )
	public Integer getZId() {
		return zId;
	}

	public void setZId(Integer zId) {
		this.zId = zId;
	}

	@ManyToOne( optional = false, targetEntity = BImpl.class, fetch = FetchType.LAZY )
	@JoinColumn( name = "bID", referencedColumnName = "bID")
	public B getB() {
		return b;
	}

	public void setB(B b) {
		this.b = b;
	}
}
