//$Id: B1.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.emops.cascade;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


@Entity
public class B1 {

	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private int id;

	@ManyToOne( fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST} )
	@JoinColumn( name = "aId" )
	private A a;

	@OneToMany( fetch = FetchType.LAZY, mappedBy = "b1", cascade = {CascadeType.PERSIST} )
	private Set<C1> c1List;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public A getA() {
		return a;
	}

	public void setA(A a) {
		this.a = a;
	}

	public Set<C1> getC1List() {
		if ( c1List == null )
			c1List = new HashSet<C1>();
		return c1List;
	}

	public void setC1List(Set<C1> list) {
		c1List = list;
	}
}