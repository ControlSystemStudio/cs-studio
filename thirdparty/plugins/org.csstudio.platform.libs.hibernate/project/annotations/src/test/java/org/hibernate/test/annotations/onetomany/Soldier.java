//$Id: Soldier.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.onetomany;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Soldier {
	private Integer id;
	private String name;
	private Troop troop;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "troop_fk")
	public Troop getTroop() {
		return troop;
	}

	public void setTroop(Troop troop) {
		this.troop = troop;
	}

	public boolean equals(Object o) {
		if ( this == o ) return true;
		if ( !( o instanceof Soldier ) ) return false;

		final Soldier soldier = (Soldier) o;

		if ( !name.equals( soldier.name ) ) return false;

		return true;
	}

	public int hashCode() {
		return name.hashCode();
	}
}
